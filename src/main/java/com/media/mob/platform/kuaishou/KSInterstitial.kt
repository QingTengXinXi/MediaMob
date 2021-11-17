package com.media.mob.platform.kuaishou

import android.app.Activity
import android.os.SystemClock
import com.kwad.sdk.api.KsAdSDK
import com.kwad.sdk.api.KsInterstitialAd
import com.kwad.sdk.api.KsLoadManager
import com.kwad.sdk.api.KsScene
import com.kwad.sdk.api.KsVideoPlayConfig
import com.media.mob.Constants
import com.media.mob.bean.TacticsInfo
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.bean.request.MediaRequestResult
import com.media.mob.helper.logger.MobLogger
import com.media.mob.media.interstitial.IInterstitial
import com.media.mob.media.interstitial.InterstitialWrapper
import com.media.mob.platform.IPlatform

class KSInterstitial(val activity: Activity) : InterstitialWrapper() {

    private val classTarget = KSInterstitial::class.java.simpleName

    /**
     * 广告平台名称
     */
    override val platformName: String = IPlatform.PLATFORM_KS

    /**
     * 广告策略信息
     */
    override var tacticsInfo: TacticsInfo? = null

    /**
     * 广告请求响应时间
     */
    override var mediaResponseTime: Long = -1L

    /**
     * 快手联盟插屏广告对象
     */
    private var interstitialAd: KsInterstitialAd? = null

    /**
     * 是否回调插屏关闭方法
     */
    private var callbackClose = false

    /**
     * 展示插屏广告
     */
    override fun show() {
        val videoPlayConfig = KsVideoPlayConfig.Builder()
            .videoSoundEnable(true)
            .skipThirtySecond(true)
            .build()

        interstitialAd?.showInterstitialAd(activity, videoPlayConfig)
    }

    /**
     * 检查广告是否有效
     */
    override fun checkMediaValidity(): Boolean {
        return interstitialAd != null && !showState && !checkMediaCacheTimeout()
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
        interstitialAd = null
    }

    fun requestInterstitial(mediaRequestParams: MediaRequestParams<IInterstitial>) {
        this.tacticsInfo = mediaRequestParams.tacticsInfo

        val scene = KsScene.Builder(mediaRequestParams.tacticsInfo.thirdSlotId.toLong()).build()

        mediaRequestParams.mediaPlatformLog.insertRequestTime()

        KsAdSDK.getLoadManager().loadInterstitialAd(scene, object : KsLoadManager.InterstitialAdListener {
            /**
             * 插屏广告请求失败
             */
            override fun onError(code: Int, message: String?) {
                MobLogger.e(classTarget, "快手联盟插屏广告请求失败: Code=${code}, Message=${message ?: "Unknown"}")

                mediaRequestParams.mediaPlatformLog.handleRequestFailed(code, message ?: "Unknown")

                mediaRequestParams.mediaRequestResult.invoke(
                    MediaRequestResult(null, 86002, "快手联盟插屏广告请求失败: Code=${code}, Message=${message ?: "Unknown"}")
                )

                destroy()
            }

            /**
             * 插屏广告服务端填充广告
             */
            override fun onRequestResult(number: Int) {
                MobLogger.e(classTarget, "快手联盟插屏广告请求填充: $number")
            }

            /**
             * 插屏广告加载成功回调
             */
            override fun onInterstitialAdLoad(interstitialAdList: MutableList<KsInterstitialAd>?) {
                if (interstitialAdList.isNullOrEmpty()) {
                    MobLogger.e(classTarget, "快手联盟插屏广告请求结果异常，返回广告对象列表为Null")

                    mediaRequestParams.mediaPlatformLog.handleRequestFailed(-1, "快手联盟插屏广告请求结果异常，返回广告对象列表为Null")

                    mediaRequestParams.mediaRequestResult.invoke(
                        MediaRequestResult(null, 86003, "快手联盟插屏广告请求结果异常，返回广告对象列表为Null")
                    )
                    return
                }

                interstitialAd = interstitialAdList.first()

                interstitialAd?.setAdInteractionListener(object : KsInterstitialAd.AdInteractionListener {
                    /**
                     * 插屏广告点击回调
                     */
                    override fun onAdClicked() {
                        MobLogger.e(classTarget, "快手联盟插屏广告点击")

                        reportMediaActionEvent(
                            "click",
                            mediaRequestParams.tacticsInfo,
                            mediaRequestParams.mediaRequestLog
                        )

                        invokeMediaClickListener()
                    }

                    /**
                     * 插屏广告展示回调
                     */
                    override fun onAdShow() {
                        MobLogger.e(classTarget, "快手联盟插屏广告展示")

                        reportMediaActionEvent(
                            "show",
                            mediaRequestParams.tacticsInfo,
                            mediaRequestParams.mediaRequestLog
                        )

                        invokeMediaShowListener()
                    }

                    /**
                     * 插屏广告关闭回调
                     */
                    override fun onAdClosed() {
                        MobLogger.e(classTarget, "快手联盟插屏广告关闭")

                        if (!callbackClose) {
                            callbackClose = true
                            invokeMediaCloseListener()
                        }
                    }

                    /**
                     * 插屏广告页面关闭
                     */
                    override fun onPageDismiss() {
                        MobLogger.e(classTarget, "快手联盟插屏广告页面关闭")

                        if (!callbackClose) {
                            callbackClose = true
                            invokeMediaCloseListener()
                        }
                    }

                    /**
                     * 插屏广告视频播放错误
                     */
                    override fun onVideoPlayError(code: Int, extra: Int) {
                        MobLogger.e(classTarget, "快手联盟插屏广告视频播放错误: Code=$code, Extra=$extra")
                    }

                    /**
                     * 插屏广告视频播放完成
                     */
                    override fun onVideoPlayEnd() {
                        MobLogger.e(classTarget, "快手联盟插屏广告视频播放完成")
                    }

                    /**
                     * 插屏广告视频播放开始
                     */
                    override fun onVideoPlayStart() {
                        MobLogger.e(classTarget, "快手联盟插屏广告视频开始播放")
                    }

                    /**
                     * 插屏广告跳过
                     */
                    override fun onSkippedAd() {
                        MobLogger.e(classTarget, "快手联盟插屏广告跳过")
                    }
                })

                mediaResponseTime = SystemClock.elapsedRealtime()

                mediaRequestParams.mediaPlatformLog.handleRequestSucceed()
                mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@KSInterstitial))
            }
        })
    }
}