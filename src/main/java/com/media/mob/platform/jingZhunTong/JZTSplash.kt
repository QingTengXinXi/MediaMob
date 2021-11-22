package com.media.mob.platform.jingZhunTong

import android.app.Activity
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import com.jd.ad.sdk.imp.JadListener
import com.jd.ad.sdk.imp.splash.JadSplash
import com.jd.ad.sdk.work.JadPlacementParams
import com.media.mob.Constants
import com.media.mob.bean.TacticsInfo
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.bean.request.MediaRequestResult
import com.media.mob.helper.logger.MobLogger
import com.media.mob.media.view.splash.ISplash
import com.media.mob.media.view.splash.SplashViewWrapper
import com.media.mob.platform.IPlatform

class JZTSplash(private val activity: Activity) : SplashViewWrapper() {

    private val classTarget = JZTSplash::class.java.simpleName

    /**
     * 广告平台名称
     */
    override val platformName: String = IPlatform.PLATFORM_JZT

    /**
     * 广告策略信息
     */
    override var tacticsInfo: TacticsInfo? = null

    /**
     * 广告请求响应时间
     */
    override var mediaResponseTime: Long = -1L

    /**
     * 京准通开屏广告对象
     */
    private var splashAd: JadSplash? = null

    /**
     * 京准通开屏广告View
     */
    private var splashView: View? = null

    /**
     * 检查广告是否有效
     */
    override fun checkMediaValidity(): Boolean {
        return splashAd != null && !showState && !checkMediaCacheTimeout()
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
        if (splashView != null) {
            viewGroup.removeAllViews()

            splashView?.let {
                viewGroup.addView(it)
            }
        }
    }

    /**
     * 销毁广告对象
     */
    override fun destroy() {
        splashAd?.destroy()
        splashAd = null

        mediaShowListener = null
        mediaClickListener = null
        mediaCloseListener = null
    }

    fun requestSplash(mediaRequestParams: MediaRequestParams<ISplash>) {
        this.tacticsInfo = mediaRequestParams.tacticsInfo

        val clickType = if (mediaRequestParams.slotParams.splashLimitClickArea) {
            JadPlacementParams.ClickAreaType.ONLY_TEXT_CLICK.type
        } else {
            JadPlacementParams.ClickAreaType.SERVER.type
        }

        val jadParams = JadPlacementParams.Builder()
            .setPlacementId(mediaRequestParams.tacticsInfo.thirdSlotId)
            .setSize(mediaRequestParams.slotParams.mediaAcceptedWidth, mediaRequestParams.slotParams.mediaAcceptedHeight)
            .setTolerateTime(mediaRequestParams.slotParams.splashRequestTimeOut / 1000F)
            .setSkipTime(5)
            .setSplashAdClickAreaType(clickType)
            .build()

        splashAd = JadSplash(activity, jadParams, object : JadListener {

            /**
             * 开屏广告加载成功回调
             */
            override fun onAdLoadSuccess() {
                MobLogger.e(classTarget, "京准通开屏广告请求成功，等待广告渲染成功。开屏广告的价格为${splashAd?.jadExtra?.price ?: 0}分")
            }

            /**
             * 开屏广告加载失败回调
             */
            override fun onAdLoadFailed(code: Int, message: String?) {
                MobLogger.e(classTarget, "京准通开屏广告加载失败: Code=${code}, Message=${message ?: "Unknown"}")

                mediaRequestParams.mediaPlatformLog.handleRequestFailed(code, message ?: "Unknown")

                mediaRequestParams.mediaRequestResult.invoke(
                    MediaRequestResult(null, 85002, "京准通开屏广告加载失败: Code=${code}, Message=${message ?: "Unknown"}")
                )

                destroy()
            }

            /**
             * 开屏广告渲染成功回调
             */
            override fun onAdRenderSuccess(view: View?) {
                if (view == null) {
                    MobLogger.e(classTarget, "京准通开屏广告渲染异常，返回广告View对象为Null")

                    mediaRequestParams.mediaPlatformLog.handleRequestFailed(-1, "京准通开屏广告渲染异常，返回广告View对象为Null")

                    mediaRequestParams.mediaRequestResult.invoke(
                        MediaRequestResult(null, 85003, "京准通开屏广告渲染异常，返回广告View对象为Null")
                    )

                    destroy()

                    return
                }

                MobLogger.e(classTarget, "京准通开屏广告渲染成功")

                mediaResponseTime = SystemClock.elapsedRealtime()

                mediaRequestParams.mediaPlatformLog.handleRequestSucceed()
                mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@JZTSplash))
            }

            /**
             * 开屏广告渲染失败回调
             */
            override fun onAdRenderFailed(code: Int, message: String?) {
                MobLogger.e(classTarget, "京准通开屏广告渲染失败: Code=${code}, Message=${message ?: "Unknown"}")

                mediaRequestParams.mediaPlatformLog.handleRequestFailed(code, message ?: "Unknown")

                mediaRequestParams.mediaRequestResult.invoke(
                    MediaRequestResult(null, 85004, "京准通开屏广告渲染失败: Code=${code}, Message=${message ?: "Unknown"}")
                )

                destroy()
            }

            /**
             * 开屏广告点击回调
             */
            override fun onAdClicked() {
                MobLogger.e(classTarget, "京准通开屏广告点击")

                reportMediaActionEvent("click", mediaRequestParams.tacticsInfo, mediaRequestParams.mediaRequestLog)

                invokeMediaClickListener()
            }

            /**
             * 开屏广告展示回调
             */
            override fun onAdExposure() {
                MobLogger.e(classTarget, "京准通开屏广告展示")

                reportMediaActionEvent("show", mediaRequestParams.tacticsInfo, mediaRequestParams.mediaRequestLog)

                invokeMediaShowListener()
            }

            /**
             * 开屏广告关闭回调
             */
            override fun onAdDismissed() {
                MobLogger.e(classTarget, "京准通开屏广告关闭")

                invokeMediaCloseListener()
            }
        })

        mediaRequestParams.mediaPlatformLog.insertRequestTime()

        splashAd?.loadAd()
    }
}