package com.media.mob.bean

import com.google.gson.annotations.SerializedName

class MobConfig(

    /**
     * 聚合平台配置信息
     */
    @SerializedName("platform_config")
    val platformConfig: ArrayList<PlatformConfig>,

    /**
     * 聚合广告位配置信息
     */
    @SerializedName("position_config")
    val positionConfig: ArrayList<PositionConfig>,

    /**
     * 全局广告配置
     */
    @SerializedName("media_config")
    val mediaConfig: MediaConfig
) {

    fun checkParamsValidity(): Boolean {
        return platformConfig.isNotEmpty()
    }

    override fun toString(): String {
        return "MobConfig(platformConfig=$platformConfig, positionConfig=$positionConfig, mediaConfig=$mediaConfig)"
    }
}