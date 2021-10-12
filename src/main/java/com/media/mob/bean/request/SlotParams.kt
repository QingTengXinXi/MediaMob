package com.media.mob.bean.request

import android.view.ViewGroup

class SlotParams {


    /**
     * 开屏广告请求超时时间，单位：毫秒
     * 优量汇取值范围 3000 ~ 5000ms
     * 穿山甲建议大于 3500ms
     */
    var splashRequestTimeOut: Long = 4000

    /**
     * 开屏广告是否全屏显示
     */
    var splashFullScreen: Boolean = false

    /**
     * 开屏广告是否限制点击区域
     */
    var splashLimitClickArea: Boolean = false

    /**
     * 强制展示下载合规天窗
     */
    var forceShowDownloadDialog : Boolean = true

    /**
     * 开屏广告使用的ViewGroup
     */
    var splashViewGroup: ViewGroup? = null
}