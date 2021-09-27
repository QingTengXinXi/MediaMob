package com.media.mob.bean

import com.media.mob.Constants

class InitialParams {

    /**
     * debug模式
     */
    var debug = false
        set(value) {
            field = value
            Constants.debug = value
        }

    /**
     * 移动安全联盟的oaid
     */
    var oaid: String? = null
        set(value) {
            field = value
            Constants.oaid = value
        }

    /**
     * imei
     */
    var imei: String? = null
        set(value) {
            field = value
            Constants.imei = value
        }

    /**
     * 使用TextureView播放视频
     */
    var useTextureView = true
        set(value) {
            field = value
            Constants.useTextureView = value
        }

    /**
     * 是否允许展示通知栏提示
     */
    var allowShowNotify: Boolean = true
        set(value) {
            field = value
            Constants.allowShowNotify = value
        }

    /**
     * 是否允许多进程
     */
    var supportMultiProcess: Boolean = false

    /**
     * 允许直接下载的网络状态集合
     */
    var allowDownloadNetworkType: IntArray = intArrayOf()
        set(value) {
            field = value
            Constants.allowDownloadNetworkType = value
        }


    companion object {
        /**
         * 网络类型
         */
        const val NETWORK_STATE_2G = 1
        const val NETWORK_STATE_3G = 2
        const val NETWORK_STATE_4G = 3
        const val NETWORK_STATE_WIFI = 4
        const val NETWORK_STATE_MOBILE = 5
    }
}