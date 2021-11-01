package com.media.mob.platform.youLiangHui

import com.media.mob.Constants
import com.media.mob.bean.InitialParams
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.bean.request.MediaRequestResult
import com.media.mob.helper.logger.MobLogger
import com.media.mob.media.interstitial.IInterstitial
import com.media.mob.media.rewardVideo.IRewardVideo
import com.media.mob.media.view.IMobView
import com.media.mob.platform.IPlatform
import com.qq.e.comm.managers.GDTADManager

class YLHPlatform(private val id: String) : IPlatform {

    private val classTarget = YLHPlatform::class.java.simpleName

    override val name: String = IPlatform.PLATFORM_YLH

    private var initialSucceed = false

    override fun initial(initialParams: InitialParams) {
        MobLogger.e(classTarget, "初始化优量汇SDK: $id")

        initialSucceed = GDTADManager.getInstance().initWith(Constants.application, id)
    }

    override fun requestSplash(mediaRequestParams: MediaRequestParams<IMobView>) {
        if (initialSucceed) {
            YLHSplash(mediaRequestParams.activity).requestSplash(mediaRequestParams)
        } else {
            mediaRequestParams.mediaPlatformLog.handleRequestFailed(
                84000,
                "优量汇广告SDK未初始化: SlotId=${mediaRequestParams.tacticsInfo.thirdSlotId}"
            )

            mediaRequestParams.mediaRequestResult.invoke(
                MediaRequestResult(null, 84000, "优量汇广告SDK未初始化: SlotId=${mediaRequestParams.tacticsInfo.thirdSlotId}")
            )
        }
    }

    override fun requestRewardVideo(mediaRequestParams: MediaRequestParams<IRewardVideo>) {
        if (initialSucceed) {
            YLHRewardVideo().requestRewardVideo(mediaRequestParams)
        } else {
            mediaRequestParams.mediaPlatformLog.handleRequestFailed(
                84000,
                "优量汇广告SDK未初始化: SlotId=${mediaRequestParams.tacticsInfo.thirdSlotId}"
            )

            mediaRequestParams.mediaRequestResult.invoke(
                MediaRequestResult(null, 84000, "优量汇广告SDK未初始化: SlotId=${mediaRequestParams.tacticsInfo.thirdSlotId}")
            )
        }
    }

    override fun requestInterstitial(mediaRequestParams: MediaRequestParams<IInterstitial>) {
        if (initialSucceed) {
            YLHInterstitial(mediaRequestParams.activity).requestInterstitial(mediaRequestParams)
        } else {
            mediaRequestParams.mediaPlatformLog.handleRequestFailed(
                84000,
                "优量汇广告SDK未初始化: SlotId=${mediaRequestParams.tacticsInfo.thirdSlotId}"
            )

            mediaRequestParams.mediaRequestResult.invoke(
                MediaRequestResult(null, 84000, "优量汇广告SDK未初始化: SlotId=${mediaRequestParams.tacticsInfo.thirdSlotId}")
            )
        }
    }
}