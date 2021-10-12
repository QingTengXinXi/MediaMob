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
     * 广告位流量策略
     */
    @SerializedName("slot_tactics_list")
    val slotTacticsList: ArrayList<ArrayList<SlotTactics>>,
) : Serializable {

    override fun toString(): String {
        return "SlotConfig(slotType='$slotType', slotTacticsList=$slotTacticsList)"
    }
}