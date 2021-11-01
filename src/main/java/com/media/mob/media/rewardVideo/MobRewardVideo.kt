package com.media.mob.media.rewardVideo

import android.app.Activity
import com.media.mob.bean.PositionConfig
import com.media.mob.bean.log.MediaRequestLog
import com.media.mob.bean.request.SlotParams
import com.media.mob.dispatch.MobRequestResult
import com.media.mob.dispatch.loader.RewardVideoLoader

class MobRewardVideo(val activity: Activity, val positionConfig: PositionConfig) : IRewardVideo {

    /**
     * 激励视频广告
     */
    private var rewardVideo: IRewardVideo? = null

    /**
     * 广告对象的平台名称
     */
    override val platformName: String
        get() {
            return rewardVideo?.platformName ?: ""
        }

    /**
     * 广告请求响应时间
     */
    override val mediaResponseTime: Long
        get() {
            return rewardVideo?.mediaResponseTime ?: -1L
        }

    /**
     * 展示上报状态
     */
    override val showReportState: Boolean
        get() {
            return rewardVideo?.showReportState ?: false
        }

    /**
     * 点击上报状态
     */
    override val clickReportState: Boolean
        get() {
            return rewardVideo?.clickReportState ?: false
        }

    /**
     * 广告展示监听
     */
    override var mediaShowListener: (() -> Unit)? = null

    /**
     * 广告点击监听
     */
    override var mediaClickListener: (() -> Unit)? = null

    /**
     * 广告关闭监听
     */
    override var mediaCloseListener: (() -> Unit)? = null

    /**
     * 广告奖励发放监听
     */
    override var mediaRewardedListener: ((Boolean) -> Unit)? = null

    /**
     * 请求成功的监听
     */
    var requestSuccessListener: (() -> Unit)? = null

    /**
     * 请求失败的监听
     */
    var requestFailedListener: ((code: Int, message: String) -> Unit)? = null

    /**
     * 激励视频广告展示
     */
    override fun show() {
        rewardVideo?.show()
    }

    /**
     * 检查广告是否有效
     */
    override fun checkMediaValidity(): Boolean {
        return rewardVideo != null && rewardVideo?.checkMediaValidity() == true
    }

    /**
     * 检查激励视频广告奖励是否发放
     */
    override fun checkRewardVerify(): Boolean {
        return rewardVideo?.checkRewardVerify() ?: false
    }

    /**
     * 销毁广告
     */
    override fun destroy() {
        mediaRewardedListener = null

        mediaShowListener = null
        mediaClickListener = null
        mediaCloseListener = null

        rewardVideo?.destroy()
        rewardVideo = null
    }

    /**
     * 请求激励视频广告
     */
    fun requestRewardVideo(slotParams: SlotParams) {
        val rewardVideoLoader = RewardVideoLoader(activity, positionConfig, MediaRequestLog(positionConfig), object :
            MobRequestResult<IRewardVideo> {

            override fun requestFailed(code: Int, message: String) {
                invokeRequestFailedListener(code, message)
            }

            override fun requestSucceed(result: IRewardVideo) {
                rewardVideo = result

                rewardVideo?.mediaShowListener = {
                    invokeMediaShowListener()
                }

                rewardVideo?.mediaClickListener = {
                    invokeMediaClickListener()
                }

                rewardVideo?.mediaCloseListener = {
                    invokeMediaCloseListener()
                }

                rewardVideo?.mediaRewardedListener = { verified ->
                    invokeRewardedListener(verified)
                }

                invokeRequestSuccessListener()
            }
        })

        rewardVideoLoader.handleRequest(slotParams)
    }

    /**
     * 执行激励视频广告请求成功回调
     */
    private fun invokeRequestSuccessListener() {
        requestSuccessListener?.invoke()
    }

    /**
     * 执行激励视频广告请求失败回调
     */
    private fun invokeRequestFailedListener(code: Int, message: String) {
        requestFailedListener?.invoke(code, message)
    }

    /**
     * 执行激励视频广告展示监听
     */
    private fun invokeMediaShowListener() {
        mediaShowListener?.invoke()
    }

    /**
     * 执行激励视频广告点击监听
     */
    private fun invokeMediaClickListener() {
        mediaClickListener?.invoke()
    }

    /**
     * 执行激励视频广告关闭监听
     */
    private fun invokeMediaCloseListener() {
        mediaCloseListener?.invoke()

        rewardVideo?.destroy()
        rewardVideo = null
    }

    /**
     * 执行激励视频广告奖励发放监听
     */
    private fun invokeRewardedListener(reward: Boolean) {
        mediaRewardedListener?.invoke(reward)
    }
}