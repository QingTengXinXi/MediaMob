package com.media.mob.media.view

import android.content.Context

class MobBaseView(context: Context) : IMobView(context) {

    /**
     * 广告对象
     */
    private var mobView: IMobView? = null

    /**
     * 展示上报状态
     */
    override val showReportState: Boolean
        get() {
            return mobView?.showReportState ?: false
        }

    /**
     * 点击上报状态
     */
    override val clickReportState: Boolean
        get() {
            return mobView?.clickReportState ?: false
        }

    /**
     * 广告对象的平台名称
     */
    override val platformName: String
        get() {
            return mobView?.platformName ?: ""
        }

    /**
     * 广告销毁
     */
    override fun destroy() {
        this.removeAllViews()

        mobView?.destroy()
        mobView = null
    }
}