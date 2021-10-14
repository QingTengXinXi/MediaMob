package com.media.mob.platform.youLiangHui.helper.bean

/**
 * 优量汇获取应用信息基类
 */
class ApkResult<T> {
  var ret: Int? = null

  var message: String? = null

  var data: T? = null

  fun checkResultAvailable(): Boolean {
    return ret == 0 && data != null
  }

  override fun toString(): String {
    return "ApkResult(ret=$ret, message=$message, data=$data)"
  }
}