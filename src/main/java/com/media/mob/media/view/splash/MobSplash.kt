package com.media.mob.media.view.splash

import android.annotation.SuppressLint
import android.app.Activity
import android.view.ViewGroup
import com.media.mob.bean.PositionConfig
import com.media.mob.bean.log.MediaRequestLog
import com.media.mob.bean.request.SlotParams
import com.media.mob.dispatch.MobRequestResult
import com.media.mob.dispatch.loader.SplashLoader
import com.media.mob.media.view.IMobView
import com.media.mob.platform.IPlatform

@SuppressLint("ViewConstructor")
class MobSplash(val activity: Activity, private val positionConfig: PositionConfig) : IMobView(activity) {

    /**
     * 广告对象
     */
    private var mobView: IMobView? = null

    /**
     * 广告对象的平台名称
     */
    override val platformName: String
        get() {
            return mobView?.platformName ?: ""
        }

    /**
     * 广告请求响应时间
     */
    override val mediaResponseTime: Long
        get() {
            return mobView?.mediaResponseTime ?: -1L
        }

    /**
     * 展示上报状态
     */
    override val showReportState: Boolean
        get() {
            return mobView?.showReportState ?: false
        }

    /**
     * 点击上报状态
     */
    override val clickReportState: Boolean
        get() {
            return mobView?.clickReportState ?: false
        }

    /**
     * 检查广告是否有效
     */
    override fun checkMediaValidity(): Boolean {
        return mobView != null && mobView?.checkMediaValidity() == true
    }

    /**
     * 销毁广告
     */
    override fun destroy() {
        super.destroy()

        mobView?.destroy()
        mobView = null
    }

    /**
     * 请求开屏广告
     */
    fun requestSplash(slotParams: SlotParams) {
        val splashLoader = SplashLoader(
            activity,
            positionConfig,
            MediaRequestLog(positionConfig),
            object : MobRequestResult<IMobView> {

                override fun requestFailed(code: Int, message: String) {
                    invokeRequestFailedListener(code, message)
                }

                override fun requestSucceed(result: IMobView) {
                    mobView = result

                    mobView?.mediaShowListener = {
                        invokeMediaShowListener()
                    }

                    mobView?.mediaClickListener = {
                        invokeMediaClickListener()
                    }

                    mobView?.mediaCloseListener = {
                        invokeMediaCloseListener()
                    }

                    if (mobView?.platformName == IPlatform.PLATFORM_CSJ) {
                        if (mobView?.parent != null && mobView?.parent is ViewGroup) {
                            (mobView?.parent as ViewGroup).removeView(mobView)
                        }

                        addView(mobView)
                    }

                    invokeRequestSuccessListener()
                }
            })

        splashLoader.handleRequest(slotParams)
    }

    /**
     * 执行开屏广告请求成功回调
     */
    private fun invokeRequestSuccessListener() {
        requestSuccessListener?.invoke()
    }

    /**
     * 执行开屏广告请求失败回调
     */
    private fun invokeRequestFailedListener(code: Int, message: String) {
        requestFailedListener?.invoke(code, message)
    }
}