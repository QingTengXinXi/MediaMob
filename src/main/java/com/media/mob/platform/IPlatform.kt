package com.media.mob.platform

import com.media.mob.bean.InitialParams
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.media.interstitial.IInterstitial
import com.media.mob.media.rewardVideo.IRewardVideo
import com.media.mob.media.view.IMobView

interface IPlatform {
    companion object {
        const val PLATFORM_BQT = "BQT"
        const val PLATFORM_CSJ = "CSJ"
        const val PLATFORM_JZT = "JZT"
        const val PLATFORM_YLH = "YLH"
    }

    /**
     * 平台名称
     */
    val name: String

    /**
     * 初始化方法
     */
    fun initial(initialParams: InitialParams)

    /**
     * 请求开屏广告
     */
    fun requestSplash(mediaRequestParams: MediaRequestParams<IMobView>)

    /**
     * 请求激励视频广告
     */
    fun requestRewardVideo(mediaRequestParams: MediaRequestParams<IRewardVideo>)

    /**
     * 请求插屏广告
     */
    fun requestInterstitial(mediaRequestParams: MediaRequestParams<IInterstitial>)
}