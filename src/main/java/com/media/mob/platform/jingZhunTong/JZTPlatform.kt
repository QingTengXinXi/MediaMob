package com.media.mob.platform.jingZhunTong

import com.media.mob.bean.InitialParams
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.media.rewardVideo.IRewardVideo
import com.media.mob.media.view.IMobView
import com.media.mob.platform.IPlatform
import com.jd.ad.sdk.JadYunSdk


import com.jd.ad.sdk.JadYunSdkConfig
import com.jd.ad.sdk.widget.JadCustomController
import com.media.mob.Constants
import com.media.mob.bean.request.MediaRequestResult
import com.media.mob.media.interstitial.IInterstitial

class JZTPlatform(private val id: String) : IPlatform {

    override val name: String = IPlatform.PLATFORM_JZT

    override fun initial(initialParams: InitialParams) {
        val config = JadYunSdkConfig.Builder()
            .setAppId(id)
            .setEnableLog(initialParams.debug)
            .setCustomController(object : JadCustomController() {
                override fun getOaid(): String {
                    return initialParams.oaid ?: ""
                }
            })
            .build()

        JadYunSdk.init(Constants.application, config)
    }

    override fun requestSplash(mediaRequestParams: MediaRequestParams<IMobView>) {
        JZTSplash(mediaRequestParams.activity).requestSplash(mediaRequestParams)
    }

    override fun requestRewardVideo(mediaRequestParams: MediaRequestParams<IRewardVideo>) {
        mediaRequestParams.mediaRequestResult.invoke(MediaRequestResult(null, 6005, "京准通暂时不支持激励视频广告"))
    }

    override fun requestInterstitial(mediaRequestParams: MediaRequestParams<IInterstitial>) {

    }
}