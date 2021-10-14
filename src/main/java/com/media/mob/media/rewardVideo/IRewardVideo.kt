package com.media.mob.media.rewardVideo

import com.media.mob.media.IMob

interface IRewardVideo : IMob {

    /**
     * 激励视频奖励下发监听
     */
    var rewardedListener: ((Boolean) -> Unit)?

    /**
     * 展示激励视频广告
     */
    fun show()

    /**
     * 检查激励视频广告是否有效
     */
    fun checkValidity(): Boolean

    /**
     * 检查激励视频广告奖励发放状态
     */
    fun checkRewardVerify(): Boolean
}