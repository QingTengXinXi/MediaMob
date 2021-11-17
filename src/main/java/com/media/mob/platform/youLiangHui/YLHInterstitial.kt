package com.media.mob.platform.youLiangHui

import android.app.Activity
import android.os.SystemClock
import com.media.mob.Constants
import com.media.mob.bean.TacticsInfo
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.bean.request.MediaRequestResult
import com.media.mob.helper.logger.MobLogger
import com.media.mob.media.interstitial.IInterstitial
import com.media.mob.media.interstitial.InterstitialWrapper
import com.media.mob.platform.IPlatform
import com.qq.e.ads.interstitial2.UnifiedInterstitialAD
import com.qq.e.ads.interstitial2.UnifiedInterstitialADListener
import com.qq.e.comm.util.AdError

class YLHInterstitial(val activity: Activity) : InterstitialWrapper() {

    private val classTarget = YLHInterstitial::class.java.simpleName

    /**
     * 广告平台名称
     */
    override val platformName: String = IPlatform.PLATFORM_YLH

    /**
     * 广告策略信息
     */
    override var tacticsInfo: TacticsInfo? = null

    /**
     * 广告请求响应时间
     */
    override var mediaResponseTime: Long = -1L

    /**
     * 优量汇插屏广告对象
     */
    private var interstitialAd: UnifiedInterstitialAD? = null

    /**
     * 优量汇插屏广告是否全屏显示
     */
    private var fullScreenShow: Boolean = false

    /**
     * 展示插屏广告
     */
    override fun show() {
        if (fullScreenShow) {
            interstitialAd?.showFullScreenAD(activity)
        } else {
            interstitialAd?.show()
        }
    }

    /**
     * 检查广告是否有效
     */
    override fun checkMediaValidity(): Boolean {
        return interstitialAd != null && interstitialAd?.isValid == true && !showState && !checkMediaCacheTimeout()
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
        interstitialAd?.destroy()
        interstitialAd = null
    }

    /**
     * 请求插屏广告
     */
    fun requestInterstitial(mediaRequestParams: MediaRequestParams<IInterstitial>) {
        this.tacticsInfo = mediaRequestParams.tacticsInfo

        interstitialAd = UnifiedInterstitialAD(
            mediaRequestParams.activity,
            mediaRequestParams.tacticsInfo.thirdSlotId,
            object : UnifiedInterstitialADListener {

                /**
                 * 插屏广告加载失败
                 */
                override fun onNoAD(error: AdError?) {
                    MobLogger.e(
                        classTarget,
                        "优量汇插屏广告请求失败: Code=${error?.errorCode ?: -1}, Message=${error?.errorMsg ?: "Unknown"}"
                    )

                    mediaRequestParams.mediaPlatformLog.handleRequestFailed(
                        error?.errorCode ?: -1,
                        error?.errorMsg ?: "Unknown"
                    )

                    mediaRequestParams.mediaRequestResult.invoke(
                        MediaRequestResult(
                            null, 84002,
                            "优量汇插屏广告请求失败: Code=${error?.errorCode ?: -1}, Message=${error?.errorMsg ?: "Unknown"}"
                        )
                    )

                    destroy()
                }

                /**
                 * 插屏广告加载完毕
                 */
                override fun onADReceive() {
                    MobLogger.e(classTarget, "优量汇插屏广告加载完毕")
                }

                /**
                 * 插屏广告视频素材缓存完毕
                 */
                override fun onVideoCached() {
                    MobLogger.e(classTarget, "优量汇插屏广告视频素材缓存完毕")
                }

                /**
                 * 插屏广告渲染成功
                 */
                override fun onRenderSuccess() {
                    MobLogger.e(classTarget, "优量汇插屏广告渲染成功")

                    mediaResponseTime = SystemClock.elapsedRealtime()

                    mediaRequestParams.mediaPlatformLog.handleRequestSucceed()
                    mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@YLHInterstitial))
                }

                /**
                 * 插屏广告渲染失败
                 */
                override fun onRenderFail() {
                    MobLogger.e(classTarget, "优量汇插屏弹窗广告渲染失败")

                    mediaRequestParams.mediaPlatformLog.handleRequestFailed(-1, "优量汇插屏弹窗广告渲染失败")

                    mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(null, 84003, "优量汇插屏弹窗广告渲染失败"))

                    destroy()
                }

                /**
                 * 插屏广告展开
                 */
                override fun onADOpened() {
                    MobLogger.e(classTarget, "优量汇插屏广告展开")
                }

                /**
                 * 插屏广告曝光
                 */
                override fun onADExposure() {
                    MobLogger.e(classTarget, "优量汇插屏广告曝光")

                    reportMediaActionEvent("show", mediaRequestParams.tacticsInfo, mediaRequestParams.mediaRequestLog)

                    invokeMediaShowListener()
                }

                /**
                 * 插屏广告点击
                 */
                override fun onADClicked() {
                    MobLogger.e(classTarget, "优量汇插屏广告点击")

                    reportMediaActionEvent("click", mediaRequestParams.tacticsInfo, mediaRequestParams.mediaRequestLog)

                    invokeMediaClickListener()
                }

                /**
                 * 插屏广告点击离开应用
                 */
                override fun onADLeftApplication() {
                    MobLogger.e(classTarget, "优量汇插屏广告点击离开应用")
                }

                /**
                 * 插屏广告关闭
                 */
                override fun onADClosed() {
                    MobLogger.e(classTarget, "优量汇插屏广告关闭")

                    invokeMediaCloseListener()
                }
            })

        mediaRequestParams.mediaPlatformLog.insertRequestTime()

        if (mediaRequestParams.slotParams.interstitialFullScreenShow) {
            fullScreenShow = true
            interstitialAd?.loadFullScreenAD()
        } else {
            fullScreenShow = false
            interstitialAd?.loadAD()
        }
    }
}