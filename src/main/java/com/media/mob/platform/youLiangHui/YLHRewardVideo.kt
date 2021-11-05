package com.media.mob.platform.youLiangHui

import android.os.SystemClock
import com.media.mob.Constants
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.bean.request.MediaRequestResult
import com.media.mob.helper.logger.MobLogger
import com.media.mob.helper.thread.runMainThread
import com.media.mob.media.rewardVideo.IRewardVideo
import com.media.mob.media.rewardVideo.RewardVideoWrapper
import com.media.mob.platform.IPlatform
import com.media.mob.platform.youLiangHui.helper.DownloadConfirmHelper
import com.qq.e.ads.rewardvideo.RewardVideoAD
import com.qq.e.ads.rewardvideo.RewardVideoADListener
import com.qq.e.comm.util.AdError
import com.qq.e.comm.util.VideoAdValidity.NONE_CACHE
import com.qq.e.comm.util.VideoAdValidity.VALID

class YLHRewardVideo : RewardVideoWrapper() {

    private val classTarget = YLHRewardVideo::class.java.simpleName

    /**
     * 广告平台名称
     */
    override val platformName: String = IPlatform.PLATFORM_YLH

    /**
     * 广告请求响应时间
     */
    override var mediaResponseTime: Long = -1L

    /**
     * 优量汇激励视频广告对象
     */
    private var rewardVideoAd: RewardVideoAD? = null

    /**
     * 激励视频是否下发奖励
     */
    private var rewardVerify: Boolean = false

    /**
     * 展示激励视频广告
     */
    override fun show() {
        runMainThread {
            rewardVideoAd?.showAD()
        }
    }

    /**
     * 检查广告是否有效
     */
    override fun checkMediaValidity(): Boolean {
        return rewardVideoAd != null && checkRewardVideoValidity(rewardVideoAd) && checkMediaCacheTime()
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
        rewardVideoAd = RewardVideoAD(
            mediaRequestParams.activity,
            mediaRequestParams.tacticsInfo.thirdSlotId,
            object : RewardVideoADListener {

                /**
                 * 激励视频广告请求失败
                 */
                override fun onError(error: AdError?) {
                    MobLogger.e(
                        classTarget,
                        "优量汇激励视频广告请求失败: Code=${error?.errorCode ?: -1}, Message=${error?.errorMsg ?: "Unknown"}"
                    )

                    mediaRequestParams.mediaPlatformLog.handleRequestFailed(
                        error?.errorCode ?: -1,
                        error?.errorMsg ?: "Unknown"
                    )

                    mediaRequestParams.mediaRequestResult.invoke(
                        MediaRequestResult(
                            null, 84002,
                            "优量汇激励视频请求失败: Code=${error?.errorCode ?: -1}, Message=${error?.errorMsg ?: "Unknown"}"
                        )
                    )

                    destroy()
                }

                /**
                 * 激励视频广告加载成功回调
                 */
                override fun onADLoad() {
                    MobLogger.e(classTarget, "优量汇激励视频广告加载成功")

                    if (mediaRequestParams.slotParams.forceShowDownloadDialog) {
                        rewardVideoAd?.setDownloadConfirmListener(DownloadConfirmHelper.downloadConfirmListener)
                    }
                }

                /**
                 * 激励视频广告素材缓存成功回调
                 */
                override fun onVideoCached() {
                    MobLogger.e(classTarget, "优量汇激励视频广告素材缓存成功")

                    mediaResponseTime = SystemClock.elapsedRealtime()

                    mediaRequestParams.mediaPlatformLog.handleRequestSucceed()
                    mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@YLHRewardVideo))
                }

                /**
                 * 激励视频广告页面展示
                 */
                override fun onADShow() {
                    MobLogger.e(classTarget, "优量汇激励视频广告页面展示")
                }

                /**
                 * 激励视频广告曝光
                 */
                override fun onADExpose() {
                    MobLogger.e(classTarget, "优量汇激励视频广告曝光")

                    invokeMediaShowListener()

                    reportMediaActionEvent("show", mediaRequestParams.tacticsInfo, mediaRequestParams.mediaRequestLog)
                }

                /**
                 * 激励视频广告奖励发放
                 */
                override fun onReward(params: MutableMap<String, Any>?) {
                    MobLogger.e(classTarget, "优量汇激励视频广告奖励发放")

                    rewardVerify = true

                    invokeMediaRewardedListener(true)
                }

                /**
                 * 激励视频广告点击
                 */
                override fun onADClick() {
                    MobLogger.e(classTarget, "优量汇激励视频广告点击")

                    invokeMediaClickListener()

                    reportMediaActionEvent("click", mediaRequestParams.tacticsInfo, mediaRequestParams.mediaRequestLog)
                }

                /**
                 * 激励视频广告播放完成
                 */
                override fun onVideoComplete() {
                    MobLogger.e(classTarget, "优量汇激励视频广告播放完成")
                }

                /**
                 * 激励视频广告关闭
                 */
                override fun onADClose() {
                    MobLogger.e(classTarget, "优量汇激励视频广告关闭")

                    invokeMediaCloseListener()
                }
            }, !mediaRequestParams.slotParams.rewardVideoMutePlay
        )


        mediaRequestParams.mediaPlatformLog.insertRequestTime()

        rewardVideoAd?.loadAD()
    }

    /**
     * 检查激励视频广告是否有效
     */
    private fun checkRewardVideoValidity(rewardVideoAD: RewardVideoAD?): Boolean {
        if (rewardVideoAD == null || rewardVideoAD.hasShown()) {
            return false
        }

        return (rewardVideoAD.checkValidity() == VALID || rewardVideoAD.checkValidity() == NONE_CACHE) && (SystemClock.elapsedRealtime() < (rewardVideoAD.expireTimestamp - 500))
    }
}