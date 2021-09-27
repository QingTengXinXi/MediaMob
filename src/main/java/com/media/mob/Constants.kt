package com.media.mob

import android.app.Application
import com.media.mob.platform.IPlatform

object Constants {

    /**
     * 广告维护的Application
     */
    lateinit var application: Application

    /**
     * debug模式
     */
    var debug: Boolean = false

    /**
     * 移动安全联盟的OAID
     */
    var oaid: String? = null

    /**
     * imei
     */
    var imei: String? = null

    /**
     * 使用TextureView播放视频
     */
    var useTextureView : Boolean = true

    /**
     * 是否允许展示通知栏提示
     */
    var allowShowNotify : Boolean = true

    /**
     * 允许直接下载的网络状态集合
     */
    var allowDownloadNetworkType: IntArray = intArrayOf()

    /**
     * 广告维护的第三方平台
     */
    var platforms = HashMap<String, IPlatform>()
}