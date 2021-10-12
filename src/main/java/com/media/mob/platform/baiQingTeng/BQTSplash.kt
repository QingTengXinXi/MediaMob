package com.media.mob.platform.baiQingTeng

import android.content.Context
import com.baidu.mobads.sdk.api.RequestParameters
import com.baidu.mobads.sdk.api.SplashAd
import com.baidu.mobads.sdk.api.SplashInteractionListener
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.bean.request.MediaRequestResult
import com.media.mob.helper.logger.MobLogger
import com.media.mob.media.view.IMobView
import com.media.mob.media.view.MobViewWrapper
import com.media.mob.platform.IPlatform

class BQTSplash(context: Context) : MobViewWrapper(context) {

    private val classTarget = BQTSplash::class.java.simpleName

    /**
     * 广告平台名称
     */
    override val platformName: String = IPlatform.PLATFORM_BQT

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
     * 销毁广告对象
     */
    override fun destroy() {
        super.destroy()

        splashAd?.destroy()
        splashAd = null
    }

    fun requestSplash(mediaRequestParams: MediaRequestParams<IMobView>) {

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

        splashAd = SplashAd(mediaRequestParams.activity, mediaRequestParams.slotTactics.thirdSlotId,
            parameters.build(), object : SplashInteractionListener {

                /**
                 * 开屏广告请求成功回调
                 */
                override fun onADLoaded() {
                    if (!callbackSuccess) {
                        callbackSuccess = true
                        mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@BQTSplash))
                    }
                }

                /**
                 * 开屏广告关闭回调
                 */
                override fun onAdDismissed() {
                    MobLogger.e(classTarget, "百青藤开屏广告关闭：$clickedState ")

                    if (canInvokeClose) {
                        if (!closeCallbackState) {
                            closeCallbackState = true
                            invokeViewCloseListener()
                        }
                    } else {
                        canInvokeClose = true
                    }
                }

                /**
                 * 开屏广告成功展示回调
                 */
                override fun onAdPresent() {
                    if (!callbackSuccess) {
                        callbackSuccess = true
                        mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@BQTSplash))
                    }

                    MobLogger.e(classTarget, "百青藤开屏广告展示")

                    invokeViewShowListener()

                    reportMediaActionEvent("show", mediaRequestParams.slotTactics, mediaRequestParams.mediaRequestLog)
                }

                /**
                 * 开屏广告点击回调
                 */
                override fun onAdClick() {
                    if (!callbackSuccess) {
                        callbackSuccess = true
                        mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@BQTSplash))
                    }

                    clickedState = true
                    canInvokeClose = false

                    MobLogger.e(classTarget, "百青藤开屏广告点: $clickedState ")

                    invokeViewClickListener()

                    reportMediaActionEvent("click", mediaRequestParams.slotTactics, mediaRequestParams.mediaRequestLog)
                }

                /**
                 * 开屏广告加载失败回调
                 */
                override fun onAdFailed(message: String?) {
                    MobLogger.e(classTarget, "百青藤开屏广告请求失败: Message=${message ?: "Unknown"}")

                    mediaRequestParams.mediaPlatformLog.handleRequestFailed(-1, message ?: "Unknown")

                    mediaRequestParams.mediaRequestResult.invoke(
                        MediaRequestResult(
                            null,
                            60006,
                            "百青藤开屏广告请求失败: Code=-1, Message=${message ?: "Unknown"}"
                        )
                    )
                    destroy()
                }

                override fun onAdCacheSuccess() {
                    MobLogger.e(classTarget, "百青藤开屏广告缓存成功")
                }

                override fun onAdCacheFailed() {
                    MobLogger.e(classTarget, "百青藤开屏广告缓存失败")
                }

                /**
                 * 开屏广告落地页关闭回调
                 */
                override fun onLpClosed() {
                    MobLogger.e(classTarget, "百青藤开屏广告落地页关闭：$clickedState ")

                    if (canInvokeClose) {
                        if (!closeCallbackState) {
                            closeCallbackState = true
                            invokeViewCloseListener()
                        }
                    } else {
                        canInvokeClose = true
                    }
                }
            })

        if (mediaRequestParams.slotTactics.thirdAppId.isNotEmpty()) {
            splashAd?.setAppSid(mediaRequestParams.slotTactics.thirdAppId)
        }

        splashAd?.loadAndShow(mediaRequestParams.slotParams.splashViewGroup)
    }
}