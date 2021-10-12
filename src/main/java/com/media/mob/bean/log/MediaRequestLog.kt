package com.media.mob.bean.log

import com.google.gson.annotations.SerializedName
import com.media.mob.bean.PositionConfig
import java.io.Serializable
import java.util.UUID

class MediaRequestLog(positionConfig: PositionConfig) : Serializable {
    /**
     * 广告位请求请求唯一标识
     */
    @SerializedName("request_id")
    var requestId: String = UUID.randomUUID().toString()

    /**
     * 中青物理广告位id
     */
    @SerializedName("position_id")
    var positionId: String = positionConfig.positionId

    /**
     * 中青物理广告位名称
     */
    @SerializedName("position_name")
    var positionName: String = positionConfig.positionName

    /**
     * 第三方广告位请求信息
     */
    @SerializedName("media_platform_logs")
    var mediaPlatformLogs: ArrayList<MediaPlatformLog> = ArrayList()

    /**
     * 广告位请求结果
     */
    @SerializedName("request_result")
    var requestResult: Boolean = false

    /**
     * 广告位请求时间
     */
    @SerializedName("request_time")
    var requestTime: Long = System.currentTimeMillis()

    /**
     * 广告位响应时间
     */
    @SerializedName("response_time")
    var responseTime: Long = 0

    /**
     * 处理广告位请求结果
     */
    fun handleRequestResult(result: Boolean) {
        responseTime = System.currentTimeMillis()
        requestResult = result
    }
}