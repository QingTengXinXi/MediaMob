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
class MobSplash(val activity: Activity, private val positionConfig: PositionConfig): IMobView(activity) {

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

    override fun destroy() {
        super.destroy()

        mobView?.destroy()
        mobView = null
    }

    fun requestSplash(slotParams: SlotParams) {
        val splashLoader = SplashLoader(activity, positionConfig, MediaRequestLog(positionConfig), object : MobRequestResult<IMobView> {

            override fun requestFailed(code: Int, message: String) {
                requestFailedListener?.invoke(code, message)
            }

            override fun requestSucceed(result: IMobView) {
                mobView = result

                mobView?.viewShowListener = {
                    invokeViewShowListener()
                }

                mobView?.viewClickListener = {
                    invokeViewClickListener()
                }

                mobView?.viewCloseListener = {
                    invokeViewCloseListener()
                }

                if (mobView?.platformName == IPlatform.PLATFORM_CSJ) {
                    if (mobView?.parent != null && mobView?.parent is ViewGroup) {
                        (mobView?.parent as ViewGroup).removeView(mobView)
                    }

                    addView(mobView)
                }

                requestSuccessListener?.invoke()
            }
        })

        splashLoader.handleRequest(slotParams)
    }
}