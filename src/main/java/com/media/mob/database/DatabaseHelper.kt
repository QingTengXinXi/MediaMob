package com.media.mob.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import com.media.mob.bean.Information
import com.media.mob.database.table.InformationTable

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "MediaMob.db", null, 1) {

    override fun onCreate(database: SQLiteDatabase?) {
        database?.execSQL("create table if not exists ${InformationTable.tableName}(${InformationTable.title} VARCHAR(200) UNIQUE, ${InformationTable.time} LONG, ${InformationTable.value} TXT)")
    }

    override fun onUpgrade(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        database?.execSQL("drop table if exists ${InformationTable.tableName}")
        database?.execSQL("create table if not exists ${InformationTable.tableName}(${InformationTable.title} VARCHAR(200) UNIQUE, ${InformationTable.time} LONG, ${InformationTable.value} TXT)")
    }

    @Synchronized
    fun requestInformation(title: String): Information? {
        var cursor: Cursor? = null

        try {
            cursor = this.readableDatabase.query(
                InformationTable.tableName, null, "${InformationTable.title} = ?",
                arrayOf(title), null, null, null
            )
            if (cursor?.moveToFirst() == true) {
                val information = Information()

                information.title = cursor.getString(cursor.getColumnIndex(InformationTable.title))
                information.value = cursor.getString(cursor.getColumnIndex(InformationTable.value))
                information.time = cursor.getLong(cursor.getColumnIndex(InformationTable.time))

                return information
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }

    companion object {
        const val TYPE_MOB_CONFIG = "Media_Mob_Config"
    }
}