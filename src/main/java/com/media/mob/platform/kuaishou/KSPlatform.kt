package com.media.mob.platform.kuaishou

import com.kwad.sdk.api.KsAdSDK
import com.kwad.sdk.api.SdkConfig
import com.media.mob.Constants
import com.media.mob.bean.InitialParams
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.bean.request.MediaRequestResult
import com.media.mob.helper.logger.MobLogger
import com.media.mob.media.interstitial.IInterstitial
import com.media.mob.media.rewardVideo.IRewardVideo
import com.media.mob.media.view.IMobView
import com.media.mob.platform.IPlatform

class KSPlatform(private val id: String) : IPlatform {

    private val classTarget = KSPlatform::class.java.simpleName

    override val name: String = IPlatform.PLATFORM_KS

    private var initialSucceed = false

    override fun initial(initialParams: InitialParams) {
        val config = SdkConfig.Builder()
            .appId(id)
            .debug(initialParams.debug)
            .showNotification(initialParams.allowShowNotify)
            .build()

        MobLogger.e(classTarget, "初始化快手联盟广告SDK: $id : ${Thread.currentThread().name}")

        initialSucceed = KsAdSDK.init(Constants.application, config)
    }

    override fun requestSplash(mediaRequestParams: MediaRequestParams<IMobView>) {
        if (initialSucceed) {
            KSSplash(mediaRequestParams.activity).requestSplash(mediaRequestParams)
        } else {
            mediaRequestParams.mediaPlatformLog.handleRequestFailed(86000, "快手联盟广告SDK未初始化: SlotId=${mediaRequestParams.tacticsInfo.thirdSlotId}")

            mediaRequestParams.mediaRequestResult.invoke(
                MediaRequestResult(null, 86000, "快手联盟广告SDK未初始化: SlotId=${mediaRequestParams.tacticsInfo.thirdSlotId}")
            )
        }
    }

    override fun requestRewardVideo(mediaRequestParams: MediaRequestParams<IRewardVideo>) {
        mediaRequestParams.mediaPlatformLog.handleRequestFailed(86001, "快手联盟暂时不支持激励视频广告")

        mediaRequestParams.mediaRequestResult.invoke(
            MediaRequestResult(null, 86001, "快手联盟暂时不支持激励视频广告")
        )
    }

    override fun requestInterstitial(mediaRequestParams: MediaRequestParams<IInterstitial>) {
        mediaRequestParams.mediaPlatformLog.handleRequestFailed(86001, "快手联盟暂时不支持插屏广告")

        mediaRequestParams.mediaRequestResult.invoke(
            MediaRequestResult(null, 86001, "快手联盟暂时不支持插屏广告")
        )
    }
}