package com.media.mob.platform.baiQingTeng

import com.baidu.mobads.sdk.api.RewardVideoAd
import com.baidu.mobads.sdk.api.RewardVideoAd.RewardVideoAdListener
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.bean.request.MediaRequestResult
import com.media.mob.helper.logger.MobLogger
import com.media.mob.helper.thread.runMainThread
import com.media.mob.media.rewardVideo.IRewardVideo
import com.media.mob.media.rewardVideo.RewardVideoWrapper
import com.media.mob.platform.IPlatform

class BQTRewardVideo : RewardVideoWrapper() {

    private val classTarget = BQTRewardVideo::class.java.simpleName

    /**
     * 广告平台名称
     */
    override val platformName: String = IPlatform.PLATFORM_YLH

    /**
     * 广告请求响应时间
     */
    override val mediaResponseTime: Long = -1L

    /**
     * 百青藤激励视频广告对象
     */
    private var rewardVideoAd: RewardVideoAd? = null

    /**
     * 激励视频是否下发奖励
     */
    private var rewardVerify: Boolean = false

    /**
     * 展示激励视频广告
     */
    override fun show() {
        runMainThread {
            rewardVideoAd?.show()
        }
    }

    /**
     * 检查广告是否有效
     */
    override fun checkMediaValidity(): Boolean {
        return rewardVideoAd != null && checkRewardVideoValidity(rewardVideoAd)
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
        rewardVideoAd = RewardVideoAd(
            mediaRequestParams.activity,
            mediaRequestParams.tacticsInfo.thirdSlotId,
            object : RewardVideoAdListener {

                /**
                 * 激励视频广告请求失败
                 */
                override fun onAdFailed(message: String?) {
                    MobLogger.e(classTarget, "百青藤激励视频广告请求失败: ${message ?: "Unknown"}")

                    mediaRequestParams.mediaPlatformLog.handleRequestFailed(-1, message ?: "Unknown")

                    mediaRequestParams.mediaRequestResult.invoke(
                        MediaRequestResult(null, 82002, "百青藤激励视频广告请求失败: Message=${message ?: "Unknown"}")
                    )

                    destroy()
                }

                /**
                 * 激励视频广告加载成功
                 */
                override fun onAdLoaded() {
                    MobLogger.e(classTarget, "百青藤激励视频广告请求成功，等待下载")
                }

                /**
                 * 激励视频广告视频物料缓存成功
                 */
                override fun onVideoDownloadSuccess() {
                    MobLogger.e(classTarget, "百青藤激励视频广告缓存成功")

                    mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@BQTRewardVideo))
                }

                /**
                 * 激励视频广告视频物料缓存失败
                 */
                override fun onVideoDownloadFailed() {
                    MobLogger.e(classTarget, "百青藤激励视频广告物料缓存失败")

                    mediaRequestParams.mediaPlatformLog.handleRequestFailed(-1, "百青藤激励视频广告物料缓存失败")

                    mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(null, 82003, "百青藤激励视频广告物料缓存失败"))

                    destroy()
                }

                /**
                 * 激励视频广告展示回调
                 */
                override fun onAdShow() {
                    MobLogger.e(classTarget, "百青藤激励视频广告展示")

                    invokeMediaShowListener()

                    reportMediaActionEvent("show", mediaRequestParams.tacticsInfo, mediaRequestParams.mediaRequestLog)
                }

                /**
                 * 激励视频广告点击回调
                 */
                override fun onAdClick() {
                    MobLogger.e(classTarget, "百青藤激励视频广告点击")

                    invokeMediaClickListener()

                    reportMediaActionEvent("click", mediaRequestParams.tacticsInfo, mediaRequestParams.mediaRequestLog)
                }

                /**
                 * 激励视频广告关闭回调
                 */
                override fun onAdClose(playScale: Float) {
                    MobLogger.e(classTarget, "百青藤激励视频广告关闭，PlayScale=$playScale")

                    invokeMediaCloseListener()
                }

                /**
                 * 激励视频广告视频播放完成回调
                 */
                override fun playCompletion() {
                    MobLogger.e(classTarget, "百青藤激励视频广告视频播放完成")
                }

                /**
                 * 激励视频广告视频跳过
                 */
                override fun onAdSkip(playScale: Float) {
                    MobLogger.e(classTarget, "百青藤激励视频广告跳过，PlayScale=$playScale")
                }

                /**
                 * 激励视频广告奖励回调
                 */
                override fun onRewardVerify(rewardVerify: Boolean) {
                    MobLogger.e(classTarget, "百青藤激励视频广告奖励回调")

                    this@BQTRewardVideo.rewardVerify = rewardVerify

                    invokeMediaRewardedListener(rewardVerify)
                }
            },
            !mediaRequestParams.slotParams.useTextureView
        )

        if (mediaRequestParams.tacticsInfo.thirdAppId.isNotEmpty()) {
            rewardVideoAd?.setAppSid(mediaRequestParams.tacticsInfo.thirdAppId)
        }

        /**
         * 设置点击跳过时是否展示提示弹框
         */
        rewardVideoAd?.setShowDialogOnSkip(true)

        /**
         * 设置是否展示奖励领取倒计时提示
         */
        rewardVideoAd?.setUseRewardCountdown(true)

        mediaRequestParams.mediaPlatformLog.insertRequestTime()

        rewardVideoAd?.load()
    }

    /**
     * 检查激励视频广告是否有效
     */
    private fun checkRewardVideoValidity(rewardVideoAD: RewardVideoAd?): Boolean {
        return rewardVideoAD != null && rewardVideoAD.isReady
    }
}