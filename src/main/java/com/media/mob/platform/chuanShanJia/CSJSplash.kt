package com.media.mob.platform.chuanShanJia

import android.app.Activity
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdLoadType
import com.bytedance.sdk.openadsdk.TTAdNative.SplashAdListener
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTSplashAd
import com.media.mob.Constants
import com.media.mob.bean.TacticsInfo
import com.media.mob.bean.request.MediaLoadType
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.bean.request.MediaRequestResult
import com.media.mob.helper.logger.MobLogger
import com.media.mob.helper.thread.runMainThread
import com.media.mob.media.view.splash.ISplash
import com.media.mob.media.view.splash.SplashViewWrapper
import com.media.mob.platform.IPlatform

class CSJSplash(private val activity: Activity) : SplashViewWrapper() {

    private val classTarget = CSJSplash::class.java.simpleName

    /**
     * 广告平台名称
     */
    override val platformName: String = IPlatform.PLATFORM_CSJ

    /**
     * 广告策略信息
     */
    override var tacticsInfo: TacticsInfo? = null

    /**
     * 广告请求响应时间
     */
    override var mediaResponseTime: Long = -1L

    /**
     * 穿山甲开屏广告对象
     */
    private var splashAd: TTSplashAd? = null

    /**
     * 开屏广告是否回调关闭方法
     */
    private var closeCallbackState = false

    /**
     * 检查广告是否有效
     */
    override fun checkMediaValidity(): Boolean {
        return splashAd != null && !showState && checkMediaCacheTimeout()
    }

    /**
     * 检查广告缓存时间
     */
    override fun checkMediaCacheTimeout(): Boolean {
        if (Constants.mediaConfig == null) {
            return false
        }

        return (SystemClock.elapsedRealtime() - mediaResponseTime) > Constants.mediaConfig.splashCacheTime
    }

    /**
     * 展示开屏广告
     */
    override fun show(viewGroup: ViewGroup) {
        if (splashAd?.splashView != null) {
            viewGroup.removeAllViews()

            splashAd?.splashView?.let {
                viewGroup.addView(it)
            }
        }
    }

    /**
     * 销毁广告对象
     */
    override fun destroy() {
        splashAd?.setSplashInteractionListener(null)
        splashAd?.setDownloadListener(null)

        splashAd = null

        mediaShowListener = null
        mediaClickListener = null
        mediaCloseListener = null
    }

    fun requestSplash(mediaRequestParams: MediaRequestParams<ISplash>) {
        this.tacticsInfo = mediaRequestParams.tacticsInfo

        val adNative = TTAdSdk.getAdManager().createAdNative(activity)

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
            .setImageAcceptedSize(1080, 1920)
            .setAdLoadType(mediaLoadType)

        if (mediaRequestParams.slotParams.splashLimitClickArea) {
            builder.setSplashButtonType(TTAdConstant.SPLASH_BUTTON_TYPE_DOWNLOAD_BAR)
        }

        if (mediaRequestParams.slotParams.forceShowDownloadDialog) {
            builder.setDownloadType(TTAdConstant.DOWNLOAD_TYPE_POPUP)
        } else {
            builder.setDownloadType(TTAdConstant.DOWNLOAD_TYPE_NO_POPUP)
        }

        val splashListener = object : SplashAdListener {

            /**
             * 开屏广告请求失败回调
             */
            override fun onError(code: Int, message: String?) {
                MobLogger.e(classTarget, "穿山甲开屏广告请求失败: Code=${code}, Message=${message ?: "Unknown"}")

                mediaRequestParams.mediaPlatformLog.handleRequestFailed(code, message ?: "Unknown")

                mediaRequestParams.mediaRequestResult.invoke(
                    MediaRequestResult(null, 83002, "穿山甲开屏广告请求失败: Code=${code}, Message=${message ?: "Unknown"}")
                )

                destroy()
            }

            /**
             * 开屏广告请求超时回调
             */
            override fun onTimeout() {
                MobLogger.e(classTarget, "穿山甲开屏广告请求超时")

                mediaRequestParams.mediaPlatformLog.handleRequestFailed(-1, "穿山甲开屏广告请求超时")

                mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(null, 83002, "穿山甲开屏广告请求超时"))
            }

            /**
             * 开屏广告请求成功回调
             */
            override fun onSplashAdLoad(ttSplashAd: TTSplashAd?) {
                if (ttSplashAd == null) {
                    MobLogger.e(classTarget, "穿山甲开屏广告请求结果异常，返回广告对象为Null")

                    mediaRequestParams.mediaPlatformLog.handleRequestFailed(-1, "穿山甲开屏广告请求结果异常，返回广告对象为Null")

                    mediaRequestParams.mediaRequestResult.invoke(
                        MediaRequestResult(null, 83004, "穿山甲开屏广告请求结果异常，返回广告对象为Null")
                    )

                    destroy()

                    return
                }

                splashAd = ttSplashAd

                splashAd?.setSplashInteractionListener(object : TTSplashAd.AdInteractionListener {
                    /**
                     * 开屏广告点击回调
                     */
                    override fun onAdClicked(view: View?, type: Int) {
                        MobLogger.e(classTarget, "穿山甲开屏广告点击")

                        reportMediaActionEvent("click",
                            mediaRequestParams.tacticsInfo,
                            mediaRequestParams.mediaRequestLog)

                        invokeMediaClickListener()
                    }

                    /**
                     * 开屏广告展示回调
                     */
                    override fun onAdShow(view: View?, type: Int) {
                        MobLogger.e(classTarget, "穿山甲开屏广告展示")

                        reportMediaActionEvent("show",
                            mediaRequestParams.tacticsInfo,
                            mediaRequestParams.mediaRequestLog)

                        invokeMediaShowListener()
                    }

                    /**
                     * 开屏广告跳过回调
                     */
                    override fun onAdSkip() {
                        MobLogger.e(classTarget, "穿山甲开屏广告跳过")

                        if (!closeCallbackState) {
                            closeCallbackState = true
                            invokeMediaCloseListener()
                        }
                    }

                    /**
                     * 开屏广告倒计时结束回调
                     */
                    override fun onAdTimeOver() {
                        MobLogger.e(classTarget, "穿山甲开屏广告关闭")

                        if (!closeCallbackState) {
                            closeCallbackState = true
                            invokeMediaCloseListener()
                        }
                    }
                })

                mediaResponseTime = SystemClock.elapsedRealtime()

                mediaRequestParams.mediaPlatformLog.handleRequestSucceed()

                mediaRequestParams.mediaRequestResult.invoke(
                    MediaRequestResult(this@CSJSplash)
                )
            }
        }

        mediaRequestParams.mediaPlatformLog.insertRequestTime()

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