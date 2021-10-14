package com.media.mob.bean

import com.google.gson.annotations.SerializedName
import com.media.mob.Constants
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

    /**
     * 检查参数是否合法
     */
    fun checkParamsValidity(): Boolean {
        return slotType.isNotEmpty() && Constants.supportType.contains(slotType) && slotTacticsConfig.isNotEmpty()
    }

    override fun toString(): String {
        return "SlotConfig(slotType='$slotType', slotTacticsConfig=$slotTacticsConfig)"
    }
}