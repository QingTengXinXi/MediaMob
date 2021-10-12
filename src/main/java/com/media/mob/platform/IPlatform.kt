package com.media.mob.platform

import com.media.mob.bean.InitialParams
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.media.view.IMobView

interface IPlatform {
    companion object {
        const val PLATFORM_BQT = "BQT"
        const val PLATFORM_CSJ = "CSJ"
        const val PLATFORM_YLH = "YLH"
    }

    /**
     * 平台名称
     */
    val name: String

    /**
     * 初始化方法
     */
    fun initial(initialParams: InitialParams)

    /**
     * 请求开屏广告
     */
    fun requestSplash(mediaRequestParams: MediaRequestParams<IMobView>)
}