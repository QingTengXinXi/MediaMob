package com.media.mob.platform.youLiangHui

import android.os.SystemClock
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
     * 优量汇激励视频广告对象
     */
    private var rewardVideoAD: RewardVideoAD? = null

    /**
     * 是否回调成功
     */
    private var callbackSuccess = false

    /**
     * 激励视频是否下发奖励
     */
    private var rewardVerify: Boolean = false

    /**
     * 展示激励视频广告
     */
    override fun show() {
        runMainThread {
            rewardVideoAD?.showAD()
        }
    }

    /**
     * 检查激励视频广告是否有效
     */
    override fun checkValidity(): Boolean {
        return checkRewardVideoValidity(rewardVideoAD)
    }

    /**
     * 检查激励视频广告奖励发放状态
     */
    override fun checkRewardVerify(): Boolean {
        return rewardVerify
    }

    /**
     * 广告销毁
     */
    override fun destroy() {
        rewardVideoAD = null
    }

    fun requestRewardVideo(mediaRequestParams: MediaRequestParams<IRewardVideo>) {
        rewardVideoAD = RewardVideoAD(
            mediaRequestParams.activity,
            mediaRequestParams.tacticsInfo.thirdSlotId,
            object : RewardVideoADListener {

                /**
                 * 激励视频广告加载成功回调
                 */
                override fun onADLoad() {
                    MobLogger.e(classTarget, "优量汇激励视频广告加载成功")

                    if (mediaRequestParams.slotParams.forceShowDownloadDialog) {
                        rewardVideoAD?.setDownloadConfirmListener(DownloadConfirmHelper.downloadConfirmListener)
                    }
                }

                /**
                 * 激励视频广告素材缓存成功回调
                 */
                override fun onVideoCached() {
                    MobLogger.e(classTarget, "优量汇激励视频广告素材缓存成功")

                    if (!callbackSuccess) {
                        callbackSuccess = true

                        mediaRequestParams.mediaPlatformLog.handleRequestSucceed()

                        mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@YLHRewardVideo))
                    }
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

                    invokeRewardedListener(true)
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

                    if (!rewardVerify) {
                        rewardVerify = true
                    }
                }

                /**
                 * 激励视频广告关闭
                 */
                override fun onADClose() {
                    MobLogger.e(classTarget, "优量汇激励视频广告关闭")

                    invokeMediaCloseListener()
                }

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
                            null, 60006,
                            "优量汇激励视频请求失败: Code=${error?.errorCode ?: -1}, Message=${error?.errorMsg ?: "Unknown"}"
                        )
                    )

                    destroy()
                }
            }, !mediaRequestParams.slotParams.rewardVideoMutePlay
        )


        mediaRequestParams.mediaPlatformLog.insertRequestTime()

        rewardVideoAD?.loadAD()
    }

    /**
     * 检查激励视频广告是否有效
     */
    private fun checkRewardVideoValidity(rewardVideoAD: RewardVideoAD?): Boolean {
        if (rewardVideoAD == null || rewardVideoAD.hasShown()) {
            return false
        }

        return (rewardVideoAD.checkValidity() == VALID || rewardVideoAD.checkValidity() == NONE_CACHE) && (SystemClock.elapsedRealtime() < (rewardVideoAD.expireTimestamp - 1000))
    }
}