package com.media.mob.platform.baiQingTeng

import android.os.SystemClock
import com.baidu.mobads.sdk.api.AdSize
import com.baidu.mobads.sdk.api.InterstitialAd
import com.baidu.mobads.sdk.api.InterstitialAdListener
import com.media.mob.bean.request.InterstitialScene
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.bean.request.MediaRequestResult
import com.media.mob.helper.logger.MobLogger
import com.media.mob.media.interstitial.IInterstitial
import com.media.mob.media.interstitial.InterstitialWrapper
import com.media.mob.platform.IPlatform

class BQTInterstitial : InterstitialWrapper() {

    private val classTarget = BQTInterstitial::class.java.simpleName

    /**
     * 广告平台名称
     */
    override val platformName: String = IPlatform.PLATFORM_BQT

    /**
     * 广告请求响应时间
     */
    override var mediaResponseTime: Long = -1L

    /**
     * 百青藤插屏广告对象
     */
    private var interstitialAd: InterstitialAd? = null

    /**
     * 百青藤插屏广告是否在视频场景下使用
     */
    private var showVideoScene: Boolean = false

    /**
     * 展示插屏广告
     */
    override fun show() {
        if (showVideoScene) {
            // interstitialAd?.showAdInParentForVideoApp()
        } else {
            interstitialAd?.showAd()
        }
    }

    /**
     * 检查广告是否有效
     */
    override fun checkMediaValidity(): Boolean {
        //TODO 检查广告请求有效性，是否请求长时间未使用（请求时间、响应时间）
        return interstitialAd != null && interstitialAd?.isAdReady == true
    }

    /**
     * 销毁广告
     */
    override fun destroy() {
        interstitialAd?.destroy()
        interstitialAd = null
    }

    fun requestInterstitial(mediaRequestParams: MediaRequestParams<IInterstitial>) {

        interstitialAd = if (mediaRequestParams.slotParams.interstitialShowVideoScene) {
            val adSize = if (mediaRequestParams.slotParams.interstitialUsedScene == InterstitialScene.BEFORE_VIDEO_PLAY) {
                AdSize.InterstitialForVideoBeforePlay
            } else {
                AdSize.InterstitialForVideoPausePlay
            }
            InterstitialAd(mediaRequestParams.activity, adSize, mediaRequestParams.tacticsInfo.thirdSlotId)
        } else {
            InterstitialAd(mediaRequestParams.activity, mediaRequestParams.tacticsInfo.thirdSlotId)
        }

        interstitialAd?.setListener(object : InterstitialAdListener {
            /**
             * 插屏广告加载成功
             */
            override fun onAdReady() {
                MobLogger.e(classTarget, "百青藤插屏广告渲染成功")

                mediaResponseTime = SystemClock.elapsedRealtime()

                mediaRequestParams.mediaPlatformLog.handleRequestSucceed()
                mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@BQTInterstitial))
            }

            /**
             * 插屏广告展示
             */
            override fun onAdPresent() {
                MobLogger.e(classTarget, "百青藤插屏广告展示")

                invokeMediaShowListener()

                reportMediaActionEvent(
                    "show",
                    mediaRequestParams.tacticsInfo,
                    mediaRequestParams.mediaRequestLog
                )
            }

            /**
             * 插屏广告点击
             */
            override fun onAdClick(interstitialAd: InterstitialAd?) {
                MobLogger.e(classTarget, "百青藤插屏广告点击")

                invokeMediaClickListener()

                reportMediaActionEvent(
                    "click",
                    mediaRequestParams.tacticsInfo,
                    mediaRequestParams.mediaRequestLog
                )
            }

            /**
             * 插屏广告关闭
             */
            override fun onAdDismissed() {
                MobLogger.e(classTarget, "百青藤插屏广告关闭")

                invokeMediaCloseListener()
            }

            /**
             * 插屏广告加载失败
             */
            override fun onAdFailed(message: String?) {
                MobLogger.e(classTarget, "百青藤插屏广告加载失败: Message=${message ?: "Unknown"}")

                mediaRequestParams.mediaPlatformLog.handleRequestFailed(-1, message ?: "Unknown")

                mediaRequestParams.mediaRequestResult.invoke(
                    MediaRequestResult(null, 82002, "百青藤插屏广告加载失败: Message=${message ?: "Unknown"}")
                )

                destroy()
            }
        })

        if (mediaRequestParams.tacticsInfo.thirdAppId.isNotEmpty()) {
            interstitialAd?.setAppSid(mediaRequestParams.tacticsInfo.thirdAppId)
        }

        mediaRequestParams.mediaPlatformLog.insertRequestTime()

        if (mediaRequestParams.slotParams.interstitialShowVideoScene) {
            showVideoScene = true
            interstitialAd?.loadAdForVideoApp(
                mediaRequestParams.slotParams.mediaAcceptedWidth.toInt(),
                mediaRequestParams.slotParams.mediaAcceptedHeight.toInt()
            )
        } else {
            showVideoScene = false
            interstitialAd?.loadAd()
        }
    }
}