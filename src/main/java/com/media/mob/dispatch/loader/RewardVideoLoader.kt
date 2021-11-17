package com.media.mob.dispatch.loader

import android.app.Activity
import com.media.mob.bean.PositionConfig
import com.media.mob.bean.log.MediaRequestLog
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.dispatch.MobRequestResult
import com.media.mob.dispatch.loader.helper.MobMediaCacheHelper
import com.media.mob.media.rewardVideo.IRewardVideo
import com.media.mob.platform.IPlatform

class RewardVideoLoader(
    activity: Activity,
    positionConfig: PositionConfig,
    mediaRequestLog: MediaRequestLog,
    mobRequestResult: MobRequestResult<IRewardVideo>,
) : MobLoader<IRewardVideo>(activity, "RewardVideo", positionConfig, mediaRequestLog, mobRequestResult) {

    override fun handlePlatformRequest(platform: IPlatform, mediaRequestParams: MediaRequestParams<IRewardVideo>) {
        platform.requestRewardVideo(mediaRequestParams)
    }

    override fun handleMobMediaCache() {
        if (mobMediaResponseCache.isNotEmpty()) {
            mobMediaResponseCache.forEach { entry ->
                if (entry.value.isNotEmpty()) {
                    entry.value.forEach {
                        if (it.tacticsInfo != null) {
                            it.tacticsInfo?.let { tacticsInfo ->
                                MobMediaCacheHelper.insertRewardVideoMobMediaCache(tacticsInfo, it)
                            }
                        }
                    }
                }
            }
        }
    }
}