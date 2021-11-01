package com.media.mob.helper.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle

open class ActivityLifecycle(private val currentActivity: Activity?) {

    private val activityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            if (currentActivity == activity) {
                activityCreated()
            }
        }

        override fun onActivityStarted(activity: Activity) {
            if (currentActivity == activity) {
                activityStarted()
            }
        }

        override fun onActivityResumed(activity: Activity) {
            if (currentActivity == activity) {
                activityResumed()
            }
        }

        override fun onActivityPaused(activity: Activity) {
            if (currentActivity == activity) {
                activityPaused()
            }
        }

        override fun onActivityStopped(activity: Activity) {
            if (currentActivity == activity) {
                activityStopped()
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
            if (currentActivity == activity) {
                activityDestroyed()
            }
        }
    }

    init {
        currentActivity?.application?.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    open fun activityCreated() {
    }

    open fun activityStarted() {
    }

    open fun activityResumed() {
    }

    open fun activityPaused() {
    }

    open fun activityStopped() {
    }

    open fun activityDestroyed() {
        currentActivity?.application?.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    fun unregisterActivityLifecycle() {
        currentActivity?.application?.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }
}