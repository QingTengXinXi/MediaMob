package com.media.mob.media

import com.media.mob.bean.TacticsInfo

interface IMob {

    /**
     * 广告平台名称
     */
    val platformName: String

    /**
     * 广告策略信息
     */
    val tacticsInfo: TacticsInfo?

    /**
     * 展示状态
     */
    val showState: Boolean

    /**
     * 点击状态
     */
    val clickState: Boolean

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
     * 检查广告缓存时间
     */
    fun checkMediaCacheTimeout(): Boolean

    /**
     * 销毁广告
     */
    fun destroy()
}