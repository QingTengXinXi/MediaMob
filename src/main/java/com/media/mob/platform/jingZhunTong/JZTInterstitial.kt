package com.media.mob.platform.jingZhunTong

import android.app.Activity
import android.os.SystemClock
import android.view.View
import com.jd.ad.sdk.imp.JadListener
import com.jd.ad.sdk.imp.interstitial.JadInterstitial
import com.jd.ad.sdk.work.JadPlacementParams
import com.media.mob.Constants
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.bean.request.MediaRequestResult
import com.media.mob.helper.logger.MobLogger
import com.media.mob.media.interstitial.IInterstitial
import com.media.mob.media.interstitial.InterstitialWrapper
import com.media.mob.platform.IPlatform

class JZTInterstitial(val activity: Activity) : InterstitialWrapper() {

    private val classTarget = JZTInterstitial::class.java.simpleName

    /**
     * 广告平台名称
     */
    override val platformName: String = IPlatform.PLATFORM_JZT

    /**
     * 广告请求响应时间
     */
    override var mediaResponseTime: Long = -1L

    /**
     * 京准通插屏广告对象
     */
    private var interstitialAd: JadInterstitial? = null

    /**
     * 展示插屏广告
     */
    override fun show() {
        interstitialAd?.showInterstitialAd(activity)
    }

    /**
     * 检查广告是否有效
     */
    override fun checkMediaValidity(): Boolean {
        return interstitialAd != null && checkMediaCacheTime()
    }

    /**
     * 检查广告缓存时间
     */
    override fun checkMediaCacheTime(): Boolean {
        if (Constants.mediaConfig == null) {
            return true
        }

        return (SystemClock.elapsedRealtime() - mediaResponseTime) < Constants.mediaConfig.interstitialCacheTime
    }

    /**
     * 销毁广告
     */
    override fun destroy() {
        interstitialAd?.destroy()
        interstitialAd = null
    }

    fun requestInterstitial(mediaRequestParams: MediaRequestParams<IInterstitial>) {
        val placementParams = JadPlacementParams.Builder()
            .setPlacementId(mediaRequestParams.tacticsInfo.thirdSlotId)
            .setSize(mediaRequestParams.slotParams.mediaAcceptedWidth, mediaRequestParams.slotParams.mediaAcceptedHeight)
            .build()

        interstitialAd = JadInterstitial(mediaRequestParams.activity, placementParams, object : JadListener {

            /**
             * 插屏广告加载成功回调
             */
            override fun onAdLoadSuccess() {
                MobLogger.e(classTarget, "京准通插屏广告请求成功，等待广告渲染成功。插屏广告的价格为${interstitialAd?.jadExtra?.price ?: 0}分")
            }

            /**
             * 插屏广告加载失败回调
             */
            override fun onAdLoadFailed(code: Int, message: String?) {
                MobLogger.e(classTarget, "京准通插屏广告加载失败: Code=${code}, Message=${message ?: "Unknown"}")

                mediaRequestParams.mediaPlatformLog.handleRequestFailed(code, message ?: "Unknown")

                mediaRequestParams.mediaRequestResult.invoke(
                    MediaRequestResult(null, 85002, "京准通插屏广告加载失败: Code=${code}, Message=${message ?: "Unknown"}")
                )

                destroy()
            }

            /**
             * 插屏广告渲染成功回调
             */
            override fun onAdRenderSuccess(view: View?) {
                if (view == null) {
                    MobLogger.e(classTarget, "京准通插屏广告渲染异常，返回广告View对象为Null")

                    mediaRequestParams.mediaPlatformLog.handleRequestFailed(-1, "京准通插屏广告渲染异常，返回广告View对象为Null")

                    mediaRequestParams.mediaRequestResult.invoke(
                        MediaRequestResult(null, 85003, "京准通插屏广告渲染异常，返回广告View对象为Null")
                    )

                    destroy()

                    return
                }

                MobLogger.e(classTarget, "京准通插屏广告渲染成功")

                mediaResponseTime = SystemClock.elapsedRealtime()

                mediaRequestParams.mediaPlatformLog.handleRequestSucceed()
                mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@JZTInterstitial))
            }

            /**
             * 插屏广告渲染失败回调
             */
            override fun onAdRenderFailed(code: Int, message: String?) {
                MobLogger.e(classTarget, "京准通插屏广告渲染失败: Code=${code}, Message=${message ?: "Unknown"}")

                mediaRequestParams.mediaPlatformLog.handleRequestFailed(code, message ?: "Unknown")

                mediaRequestParams.mediaRequestResult.invoke(
                    MediaRequestResult(null, 85004, "京准通插屏广告渲染失败: Code=${code}, Message=${message ?: "Unknown"}")
                )

                destroy()
            }

            /**
             * 插屏广告点击回调
             */
            override fun onAdClicked() {
                MobLogger.e(classTarget, "京准通插屏广告点击")

                invokeMediaClickListener()

                reportMediaActionEvent(
                    "click",
                    mediaRequestParams.tacticsInfo,
                    mediaRequestParams.mediaRequestLog
                )
            }

            /**
             * 插屏广告曝光回调
             */
            override fun onAdExposure() {
                MobLogger.e(classTarget, "京准通插屏广告展示")

                invokeMediaShowListener()

                reportMediaActionEvent(
                    "show",
                    mediaRequestParams.tacticsInfo,
                    mediaRequestParams.mediaRequestLog
                )
            }

            /**
             * 开屏广告关闭回调
             */
            override fun onAdDismissed() {
                MobLogger.e(classTarget, "京准通插屏广告关闭")

                invokeMediaCloseListener()
            }
        })

        mediaRequestParams.mediaPlatformLog.insertRequestTime()

        interstitialAd?.loadAd()
    }
}