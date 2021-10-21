package com.media.mob.platform.chuanShanJia

import com.bytedance.sdk.openadsdk.TTAdConfig
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTAdSdk.InitCallback

import com.media.mob.Constants
import com.media.mob.bean.InitialParams
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.helper.logger.MobLogger
import com.media.mob.helper.thread.runMainThread
import com.media.mob.media.interstitial.IInterstitial
import com.media.mob.media.rewardVideo.IRewardVideo
import com.media.mob.media.view.IMobView
import com.media.mob.platform.IPlatform

class CSJPlatform(private val id: String) : IPlatform {

    private val classTarget = CSJPlatform::class.java.simpleName

    override val name: String = IPlatform.PLATFORM_CSJ

    override fun initial(initialParams: InitialParams) {
        val builder = TTAdConfig.Builder()
            .appId(id)
            .debug(initialParams.debug)
            .useTextureView(Constants.useTextureView)
            .allowShowNotify(initialParams.allowShowNotify)
            .supportMultiProcess(initialParams.supportMultiProcess)
            .needClearTaskReset()

        if (Constants.allowDownloadNetworkType.isNotEmpty()) {
            builder.directDownloadNetworkType(*transformationNetworkType(Constants.allowDownloadNetworkType))
        }

        MobLogger.e(classTarget, "初始化穿山甲SDK: $id")

        runMainThread {
            TTAdSdk.init(Constants.application, builder.build(), object : InitCallback {
                override fun success() {
                    MobLogger.e(classTarget, "穿山甲SDK初始化成功")
                }

                override fun fail(code: Int, message: String?) {
                    MobLogger.e(classTarget, "穿山甲SDK初始化失败: Code=$code, Message=${message ?: "unknown"}")
                }
            })
        }
    }

    override fun requestSplash(mediaRequestParams: MediaRequestParams<IMobView>) {
        CSJSplash(mediaRequestParams.activity).requestSplash(mediaRequestParams)
    }

    override fun requestRewardVideo(mediaRequestParams: MediaRequestParams<IRewardVideo>) {
        CSJRewardVideo(mediaRequestParams.activity).requestRewardVideo(mediaRequestParams)
    }

    override fun requestInterstitial(mediaRequestParams: MediaRequestParams<IInterstitial>) {

    }

    /**
     * 转换网络类型
     */
    private fun transformationNetworkType(networkTypes: IntArray): IntArray {
        if (networkTypes.isEmpty()) {
            return intArrayOf()
        }

        val transformedNetworkTypes = intArrayOf(networkTypes.size)
        networkTypes.forEachIndexed { index, i ->
            when (i) {
                InitialParams.NETWORK_STATE_2G -> {
                    transformedNetworkTypes[index] = TTAdConstant.NETWORK_STATE_2G
                }
                InitialParams.NETWORK_STATE_3G -> {
                    transformedNetworkTypes[index] = TTAdConstant.NETWORK_STATE_3G
                }
                InitialParams.NETWORK_STATE_4G -> {
                    transformedNetworkTypes[index] = TTAdConstant.NETWORK_STATE_4G
                }
                InitialParams.NETWORK_STATE_WIFI -> {
                    transformedNetworkTypes[index] = TTAdConstant.NETWORK_STATE_WIFI
                }
                InitialParams.NETWORK_STATE_MOBILE -> {
                    transformedNetworkTypes[index] = TTAdConstant.NETWORK_STATE_MOBILE
                }
            }
        }
        return transformedNetworkTypes
    }
}