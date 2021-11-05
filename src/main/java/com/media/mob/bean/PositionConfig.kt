package com.media.mob.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PositionConfig(

    /**
     * 物理广告位id
     */
    @SerializedName("position_id")
    var positionId: String,

    /**
     * 物理广告位名称
     */
    @SerializedName("position_name")
    var positionName: String,

    /**
     * 广告位是否开启预加载
     */
    @SerializedName("position_preload_state")
    val positionPreloadState: Boolean = false,

    /**
     * 广告位配置信息
     */
    @SerializedName("slot_config")
    var slotConfig: SlotConfig
) : Serializable {

    override fun toString(): String {
        return "PositionConfig(positionId='$positionId', positionName='$positionName', positionPreloadState=$positionPreloadState, slotConfig=$slotConfig)"
    }
}