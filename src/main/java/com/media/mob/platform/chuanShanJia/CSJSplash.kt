package com.media.mob.platform.chuanShanJia

import android.content.Context
import android.view.View
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdLoadType
import com.bytedance.sdk.openadsdk.TTAdNative.SplashAdListener
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.media.view.IMobView
import com.media.mob.media.view.MobViewWrapper
import com.media.mob.platform.IPlatform
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTSplashAd
import com.media.mob.bean.request.MediaRequestResult
import com.media.mob.helper.logger.MobLogger
import com.media.mob.helper.thread.runMainThread

class CSJSplash(context: Context) : MobViewWrapper(context) {

    private val classTarget = CSJSplash::class.java.simpleName

    /**
     * 广告平台名称
     */
    override val platformName: String = IPlatform.PLATFORM_CSJ

    /**
     * 穿山甲开屏广告对象
     */
    private var splashAd: TTSplashAd? = null

    /**
     * 开屏广告是否回调关闭方法
     */
    private var closeCallbackState = false

    /**
     * 销毁广告对象
     */
    override fun destroy() {
        super.destroy()

        splashAd?.setSplashInteractionListener(null)
        splashAd = null
    }

    fun requestSplash(mediaRequestParams: MediaRequestParams<IMobView>) {
        val adNative = TTAdSdk.getAdManager().createAdNative(mediaRequestParams.activity)

        val builder = AdSlot.Builder()
            .setCodeId(mediaRequestParams.slotTactics.thirdSlotId)
            .setImageAcceptedSize(1080, 1920)

        if (mediaRequestParams.slotParams.splashLimitClickArea) {
            builder.setSplashButtonType(TTAdConstant.SPLASH_BUTTON_TYPE_DOWNLOAD_BAR)
        }

        if (mediaRequestParams.slotParams.forceShowDownloadDialog) {
            builder.setDownloadType(TTAdConstant.DOWNLOAD_TYPE_POPUP)
        } else {
            builder.setDownloadType(TTAdConstant.DOWNLOAD_TYPE_NO_POPUP)
        }

        builder.setAdLoadType(TTAdLoadType.LOAD)

        val splashListener = object : SplashAdListener {

            /**
             * 开屏广告请求失败回调
             */
            override fun onError(code: Int, message: String?) {
                MobLogger.e(classTarget, "穿山甲开屏广告请求失败: Code=${code}, Message=${message ?: "Unknown"}")

                mediaRequestParams.mediaRequestResult.invoke(
                    MediaRequestResult(null, 60006, "穿山甲开屏广告请求失败: Code=${code}, Message=${message ?: "Unknown"}")
                )

                destroy()
            }

            /**
             * 开屏广告请求超时回调
             */
            override fun onTimeout() {
                MobLogger.e(classTarget, "穿山甲开屏广告请求超时")

                mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(null, 60010, "穿山甲开屏广告请求超时"))
            }

            override fun onSplashAdLoad(ttSplashAd: TTSplashAd?) {
                if (ttSplashAd == null) {
                    MobLogger.e(classTarget, "穿山甲开屏广告请求结果异常")

                    mediaRequestParams.mediaRequestResult.invoke(
                        MediaRequestResult(null, 60005, "穿山甲开屏广告请求结果异常，返回结果为Null")
                    )

                    destroy()

                    return
                }

                splashAd = ttSplashAd

                mediaRequestParams.slotParams.splashViewGroup?.removeAllViews()

                splashAd?.splashView?.let {
                    mediaRequestParams.slotParams.splashViewGroup?.addView(it)
                }

                mediaRequestParams.mediaRequestResult.invoke(
                    MediaRequestResult(this@CSJSplash)
                )

                splashAd?.setSplashInteractionListener(object : TTSplashAd.AdInteractionListener {
                    /**
                     * 开屏广告点击回调
                     */
                    override fun onAdClicked(view: View?, type: Int) {
                        MobLogger.e(classTarget, "穿山甲开屏广告点击")

                        invokeViewClickListener()

                        reportMediaActionEvent(
                            "click",
                            mediaRequestParams.slotTactics,
                            mediaRequestParams.mediaRequestLog
                        )
                    }

                    /**
                     * 开屏广告展示回调
                     */
                    override fun onAdShow(view: View?, type: Int) {
                        MobLogger.e(classTarget, "穿山甲开屏广告展示")

                        invokeViewShowListener()

                        reportMediaActionEvent(
                            "show",
                            mediaRequestParams.slotTactics,
                            mediaRequestParams.mediaRequestLog
                        )
                    }

                    /**
                     * 开屏广告跳过回调
                     */
                    override fun onAdSkip() {
                        MobLogger.e(classTarget, "穿山甲开屏广告跳过")

                        if (!closeCallbackState) {
                            closeCallbackState = true
                            invokeViewCloseListener()
                        }
                    }

                    /**
                     * 开屏广告倒计时结束回调
                     */
                    override fun onAdTimeOver() {
                        MobLogger.e(classTarget, "穿山甲开屏广告关闭")

                        if (!closeCallbackState) {
                            closeCallbackState = true
                            invokeViewCloseListener()
                        }
                    }
                })
            }
        }

        /**
         * 子线程请求会有异常
         */
        runMainThread {
            if (mediaRequestParams.slotParams.splashRequestTimeOut > 0) {
                adNative.loadSplashAd(
                    builder.build(),
                    splashListener,
                    mediaRequestParams.slotParams.splashRequestTimeOut.toInt()
                )
            } else {
                adNative.loadSplashAd(builder.build(), splashListener)
            }
        }
    }
}