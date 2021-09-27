package com.media.mob.bean

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class Information {
    @SerializedName("title")
    var title: String? = null

    @SerializedName("value")
    var value: String? = null

    @SerializedName("time")
    var time: Long = 0L

    fun <T> transformData(className: Class<T>): T? {
        if (value.isNullOrEmpty()) {
            return null
        } else {
            try {
                return Gson().fromJson(value, className)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return null
        }
    }
}