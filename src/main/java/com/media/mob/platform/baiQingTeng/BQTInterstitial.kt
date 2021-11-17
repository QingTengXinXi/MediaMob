package com.media.mob.platform.baiQingTeng

import android.os.SystemClock
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.baidu.mobads.sdk.api.AdSize
import com.baidu.mobads.sdk.api.ExpressInterstitialAd
import com.baidu.mobads.sdk.api.ExpressInterstitialListener
import com.baidu.mobads.sdk.api.InterstitialAd
import com.baidu.mobads.sdk.api.InterstitialAdListener
import com.media.mob.Constants
import com.media.mob.bean.TacticsInfo
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
     * 广告策略信息
     */
    override var tacticsInfo: TacticsInfo? = null

    /**
     * 广告请求响应时间
     */
    override var mediaResponseTime: Long = -1L

    /**
     * 百青藤插屏广告对象
     */
    private var interstitialAd: InterstitialAd? = null

    /**
     * 百青藤新模板插屏广告对象
     */
    private var expressInterstitialAd: ExpressInterstitialAd? = null

    /**
     * 百青藤插屏广告是否在视频场景下使用
     */
    private var interstitialShowVideoScene: Boolean = false

    /**
     * 百青藤插屏广告是否为新模板插屏
     */
    private var requestNewTemplateExpress: Boolean = false

    /**
     * 百青藤插屏广告视频场景下使用的ViewGroup
     */
    private var interstitialShowViewGroup: RelativeLayout? = null

    /**
     * 展示插屏广告
     */
    override fun show() {
        if (requestNewTemplateExpress) {
            expressInterstitialAd?.show()
        } else {
            if (interstitialShowVideoScene) {
                interstitialShowViewGroup?.let {
                    interstitialAd?.showAdInParentForVideoApp(it)
                }
            } else {
                interstitialAd?.showAd()
            }
        }
    }

    /**
     * 检查广告是否有效
     */
    override fun checkMediaValidity(): Boolean {
        return if (requestNewTemplateExpress) {
            expressInterstitialAd != null && expressInterstitialAd?.isReady == true && !showState && !checkMediaCacheTimeout()
        } else {
            interstitialAd != null && interstitialAd?.isAdReady == true && !showState && !checkMediaCacheTimeout()
        }
    }

    /**
     * 检查广告缓存时间
     */
    override fun checkMediaCacheTimeout(): Boolean {
        if (Constants.mediaConfig == null) {
            return false
        }

        return (SystemClock.elapsedRealtime() - mediaResponseTime) > Constants.mediaConfig.interstitialCacheTime
    }

    /**
     * 销毁广告
     */
    override fun destroy() {
        if (interstitialShowViewGroup != null) {
            try {
                if (interstitialShowViewGroup?.parent != null && interstitialShowViewGroup?.parent is ViewGroup) {
                    (interstitialShowViewGroup?.parent as ViewGroup).removeView(interstitialShowViewGroup)
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            interstitialShowViewGroup?.removeAllViews()
            interstitialShowViewGroup = null
        }

        expressInterstitialAd = null

        interstitialAd?.destroy()
        interstitialAd = null
    }

    /**
     * 请求插屏广告
     */
    fun requestInterstitial(mediaRequestParams: MediaRequestParams<IInterstitial>) {
        this.tacticsInfo = mediaRequestParams.tacticsInfo

        if (mediaRequestParams.slotParams.interstitialNewTemplateExpress) {
            requestNewTemplateExpress = true
            requestNewTemplate(mediaRequestParams)
        } else {
            requestNewTemplateExpress = false
            requestTemplate(mediaRequestParams)
        }
    }

    /**
     * 请求百青藤新模板渲染插屏广告
     */
    private fun requestNewTemplate(mediaRequestParams: MediaRequestParams<IInterstitial>) {
        expressInterstitialAd =
            ExpressInterstitialAd(mediaRequestParams.activity, mediaRequestParams.tacticsInfo.thirdSlotId)

        expressInterstitialAd?.setLoadListener(object : ExpressInterstitialListener {
            /**
             * 插屏广告加载成功
             */
            override fun onADLoaded() {
                MobLogger.e(classTarget, "百青藤插屏广告加载成功: ECPM=${expressInterstitialAd?.ecpmLevel}")

                mediaResponseTime = SystemClock.elapsedRealtime()

                mediaRequestParams.mediaPlatformLog.handleRequestSucceed()
                mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@BQTInterstitial))
            }

            /**
             * 插屏广告点击
             */
            override fun onAdClick() {
                MobLogger.e(classTarget, "百青藤插屏广告点击")

                reportMediaActionEvent("click", mediaRequestParams.tacticsInfo, mediaRequestParams.mediaRequestLog)

                invokeMediaClickListener()
            }

            /**
             * 插屏广告关闭
             */
            override fun onAdClose() {
                MobLogger.e(classTarget, "百青藤插屏广告关闭")

                invokeMediaCloseListener()
            }

            /**
             * 插屏广告请求失败
             */
            override fun onAdFailed(code: Int, message: String?) {
                MobLogger.e(classTarget, "百青藤插屏广告请求失败: Code= $code, Message=${message ?: "Unknown"}")

                mediaRequestParams.mediaPlatformLog.handleRequestFailed(code, message ?: "Unknown")

                mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(null,
                    82002,
                    "百青藤插屏广告加载失败: Code=$code, Message=${message ?: "Unknown"}"))

                destroy()
            }

            /**
             * 插屏广告无广告返回
             */
            override fun onNoAd(code: Int, message: String?) {
                MobLogger.e(classTarget, "百青藤插屏广告无广告返回: Code= $code, Message=${message ?: "Unknown"}")

                mediaRequestParams.mediaPlatformLog.handleRequestFailed(code, message ?: "Unknown")

                mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(null,
                    82002,
                    "百青藤插屏广告无广告返回: Code=$code, Message=${message ?: "Unknown"}"))

                destroy()
            }

            /**
             * 插屏广告曝光成功
             */
            override fun onADExposed() {
                MobLogger.e(classTarget, "百青藤插屏广告展示")

                reportMediaActionEvent("show", mediaRequestParams.tacticsInfo, mediaRequestParams.mediaRequestLog)

                invokeMediaShowListener()
            }

            /**
             * 插屏广告曝光失败
             */
            override fun onADExposureFailed() {
                MobLogger.e(classTarget, "百青藤插屏广告展示失败")
            }

            /**
             * 插屏广告视频缓存成功
             */
            override fun onVideoDownloadSuccess() {
                MobLogger.e(classTarget, "百青藤插屏广告视频缓存成功")
            }

            /**
             * 插屏广告视频缓存失败
             */
            override fun onVideoDownloadFailed() {
                MobLogger.e(classTarget, "百青藤插屏广告视频缓存失败")
            }

            /**
             * 插屏广告加载成功
             */
            override fun onLpClosed() {
                MobLogger.e(classTarget, "百青藤插屏广告落地页关闭")
            }
        })

        mediaRequestParams.mediaPlatformLog.insertRequestTime()

        expressInterstitialAd?.load()
    }

    /**
     * 请求百青藤模板渲染插屏广告
     */
    private fun requestTemplate(mediaRequestParams: MediaRequestParams<IInterstitial>) {
        interstitialAd = if (mediaRequestParams.slotParams.interstitialShowVideoScene) {
            val adSize =
                if (mediaRequestParams.slotParams.interstitialUsedScene == InterstitialScene.BEFORE_VIDEO_PLAY) {
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

                reportMediaActionEvent("show", mediaRequestParams.tacticsInfo, mediaRequestParams.mediaRequestLog)

                invokeMediaShowListener()
            }

            /**
             * 插屏广告点击
             */
            override fun onAdClick(interstitialAd: InterstitialAd?) {
                MobLogger.e(classTarget, "百青藤插屏广告点击")

                reportMediaActionEvent("click", mediaRequestParams.tacticsInfo, mediaRequestParams.mediaRequestLog)

                invokeMediaClickListener()
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

                mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(null,
                    82002,
                    "百青藤插屏广告加载失败: Message=${message ?: "Unknown"}"))

                destroy()
            }
        })

        if (mediaRequestParams.tacticsInfo.thirdAppId.isNotEmpty()) {
            interstitialAd?.setAppSid(mediaRequestParams.tacticsInfo.thirdAppId)
        }

        mediaRequestParams.mediaPlatformLog.insertRequestTime()

        if (mediaRequestParams.slotParams.interstitialShowVideoScene) {
            interstitialShowVideoScene = true
            interstitialShowViewGroup = mediaRequestParams.slotParams.interstitialShowViewGroup

            interstitialAd?.loadAdForVideoApp(
                mediaRequestParams.slotParams.mediaAcceptedWidth.toInt(),
                mediaRequestParams.slotParams.mediaAcceptedHeight.toInt()
            )
        } else {
            interstitialShowVideoScene = false
            interstitialAd?.loadAd()
        }
    }
}