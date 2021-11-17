package com.media.mob.media.view

import android.content.Context
import com.media.mob.bean.TacticsInfo
import com.media.mob.bean.log.MediaRequestLog

abstract class MobViewWrapper(context: Context) : IMobView(context) {

    /**
     * 展示状态
     */
    override var showState: Boolean = false

    /**
     * 点击状态
     */
    override var clickState: Boolean = false

    /**
     * 销毁广告
     */
    override fun destroy() {
        mediaShowListener = null
        mediaClickListener = null
        mediaCloseListener = null
    }

    /**
     * 上报广告行为事件
     */
    fun reportMediaActionEvent(event: String, tacticsInfo: TacticsInfo, mediaRequestLog: MediaRequestLog) {
        if (("show" == event && !showState) || ("click" == event && !clickState)) {
            if ("show" == event) {
                showState = true
            } else if ("click" == event) {
                clickState = true
            }

            val params = HashMap<String, String>()
            params["request_id"] = mediaRequestLog.requestId

            params["position_id"] = mediaRequestLog.positionId
            params["position_name"] = mediaRequestLog.positionName

            params["third_slot_type"] = "Splash"

            params["third_app_id"] = tacticsInfo.thirdAppId
            params["third_slot_id"] = tacticsInfo.thirdSlotId
            params["third_platform_name"] = tacticsInfo.thirdPlatformName

            //TODO 处理广告行为事件上报
        }
    }
}