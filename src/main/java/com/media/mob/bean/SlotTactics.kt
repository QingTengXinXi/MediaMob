package com.media.mob.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SlotTactics(
    /**
     * 第三方广告平台的应用id
     */
    @SerializedName("tactics_weight")
    val tacticsWeight: Int,

    /**
     * 第三方广告平台的应用id
     */
    @SerializedName("third_app_id")
    val thirdAppId: String,

    /**
     * 第三方广告平台的广告位id
     */
    @SerializedName("third_slot_id")
    val thirdSlotId: String,

    /**
     * 第三方广告平台的名称
     */
    @SerializedName("third_platform_name")
    var thirdPlatformName: String,
):Serializable {

}