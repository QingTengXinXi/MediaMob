package com.media.mob.media

interface IMob {

    /**
     * 广告平台名称
     */
    val platformName: String

    /**
     * 展示上报状态
     */
    val showReportState: Boolean

    /**
     * 点击上报状态
     */
    val clickReportState: Boolean

    /**
     * 广告请求响应时间
     */
    val mediaResponseTime: Long

    /**
     * 广告展示监听
     */
    var mediaShowListener: (() -> Unit)?

    /**
     * 广告点击监听
     */
    var mediaClickListener: (() -> Unit)?

    /**
     * 广告关闭监听
     */
    var mediaCloseListener: (() -> Unit)?

    /**
     * 检查广告有效性
     */
    fun checkMediaValidity(): Boolean

    /**
     * 销毁广告
     */
    fun destroy()
}