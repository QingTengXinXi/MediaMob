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
    var splashFullScreenShow: Boolean = false

    /**
     * 开屏广告是否限制点击区域
     */
    var splashLimitClickArea: Boolean = false

    /**
     * 开屏广告使用的ViewGroup
     */
    var splashShowViewGroup: ViewGroup? = null

    /**
     * 插屏广告是否全屏展示（广点通插屏广告样式为弹窗和全屏的请求接口有区分）
     */
    var interstitialFullScreenShow: Boolean = false

    /**
     * 插屏广告是否使用新模板渲染（穿山甲插屏广告新、旧模板广告请求接口有区分）
     */
    var interstitialNewTemplateExpress: Boolean = true

    /**
     * 插屏广告是否是在视频场景下使用(百青藤插屏广告视频场景和普通场景请求接口有区分)
     */
    var interstitialShowVideoScene: Boolean = false

    /**
     * 插屏广告在视频场景下具体的展示位置（仅在视频场景下使用百青藤插屏广告有效）
     */
    var interstitialUsedScene: InterstitialScene = InterstitialScene.BEFORE_VIDEO_PLAY








    /**
     * 强制展示下载合规弹窗
     */
    var forceShowDownloadDialog: Boolean = true

    /**
     * 激励视频是否静音播放
     */
    var rewardVideoMutePlay: Boolean = false

    /**
     * 本次广告请求的用途
     */
    var mediaLoadType: MediaLoadType = MediaLoadType.LOAD

    /**
     * 广告期望宽度: dp
     */
    var mediaAcceptedWidth: Float = 100F

    /**
     * 广告期望高度: dp
     */
    var mediaAcceptedHeight: Float = 100F

    /**
     * 是否使用TextureView播放视频
     */
    var useTextureView: Boolean = true
}