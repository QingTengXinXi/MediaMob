package com.media.mob.platform.kuaishou

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.kwad.sdk.api.KsAdSDK
import com.kwad.sdk.api.KsLoadManager
import com.kwad.sdk.api.KsScene
import com.kwad.sdk.api.KsSplashScreenAd
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.bean.request.MediaRequestResult
import com.media.mob.helper.lifecycle.ActivityLifecycle
import com.media.mob.helper.logger.MobLogger
import com.media.mob.media.view.IMobView
import com.media.mob.media.view.MobViewWrapper
import com.media.mob.platform.IPlatform

class KSSplash(context: Context) : MobViewWrapper(context) {
    
    private val classTarget = KSSplash::class.java.simpleName

    /**
     * 广告平台名称
     */
    override val platformName: String = IPlatform.PLATFORM_KS

    /**
     * 快手联盟开屏广告对象
     */
    private var splashAd: KsSplashScreenAd? = null
    
    private var clicked = false

    private var paused = false

    private var closeCallback = false

    /**
     * 是否回调开屏展示方法
     */
    private var callbackShow = false

    /**
     * 是否回调开屏关闭方法
     */
    private var callbackClose = false

    fun requestSplash(mediaRequestParams: MediaRequestParams<IMobView>) {
        object : ActivityLifecycle(mediaRequestParams.activity) {
            override fun activityResumed() {
                super.activityResumed()
                paused = false

                if (clicked) {
                    if (!closeCallback) {
                        closeCallback = true
                        invokeMediaCloseListener()
                    }
                }
            }

            override fun activityPaused() {
                super.activityPaused()
                paused = true
            }
        }

        val scene = KsScene.Builder(mediaRequestParams.tacticsInfo.thirdSlotId.toLong())
            .needShowMiniWindow(false)
            .build()

        KsAdSDK.getLoadManager().loadSplashScreenAd(scene, object : KsLoadManager.SplashScreenAdListener {
            override fun onError(code: Int, message: String?) {
                MobLogger.e(classTarget, "快手联盟开屏广告请求失败: Code=${code}, Message=${message ?: "Unknown"}")

                mediaRequestParams.mediaPlatformLog.handleRequestFailed(code, message ?: "Unknown")

                mediaRequestParams.mediaRequestResult.invoke(
                    MediaRequestResult(null, 85002, "快手联盟开屏广告请求失败: Code=${code}, Message=${message ?: "Unknown"}")
                )

                destroy()
            }

            override fun onRequestResult(number: Int) {
                MobLogger.e(classTarget, "快手联盟开屏广告请求填充: $number")
            }

            override fun onSplashScreenAdLoad(splashScreenAd: KsSplashScreenAd?) {
                if (splashScreenAd == null) {
                    MobLogger.e(classTarget, "快手联盟开屏广告请求结果异常，返回广告对象为Null")

                    mediaRequestParams.mediaPlatformLog.handleRequestFailed(-1, "快手联盟开屏广告请求结果异常，返回广告对象为Null")

                    mediaRequestParams.mediaRequestResult.invoke(
                        MediaRequestResult(null, 85003, "快手联盟开屏广告请求结果异常，返回广告对象为Null")
                    )
                    return
                }

                splashAd = splashScreenAd

                val view: View? = splashAd?.getView(mediaRequestParams.activity, object : KsSplashScreenAd.SplashScreenAdInteractionListener {
                    override fun onAdClicked() {
                        MobLogger.e(classTarget, "快手联盟开屏广告点击")

                        clicked = true

                        invokeMediaClickListener()

                        reportMediaActionEvent(
                            "click",
                            mediaRequestParams.tacticsInfo,
                            mediaRequestParams.mediaRequestLog
                        )
                    }

                    override fun onAdShowError(code: Int, message: String?) {
                        MobLogger.e(classTarget, "快手联盟开屏广告展示失败: Code=$code, Message=${message ?: "Unknown"}")
                    }

                    override fun onAdShowEnd() {
                        MobLogger.e(classTarget, "快手联盟开屏广告展示完毕")

                        if (!callbackShow) {
                            callbackShow = true

                            invokeMediaShowListener()

                            reportMediaActionEvent(
                                "show",
                                mediaRequestParams.tacticsInfo,
                                mediaRequestParams.mediaRequestLog
                            )
                        }

                        if (!paused) {
                            if (!callbackClose) {
                                callbackClose = true

                                invokeMediaCloseListener()
                            }
                        }
                    }

                    override fun onAdShowStart() {
                        MobLogger.e(classTarget, "快手联盟开屏广告展示")

                        if (!callbackShow) {
                            callbackShow = true

                            invokeMediaShowListener()

                            reportMediaActionEvent(
                                "show",
                                mediaRequestParams.tacticsInfo,
                                mediaRequestParams.mediaRequestLog
                            )
                        }
                    }

                    override fun onSkippedAd() {
                        MobLogger.e(classTarget, "快手联盟开屏广告跳过")

                        if (!paused) {
                            if (!callbackClose) {
                                callbackClose = true

                                invokeMediaCloseListener()
                            }
                        }
                    }

                    override fun onDownloadTipsDialogShow() {
                        MobLogger.e(classTarget, "快手联盟开屏广告下载弹窗展示")
                    }

                    override fun onDownloadTipsDialogDismiss() {
                        MobLogger.e(classTarget, "快手联盟开屏广告下载弹窗关闭")
                        if (!paused) {
                            if (!callbackClose) {
                                callbackClose = true

                                invokeMediaCloseListener()
                            }
                        }
                    }

                    override fun onDownloadTipsDialogCancel() {
                        MobLogger.e(classTarget, "快手联盟开屏广告下载弹窗取消")
                        if (!paused) {
                            if (!callbackClose) {
                                callbackClose = true

                                invokeMediaCloseListener()
                            }
                        }
                    }
                })

                if (!mediaRequestParams.activity.isFinishing) {
                    if (view != null && mediaRequestParams.slotParams.splashShowViewGroup != null) {
                        mediaRequestParams.slotParams.splashShowViewGroup?.removeAllViews()

                        view.layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        mediaRequestParams.slotParams.splashShowViewGroup?.addView(view)

                        mediaRequestParams.mediaPlatformLog.handleRequestSucceed()

                        mediaRequestParams.mediaRequestResult.invoke(
                            MediaRequestResult(this@KSSplash)
                        )
                    } else {

                        mediaRequestParams.mediaPlatformLog.handleRequestFailed(-1, "快手联盟开屏广告展示异常，开屏广告View为Null")

                        mediaRequestParams.mediaRequestResult.invoke(
                            MediaRequestResult(null, 85004, "快手联盟开屏广告请求结果异常，返回广告对象为Null")
                        )
                    }
                }
            }
        })
    }

    override fun destroy() {
        splashAd = null
    }
}