package com.media.mob.dispatch.loader

import android.app.Activity
import com.media.mob.bean.PositionConfig
import com.media.mob.bean.log.MediaRequestLog
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.dispatch.MobRequestResult
import com.media.mob.media.interstitial.IInterstitial
import com.media.mob.platform.IPlatform

class InterstitialLoader(
    activity: Activity,
    positionConfig: PositionConfig,
    mediaRequestLog: MediaRequestLog,
    mobRequestResult: MobRequestResult<IInterstitial>
) : MobLoader<IInterstitial>(activity, "Interstitial", positionConfig, mediaRequestLog, mobRequestResult) {

    override fun handlePlatformRequest(platform: IPlatform, mediaRequestParams: MediaRequestParams<IInterstitial>) {
        platform.requestInterstitial(mediaRequestParams)
    }
}