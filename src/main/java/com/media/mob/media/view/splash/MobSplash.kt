package com.media.mob.media.view.splash

import android.annotation.SuppressLint
import android.app.Activity
import android.view.ViewGroup
import com.media.mob.bean.PositionConfig
import com.media.mob.bean.TacticsInfo
import com.media.mob.bean.log.MediaRequestLog
import com.media.mob.bean.request.SlotParams
import com.media.mob.dispatch.MobRequestResult
import com.media.mob.dispatch.loader.SplashLoader
import com.media.mob.dispatch.loader.helper.MobMediaCacheHelper

@SuppressLint("ViewConstructor")
class MobSplash(val activity: Activity, private val positionConfig: PositionConfig) : ISplash {

    /**
     * 广告对象
     */
    private var splash: ISplash? = null

    /**
     * 广告对象的平台名称
     */
    override val platformName: String
        get() {
            return splash?.platformName ?: ""
        }

    /**
     * 广告策略信息
     */
    override val tacticsInfo: TacticsInfo?
        get() {
            return splash?.tacticsInfo
        }

    /**
     * 广告请求响应时间
     */
    override val mediaResponseTime: Long
        get() {
            return splash?.mediaResponseTime ?: -1L
        }

    /**
     * 展示状态
     */
    override val showState: Boolean
        get() {
            return splash?.showState ?: false
        }

    /**
     * 点击状态
     */
    override val clickState: Boolean
        get() {
            return splash?.clickState ?: false
        }

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
     * 检查广告是否有效
     */
    override fun checkMediaValidity(): Boolean {
        return splash != null && splash?.checkMediaValidity() == true
    }

    /**
     * 检查广告缓存时间
     */
    override fun checkMediaCacheTimeout(): Boolean {
        return splash != null && splash?.checkMediaCacheTimeout() == true
    }

    override fun show(viewGroup: ViewGroup) {
        if (splash != null) {
            splash?.show(viewGroup)
        }
    }

    /**
     * 销毁广告
     */
    override fun destroy() {
        if (splash != null) {
            if (splash?.checkMediaValidity() == true) {
                splash?.let {
                    it.tacticsInfo?.let { tacticsInfo ->
                        MobMediaCacheHelper.insertSplashMobMediaCache(tacticsInfo, it)
                    }
                }
            } else {
                splash?.destroy()
                splash = null
            }
        }
    }

    /**
     * 请求开屏广告
     */
    fun requestSplash(slotParams: SlotParams) {
        val splashLoader = SplashLoader(
            activity,
            positionConfig,
            MediaRequestLog(positionConfig),
            object : MobRequestResult<ISplash> {

                override fun requestFailed(code: Int, message: String) {
                    invokeRequestFailedListener(code, message)
                }

                override fun requestSucceed(result: ISplash) {
                    splash = result

                    splash?.mediaShowListener = {
                        invokeMediaShowListener()
                    }

                    splash?.mediaClickListener = {
                        invokeMediaClickListener()
                    }

                    splash?.mediaCloseListener = {
                        invokeMediaCloseListener()
                    }

                    invokeRequestSuccessListener()
                }
            })

        splashLoader.handleRequest(slotParams)
    }

    /**
     * 执行开屏广告请求成功回调
     */
    private fun invokeRequestSuccessListener() {
        requestSuccessListener?.invoke()
    }

    /**
     * 执行开屏广告请求失败回调
     */
    private fun invokeRequestFailedListener(code: Int, message: String) {
        requestFailedListener?.invoke(code, message)
    }


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
}