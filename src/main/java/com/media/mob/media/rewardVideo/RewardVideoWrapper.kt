package com.media.mob.media.rewardVideo

import com.media.mob.bean.TacticsInfo
import com.media.mob.bean.log.MediaRequestLog

abstract class RewardVideoWrapper : IRewardVideo {

    /**
     * 展示上报状态
     */
    override var showReportState: Boolean = false

    /**
     * 点击上报状态
     */
    override var clickReportState: Boolean = false

    /**
     * 广告展示监听
     */
    override var mediaShowListener: (() -> Unit)? = null

    /**
     * 广告点击监听
     */
    override var mediaClickListener: (() -> Unit)? = null

    /**
     * 广告关闭监听
     */
    override var mediaCloseListener: (() -> Unit)? = null

    /**
     * 广告奖励发放监听
     */
    override var mediaRewardedListener: ((Boolean) -> Unit)? = null

    /**
     * 执行广告展示监听回调
     */
    fun invokeMediaShowListener() {
        mediaShowListener?.invoke()
    }

    /**
     * 执行广告点击监听回调
     */
    fun invokeMediaClickListener() {
        mediaClickListener?.invoke()
    }

    /**
     * 执行广告关闭监听回调
     */
    fun invokeMediaCloseListener() {
        mediaCloseListener?.invoke()
    }

    /**
     * 执行广告奖励发放监听回调
     */
    fun invokeMediaRewardedListener(result: Boolean) {
        mediaRewardedListener?.invoke(result)
    }

    /**
     * 上报广告行为事件
     */
    fun reportMediaActionEvent(event: String, tacticsInfo: TacticsInfo, mediaRequestLog: MediaRequestLog) {
        if (("show" == event && !showReportState) || ("click" == event && !clickReportState)) {

            if ("show" == event) {
                showReportState = true
            } else if ("click" == event) {
                clickReportState = true
            }

            val params = HashMap<String, String>()
            params["request_id"] = mediaRequestLog.requestId
            params["position_id"] = mediaRequestLog.positionId
            params["position_name"] = mediaRequestLog.positionName
            params["third_slot_type"] = "RewardVideo"
            params["third_app_id"] = tacticsInfo.thirdAppId
            params["third_slot_id"] = tacticsInfo.thirdSlotId
            params["third_platform_name"] = tacticsInfo.thirdPlatformName

            //TODO 处理广告行为事件上报
        }
    }
}