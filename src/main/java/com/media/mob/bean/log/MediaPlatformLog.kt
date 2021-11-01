package com.media.mob.bean.log

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MediaPlatformLog(
    /**
     * 第三方广告平台id
     */
    @SerializedName("third_app_id")
    var thirdAppId: String,

    /**
     * 第三方广告位id
     */
    @SerializedName("third_slot_id")
    var thirdSlotId: String,

    /**
     * 第三方广告位类型
     */
    @SerializedName("third_slot_type")
    var thirdSlotType: String,

    /**
     * 第三方广告平台名称
     */
    @SerializedName("third_platform_name")
    var thirdPlatformName: String

) : Serializable {

    /**
     * 第三方广告位请求结果
     */
    @SerializedName("third_request_result")
    var thirdRequestResult: Boolean = false

    /**
     * 第三方广告位请求时间
     */
    @SerializedName("third_request_time")
    var thirdRequestTime: Long = -1L

    /**
     * 第三方广告位响应时间
     */
    @SerializedName("third_response_time")
    var thirdResponseTime: Long = -1L

    /**
     * 第三方广告位请求失败Code
     */
    @SerializedName("third_fail_code")
    var thirdFailCode: Int = -1

    /**
     * 第三方广告位请求失败Message
     */
    @SerializedName("third_fail_message")
    var thirdFailMessage: String = "Unknown"

    /**
     * 插入广告位请求时间
     */
    fun insertRequestTime() {
        thirdRequestTime = System.currentTimeMillis()
    }

    /**
     * 处理广告位请求失败信息
     */
    fun handleRequestFailed(code: Int?, message: String?) {
        thirdResponseTime = System.currentTimeMillis()

        thirdRequestResult = false

        thirdFailCode = code ?: -1
        thirdFailMessage = message ?: "Unknown"
    }

    /**
     * 处理广告位请求成功信息
     */
    fun handleRequestSucceed() {
        thirdResponseTime = System.currentTimeMillis()

        thirdRequestResult = true
    }

    override fun toString(): String {
        return "MediaPlatformLog(thirdAppId='$thirdAppId', thirdSlotId='$thirdSlotId', thirdSlotType='$thirdSlotType', thirdPlatformName='$thirdPlatformName', thirdRequestResult=$thirdRequestResult, thirdRequestTime=$thirdRequestTime, thirdResponseTime=$thirdResponseTime, thirdFailCode=$thirdFailCode, thirdFailMessage='$thirdFailMessage')"
    }


}