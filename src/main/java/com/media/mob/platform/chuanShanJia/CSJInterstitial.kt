package com.media.mob.platform.chuanShanJia

import android.app.Activity
import android.content.res.Configuration
import android.view.View
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdLoadType
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTAdNative.FullScreenVideoAdListener
import com.bytedance.sdk.openadsdk.TTAdNative.NativeExpressAdListener
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd.FullScreenVideoAdInteractionListener
import com.bytedance.sdk.openadsdk.TTNativeExpressAd
import com.media.mob.bean.request.MediaLoadType
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.bean.request.MediaRequestResult
import com.media.mob.helper.logger.MobLogger
import com.media.mob.media.interstitial.IInterstitial
import com.media.mob.media.interstitial.InterstitialWrapper
import com.media.mob.platform.IPlatform

class CSJInterstitial(val activity: Activity): InterstitialWrapper() {

    private val classTarget = CSJInterstitial::class.java.simpleName

    /**
     * 广告平台名称
     */
    override val platformName: String = IPlatform.PLATFORM_CSJ

    /**
     * 穿山甲模板渲染插屏广告对象
     */
    private var interstitialExpressAd: TTNativeExpressAd? = null

    /**
     * 穿山甲新模板渲染插屏广告对象
     */
    private var interstitialNewExpressAd: TTFullScreenVideoAd? = null

    /**
     * 穿山甲插屏广告是否位新插屏
     */
    private var requestNewTemplateExpress: Boolean = false

    /**
     * 展示插屏广告
     */
    override fun show() {
        if (requestNewTemplateExpress) {
            interstitialNewExpressAd?.showFullScreenVideoAd(activity)
        } else {
            interstitialExpressAd?.showInteractionExpressAd(activity)
        }
    }

    /**
     * 广告销毁
     */
    override fun destroy() {
        interstitialNewExpressAd = null
    }

    fun requestInterstitial(mediaRequestParams: MediaRequestParams<IInterstitial>) {
        val adNative = TTAdSdk.getAdManager().createAdNative(mediaRequestParams.activity)

        val mediaOrientation = when (mediaRequestParams.activity.resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                TTAdConstant.VERTICAL
            }
            else -> {
                TTAdConstant.HORIZONTAL
            }
        }

        val mediaLoadType = when (mediaRequestParams.slotParams.mediaLoadType) {
            MediaLoadType.LOAD -> {
                TTAdLoadType.LOAD
            }
            MediaLoadType.PRELOAD -> {
                TTAdLoadType.PRELOAD
            }
            else -> {
                TTAdLoadType.UNKNOWN
            }
        }

        val builder = AdSlot.Builder()
            .setCodeId(mediaRequestParams.tacticsInfo.thirdSlotId)
            .setExpressViewAcceptedSize(
                mediaRequestParams.slotParams.mediaAcceptedWidth,
                mediaRequestParams.slotParams.mediaAcceptedHeight
            )
            .setAdCount(1)
            .setSupportDeepLink(true)
            .setOrientation(mediaOrientation)
            .setAdLoadType(mediaLoadType)

        if (mediaRequestParams.slotParams.forceShowDownloadDialog) {
            builder.setDownloadType(TTAdConstant.DOWNLOAD_TYPE_POPUP)
        } else {
            builder.setDownloadType(TTAdConstant.DOWNLOAD_TYPE_NO_POPUP)
        }

        mediaRequestParams.mediaPlatformLog.insertRequestTime()

        if (mediaRequestParams.slotParams.interstitialNewTemplateExpress) {
            requestNewTemplateExpress = true
            requestNewTemplate(adNative, builder.build(), mediaRequestParams)
        } else {
            requestNewTemplateExpress = false
            requestTemplate(adNative, builder.build(), mediaRequestParams)
        }
    }

    private fun requestTemplate(adNative: TTAdNative, adSlot: AdSlot, mediaRequestParams: MediaRequestParams<IInterstitial>) {
        adNative.loadInteractionExpressAd(adSlot, object : NativeExpressAdListener {
            /**
             * 插屏广告请求失败回调
             */
            override fun onError(code: Int, message: String?) {
                MobLogger.e(classTarget, "穿山甲插屏广告请求失败: Code=$code, Message=${message ?: "Unknown"}")

                mediaRequestParams.mediaPlatformLog.handleRequestFailed(code, message ?: "Unknown")

                mediaRequestParams.mediaRequestResult.invoke(
                    MediaRequestResult(null, 60006, "穿山甲插屏广告请求失败: Code=$code, Message=${message ?: "Unknown"}")
                )

                destroy()
            }

            /**
             * 插屏广告请求成功回调
             */
            override fun onNativeExpressAdLoad(nativeExpressAdList: MutableList<TTNativeExpressAd>?) {
                if (nativeExpressAdList.isNullOrEmpty()) {
                    MobLogger.e(classTarget, "穿山甲插屏广告请求结果异常，返回的广告对象列表为Null")

                    mediaRequestParams.mediaPlatformLog.handleRequestFailed(-1, "穿山甲插屏广告请求结果异常，返回的广告对象列表为Null")

                    mediaRequestParams.mediaRequestResult.invoke(
                        MediaRequestResult(null, 60005, "穿山甲插屏广告请求结果异常，返回的广告对象列表为Null")
                    )

                    destroy()

                    return
                }

                MobLogger.e(classTarget, "穿山甲插屏广告请求成功，等待模板渲染成功")

                interstitialExpressAd = nativeExpressAdList.first()

                interstitialExpressAd?.setExpressInteractionListener(object : TTNativeExpressAd.AdInteractionListener {

                    /**
                     * 插屏广告点击
                     */
                    override fun onAdClicked(view: View?, type: Int) {
                        MobLogger.e(classTarget, "穿山甲插屏广告点击")

                        invokeMediaClickListener()

                        reportMediaActionEvent(
                            "click",
                            mediaRequestParams.tacticsInfo,
                            mediaRequestParams.mediaRequestLog
                        )
                    }

                    /**
                     * 插屏广告展示
                     */
                    override fun onAdShow(view: View?, type: Int) {
                        MobLogger.e(classTarget, "穿山甲插屏广告展示")

                        invokeMediaShowListener()

                        reportMediaActionEvent(
                            "show",
                            mediaRequestParams.tacticsInfo,
                            mediaRequestParams.mediaRequestLog
                        )
                    }

                    /**
                     * 插屏广告渲染失败
                     */
                    override fun onRenderFail(view: View?, message: String?, code: Int) {
                        MobLogger.e(classTarget, "穿山甲插屏广告渲染失败: Code=${code}, Message=${message ?: "Unknown"}")

                        mediaRequestParams.mediaPlatformLog.handleRequestFailed(code, message ?: "Unknown")

                        mediaRequestParams.mediaRequestResult.invoke(
                            MediaRequestResult(null, 60006, "穿山甲插屏广告渲染失败: Code=${code}, Message=${message ?: "Unknown"}")
                        )

                        destroy()
                    }

                    /**
                     * 插屏广告渲染成功
                     */
                    override fun onRenderSuccess(view: View?, width: Float, height: Float) {
                        MobLogger.e(classTarget, "穿山甲插屏广告渲染成功")

                        mediaRequestParams.mediaPlatformLog.handleRequestSucceed()
                        mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@CSJInterstitial))
                    }

                    /**
                     * 插屏广告点击
                     */
                    override fun onAdDismiss() {
                        MobLogger.e(classTarget, "穿山甲插屏广告关闭")

                        invokeMediaCloseListener()
                    }
                })

                interstitialExpressAd?.render()
            }
        })
    }

    private fun requestNewTemplate(adNative: TTAdNative, adSlot: AdSlot, mediaRequestParams: MediaRequestParams<IInterstitial>) {
        adNative.loadFullScreenVideoAd(adSlot, object : FullScreenVideoAdListener {

            /**
             * 插屏广告请求失败回调
             */
            override fun onError(code: Int, message: String?) {
                MobLogger.e(classTarget, "穿山甲插屏广告请求失败: Code=$code, Message=${message ?: "Unknown"}")

                mediaRequestParams.mediaPlatformLog.handleRequestFailed(code, message ?: "Unknown")

                mediaRequestParams.mediaRequestResult.invoke(
                    MediaRequestResult(null, 60006, "穿山甲插屏广告请求失败: Code=$code, Message=${message ?: "Unknown"}")
                )

                destroy()
            }

            /**
             * 插屏广告加载完成的回调
             */
            override fun onFullScreenVideoAdLoad(fullscreenVideoAd: TTFullScreenVideoAd?) {
                if (fullscreenVideoAd == null) {
                    MobLogger.e(classTarget, "穿山甲插屏广告请求结果异常，返回的广告对象为Null")

                    mediaRequestParams.mediaPlatformLog.handleRequestFailed(-1, "穿山甲插屏广告请求结果异常，返回的广告对象为Null")

                    mediaRequestParams.mediaRequestResult.invoke(
                        MediaRequestResult(null, 60005, "穿山甲插屏广告请求结果异常，返回的广告对象为Null")
                    )

                    destroy()

                    return
                }

                interstitialNewExpressAd = fullscreenVideoAd

                MobLogger.e(
                    classTarget,
                    "穿山甲插屏广告展示截止时间: ${interstitialNewExpressAd?.expirationTimestamp} : 当前时间: ${System.currentTimeMillis()}"
                )

                interstitialNewExpressAd?.setShowDownLoadBar(true)

                interstitialNewExpressAd?.setFullScreenVideoAdInteractionListener(object : FullScreenVideoAdInteractionListener {

                    /**
                     * 插屏广告展示回调
                     */
                    override fun onAdShow() {
                        MobLogger.e(classTarget, "穿山甲插屏广告展示")

                        invokeMediaShowListener()

                        reportMediaActionEvent(
                            "show",
                            mediaRequestParams.tacticsInfo,
                            mediaRequestParams.mediaRequestLog
                        )
                    }

                    /**
                     * 插屏广告下载Bar点击回调
                     */
                    override fun onAdVideoBarClick() {
                        MobLogger.e(classTarget, "穿山甲插屏广告点击")

                        invokeMediaClickListener()

                        reportMediaActionEvent(
                            "click",
                            mediaRequestParams.tacticsInfo,
                            mediaRequestParams.mediaRequestLog
                        )
                    }

                    /**
                     * 插屏广告关闭回调
                     */
                    override fun onAdClose() {
                        MobLogger.e(classTarget, "穿山甲插屏广告关闭")

                        invokeMediaCloseListener()
                    }

                    /**
                     * 插屏广告视频播放完成回调
                     */
                    override fun onVideoComplete() {
                        MobLogger.e(classTarget, "穿山甲插屏广告视频播放完成")
                    }

                    /**
                     * 插屏广告跳过视频播放回调
                     */
                    override fun onSkippedVideo() {
                        MobLogger.e(classTarget, "穿山甲插屏广告跳过视频播放回调")
                    }
                })

            }

            /**
             * 插屏广告物料缓存成功回调
             */
            override fun onFullScreenVideoCached() {
                MobLogger.e(classTarget, "穿山甲插屏广告物料缓存成功")
            }

            /**
             * 插屏广告物料缓存成功回调
             */
            override fun onFullScreenVideoCached(fullScreenVideoAd: TTFullScreenVideoAd?) {
                MobLogger.e(classTarget, "穿山甲插屏广告物料缓存成功")

                mediaRequestParams.mediaPlatformLog.handleRequestSucceed()
                mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@CSJInterstitial))
            }
        })
    }
}