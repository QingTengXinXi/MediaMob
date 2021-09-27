package com.media.mob.helper

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

fun Context.checkPermissionGranted(permission: String): Boolean {

    val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.checkSelfPermission(permission)
    } else {
        -1
    }

    val permissionCode = this.checkCallingOrSelfPermission(permission)

    return result == PackageManager.PERMISSION_GRANTED && permissionCode == PackageManager.PERMISSION_GRANTED
}