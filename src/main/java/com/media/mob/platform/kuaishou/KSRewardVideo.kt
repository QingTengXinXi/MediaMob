package com.media.mob.platform.kuaishou

import android.app.Activity
import android.os.SystemClock
import com.kwad.sdk.api.KsAdSDK
import com.kwad.sdk.api.KsLoadManager
import com.kwad.sdk.api.KsRewardVideoAd
import com.kwad.sdk.api.KsRewardVideoAd.RewardAdInteractionListener
import com.kwad.sdk.api.KsScene
import com.kwad.sdk.api.KsVideoPlayConfig
import com.kwad.sdk.api.core.KsAdSdkApi
import com.media.mob.Constants
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.bean.request.MediaRequestResult
import com.media.mob.helper.logger.MobLogger
import com.media.mob.helper.thread.runMainThread
import com.media.mob.media.rewardVideo.IRewardVideo
import com.media.mob.media.rewardVideo.RewardVideoWrapper
import com.media.mob.platform.IPlatform
import com.media.mob.platform.youLiangHui.YLHRewardVideo
import com.media.mob.platform.youLiangHui.helper.DownloadConfirmHelper
import com.qq.e.ads.rewardvideo.RewardVideoAD
import com.qq.e.ads.rewardvideo.RewardVideoADListener
import com.qq.e.comm.util.AdError
import com.qq.e.comm.util.VideoAdValidity.NONE_CACHE
import com.qq.e.comm.util.VideoAdValidity.VALID

class KSRewardVideo(private val activity: Activity) : RewardVideoWrapper() {

    private val classTarget = KSRewardVideo::class.java.simpleName

    /**
     * 广告平台名称
     */
    override val platformName: String = IPlatform.PLATFORM_KS

    /**
     * 广告请求响应时间
     */
    override var mediaResponseTime: Long = -1L

    /**
     * 快手联盟激励视频广告对象
     */
    private var rewardVideoAd: KsRewardVideoAd? = null

    /**
     * 激励视频是否下发奖励
     */
    private var rewardVerify: Boolean = false

    /**
     * 展示激励视频广告
     */
    override fun show() {
        runMainThread {
            rewardVideoAd?.showRewardVideoAd(activity, null)
        }
    }

    /**
     * 检查广告是否有效
     */
    override fun checkMediaValidity(): Boolean {
        return rewardVideoAd != null && rewardVideoAd?.isAdEnable == true && checkMediaCacheTime()
    }

    /**
     * 检查广告缓存时间
     */
    override fun checkMediaCacheTime(): Boolean {
        if (Constants.mediaConfig == null) {
            return true
        }

        return (SystemClock.elapsedRealtime() - mediaResponseTime) < Constants.mediaConfig.rewardVideoCacheTime
    }

    /**
     * 检查激励视频广告奖励发放状态
     */
    override fun checkRewardVerify(): Boolean {
        return rewardVerify
    }

    /**
     * 销毁广告
     */
    override fun destroy() {
        rewardVideoAd = null
    }

    fun requestRewardVideo(mediaRequestParams: MediaRequestParams<IRewardVideo>) {
        val scene = KsScene.Builder(mediaRequestParams.tacticsInfo.thirdSlotId.toLong())
            .build()

        mediaRequestParams.mediaPlatformLog.insertRequestTime()

        KsAdSDK.getLoadManager().loadRewardVideoAd(scene, object : KsLoadManager.RewardVideoAdListener {

            /**
             * 激励视频广告请求失败
             */
            override fun onError(code: Int, message: String?) {
                MobLogger.e(classTarget, "快手联盟激励视频广告请求失败: Code=${code}, Message=${message ?: "Unknown"}")

                mediaRequestParams.mediaPlatformLog.handleRequestFailed(code, message ?: "Unknown")

                mediaRequestParams.mediaRequestResult.invoke(
                    MediaRequestResult(null, 86002, "快手联盟激励视频广告请求失败: Code=${code}, Message=${message ?: "Unknown"}")
                )

                destroy()
            }

            /**
             * 激励视频广告服务端填充广告
             */
            override fun onRequestResult(number: Int) {
                MobLogger.e(classTarget, "快手联盟激励视频广告请求填充: $number")
            }

            /**
             * 激励视频广告加载成功回调
             */
            override fun onRewardVideoAdLoad(rewardVideoAdList: MutableList<KsRewardVideoAd>?) {
                if (rewardVideoAdList.isNullOrEmpty()) {
                    MobLogger.e(classTarget, "快手联盟激励视频广告请求结果异常，返回广告对象列表为Null")

                    mediaRequestParams.mediaPlatformLog.handleRequestFailed(-1, "快手联盟激励视频广告请求结果异常，返回广告对象列表为Null")

                    mediaRequestParams.mediaRequestResult.invoke(
                        MediaRequestResult(null, 86003, "快手联盟激励视频广告请求结果异常，返回广告对象列表为Null")
                    )
                    return
                }

                rewardVideoAd = rewardVideoAdList.first()

                rewardVideoAd?.setRewardAdInteractionListener(object : RewardAdInteractionListener {

                    /**
                     * 激励视频广告点击回调
                     */
                    override fun onAdClicked() {
                        MobLogger.e(classTarget, "快手联盟激励视频广告点击")

                        invokeMediaClickListener()

                        reportMediaActionEvent(
                            "click",
                            mediaRequestParams.tacticsInfo,
                            mediaRequestParams.mediaRequestLog
                        )
                    }

                    /**
                     * 激励视频广告关闭回调
                     */
                    override fun onPageDismiss() {
                        MobLogger.e(classTarget, "快手联盟激励视频广告关闭")

                        invokeMediaCloseListener()
                    }

                    /**
                     * 激励视频广告播放异常回调
                     */
                    override fun onVideoPlayError(code: Int, extra: Int) {
                        MobLogger.e(classTarget, "快手联盟激励视频广告播放错误: Code=$code, Extra=$extra")
                    }

                    /**
                     * 激励视频广告播放完成回调
                     */
                    override fun onVideoPlayEnd() {
                        MobLogger.e(classTarget, "快手联盟激励视频广告播放完成回调")
                    }

                    /**
                     * 激励视频广告播放跳过回调
                     */
                    override fun onVideoSkipToEnd(speed: Long) {
                        MobLogger.e(classTarget, "快手联盟激励视频广告点击跳过: $speed")
                    }

                    /**
                     * 激励视频广告播放开始回调
                     */
                    override fun onVideoPlayStart() {
                        MobLogger.e(classTarget, "快手联盟激励视频广告开始播放")

                        invokeMediaShowListener()

                        reportMediaActionEvent(
                            "show",
                            mediaRequestParams.tacticsInfo,
                            mediaRequestParams.mediaRequestLog
                        )
                    }

                    /**
                     * 激励视频广告获取加你回调
                     */
                    override fun onRewardVerify() {
                        MobLogger.e(classTarget, "快手联盟激励视频广告奖励下发")

                        rewardVerify = true
                    }

                    /**
                     * 激励视频⼴告分阶段获取激励
                     */
                    override fun onRewardStepVerify(taskType: Int, currentTaskStatus: Int) {
                        MobLogger.e(
                            classTarget,
                            "快手联盟激励视频广告告分阶段获取激励: TaskType=$taskType, TaskStatus=$currentTaskStatus"
                        )
                    }
                })

                mediaResponseTime = SystemClock.elapsedRealtime()

                mediaRequestParams.mediaPlatformLog.handleRequestSucceed()
                mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@KSRewardVideo))
            }
        })
    }
}