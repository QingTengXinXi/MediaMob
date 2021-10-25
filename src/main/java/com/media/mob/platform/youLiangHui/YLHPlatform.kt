package com.media.mob.platform.youLiangHui

import com.media.mob.Constants
import com.media.mob.bean.InitialParams
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.helper.logger.MobLogger
import com.media.mob.media.interstitial.IInterstitial
import com.media.mob.media.rewardVideo.IRewardVideo
import com.media.mob.media.view.IMobView
import com.media.mob.platform.IPlatform
import com.qq.e.comm.managers.GDTAdSdk

class YLHPlatform(private val id: String) : IPlatform {

    private val classTarget = YLHPlatform::class.java.simpleName

    override val name: String = IPlatform.PLATFORM_YLH

    override fun initial(initialParams: InitialParams) {
        MobLogger.e(classTarget, "初始化优量汇SDK: $id")

        GDTAdSdk.init(Constants.application, id)
    }

    override fun requestSplash(mediaRequestParams: MediaRequestParams<IMobView>) {
        YLHSplash(mediaRequestParams.activity).requestSplash(mediaRequestParams)
    }

    override fun requestRewardVideo(mediaRequestParams: MediaRequestParams<IRewardVideo>) {
        YLHRewardVideo().requestRewardVideo(mediaRequestParams)
    }

    override fun requestInterstitial(mediaRequestParams: MediaRequestParams<IInterstitial>) {
        YLHInterstitial(mediaRequestParams.activity).requestInterstitial(mediaRequestParams)
    }
}