package com.media.mob.media.rewardVideo

import com.media.mob.bean.TacticsInfo
import com.media.mob.bean.log.MediaRequestLog

abstract class RewardVideoWrapper : IRewardVideo {

    override var showReportState: Boolean = false

    override var clickReportState: Boolean = false

    override var mediaShowListener: (() -> Unit)? = null

    override var mediaClickListener: (() -> Unit)? = null

    override var mediaCloseListener: (() -> Unit)? = null

    override var rewardedListener: ((Boolean) -> Unit)? = null

    fun invokeMediaShowListener() {
        mediaShowListener?.invoke()
    }

    fun invokeMediaClickListener() {
        mediaClickListener?.invoke()
    }

    fun invokeMediaCloseListener() {
        mediaCloseListener?.invoke()
    }

    fun invokeRewardedListener(result: Boolean) {
        rewardedListener?.invoke(result)
    }

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
        }
    }
}