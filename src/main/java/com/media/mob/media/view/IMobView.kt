package com.media.mob.media.view

import android.content.Context
import android.widget.FrameLayout
import com.media.mob.media.IMob

abstract class IMobView(context: Context) : FrameLayout(context), IMob {

    override var viewShowListener: (() -> Unit)? = null

    override var viewClickListener: (() -> Unit)? = null

    override var viewCloseListener: (() -> Unit)? = null

    /**
     * 请求成功的监听
     */
    var requestSuccessListener: (() -> Unit)? = null

    /**
     * 请求失败的监听
     */
    var requestFailedListener: ((code: Int, message: String) -> Unit)? = null

    /**
     * 执行广告View展示监听
     */
    fun invokeViewShowListener() {
        viewShowListener?.invoke()
    }

    /**
     * 执行广告View点击监听
     */
    fun invokeViewClickListener() {
        viewClickListener?.invoke()
    }

    /**
     * 执行广告View关闭监听
     */
    fun invokeViewCloseListener() {
        viewCloseListener?.invoke()
    }

    override fun destroy() {
        this.removeAllViews()

        viewShowListener = null
        viewClickListener = null
        viewCloseListener = null
    }
}