package com.media.mob.helper

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.security.MessageDigest
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

fun Context.checkPermissionGranted(permission: String): Boolean {
    val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.checkSelfPermission(permission)
    } else {
        -1
    }

    val permissionCode = this.checkCallingOrSelfPermission(permission)

    return result == PackageManager.PERMISSION_GRANTED && permissionCode == PackageManager.PERMISSION_GRANTED
}

/**
 * 格式化文件大小
 */
fun Long.formatFileSize(): String {
    if (this <= 0) return "0B"
    val units = arrayOf("B", "kB", "MB", "GB", "TB")
    val digitGroups = (log10(this.toDouble()) / log10(1024.0)).toInt()
    return DecimalFormat("#,##0.##").format(this / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
}

/**
 * 格式化
 */
fun String.digest(): String {
    try {
        val instance: MessageDigest = MessageDigest.getInstance("MD5")

        val digest: ByteArray = instance.digest(this.toByteArray())
        val stringBuffer = StringBuffer()

        for (byte in digest) {
            val i: Int = byte.toInt() and 0xff
            var hexString = Integer.toHexString(i)
            if (hexString.length < 2) {
                hexString = "0$hexString"
            }
            stringBuffer.append(hexString)
        }
        return stringBuffer.toString()
    } catch (exception: Throwable) {
        exception.printStackTrace()
    }
    return ""
}