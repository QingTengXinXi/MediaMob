package com.media.mob.platform.baiQingTeng

import android.Manifest.permission
import com.baidu.mobads.sdk.api.BDAdConfig
import com.baidu.mobads.sdk.api.MobadsPermissionSettings
import com.media.mob.Constants
import com.media.mob.bean.InitialParams
import com.media.mob.helper.logger.MobLogger
import com.media.mob.helper.checkPermissionGranted
import com.media.mob.platform.IPlatform

class BQTPlatform(private val id: String) : IPlatform {

    private val classTarget = BQTPlatform::class.java.simpleName

    override val name: String = IPlatform.PLATFORM_BQT

    override fun initial(initialParams: InitialParams) {

        MobLogger.e(classTarget, "初始化百青藤SDK: $id")

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
}