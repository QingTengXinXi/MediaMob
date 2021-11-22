package com.media.mob.dispatch.loader.helper

import com.media.mob.bean.TacticsInfo
import com.media.mob.media.interstitial.IInterstitial
import com.media.mob.media.rewardVideo.IRewardVideo
import com.media.mob.media.view.splash.ISplash
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

object MobMediaCacheHelper {

    /**
     * 开屏广告全局缓存
     */
    private val SPLASH_SPLASH_MEDIA_CACHES: ConcurrentHashMap<TacticsInfo, ConcurrentLinkedQueue<ISplash>> by lazy {
        ConcurrentHashMap<TacticsInfo, ConcurrentLinkedQueue<ISplash>>()
    }

    /**
     * 激励视频广告全局缓存
     */
    private val rewardVideoMobMediaCache: ConcurrentHashMap<TacticsInfo, ConcurrentLinkedQueue<IRewardVideo>> by lazy {
        ConcurrentHashMap<TacticsInfo, ConcurrentLinkedQueue<IRewardVideo>>()
    }

    /**
     * 插屏广告全局缓存
     */
    private val interstitialMobMediaCache: ConcurrentHashMap<TacticsInfo, ConcurrentLinkedQueue<IInterstitial>> by lazy {
        ConcurrentHashMap<TacticsInfo, ConcurrentLinkedQueue<IInterstitial>>()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> checkMobMediaCommonCache(tacticsInfo: TacticsInfo, slotType: String): T? {
        return when (slotType) {
            "Splash" -> {
                if (SPLASH_SPLASH_MEDIA_CACHES.containsKey(tacticsInfo)) {
                    SPLASH_SPLASH_MEDIA_CACHES[tacticsInfo]?.poll() as? T
                } else {
                    null
                }
            }
            "RewardVideo" -> {
                if (rewardVideoMobMediaCache.containsKey(tacticsInfo)) {
                    rewardVideoMobMediaCache[tacticsInfo]?.poll() as? T
                } else {
                    null
                }
            }
            "Interstitial" -> {
                if (interstitialMobMediaCache.containsKey(tacticsInfo)) {
                    interstitialMobMediaCache[tacticsInfo]?.poll() as? T
                } else {
                    null
                }
            }
            else -> {
                null
            }
        }
    }

    fun insertSplashMobMediaCache(tacticsInfo: TacticsInfo, value: ISplash) {
        if (SPLASH_SPLASH_MEDIA_CACHES.containsKey(tacticsInfo)) {
            SPLASH_SPLASH_MEDIA_CACHES[tacticsInfo]?.apply {
                this.offer(value)
            }
        } else {
            SPLASH_SPLASH_MEDIA_CACHES[tacticsInfo] = ConcurrentLinkedQueue<ISplash>().apply {
                this.offer(value)
            }
        }
    }

    fun insertRewardVideoMobMediaCache(tacticsInfo: TacticsInfo, value: IRewardVideo) {
        if (rewardVideoMobMediaCache.containsKey(tacticsInfo)) {
            rewardVideoMobMediaCache[tacticsInfo]?.apply {
                this.offer(value)
            }
        } else {
            rewardVideoMobMediaCache[tacticsInfo] = ConcurrentLinkedQueue<IRewardVideo>().apply {
                this.offer(value)
            }
        }
    }

    fun insertInterstitialMobMediaCache(tacticsInfo: TacticsInfo, value: IInterstitial) {
        if (interstitialMobMediaCache.containsKey(tacticsInfo)) {
            interstitialMobMediaCache[tacticsInfo]?.apply {
                this.offer(value)
            }
        } else {
            interstitialMobMediaCache[tacticsInfo] = ConcurrentLinkedQueue<IInterstitial>().apply {
                this.offer(value)
            }
        }
    }
}