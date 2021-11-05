package com.media.mob.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MediaConfig(

    @SerializedName("splash_cache_time")
    var splashCacheTime: Long,

    @SerializedName("reward_video_cache_time")
    var rewardVideoCacheTime: Long,

    @SerializedName("interstitial_cache_time")
    var interstitialCacheTime: Long,



) : Serializable {

}