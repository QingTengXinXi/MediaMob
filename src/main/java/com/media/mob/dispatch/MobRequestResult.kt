package com.media.mob.dispatch

import androidx.annotation.Keep

@Keep
interface MobRequestResult<T> {
    fun requestFailed(code: Int, message: String)

    fun requestSucceed(result: T)
}