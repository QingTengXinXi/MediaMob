package com.media.mob.platform.youLiangHui

import android.content.Context
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.bean.request.MediaRequestResult
import com.media.mob.helper.logger.MobLogger
import com.media.mob.helper.thread.runMainThread
import com.media.mob.media.view.IMobView
import com.media.mob.media.view.MobViewWrapper
import com.media.mob.platform.IPlatform
import com.qq.e.ads.splash.SplashAD
import com.qq.e.ads.splash.SplashADListener
import com.qq.e.comm.util.AdError

class YLHSplash(context: Context): MobViewWrapper(context) {

    private val classTarget = YLHSplash::class.java.simpleName

    /**
     * 广告平台名称
     */
    override val platformName: String = IPlatform.PLATFORM_YLH

    /**
     * 优量汇开屏广告对象
     */
    private var splashAd : SplashAD? = null


    private var paused = false


    /**
     * 是否回调成功
     */
    private var callbackSuccess = false

    /**
     * 开屏广告点击状态
     */
    private var clickedState = false

    fun requestSplash(mediaRequestParams: MediaRequestParams<IMobView>) {
        splashAd = SplashAD(mediaRequestParams.activity, mediaRequestParams.slotTactics.thirdSlotId,
            object : SplashADListener {

                /**
                 * 开屏广告关闭
                 */
                override fun onADDismissed() {
                    MobLogger.e(classTarget, "优量汇开屏广告关闭: $clickedState")
                    if (clickedState) {
                        runMainThread {
                            if (!paused) {
                                clickedState = false
                                invokeViewCloseListener()
                            }
                        }
                    } else {
                        invokeViewCloseListener()
                    }
                }

                /**
                 * 开屏广告加载失败
                 */
                override fun onNoAD(adError: AdError?) {
                    MobLogger.e(classTarget,
                        "优量汇开屏广告请求失败: Code=${adError?.errorCode ?: -1}, Message=${adError?.errorMsg ?: "Unknown"}")

                    mediaRequestParams.mediaRequestResult.invoke(
                         MediaRequestResult(null, 60006,
                            "优量汇开屏广告请求失败: Code=${adError?.errorCode ?: -1}, Message=${adError?.errorMsg ?: "Unknown"}")
                    )
                }

                /**
                 * 开屏广告成功展示
                 */
                override fun onADPresent() {
                    MobLogger.e(classTarget, "优量汇开屏广告展示")

                    if (!callbackSuccess) {
                        callbackSuccess = true
                        mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@YLHSplash))
                    }
                }

                /**
                 * 开屏广告点击
                 */
                override fun onADClicked() {
                    clickedState = true

                    MobLogger.e(classTarget, "优量汇开屏广告点击")

                    invokeViewClickListener()

                    reportMediaActionEvent("click", mediaRequestParams.slotTactics, mediaRequestParams.mediaRequestLog)
                }

                /**
                 * 开屏广告倒计时回调(ms)
                 */
                override fun onADTick(millisUntilFinished: Long) {
                    MobLogger.e(classTarget, "优量汇开屏广告倒计时: $millisUntilFinished")
                }

                /**
                 * 开屏广告曝光
                 */
                override fun onADExposure() {
                    MobLogger.e(classTarget, "优量汇开屏广告曝光")

                    if (!callbackSuccess) {
                        callbackSuccess = true
                        mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@YLHSplash))
                    }

                    invokeViewShowListener()

                    reportMediaActionEvent("show", mediaRequestParams.slotTactics, mediaRequestParams.mediaRequestLog)
                }

                /**
                 * 开屏广告加载成功回调
                 */
                override fun onADLoaded(expireTimestamp: Long) {
                    if (!callbackSuccess) {
                        callbackSuccess = true
                        mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@YLHSplash))
                    }
                }
            }, if (mediaRequestParams.slotParams.splashRequestTimeOut >= 0) mediaRequestParams.slotParams.splashRequestTimeOut.toInt() else 0)

        // if (mediaRequestParams.slotParams.forceShowDownloadDialog) {
        //     splashAd?.setDownloadConfirmListener(DownloadConfirmHelper.downloadConfirmListener)
        // }

        splashAd?.fetchAndShowIn(mediaRequestParams.slotParams.splashViewGroup)
    }
}