package com.media.mob.platform.baiQingTeng

import android.Manifest.permission
import com.baidu.mobads.sdk.api.BDAdConfig
import com.baidu.mobads.sdk.api.MobadsPermissionSettings
import com.media.mob.Constants
import com.media.mob.bean.InitialParams
import com.media.mob.bean.request.MediaRequestParams
import com.media.mob.helper.checkPermissionGranted
import com.media.mob.helper.logger.MobLogger
import com.media.mob.media.interstitial.IInterstitial
import com.media.mob.media.rewardVideo.IRewardVideo
import com.media.mob.media.view.splash.ISplash
import com.media.mob.platform.IPlatform

class BQTPlatform(private val id: String) : IPlatform {

    private val classTarget = BQTPlatform::class.java.simpleName

    override val name: String = IPlatform.PLATFORM_BQT

    /**
     * 平台初始化方法
     */
    override fun initial(initialParams: InitialParams) {
        MobLogger.e(classTarget, "初始化百青藤广告SDK: $id")

        BDAdConfig.Builder()
            .setAppsid(id)
            .build(Constants.application)
            .init()

        if (Constants.application.checkPermissionGranted(permission.READ_PHONE_STATE)) {
            MobadsPermissionSettings.setPermissionAppList(true)
            MobadsPermissionSettings.setPermissionReadDeviceID(true)
        }

        if (Constants.application.checkPermissionGranted(permission.ACCESS_COARSE_LOCATION)) {
            MobadsPermissionSettings.setPermissionLocation(true)
        }

        if (Constants.application.checkPermissionGranted(permission.WRITE_EXTERNAL_STORAGE)) {
            MobadsPermissionSettings.setPermissionStorage(true)
        }
    }

    /**
     * 请求开屏广告
     */
    override fun requestSplash(mediaRequestParams: MediaRequestParams<ISplash>) {
        BQTSplash(mediaRequestParams.activity).requestSplash(mediaRequestParams)
    }

    /**
     * 请求激励视频广告
     */
    override fun requestRewardVideo(mediaRequestParams: MediaRequestParams<IRewardVideo>) {
        BQTRewardVideo().requestRewardVideo(mediaRequestParams)
    }

    /**
     * 请求插屏广告
     */
    override fun requestInterstitial(mediaRequestParams: MediaRequestParams<IInterstitial>) {
        BQTInterstitial().requestInterstitial(mediaRequestParams)
    }
}