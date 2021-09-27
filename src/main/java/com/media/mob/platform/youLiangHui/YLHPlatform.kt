package com.media.mob.platform.youLiangHui

import com.media.mob.Constants
import com.media.mob.bean.InitialParams
import com.media.mob.platform.IPlatform
import com.qq.e.comm.managers.GDTAdSdk

class YLHPlatform(private val id: String) : IPlatform {
    override val name: String = IPlatform.PLATFORM_YLH

    override fun initial(initialParams: InitialParams) {
        GDTAdSdk.init(Constants.application, id)
    }
}