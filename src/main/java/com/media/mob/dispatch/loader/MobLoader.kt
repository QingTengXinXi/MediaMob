package com.media.mob.dispatch.loader

import android.app.Activity
import com.media.mob.Constants
import com.media.mob.bean.PositionConfig
import com.media.mob.bean.SlotTactics
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
    val slotType: String,
    val positionConfig: PositionConfig,
    val mediaRequestLog: MediaRequestLog,
    val mobRequestResult: MobRequestResult<T>
) {

    private val classTarget = MobLoader::class.java.simpleName

    /**
     * 广告位流量策略配置
     */
    private val slotTacticsQueue: ConcurrentLinkedQueue<ArrayList<SlotTactics>> by lazy {
        ConcurrentLinkedQueue<ArrayList<SlotTactics>>()
    }

    /**
     * 初始化配置信息
     */
    init {
        slotTacticsQueue.clear()

        positionConfig.slotConfig.slotTacticsList.forEach {
            slotTacticsQueue.add(it)
        }
    }

    /**
     * 处理广告流量策略配置
     */
    fun handleRequest(slotParams: SlotParams) {
        val slotTactics = choiceSlotTactics()

        MobLogger.e(classTarget, "获取到的策略信息为: $slotTactics")

        if (slotTactics == null) {
            invokeRequestFailed()
        } else {
            handleSlotTactics(slotParams, slotTactics)
        }
    }

    /**
     * 处理广告位策略执行
     */
    private fun handleSlotTactics(slotParams: SlotParams, slotTactics: SlotTactics) {
        val mediaPlatformLogs =
            MediaPlatformLog(slotTactics.thirdAppId, slotTactics.thirdSlotId, slotType, slotTactics.thirdPlatformName)

        if (!Constants.platforms.containsKey(slotTactics.thirdPlatformName)) {
            handleTacticsFailed(slotParams, mediaPlatformLogs)
        } else {
            val platform = Constants.platforms[slotTactics.thirdPlatformName]

            if (platform != null) {
                runMobMediaLoaderThread {
                    handlePlatformRequest(
                        platform,
                        MediaRequestParams(
                            activity,
                            slotParams,
                            slotTactics,
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
    private fun choiceSlotTactics(): SlotTactics? {
        if (slotTacticsQueue.isNotEmpty()) {
            val slotTacticsList = slotTacticsQueue.poll()

            if (slotTacticsList != null && slotTacticsList.size > 0) {
                return if (slotTacticsList.size > 1) {
                    val weightRandom = WeightRandom(slotTacticsList)
                    weightRandom.random()
                } else {
                    slotTacticsList.first()
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