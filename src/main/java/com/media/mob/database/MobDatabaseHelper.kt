package com.media.mob.database

import com.google.gson.Gson
import com.media.mob.Constants
import com.media.mob.bean.MobConfig
import com.media.mob.helper.data.DataHelper

object MobDatabaseHelper {

    private val databaseHelper: DatabaseHelper by lazy {
        DatabaseHelper(Constants.application)
    }

    fun requestMobConfig(): MobConfig? {
        val information = databaseHelper.requestInformation(DatabaseHelper.TYPE_MOB_CONFIG)

        if (information != null && information.value?.isEmpty() == false) {
            val mobConfig = information.transformData(MobConfig::class.java)

            if (mobConfig != null && mobConfig.checkParamsValidity()) {
                return mobConfig
            }
        }

        return try {
            Gson().fromJson(DataHelper.defaultMobConfig, MobConfig::class.java)
        } catch (exception: Exception) {
            exception.printStackTrace()
            null
        }
    }
}