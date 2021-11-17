package com.media.mob.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TacticsInfo(
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

    /**
     * 广告位策略信息的权重
     */
    @SerializedName("tactics_info_weight")
    var tacticsInfoWeight: Int = 10,

    /**
     * 广告位策略信息的序列
     */
    @SerializedName("tactics_info_sequence")
    var tacticsInfoSequence: Int = 1,
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TacticsInfo

        if (thirdAppId != other.thirdAppId) return false
        if (thirdSlotId != other.thirdSlotId) return false
        if (thirdPlatformName != other.thirdPlatformName) return false
        if (tacticsInfoWeight != other.tacticsInfoWeight) return false
        if (tacticsInfoSequence != other.tacticsInfoSequence) return false

        return true
    }

    override fun hashCode(): Int {
        var result = thirdAppId.hashCode()
        result = 31 * result + thirdSlotId.hashCode()
        result = 31 * result + thirdPlatformName.hashCode()
        result = 31 * result + tacticsInfoWeight
        result = 31 * result + tacticsInfoSequence
        return result
    }

    override fun toString(): String {
        return "TacticsInfo(thirdAppId='$thirdAppId', thirdSlotId='$thirdSlotId', thirdPlatformName='$thirdPlatformName', tacticsInfoWeight=$tacticsInfoWeight, tacticsInfoSequence=$tacticsInfoSequence)"
    }
}