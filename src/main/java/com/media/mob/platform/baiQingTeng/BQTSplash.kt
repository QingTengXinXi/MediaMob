package com.media.mob.platform.baiQingTeng

import android.annotation.SuppressLint
import android.app.Activity
import android.os.SystemClock
import com.baidu.mobads.sdk.api.RequestParameters
import com.baidu.mobads.sdk.api.SplashAd
import com.baidu.mobads.sdk.api.SplashInteractionListener
import com.media.mob.Constants
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.bean.request.MediaRequestResult
import com.media.mob.helper.lifecycle.ActivityLifecycle
import com.media.mob.helper.logger.MobLogger
import com.media.mob.media.view.IMobView
import com.media.mob.media.view.MobViewWrapper
import com.media.mob.platform.IPlatform

@SuppressLint("ViewConstructor")
class BQTSplash(private val activity: Activity) : MobViewWrapper(activity) {

    private val classTarget = BQTSplash::class.java.simpleName

    /**
     * 广告平台名称
     */
    override val platformName: String = IPlatform.PLATFORM_BQT

    /**
     * 广告请求响应时间
     */
    override var mediaResponseTime: Long = -1L

    /**
     * 百青藤开屏广告对象
     */
    private var splashAd: SplashAd? = null

    /**
     * 是否回调成功
     */
    private var callbackSuccess = false

    /**
     * 开屏广告点击状态
     */
    private var clickedState = false

    /**
     * 是否回调开屏关闭方法
     */
    private var canInvokeClose = true

    /**
     * 开屏广告是否回调关闭方法
     */
    private var closeCallbackState = false

    /**
     * Activity生命周期监测
     */
    private var activityLifecycle: ActivityLifecycle? = null

    /**
     * 检查广告是否有效
     */
    override fun checkMediaValidity(): Boolean {
        return splashAd != null && splashAd?.isReady == true && checkMediaValidity()
    }

    /**
     * 检查广告缓存时间
     */
    override fun checkMediaCacheTime(): Boolean {
        if (Constants.mediaConfig == null) {
            return true
        }

        return (SystemClock.elapsedRealtime() - mediaResponseTime) < Constants.mediaConfig.splashCacheTime
    }

    /**
     * 销毁广告对象
     */
    override fun destroy() {
        super.destroy()

        activityLifecycle?.unregisterActivityLifecycle(activity)

        splashAd?.destroy()
        splashAd = null
    }

    fun requestSplash(mediaRequestParams: MediaRequestParams<IMobView>) {
        activityLifecycle = object : ActivityLifecycle(activity) {
            override fun activityResumed() {
                super.activityResumed()

                if (clickedState && canInvokeClose) {
                    if (!closeCallbackState) {
                        closeCallbackState = true

                        invokeMediaCloseListener()

                        MobLogger.e(classTarget, "百青藤开屏广告执行广告关闭：$clickedState : $canInvokeClose")
                    }
                }
            }
        }

        val parameters = RequestParameters.Builder()
        /**
         * 设置开屏广告请求超时
         */
        parameters.addExtra(SplashAd.KEY_TIMEOUT, mediaRequestParams.slotParams.splashRequestTimeOut.toString())

        /**
         * 是否显示下载类广告的“隐私”、“权限”等字段
         */
        parameters.addExtra(SplashAd.KEY_DISPLAY_DOWNLOADINFO, "true")

        /**
         * 是否在加载开屏物料后回调请求成功，默认请求到广告立即回调
         */
        parameters.addExtra(SplashAd.KEY_LOAD_AFTER_CACHE_END, "false")

        /**
         * 是否展示点击引导按钮，默认不展示。若设置可限制点击区域，则此选项默认打开
         */
        parameters.addExtra(SplashAd.KEY_DISPLAY_CLICK_REGION, "true")

        /**
         * 是否限制点击区域，默认不限制
         */
        if (mediaRequestParams.slotParams.splashLimitClickArea) {
            parameters.addExtra(SplashAd.KEY_LIMIT_REGION_CLICK, "true")
        } else {
            parameters.addExtra(SplashAd.KEY_LIMIT_REGION_CLICK, "false")
        }

        /**
         * 用户点击开屏下载类广告时，是否弹出Dialog
         */
        if (mediaRequestParams.slotParams.forceShowDownloadDialog) {
            parameters.addExtra(SplashAd.KEY_POPDIALOG_DOWNLOAD, "true")
        } else {
            parameters.addExtra(SplashAd.KEY_POPDIALOG_DOWNLOAD, "false")
        }

        splashAd = SplashAd(mediaRequestParams.activity, mediaRequestParams.tacticsInfo.thirdSlotId,
            parameters.build(), object : SplashInteractionListener {

                /**
                 * 开屏广告请求成功回调
                 */
                override fun onADLoaded() {
                    if (!callbackSuccess) {
                        callbackSuccess = true

                        mediaResponseTime = SystemClock.elapsedRealtime()

                        mediaRequestParams.mediaPlatformLog.handleRequestSucceed()
                        mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@BQTSplash))
                    }
                }

                /**
                 * 开屏广告加载失败回调
                 */
                override fun onAdFailed(message: String?) {
                    MobLogger.e(classTarget, "百青藤开屏广告请求失败: Message=${message ?: "Unknown"}")

                    mediaRequestParams.mediaPlatformLog.handleRequestFailed(-1, message ?: "Unknown")

                    mediaRequestParams.mediaRequestResult.invoke(
                        MediaRequestResult(null, 82002, "百青藤开屏广告请求失败: Message=${message ?: "Unknown"}")
                    )

                    destroy()
                }

                /**
                 * 开屏广告物料缓存成功回调
                 */
                override fun onAdCacheSuccess() {
                    MobLogger.e(classTarget, "百青藤开屏广告物料缓存成功")
                }

                /**
                 * 开屏广告物料缓存失败回调
                 */
                override fun onAdCacheFailed() {
                    MobLogger.e(classTarget, "百青藤开屏广告物料缓存失败")

                    mediaRequestParams.mediaPlatformLog.handleRequestFailed(-1, "百青藤开屏广告物料缓存失败")

                    mediaRequestParams.mediaRequestResult.invoke(
                        MediaRequestResult(null, 82003, "百青藤开屏广告物料缓存失败")
                    )

                    destroy()
                }

                /**
                 * 开屏广告成功展示回调
                 */
                override fun onAdPresent() {
                    if (!callbackSuccess) {
                        callbackSuccess = true

                        mediaResponseTime = SystemClock.elapsedRealtime()

                        mediaRequestParams.mediaPlatformLog.handleRequestSucceed()
                        mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@BQTSplash))
                    }

                    MobLogger.e(classTarget, "百青藤开屏广告展示")

                    invokeMediaShowListener()

                    reportMediaActionEvent("show", mediaRequestParams.tacticsInfo, mediaRequestParams.mediaRequestLog)
                }

                /**
                 * 开屏广告点击回调
                 */
                override fun onAdClick() {
                    if (!callbackSuccess) {
                        callbackSuccess = true

                        mediaResponseTime = SystemClock.elapsedRealtime()

                        mediaRequestParams.mediaPlatformLog.handleRequestSucceed()
                        mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@BQTSplash))
                    }

                    clickedState = true
                    canInvokeClose = false

                    MobLogger.e(classTarget, "百青藤开屏广告点: $clickedState ")

                    invokeMediaClickListener()

                    reportMediaActionEvent("click", mediaRequestParams.tacticsInfo, mediaRequestParams.mediaRequestLog)
                }

                /**
                 * 开屏广告关闭回调
                 */
                override fun onAdDismissed() {
                    MobLogger.e(classTarget, "百青藤开屏广告关闭：$clickedState : $canInvokeClose")

                    if (canInvokeClose) {
                        if (!closeCallbackState) {
                            closeCallbackState = true

                            MobLogger.e(classTarget, "百青藤开屏广告执行广告关闭：$clickedState : $canInvokeClose")

                            invokeMediaCloseListener()
                        }
                    } else {
                        canInvokeClose = true
                    }
                }

                /**
                 * 开屏广告落地页关闭回调
                 */
                override fun onLpClosed() {
                    MobLogger.e(classTarget, "百青藤开屏广告落地页关闭：$clickedState : $canInvokeClose")

                    if (canInvokeClose) {
                        if (!closeCallbackState) {
                            closeCallbackState = true

                            invokeMediaCloseListener()

                            MobLogger.e(classTarget, "百青藤开屏广告执行广告关闭：$clickedState : $canInvokeClose")
                        }
                    } else {
                        canInvokeClose = true
                    }
                }
            })

        if (mediaRequestParams.tacticsInfo.thirdAppId.isNotEmpty()) {
            splashAd?.setAppSid(mediaRequestParams.tacticsInfo.thirdAppId)
        }

        mediaRequestParams.mediaPlatformLog.insertRequestTime()

        splashAd?.loadAndShow(mediaRequestParams.slotParams.splashShowViewGroup)
    }
}