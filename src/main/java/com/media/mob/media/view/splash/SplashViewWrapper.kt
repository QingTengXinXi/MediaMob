package com.media.mob.media.view.splash

import com.media.mob.bean.TacticsInfo
import com.media.mob.bean.log.MediaRequestLog

abstract class SplashViewWrapper : ISplash {

    /**
     * 展示状态
     */
    override var showState: Boolean = false

    /**
     * 点击状态
     */
    override var clickState: Boolean = false

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
     * 执行广告View展示监听
     */
    fun invokeMediaShowListener() {
        mediaShowListener?.invoke()
    }

    /**
     * 执行广告View点击监听
     */
    fun invokeMediaClickListener() {
        mediaClickListener?.invoke()
    }

    /**
     * 执行广告View关闭监听
     */
    fun invokeMediaCloseListener() {
        mediaCloseListener?.invoke()
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