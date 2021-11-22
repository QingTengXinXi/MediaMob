package com.media.mob.dispatch.loader

import android.app.Activity
import com.media.mob.bean.PositionConfig
import com.media.mob.bean.log.MediaRequestLog
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.dispatch.MobRequestResult
import com.media.mob.dispatch.loader.helper.MobMediaCacheHelper
import com.media.mob.media.view.splash.ISplash
import com.media.mob.platform.IPlatform

class SplashLoader(
    activity: Activity,
    positionConfig: PositionConfig,
    mediaRequestLog: MediaRequestLog,
    splashRequestResult: MobRequestResult<ISplash>
) : MobLoader<ISplash>(activity, "Splash", positionConfig, mediaRequestLog, splashRequestResult) {

    override fun handlePlatformRequest(platform: IPlatform, mediaRequestParams: MediaRequestParams<ISplash>) {
        platform.requestSplash(mediaRequestParams)
    }

    override fun  handleMobMediaCache() {
        if (mobMediaResponseCache.isNotEmpty()) {
            mobMediaResponseCache.forEach { entry ->
                if (entry.value.isNotEmpty()) {
                    entry.value.forEach {
                        if (it.tacticsInfo != null) {
                            it.tacticsInfo?.let { tacticsInfo ->
                                MobMediaCacheHelper.insertSplashMobMediaCache(tacticsInfo, it)
                            }
                        }
                    }
                }
            }
        }
    }
}