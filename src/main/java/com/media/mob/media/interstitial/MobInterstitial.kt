package com.media.mob.media.interstitial

import android.app.Activity
import com.media.mob.bean.PositionConfig
import com.media.mob.bean.log.MediaRequestLog
import com.media.mob.bean.request.SlotParams
import com.media.mob.dispatch.MobRequestResult
import com.media.mob.dispatch.loader.InterstitialLoader

class MobInterstitial(val activity: Activity, val positionConfig: PositionConfig) : IInterstitial {

    /**
     * 插屏广告
     */
    private var interstitial: IInterstitial? = null

    /**
     * 广告对象的平台名称
     */
    override val platformName: String
        get() {
            return interstitial?.platformName ?: ""
        }

    /**
     * 展示上报状态
     */
    override val showReportState: Boolean
        get() {
            return interstitial?.showReportState ?: false
        }

    /**
     * 点击上报状态
     */
    override val clickReportState: Boolean
        get() {
            return interstitial?.clickReportState ?: false
        }

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
     * 请求成功的监听
     */
    var requestSuccessListener: (() -> Unit)? = null

    /**
     * 请求失败的监听
     */
    var requestFailedListener: ((code: Int, message: String) -> Unit)? = null

    override fun show() {
        
    }

    override fun destroy() {
        
    }

    /**
     * 请求插屏广告
     */
    fun requestInterstitial(slotParams: SlotParams) {
        val interstitialLoader = InterstitialLoader(activity, positionConfig, MediaRequestLog(positionConfig), object :
            MobRequestResult<IInterstitial> {
            override fun requestFailed(code: Int, message: String) {
                invokeRequestFailedListener(code, message)
            }

            override fun requestSucceed(result: IInterstitial) {
                interstitial = result

                interstitial?.mediaShowListener = {
                    invokeMediaShowListener()
                }

                interstitial?.mediaClickListener = {
                    invokeMediaClickListener()
                }

                interstitial?.mediaCloseListener = {
                    invokeMediaCloseListener()
                }

                invokeRequestSuccessListener()
            }
        })

        interstitialLoader.handleRequest(slotParams)
    }

    /**
     * 执行插屏广告请求成功回调
     */
    private fun invokeRequestSuccessListener() {
        requestSuccessListener?.invoke()
    }

    /**
     * 执行插屏广告请求失败回调
     */
    private fun invokeRequestFailedListener(code: Int, message: String) {
        requestFailedListener?.invoke(code, message)
    }


    /**
     * 执行插屏广告展示监听
     */
    private fun invokeMediaShowListener() {
        mediaShowListener?.invoke()
    }

    /**
     * 执行插屏广告点击监听
     */
    private fun invokeMediaClickListener() {
        mediaClickListener?.invoke()
    }

    /**
     * 执行插屏广告关闭监听
     */
    private fun invokeMediaCloseListener() {
        mediaCloseListener?.invoke()
    }
}