package com.media.mob

import android.app.Application
import androidx.annotation.Keep
import com.media.mob.bean.InitialParams
import com.media.mob.bean.MobConfig
import com.media.mob.database.MobDatabaseHelper
import com.media.mob.helper.logger.MobLogger
import com.media.mob.platform.IPlatform
import com.media.mob.platform.baiQingTeng.BQTPlatform
import com.media.mob.platform.chuanShanJia.CSJPlatform
import com.media.mob.platform.jingZhunTong.JZTPlatform
import com.media.mob.platform.kuaishou.KSPlatform
import com.media.mob.platform.youLiangHui.YLHPlatform

@Keep
object MediaMob {

    private val classTarget = MediaMob::class.java.simpleName

    /**
     * 广告平台是否正在初始化
     */
    internal var initialingMob = false

    /**
     * 广告平台是否初始化失败
     */
    internal var initialMobFailed = true

    /**
     * 初始化广告SDK
     */
    @Synchronized
    fun initial(application: Application, initialParams: InitialParams, initialCallback: ((Boolean, String) -> Unit)?) {
        Constants.application = application

        if (!initialingMob && initialMobFailed) {
            initialingMob = true

            initialMobConfigs(initialParams, initialCallback)
        } else {
            initialCallback?.invoke(false, "聚合广告平台正在初始化，请稍后再试: $initialingMob : $initialMobFailed")
        }
    }

    private fun initialMobConfigs(initialParams: InitialParams, initialCallback: ((Boolean, String) -> Unit)?) {
        val mobConfig = MobDatabaseHelper.requestMobConfig()

        if (mobConfig != null && mobConfig.checkParamsValidity()) {
            handleMobConfigs(mobConfig, initialParams, initialCallback)
        } else {
            initialingMob = false
            initialMobFailed = true

            initialCallback?.invoke(false, "聚合广告配置信息异常，请检查广告配置信息是否为空")
        }
    }

    private fun handleMobConfigs(
        mobConfig: MobConfig,
        initialParams: InitialParams,
        initialCallback: ((Boolean, String) -> Unit)?
    ) {
        mobConfig.platformConfig.forEach {
            when (it.platformName) {
                IPlatform.PLATFORM_KS -> {
                    Constants.platforms[IPlatform.PLATFORM_KS] = KSPlatform(it.platformAppId)
                    Constants.platforms[IPlatform.PLATFORM_KS]?.initial(initialParams)
                }
                IPlatform.PLATFORM_BQT -> {
                    Constants.platforms[IPlatform.PLATFORM_BQT] = BQTPlatform(it.platformAppId)
                    Constants.platforms[IPlatform.PLATFORM_BQT]?.initial(initialParams)
                }
                IPlatform.PLATFORM_CSJ -> {
                    Constants.platforms[IPlatform.PLATFORM_CSJ] = CSJPlatform(it.platformAppId)
                    Constants.platforms[IPlatform.PLATFORM_CSJ]?.initial(initialParams)
                }
                IPlatform.PLATFORM_JZT -> {
                    Constants.platforms[IPlatform.PLATFORM_JZT] = JZTPlatform(it.platformAppId)
                    Constants.platforms[IPlatform.PLATFORM_JZT]?.initial(initialParams)
                }
                IPlatform.PLATFORM_YLH -> {
                    Constants.platforms[IPlatform.PLATFORM_YLH] = YLHPlatform(it.platformAppId)
                    Constants.platforms[IPlatform.PLATFORM_YLH]?.initial(initialParams)
                }
                else -> {
                    MobLogger.e(classTarget, "聚合广告SDK暂不支持的第三方广告平台: ${it.platformName}")
                }
            }
        }

        initialingMob = false
        initialMobFailed = false

        //TODO 回调是否需要等待平台初始化成功后再执行 ？？？
        initialCallback?.invoke(true, "聚合广告平台初始化成功")
    }
}