package com.media.mob.platform.jingZhunTong

import android.content.Context
import android.view.View
import com.jd.ad.sdk.imp.JadListener
import com.jd.ad.sdk.imp.splash.JadSplash
import com.jd.ad.sdk.work.JadPlacementParams
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.bean.request.MediaRequestResult
import com.media.mob.helper.logger.MobLogger
import com.media.mob.media.view.IMobView
import com.media.mob.media.view.MobViewWrapper
import com.media.mob.platform.IPlatform

class JZTSplash(context: Context) : MobViewWrapper(context) {

    private val classTarget = JZTSplash::class.java.simpleName

    /**
     * 广告平台名称
     */
    override val platformName: String = IPlatform.PLATFORM_JZT

    /**
     * 京准通开屏广告对象
     */
    private var splashAd: JadSplash? = null

    /**
     * 销毁广告对象
     */
    override fun destroy() {
        super.destroy()

        splashAd?.destroy()
        splashAd = null
    }

    fun requestSplash(mediaRequestParams: MediaRequestParams<IMobView>) {

        val clickType = if (mediaRequestParams.slotParams.splashLimitClickArea) {
            JadPlacementParams.ClickAreaType.ONLY_TEXT_CLICK.type
        } else {
            JadPlacementParams.ClickAreaType.SERVER.type
        }

        val jadParams = JadPlacementParams.Builder()
            .setPlacementId(mediaRequestParams.tacticsInfo.thirdSlotId)
            .setSize(mediaRequestParams.slotParams.viewAcceptedWidth, mediaRequestParams.slotParams.viewAcceptedHeight)
            .setTolerateTime(mediaRequestParams.slotParams.splashRequestTimeOut / 1000F)
            .setSkipTime(5)
            .setSplashAdClickAreaType(clickType)
            .build()

        splashAd = JadSplash(mediaRequestParams.activity, jadParams, object : JadListener {

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
                MobLogger.e(classTarget, "京准通开屏广告请求失败: Code=${code}, Message=${message ?: "Unknown"}")

                mediaRequestParams.mediaPlatformLog.handleRequestFailed(code, message ?: "Unknown")

                mediaRequestParams.mediaRequestResult.invoke(
                    MediaRequestResult(null, 60006, "京准通开屏广告请求失败: Code=${code}, Message=${message ?: "Unknown"}")
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
                        MediaRequestResult(null, 60006, "京准通开屏广告渲染异常，返回广告View对象为Null")
                    )

                    destroy()

                    return
                }

                MobLogger.e(classTarget, "京准通开屏广告成功")

                mediaRequestParams.slotParams.splashViewGroup?.removeAllViews()
                mediaRequestParams.slotParams.splashViewGroup?.addView(view)

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
                    MediaRequestResult(null, 60006, "京准通开屏广告渲染失败: Code=${code}, Message=${message ?: "Unknown"}")
                )

                destroy()
            }

            /**
             * 开屏广告点击回调
             */
            override fun onAdClicked() {
                MobLogger.e(classTarget, "京准通开屏广告点击")

                invokeMediaClickListener()

                reportMediaActionEvent(
                    "click",
                    mediaRequestParams.tacticsInfo,
                    mediaRequestParams.mediaRequestLog
                )
            }

            /**
             * 开屏广告展示回调
             */
            override fun onAdExposure() {
                MobLogger.e(classTarget, "京准通开屏广告展示")

                invokeMediaShowListener()

                reportMediaActionEvent(
                    "show",
                    mediaRequestParams.tacticsInfo,
                    mediaRequestParams.mediaRequestLog
                )
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