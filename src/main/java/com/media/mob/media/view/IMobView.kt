package com.media.mob.media.view

import android.content.Context
import android.widget.FrameLayout
import com.media.mob.media.IMob

abstract class IMobView(context: Context) : FrameLayout(context), IMob {

    /**
     * 广告展示监听
     */
    override var mediaShowListener: (() -> Unit)? = null

    /**
     * 广告点击监听
     */
    override var mediaClickListener: (() -> Unit)? = null

    /**
     * 广告关闭监听
     */
    override var mediaCloseListener: (() -> Unit)? = null

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
    fun invokeMediaShowListener() {
        mediaShowListener?.invoke()
    }

    /**
     * 执行广告View点击监听
     */
    fun invokeMediaClickListener() {
        mediaClickListener?.invoke()
    }

    /**
     * 执行广告View关闭监听
     */
    fun invokeMediaCloseListener() {
        mediaCloseListener?.invoke()
    }

    override fun destroy() {
        this.removeAllViews()

        mediaShowListener = null
        mediaClickListener = null
        mediaCloseListener = null
    }
}