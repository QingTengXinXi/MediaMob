package com.media.mob.media.interstitial

import android.app.Activity
import com.media.mob.bean.PositionConfig
import com.media.mob.bean.TacticsInfo
import com.media.mob.bean.log.MediaRequestLog
import com.media.mob.bean.request.SlotParams
import com.media.mob.dispatch.MobRequestResult
import com.media.mob.dispatch.loader.InterstitialLoader
import com.media.mob.dispatch.loader.helper.MobMediaCacheHelper

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
     * 广告策略信息
     */
    override val tacticsInfo: TacticsInfo?
        get() {
            return interstitial?.tacticsInfo
        }

    /**
     * 广告请求响应时间
     */
    override val mediaResponseTime: Long
        get() {
            return interstitial?.mediaResponseTime ?: -1L
        }

    /**
     * 展示状态
     */
    override val showState: Boolean
        get() {
            return interstitial?.showState ?: false
        }

    /**
     * 点击状态
     */
    override val clickState: Boolean
        get() {
            return interstitial?.clickState ?: false
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

    /**
     * 展示插屏广告
     */
    override fun show() {
        interstitial?.show()
    }

    /**
     * 检查广告是否有效
     */
    override fun checkMediaValidity(): Boolean {
        return interstitial != null && interstitial?.checkMediaValidity() == true
    }

    /**
     * 检查广告缓存时间
     */
    override fun checkMediaCacheTimeout(): Boolean {
        return interstitial != null && interstitial?.checkMediaCacheTimeout() == true
    }

    /**
     * 销毁广告
     */
    override fun destroy() {
        if (interstitial != null) {
            if (interstitial?.checkMediaValidity() == true) {
                interstitial?.let {
                    it.tacticsInfo?.let { tacticsInfo ->
                        MobMediaCacheHelper.insertInterstitialMobMediaCache(tacticsInfo, it)
                    }
                }
            } else {
                interstitial?.destroy()
                interstitial = null
            }
        }

        mediaShowListener = null
        mediaClickListener = null
        mediaCloseListener = null
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