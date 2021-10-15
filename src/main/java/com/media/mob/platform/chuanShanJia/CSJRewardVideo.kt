package com.media.mob.platform.chuanShanJia

import android.app.Activity
import android.content.res.Configuration
import com.bytedance.sdk.openadsdk.AdSlot.Builder
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdLoadType
import com.bytedance.sdk.openadsdk.TTAdNative.RewardVideoAdListener
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTRewardVideoAd
import com.bytedance.sdk.openadsdk.TTRewardVideoAd.RewardAdInteractionListener
import com.media.mob.bean.request.MediaLoadType
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.bean.request.MediaRequestResult
import com.media.mob.helper.logger.MobLogger
import com.media.mob.helper.thread.runMainThread
import com.media.mob.media.rewardVideo.IRewardVideo
import com.media.mob.media.rewardVideo.RewardVideoWrapper
import com.media.mob.platform.IPlatform

class CSJRewardVideo(val activity: Activity) : RewardVideoWrapper() {

    private val classTarget = CSJRewardVideo::class.java.simpleName

    /**
     * 广告平台名称
     */
    override val platformName: String = IPlatform.PLATFORM_YLH

    /**
     * 穿山甲激励视频广告对象
     */
    private var rewardVideoAd: TTRewardVideoAd? = null

    /**
     * 激励视频是否下发奖励
     */
    private var rewardVerify: Boolean = false

    /**
     * 展示激励视频广告
     */
    override fun show() {
        runMainThread {
            rewardVideoAd?.showRewardVideoAd(activity)
        }
    }

    /**
     * 检查激励视频广告是否有效
     */
    override fun checkValidity(): Boolean {
        return checkRewardVideoValidity(rewardVideoAd)
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
        rewardVideoAd = null
    }

    fun requestRewardVideo(mediaRequestParams: MediaRequestParams<IRewardVideo>) {
        val adNative = TTAdSdk.getAdManager().createAdNative(mediaRequestParams.activity)

        val mediaOrientation = when (mediaRequestParams.activity.resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                TTAdConstant.VERTICAL
            }
            else -> {
                TTAdConstant.HORIZONTAL
            }
        }

        val mediaLoadType = when (mediaRequestParams.slotParams.mediaLoadType) {
            MediaLoadType.LOAD -> {
                TTAdLoadType.LOAD
            }
            MediaLoadType.PRELOAD -> {
                TTAdLoadType.PRELOAD
            }
            else -> {
                TTAdLoadType.UNKNOWN
            }
        }
        val builder = Builder().setCodeId(mediaRequestParams.tacticsInfo.thirdSlotId)
            .setExpressViewAcceptedSize(
                mediaRequestParams.slotParams.viewAcceptedWidth,
                mediaRequestParams.slotParams.viewAcceptedHeight
            )
            .setOrientation(mediaOrientation)
            .setAdLoadType(mediaLoadType)

        if (mediaRequestParams.slotParams.forceShowDownloadDialog) {
            builder.setDownloadType(TTAdConstant.DOWNLOAD_TYPE_POPUP)
        } else {
            builder.setDownloadType(TTAdConstant.DOWNLOAD_TYPE_NO_POPUP)
        }

        mediaRequestParams.mediaPlatformLog.insertRequestTime()

        adNative.loadRewardVideoAd(builder.build(), object : RewardVideoAdListener {

            /**
             * 激励视频广告请求失败回调
             */
            override fun onError(code: Int, message: String?) {
                MobLogger.e(classTarget, "穿山甲激励视频广告请求失败: Code=$code, Message=${message ?: "Unknown"}")

                mediaRequestParams.mediaPlatformLog.handleRequestFailed(code, message ?: "Unknown")

                mediaRequestParams.mediaRequestResult.invoke(
                    MediaRequestResult(null, 60006, "穿山甲激励视频广告请求失败: Code=$code, Message=${message ?: "Unknown"}")
                )

                destroy()
            }

            /**
             * 激励视频广告加载完成的回调
             */
            override fun onRewardVideoAdLoad(ttRewardedVideoAd: TTRewardVideoAd?) {
                if (ttRewardedVideoAd == null) {
                    MobLogger.e(classTarget, "穿山甲激励视频广告请求结果异常，返回的广告对象为Null")

                    mediaRequestParams.mediaPlatformLog.handleRequestFailed(-1, "穿山甲激励视频广告请求结果异常，返回的广告对象为Null")

                    mediaRequestParams.mediaRequestResult.invoke(
                        MediaRequestResult(null, 60005, "穿山甲激励视频广告请求结果为空")
                    )

                    destroy()

                    return
                }

                rewardVideoAd = ttRewardedVideoAd

                MobLogger.e(classTarget, "穿山甲激励视频广告展示截止时间: ${rewardVideoAd?.expirationTimestamp} : 当前时间: ${System.currentTimeMillis()}")

                rewardVideoAd?.setShowDownLoadBar(true)

                rewardVideoAd?.setRewardAdInteractionListener(object : RewardAdInteractionListener {

                    /**
                     * 激励视频广告的展示回调
                     */
                    override fun onAdShow() {
                        MobLogger.e(classTarget, "穿山甲激励视频广告展示")

                        invokeMediaShowListener()

                        reportMediaActionEvent(
                            "show",
                            mediaRequestParams.tacticsInfo,
                            mediaRequestParams.mediaRequestLog
                        )
                    }

                    /**
                     * 激励视频广告下载bar点击回调（注：激励视频播放完成以后展示的落地页点击不会回调该接口）
                     */
                    override fun onAdVideoBarClick() {
                        MobLogger.e(classTarget, "穿山甲激励视频广告点击")

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
                    override fun onAdClose() {
                        MobLogger.e(classTarget, "穿山甲激励视频广告关闭")

                        invokeMediaCloseListener()
                    }

                    /**
                     * 激励视频广告视频播放完成回调
                     */
                    override fun onVideoComplete() {
                        MobLogger.e(classTarget, "穿山甲激励视频广告视频播放完成")
                    }

                    /**
                     * 激励视频广告播放错误
                     */
                    override fun onVideoError() {
                        MobLogger.e(classTarget, "穿山甲激励视频广告播放错误")
                    }

                    /**
                     * 激励视频奖励验证回调
                     */
                    override fun onRewardVerify(
                        rewardVerify: Boolean,
                        rewardAmount: Int,
                        rewardName: String?,
                        code: Int,
                        message: String?
                    ) {
                        MobLogger.e(
                            classTarget,
                            "穿山甲激励视频广告奖励下发 Verify:$rewardVerify, RewardAmount:$rewardAmount, RewardName:$rewardName, ErrorCode:$code, ErrorMessage:$message"
                        )

                        this@CSJRewardVideo.rewardVerify = rewardVerify

                        invokeRewardedListener(rewardVerify)
                    }

                    /**
                     * 激励视频跳过视频播放回调
                     */
                    override fun onSkippedVideo() {
                        MobLogger.e(classTarget, "穿山甲激励视频跳过视频播放回调")
                    }
                })
            }

            /**
             * 激励视频广告视频本地加载完成的回调
             */
            override fun onRewardVideoCached() {
            }

            /**
             * 激励视频广告视频本地加载完成的回调
             */
            override fun onRewardVideoCached(rewardVideoAD: TTRewardVideoAd?) {
                MobLogger.e(classTarget, "穿山甲激励视频广告缓存成功")

                mediaRequestParams.mediaPlatformLog.handleRequestSucceed()
                mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(this@CSJRewardVideo))
            }
        })
    }

    /**
     * 检查激励视频广告是否有效
     */
    private fun checkRewardVideoValidity(rewardVideoAD: TTRewardVideoAd?): Boolean {
        return rewardVideoAD != null && System.currentTimeMillis() < rewardVideoAD.expirationTimestamp
    }
}