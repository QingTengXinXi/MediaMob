package com.media.mob.helper.logger

import android.util.Log
import com.media.mob.Constants

object MobLogger {
    private var defaultPrefix = "MediaMob"

    fun e(
        message: String
    ) {
        if (Constants.debug) {
            Log.e(defaultPrefix, message)
        }
    }

    fun e(
        tag: String,
        message: String
    ) {
        if (Constants.debug) {
            Log.e("$defaultPrefix-$tag", message)
        }
    }

    fun w(
        message: String
    ) {
        if (Constants.debug) {
            Log.w(defaultPrefix, message)
        }
    }

    fun w(
        tag: String,
        message: String
    ) {
        if (Constants.debug) {
            Log.w("$defaultPrefix-$tag", message)
        }
    }

    fun i(
        message: String
    ) {
        if (Constants.debug) {
            Log.i(defaultPrefix, message)
        }
    }

    fun i(
        tag: String,
        message: String
    ) {
        if (Constants.debug) {
            Log.i("$defaultPrefix-$tag", message)
        }
    }

    fun d(
        message: String
    ) {
        if (Constants.debug) {
            Log.d(defaultPrefix, message)
        }
    }

    fun d(
        tag: String,
        message: String
    ) {
        if (Constants.debug) {
            Log.d("$defaultPrefix-$tag", message)
        }
    }

    fun v(
        message: String
    ) {
        if (Constants.debug) {
            Log.v(defaultPrefix, message)
        }
    }

    fun v(
        tag: String,
        message: String
    ) {
        if (Constants.debug) {
            Log.v("$defaultPrefix-$tag", message)
        }
    }
}