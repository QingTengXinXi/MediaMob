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
import com.media.mob.helper.WeightRandom
import com.media.mob.helper.logger.MobLogger
import com.media.mob.helper.thread.runMobMediaLoaderThread
import com.media.mob.platform.IPlatform
import java.util.concurrent.ConcurrentLinkedQueue

abstract class MobLoader<T>(
    val activity: Activity,
    private val slotType: String,
    private val positionConfig: PositionConfig,
    private val mediaRequestLog: MediaRequestLog,
    private val mobRequestResult: MobRequestResult<T>
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
    private val tacticsInfoQueue: ConcurrentLinkedQueue<TacticsInfo> by lazy {
        ConcurrentLinkedQueue<TacticsInfo>()
    }

    /**
     * 初始化配置信息
     */
    init {
        tacticsConfigQueue.clear()

        positionConfig.slotConfig.slotTacticsConfig.forEach {
            tacticsConfigQueue.add(it)
        }
    }

    /**
     * 处理广告流量策略配置
     */
    fun handleRequest(slotParams: SlotParams) {
        val tacticsInfo = choiceSlotTactics()

        MobLogger.e(classTarget, "获取到的策略信息为: $tacticsInfo")

        if (tacticsInfo == null) {
            invokeRequestFailed()
        } else {
            handleSlotTactics(slotParams, tacticsInfo)
        }
    }

    /**
     * 处理广告位策略执行
     */
    private fun handleSlotTactics(slotParams: SlotParams, tacticsInfo: TacticsInfo) {
        val mediaPlatformLogs =
            MediaPlatformLog(tacticsInfo.thirdAppId, tacticsInfo.thirdSlotId, slotType, tacticsInfo.thirdPlatformName)

        if (!Constants.platforms.containsKey(tacticsInfo.thirdPlatformName)) {
            handleTacticsFailed(slotParams, mediaPlatformLogs)
        } else {
            val platform = Constants.platforms[tacticsInfo.thirdPlatformName]

            if (platform != null) {
                runMobMediaLoaderThread {
                    handlePlatformRequest(
                        platform,
                        MediaRequestParams(
                            activity,
                            slotParams,
                            tacticsInfo,
                            mediaRequestLog,
                            mediaPlatformLogs
                        ) { result: MediaRequestResult<T> ->
                            if (result.data != null && result.code == 200) {
                                handleTacticsSuccess(result.data, mediaPlatformLogs)
                            } else {
                                handleTacticsFailed(slotParams, mediaPlatformLogs)
                            }
                        })
                }
            }
        }
    }

    /**
     * 处理策略执行失败
     */
    private fun handleTacticsFailed(slotParams: SlotParams, mediaPlatformLog: MediaPlatformLog) {
        MobLogger.e(classTarget, "第三方广告平台请求失败: $mediaPlatformLog")

        mediaRequestLog.mediaPlatformLogs.add(mediaPlatformLog)

        handleRequest(slotParams)
    }

    /**
     * 处理策略执行成功
     */
    private fun handleTacticsSuccess(result: T, mediaPlatformLog: MediaPlatformLog) {
        MobLogger.e(classTarget, "第三方广告平台请求成功: $mediaPlatformLog")

        mediaRequestLog.mediaPlatformLogs.add(mediaPlatformLog)

        invokeRequestSuccess(result)
    }

    /**
     * 执行请求失败回调
     */
    private fun invokeRequestFailed() {
        mediaRequestLog.handleRequestResult(false)

        mobRequestResult.requestFailed(10000, "广告位策略配置执行完成，无广告平台填充")

        //TODO 上报广告位请求失败
    }

    /**
     * 执行请求成功回调
     */
    private fun invokeRequestSuccess(result: T) {
        mediaRequestLog.handleRequestResult(true)

        mobRequestResult.requestSucceed(result)

        //TODO 上报广告位请求成功
    }

    /**
     * 获取一个广告位策略
     */
    private fun choiceSlotTactics(): TacticsInfo? {
        if (tacticsInfoQueue.isNotEmpty()) {
            return tacticsInfoQueue.poll()
        }

        if (tacticsConfigQueue.isNotEmpty()) {
            val tacticsConfig = tacticsConfigQueue.poll()

            if (tacticsConfig?.checkParamsValidity() == true) {
                return when (tacticsConfig.tacticsType) {
                    TacticsType.TYPE_WEIGHT -> {
                        val weightRandom = WeightRandom(tacticsConfig)
                        weightRandom.random()
                    }
                    TacticsType.TYPE_PRIORITY -> {
                        tacticsInfoQueue.addAll(tacticsConfig.tacticsInfoList)
                        tacticsInfoQueue.poll()
                    }
                    else -> {
                        MobLogger.e(classTarget, "暂不支持并行请求的广告位执行策略")
                        null
                    }
                }
            }
        }
        return null
    }

    /**
     * 请求广告的具体逻辑
     */
    abstract fun handlePlatformRequest(platform: IPlatform, mediaRequestParams: MediaRequestParams<T>)
}