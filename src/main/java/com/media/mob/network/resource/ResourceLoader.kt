package com.media.mob.network.resource

import com.media.mob.Constants
import com.media.mob.helper.logger.MobLogger
import com.media.mob.network.NetworkHelper
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

object ResourceLoader {

  private val classTarget = ResourceLoader::class.java.simpleName

  private val resourceExecutors = Executors.newFixedThreadPool(3, ThreadFactory {
    return@ThreadFactory Thread(it, "Mob-ResourceLoader")
  })

  private val byteCache: ByteCache by lazy {
    var file = Constants.application.cacheDir

    if (file == null) {
      file = Constants.application.externalCacheDir
    }

    ByteCache(file, 1)
  }

  fun loadResource(
    callback: (ByteArray?) -> Unit,
    url: String
  ) {
    resourceExecutors.submit {
      val bytes = byteCache.load(url)
      if (bytes != null) {
        MobLogger.e(classTarget, "从缓存获取资源: $bytes")
        callback.invoke(bytes)
      } else {
        NetworkHelper.requestByteArray(url) { result ->
          if (result?.isNotEmpty() == true) {
            byteCache.save(url, result)
            MobLogger.e(classTarget, "从网络获取资源: $result")
          }
          callback.invoke(result)
        }
      }
    }
  }
}