package com.media.mob.media.rewardVideo

import com.media.mob.media.IMob

interface IRewardVideo : IMob {

    /**
     * 激励视频奖励下发监听
     */
    var mediaRewardedListener: ((Boolean) -> Unit)?

    /**
     * 展示激励视频广告
     */
    fun show()

    /**
     * 检查激励视频广告奖励发放状态
     */
    fun checkRewardVerify(): Boolean
}