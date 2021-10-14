package com.media.mob.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PlatformConfig(
    /**
     * 平台的应用id
     */
    @SerializedName("platform_app_id")
    val platformAppId: String,

    /**
     * 平台的名称（缩写）
     */
    @SerializedName("platform_name")
    val platformName: String
) : Serializable {

    override fun toString(): String {
        return "PlatformConfig(platformAppId='$platformAppId', platformName='$platformName')"
    }
}