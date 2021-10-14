package com.media.mob.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SlotConfig(
    /**
     * 广告位类型
     */
    @SerializedName("slot_type")
    val slotType: String,

    /**
     * 广告位流量策略配置
     */
    @SerializedName("slot_tactics_config")
    val slotTacticsConfig: ArrayList<TacticsConfig>,
) : Serializable {

    override fun toString(): String {
        return "SlotConfig(slotType='$slotType', slotTacticsConfig=$slotTacticsConfig)"
    }
}