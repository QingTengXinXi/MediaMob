package com.media.mob.bean.request

data class MediaRequestResult<T>(val data: T?, val code: Int = 200, val message: String = "success")