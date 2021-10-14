package com.media.mob.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TacticsConfig(

    /**
     * 广告位流量策略类型
     */
    @SerializedName("tactics_type")
    val tacticsType: TacticsType,

    /**
     * 广告位流量策略列表
     */
    @SerializedName("tactics_info_list")
    val tacticsInfoList: ArrayList<TacticsInfo>,
) : Serializable {

    /**
     * 检查参数是否合法
     */
    fun checkParamsValidity(): Boolean {
        return tacticsType.typeName.isNotEmpty() && tacticsInfoList.isNotEmpty()
    }

    override fun toString(): String {
        return "TacticsConfig(tacticsType='$tacticsType', tacticsInfoList=$tacticsInfoList)"
    }
}