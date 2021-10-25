package com.media.mob.platform.youLiangHui

import android.content.Context
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.bean.request.MediaRequestResult
import com.media.mob.helper.lifecycle.ActivityLifecycle
import com.media.mob.helper.logger.MobLogger
import com.media.mob.media.view.IMobView
import com.media.mob.media.view.MobViewWrapper
import com.media.mob.platform.IPlatform
import com.media.mob.platform.youLiangHui.helper.DownloadConfirmHelper
import com.qq.e.ads.splash.SplashAD
import com.qq.e.ads.splash.SplashADListener
import com.qq.e.comm.util.AdError

class YLHSplash(context: Context) : MobViewWrapper(context) {

    private val classTarget = YLHSplash::class.java.simpleName

    /**
     * 广告平台名称
     */
    override val platformName: String = IPlatform.PLATFORM_YLH

    /**
     * 优量汇开屏广告对象
     */
    private var splashAd: SplashAD? = null

    /**
     * 开屏广告展示的Activity是否为Pause状态
     */
    private var activityPaused = false

    /**
     * 是否回调成功
     */
    private var callbackSuccess = false

    /**
     * 开屏广告点击状态
     */
    private var clickedState = false

    /**
     * Activity生命周期监测
     */
    private var activityLifecycle: ActivityLifecycle? = null

    fun requestSplash(mediaRequestParams: MediaRequestParams<IMobView>) {
        activityLifecycle = object : ActivityLifecycle(mediaRequestParams.activity) {
            override fun activityResumed() {
                super.activityResumed()

                activityPaused = false

                MobLogger.e(classTarget, "优量汇开屏广告Activity进入Resume状态: $clickedState : $activityPaused")

                if (clickedState) {
                    invokeMediaCloseListener()

                    MobLogger.e(classTarget, "优量汇开屏广告执行广告关闭：$clickedState : $activityPaused")
                }
            }

            override fun activityPaused() {
                super.activityPaused()
                activityPaused = true

                MobLogger.e(classTarget, "优量汇开屏广告Activity进入Pause状态: $clickedState : $activityPaused")
            }
        }

        splashAd = SplashAD(
            mediaRequestParams.activity,
            mediaRequestParams.tacticsInfo.thirdSlotId,
            object : SplashADListener {
                /**
                 * 开屏广告加载失败
                 */
                override fun onNoAD(adError: AdError?) {
                    MobLogger.e(
                        classTarget,
                        "优量汇开屏广告请求失败: Code=${adError?.errorCode ?: -1}, Message=${adError?.errorMsg ?: "Unknown"}"
                    )

                    mediaRequestParams.mediaPlatformLog.handleRequestFailed(
                        adError?.errorCode ?: -1,
                        adError?.errorMsg ?: "Unknown"
                    )

                    mediaRequestParams.mediaRequestResult.invoke(
                        MediaRequestResult(
                            null, 60006,
                            "优量汇开屏广告请求失败: Code=${adError?.errorCode ?: -1}, Message=${adError?.errorMsg ?: "Unknown"}"
                        )
                    )
                }

                /**
                 * 开屏广告加载成功回调
                 */
                override fun onADLoaded(expireTimestamp: Long) {
                    if (!callbackSuccess) {
                        callbackSuccess = true

                        mediaRequestParams.mediaPlatformLog.handleRequestSucceed()

                        mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@YLHSplash))
                    }
                }

                /**
                 * 开屏广告成功展示
                 */
                override fun onADPresent() {
                    if (!callbackSuccess) {
                        callbackSuccess = true

                        mediaRequestParams.mediaPlatformLog.handleRequestSucceed()

                        mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@YLHSplash))
                    }

                    MobLogger.e(classTarget, "优量汇开屏广告展示")
                }

                /**
                 * 开屏广告曝光
                 */
                override fun onADExposure() {
                    if (!callbackSuccess) {
                        callbackSuccess = true

                        mediaRequestParams.mediaPlatformLog.handleRequestSucceed()

                        mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@YLHSplash))
                    }

                    MobLogger.e(classTarget, "优量汇开屏广告曝光")

                    invokeMediaShowListener()

                    reportMediaActionEvent("show", mediaRequestParams.tacticsInfo, mediaRequestParams.mediaRequestLog)
                }

                /**
                 * 开屏广告点击
                 */
                override fun onADClicked() {
                    clickedState = true

                    MobLogger.e(classTarget, "优量汇开屏广告点击")

                    invokeMediaClickListener()

                    reportMediaActionEvent("click", mediaRequestParams.tacticsInfo, mediaRequestParams.mediaRequestLog)
                }

                /**
                 * 开屏广告倒计时回调(ms)
                 */
                override fun onADTick(millisUntilFinished: Long) {
                    MobLogger.e(classTarget, "优量汇开屏广告倒计时: $millisUntilFinished")
                }

                /**
                 * 开屏广告关闭
                 */
                override fun onADDismissed() {
                    MobLogger.e(
                        classTarget,
                        "优量汇开屏广告关闭: $clickedState : $activityPaused : ${Thread.currentThread().name}"
                    )

                    if (clickedState) {
                        if (!activityPaused) {
                            invokeMediaCloseListener()

                            MobLogger.e(classTarget, "优量汇开屏广告执行广告关闭：$clickedState : $activityPaused")
                        }
                    } else {
                        invokeMediaCloseListener()

                        MobLogger.e(classTarget, "优量汇开屏广告执行广告关闭：$clickedState : $activityPaused")
                    }
                }
            },
            if (mediaRequestParams.slotParams.splashRequestTimeOut >= 0) mediaRequestParams.slotParams.splashRequestTimeOut.toInt() else 0
        )

        if (mediaRequestParams.slotParams.forceShowDownloadDialog) {
            splashAd?.setDownloadConfirmListener(DownloadConfirmHelper.downloadConfirmListener)
        }

        mediaRequestParams.mediaPlatformLog.insertRequestTime()

        if (mediaRequestParams.slotParams.splashFullScreenShow) {
            splashAd?.fetchFullScreenAndShowIn(mediaRequestParams.slotParams.splashShowViewGroup)
        } else {
            splashAd?.fetchAndShowIn(mediaRequestParams.slotParams.splashShowViewGroup)
        }
    }
}