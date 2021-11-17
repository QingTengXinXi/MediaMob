package com.media.mob.helper.thread

import android.os.Handler
import android.os.Looper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import java.util.concurrent.ThreadFactory

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

fun runMainThreadDelayed(delay: Long, run: () -> Unit) {
    mainHandler.postDelayed({
        try {
            run.invoke()
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }, delay)
}

val handleExecutorService: ExecutorService = Executors.newFixedThreadPool(1, ThreadFactory {
    return@ThreadFactory Thread(it, "Mob-ThreadUtil")
})

fun runBackgroundThread(run: () -> Unit) {
    handleExecutorService.submit {
        try {
            run.invoke()
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }
}