package com.media.mob.helper.thread

import android.os.Handler
import android.os.Looper

val mainHandler = Handler(Looper.getMainLooper())

fun runMainThread(runnable: () -> Unit) {
    mainHandler.post {
        try {
            runnable.invoke()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}