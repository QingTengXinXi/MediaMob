package com.media.mob.platform

import com.media.mob.bean.InitialParams

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
}