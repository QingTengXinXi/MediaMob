package com.media.mob.dispatch.loader

import android.app.Activity
import com.media.mob.bean.PositionConfig
import com.media.mob.bean.log.MediaRequestLog
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.dispatch.MobRequestResult
import com.media.mob.media.view.IMobView
import com.media.mob.platform.IPlatform

class SplashLoader(
    activity: Activity,
    positionConfig: PositionConfig,
    mediaRequestLog: MediaRequestLog,
    mobRequestResult: MobRequestResult<IMobView>
) : MobLoader<IMobView>(activity, "Splash", positionConfig, mediaRequestLog, mobRequestResult) {

    override fun handlePlatformRequest(platform: IPlatform, mediaRequestParams: MediaRequestParams<IMobView>) {
        platform.requestSplash(mediaRequestParams)
    }
}