package com.media.mob.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TacticsConfig(

    /**
     * 广告位流量策略类型
     */
    @SerializedName("tactics_type")
    var tacticsType: TacticsType,

    /**
     * 广告位流量策略序列
     */
    @SerializedName("tactics_sequence")
    var tacticsSequence: Int = 1,
    /**
     * 广告位流量策略列表
     */
    @SerializedName("tactics_info_list")
    var tacticsInfoList: ArrayList<TacticsInfo>,

    /**
     * 广告位流量策略是否开启优先返回填充的广告（注：该策略开关只对并行请求流量策略有效，开启后并行请求的流量策略配置一旦有广告平台请求成功会立即返回广告）
     */
    @SerializedName("tactics_priority_return")
    var tacticsPriorityReturn: Boolean = false,

    /**
     * 广告位流量策略检查请求成功的时间，单位：毫秒（注：该配置目前只对并行请求流量策略生效，开启后会在固定的时间去检测并行请求的流量策略配置的请求结果，一旦有策略配置请求成功会立即返回广告）
     */
    @SerializedName("tactics_check_request_time")
    var tacticsCheckRequestTime: Long = 3000,

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