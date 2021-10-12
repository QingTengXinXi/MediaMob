package com.media.mob.helper.thread

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

val mediaExecutorService: ExecutorService = Executors.newFixedThreadPool(10, ThreadFactory {
    return@ThreadFactory Thread(it, "Mob-MedialLoader")
})

fun runMobMediaLoaderThread(run: () -> Unit) {
    mediaExecutorService.submit {
        try {
            run.invoke()
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }
}