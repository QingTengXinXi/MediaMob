package com.media.mob.dispatch.loader

import android.app.Activity
import com.media.mob.Constants
import com.media.mob.bean.PositionConfig
import com.media.mob.bean.TacticsConfig
import com.media.mob.bean.TacticsInfo
import com.media.mob.bean.TacticsType
import com.media.mob.bean.log.MediaPlatformLog
import com.media.mob.bean.log.MediaRequestLog
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.bean.request.MediaRequestResult
import com.media.mob.bean.request.SlotParams
import com.media.mob.dispatch.MobRequestResult
import com.media.mob.dispatch.loader.helper.MobMediaCacheHelper
import com.media.mob.helper.WeightRandom
import com.media.mob.helper.logger.MobLogger
import com.media.mob.helper.thread.runMainThreadDelayed
import com.media.mob.helper.thread.runMobMediaLoaderThread
import com.media.mob.media.IMob
import com.media.mob.media.interstitial.IInterstitial
import com.media.mob.media.rewardVideo.IRewardVideo
import com.media.mob.media.view.IMobView
import com.media.mob.platform.IPlatform
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ConcurrentSkipListMap

abstract class MobLoader<T : IMob>(
    val activity: Activity,
    private val slotType: String,
    private val positionConfig: PositionConfig,
    private val mediaRequestLog: MediaRequestLog,
    private val mobRequestResult: MobRequestResult<T>,
) {
    private val classTarget = MobLoader::class.java.simpleName

    /**
     * 广告位流量策略配置
     */
    private val tacticsConfigQueue: ConcurrentLinkedQueue<TacticsConfig> by lazy {
        ConcurrentLinkedQueue<TacticsConfig>()
    }

    /**
     * 流量优先级策略配置
     */
    private val priorityTacticsInfoQueue: ConcurrentLinkedQueue<TacticsInfo> by lazy {
        ConcurrentLinkedQueue<TacticsInfo>()
    }

    /**
     * 广告位配置策略请求结果缓存
     */
    val mobMediaResponseCache: ConcurrentSkipListMap<String, ConcurrentLinkedQueue<T>> by lazy {
        ConcurrentSkipListMap()
    }

    /**
     * 是否回调了请求结果回调
     */
    private var invokedRequestResultCallback: Boolean = false

    /**
     * 是否已检查了并行请求策略配置结果
     */
    private var checkedParallelResultFailed: Boolean = false

    /**
     * 初始化配置信息
     */
    init {
        MobLogger.e(classTarget, "PositionConfig: $positionConfig")

        tacticsConfigQueue.clear()

        if (positionConfig.slotConfig.checkParamsValidity()) {
            positionConfig.slotConfig.slotTacticsConfig.forEach {
                tacticsConfigQueue.add(it)
            }
        }
    }

    /**
     * 处理广告流量策略配置
     */
    fun handleRequest(slotParams: SlotParams) {
        val tacticsConfig = tacticsConfigQueue.poll()

        if (tacticsConfig == null) {
            invokeRequestFailed()
        } else {
            handleTacticsConfig(tacticsConfig, slotParams)
        }
    }

    /**
     * 处理广告位策略配置
     */
    private fun handleTacticsConfig(tacticsConfig: TacticsConfig, slotParams: SlotParams) {
        if (tacticsConfig.checkParamsValidity()) {
            return when (tacticsConfig.tacticsType) {
                TacticsType.TYPE_WEIGHT -> {
                    handleWeightTacticsConfig(tacticsConfig, slotParams)
                }
                TacticsType.TYPE_PRIORITY -> {
                    handlePriorityTacticsConfig(tacticsConfig, slotParams)
                }
                else -> {
                    checkedParallelResultFailed = false
                    handleParallelTacticsConfig(tacticsConfig, slotParams)
                }
            }
        } else {
            MobLogger.e(classTarget, "广告位策略配置参数检测异常，检查下一条广告位策略配置")
            handleRequest(slotParams)
        }
    }

    /**
     * 处理权重类型的广告位策略配置
     */
    private fun handleWeightTacticsConfig(tacticsConfig: TacticsConfig, slotParams: SlotParams) {
        val weightRandom = WeightRandom(tacticsConfig)
        val tacticsInfo = weightRandom.random()

        if (tacticsInfo != null) {
            MobLogger.e(classTarget, "权重类型广告位策略配置随机到的策略信息为: $tacticsInfo")

            val result = MobMediaCacheHelper.checkMobMediaCommonCache<T>(tacticsInfo, slotType)

            MobLogger.e(classTarget, "权重类型广告位策略配置从公共缓存获取广告位请求结果: $tacticsInfo : ${result != null}")

            if (result == null) {
                handleSlotTactics(tacticsConfig, tacticsInfo, slotParams)
            } else {
                invokeRequestSuccess(result)
            }
        } else {
            MobLogger.e(classTarget, "权重类型广告位策略配置随机到的策略信息为空，检查下一条广告位策略配置")
            handleRequest(slotParams)
        }
    }

    /**
     * 处理优先级类型的广告位策略配置
     */
    private fun handlePriorityTacticsConfig(tacticsConfig: TacticsConfig, slotParams: SlotParams) {
        priorityTacticsInfoQueue.addAll(tacticsConfig.tacticsInfoList)
        checkPriorityTacticsInfoQueue(tacticsConfig, slotParams)
    }

    /**
     * 检查优先级类型的广告位策略队列是否执行完成
     */
    private fun checkPriorityTacticsInfoQueue(tacticsConfig: TacticsConfig, slotParams: SlotParams) {
        val tacticsInfo = priorityTacticsInfoQueue.poll()

        if (tacticsInfo != null) {
            MobLogger.e(classTarget, "优先级类型广告位策略配置随机到的策略信息为: $tacticsInfo")

            val result = MobMediaCacheHelper.checkMobMediaCommonCache<T>(tacticsInfo, slotType)

            MobLogger.e(classTarget, "优先级类型广告位策略配置从公共缓存获取广告位请求结果: $tacticsInfo : ${result != null}")

            if (result == null) {
                handleSlotTactics(tacticsConfig, tacticsInfo, slotParams)
            } else {
                invokeRequestSuccess(result)
            }
        } else {
            MobLogger.e(classTarget, "优先级类型广告位策略配置获取到的策略信息为空，检查下一条广告位策略配置")
            handleRequest(slotParams)
        }
    }

    /**
     * 处理并行类型的广告位策略配置
     */
    private fun handleParallelTacticsConfig(tacticsConfig: TacticsConfig, slotParams: SlotParams) {
        if (tacticsConfig.tacticsInfoList.isNotEmpty()) {
            var invokedSuccessCallback = false

            tacticsConfig.tacticsInfoList.forEach {
                val result = MobMediaCacheHelper.checkMobMediaCommonCache<T>(it, slotType)

                MobLogger.e(classTarget, "并行类型广告位策略配置从公共缓存获取广告位请求结果: $it : ${result != null}")

                if (result == null) {
                    handleSlotTactics(tacticsConfig, it, slotParams)
                } else {
                    if (tacticsConfig.tacticsPriorityReturn) {
                        MobLogger.e(classTarget, "并行类型广告位策略配置开启优先返回填充的广告开关，回调广告请求成功回调: $it")

                        if (!invokedSuccessCallback) {
                            invokedSuccessCallback = true
                        }

                        invokeRequestSuccess(result)
                    } else {
                        MobLogger.e(classTarget, "并行类型广告位策略配置未开启优先返回填充的广告开关，等待定时检测广告位请求结果: ${tacticsConfig.tacticsCheckRequestTime}")
                        val key = "${tacticsConfig.tacticsSequence}-${it.tacticsInfoSequence}"

                        if (mobMediaResponseCache.containsKey(key)) {
                            mobMediaResponseCache[key]?.apply {
                                this.offer(result)
                            }
                        } else {
                            mobMediaResponseCache[key] = ConcurrentLinkedQueue<T>().apply {
                                this.offer(result)
                            }
                        }
                    }
                }
            }

            if (!invokedSuccessCallback && tacticsConfig.tacticsCheckRequestTime > 0L) {
                MobLogger.e(classTarget, "并行类型广告位策略配置中无已请求成功的配置，定时检测广告位请求结果: ${tacticsConfig.tacticsCheckRequestTime}")
                runMainThreadDelayed(tacticsConfig.tacticsCheckRequestTime) {
                    checkParallelRequestResult(slotParams)
                }
            }
        } else {
            MobLogger.e(classTarget, "并行类型广告位策略配置的策略信息列表为空，检查下一条广告位策略配置")
            handleRequest(slotParams)
        }
    }

    /**
     * 检查并行类型的广告位策略请求结果
     */
    private fun checkParallelRequestResult(slotParams: SlotParams) {
        if (mobMediaResponseCache.isNotEmpty()) {
            MobLogger.e(classTarget, "并行类型广告位策略配置的请求结果不为空，从中查询返回结果: Size=${mobMediaResponseCache.size}")

            val firstKey = mobMediaResponseCache.firstKey()

            MobLogger.e(classTarget, "并行类型广告位策略配置的请求结果中，获取到的最小的配置序列为: $firstKey")

            if (!firstKey.isNullOrEmpty()) {
                val result = mobMediaResponseCache[firstKey]?.poll()

                if (result != null) {
                    MobLogger.e(classTarget, "并行类型广告位策略配置的请求结果中，获取到的广告信息不为空，执行请求成功操作")
                    invokeRequestSuccess(result)
                    return
                }
            }

            MobLogger.e(classTarget, "并行类型广告位策略配置的请求结果中获取广告位请求结果失败，检查下一条广告位策略配置")
            checkedParallelResultFailed = true
            handleRequest(slotParams)
        } else {
            MobLogger.e(classTarget, "并行类型广告位策略配置的请求结果为空，检查下一条广告位策略配置")
            checkedParallelResultFailed = true
            handleRequest(slotParams)
        }
    }

    /**
     * 处理广告位策略执行
     */
    private fun handleSlotTactics(tacticsConfig: TacticsConfig, tacticsInfo: TacticsInfo, slotParams: SlotParams) {
        MobLogger.e(classTarget, "处理广告位策略执行: $tacticsInfo")

        val mediaPlatformLog = MediaPlatformLog(tacticsInfo.thirdAppId, tacticsInfo.thirdSlotId, slotType, tacticsInfo.thirdPlatformName)

        if (!Constants.platforms.containsKey(tacticsInfo.thirdPlatformName) && Constants.platforms[tacticsInfo.thirdPlatformName] == null) {
            handleRequest(slotParams)
        } else {
            val platform = Constants.platforms[tacticsInfo.thirdPlatformName]

            if (platform != null) {
                runMobMediaLoaderThread {
                    handlePlatformRequest(platform, MediaRequestParams(activity, slotParams, tacticsInfo, tacticsConfig, mediaRequestLog, mediaPlatformLog) { result: MediaRequestResult<T> ->

                        mediaRequestLog.mediaPlatformLogs.add(mediaPlatformLog)

                        if (result.data != null && result.code == 200) {
                            handleTacticsSuccess(result.data, tacticsInfo, tacticsConfig)
                        } else {
                            handleTacticsFailed(slotParams, tacticsInfo, tacticsConfig)
                        }
                    })
                }
            }
        }
    }

    /**
     * 处理策略执行失败
     */
    private fun handleTacticsFailed(slotParams: SlotParams, tacticsInfo: TacticsInfo, tacticsConfig: TacticsConfig) {
        MobLogger.e(classTarget, "广告位策略配置请求失败: $tacticsInfo : ${tacticsConfig.tacticsType}")

        if (tacticsConfig.tacticsType == TacticsType.TYPE_PARALLEL) {
            MobLogger.e(classTarget, "广告位策略配置为并行请求策略，等待定时检测请求结果")
        } else {
            MobLogger.e(classTarget, "广告位策略配置非并行请求策略，检查下一条广告位策略配置")
            handleRequest(slotParams)
        }
    }

    /**
     * 处理策略执行成功
     */
    private fun handleTacticsSuccess(result: T, tacticsInfo: TacticsInfo, tacticsConfig: TacticsConfig) {
        MobLogger.e(classTarget, "广告位策略配置请求成功: $tacticsInfo : ${tacticsConfig.tacticsType}")

        if (tacticsConfig.tacticsType == TacticsType.TYPE_PARALLEL) {
            MobLogger.e(classTarget, "广告位策略配置为并行请求策略，判断是否已回调请求结果接口: $invokedRequestResultCallback")
            if (invokedRequestResultCallback || checkedParallelResultFailed) {
                MobLogger.e(classTarget, "广告位策略配置为并行请求策略，已回调请求结果接口，将请求结果存储到公共缓存中")
                when (slotType) {
                    "Splash" -> {
                        MobMediaCacheHelper.insertSplashMobMediaCache(tacticsInfo, result as IMobView)
                    }
                    "RewardVideo" -> {
                        MobMediaCacheHelper.insertRewardVideoMobMediaCache(tacticsInfo, result as IRewardVideo)
                    }
                    "Interstitial" -> {
                        MobMediaCacheHelper.insertInterstitialMobMediaCache(tacticsInfo, result as IInterstitial)
                    }
                }
            } else {
                if (tacticsConfig.tacticsPriorityReturn) {
                    MobLogger.e(classTarget, "广告位策略配置为并行请求策略，未回调请求结果接口，策略开启优先返回填充的广告开关，回调广告请求成功回调: ${result.tacticsInfo}")

                    invokeRequestSuccess(result)
                } else {
                    MobLogger.e(classTarget, "广告位策略配置为并行请求策略，未回调请求结果接口，将请求结果存储到请求缓存中")
                    val key = "${tacticsConfig.tacticsSequence}-${tacticsInfo.tacticsInfoSequence}"
                    if (mobMediaResponseCache.containsKey(key)) {
                        mobMediaResponseCache[key]?.apply {
                            offer(result)
                        }
                    } else {
                        mobMediaResponseCache[key] = ConcurrentLinkedQueue<T>().apply {
                            offer(result)
                        }
                    }
                }
            }
        } else {
            MobLogger.e(classTarget, "广告位策略配置非并行请求策略，回调请求成功接口")
            invokeRequestSuccess(result)
        }
    }

    /**
     * 执行请求失败回调
     */
    private fun invokeRequestFailed() {
        MobLogger.e(classTarget, "广告位策略请求成功，回调请求成功回调")

        mediaRequestLog.handleRequestResult(false)

        if (!invokedRequestResultCallback) {
            invokedRequestResultCallback = true
        }

        mobRequestResult.requestFailed(81001, "广告位策略配置执行完成，无广告平台填充")

        handleMobMediaCache()
    }

    /**
     * 执行请求成功回调
     */
    private fun invokeRequestSuccess(result: T) {
        MobLogger.e(classTarget, "广告位策略请求成功，回调请求成功回调")

        mediaRequestLog.handleRequestResult(true)

        if (!invokedRequestResultCallback) {
            invokedRequestResultCallback = true
        }

        mobRequestResult.requestSucceed(result)

        handleMobMediaCache()
    }

    /**
     * 处理广告位策略信息请求的具体逻辑
     */
    abstract fun handlePlatformRequest(platform: IPlatform, mediaRequestParams: MediaRequestParams<T>)

    /**
     * 处理广告位策略配置请求的缓存结果
     */
    abstract fun handleMobMediaCache()
}