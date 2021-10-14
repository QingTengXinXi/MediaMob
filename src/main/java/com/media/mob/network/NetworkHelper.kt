package com.media.mob.network

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object NetworkHelper {

    private const val METHOD_GET = "GET"

    private const val READ_TIMEOUT = 10000
    private const val CONNECT_TIMEOUT = 10000

    @Throws(IOException::class)
    fun request(link: String, callback: (String?) -> Unit) {
        var httpURLConnection: HttpURLConnection? = null

        try {
            val url = URL(link)
            httpURLConnection = url.openConnection() as HttpURLConnection?
            httpURLConnection?.requestMethod = METHOD_GET

            httpURLConnection?.readTimeout = READ_TIMEOUT
            httpURLConnection?.connectTimeout = CONNECT_TIMEOUT

            httpURLConnection?.connect()

            val result = httpURLConnection?.inputStream?.use { inputStream ->
                inputStream.bufferedReader().use { reader ->
                    reader.readText()
                }
            }

            callback.invoke(result)
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            callback.invoke(null)
        } finally {
            httpURLConnection?.disconnect()
        }
    }

    fun requestByteArray(link: String, callback: (ByteArray?) -> Unit) {
        var httpURLConnection: HttpURLConnection? = null

        try {
            val url = URL(link)
            httpURLConnection = url.openConnection() as HttpURLConnection?
            httpURLConnection?.requestMethod = METHOD_GET

            httpURLConnection?.readTimeout = READ_TIMEOUT
            httpURLConnection?.connectTimeout = CONNECT_TIMEOUT

            httpURLConnection?.connect()

            val result = httpURLConnection?.inputStream?.use { inputStream ->
                inputStream.readBytes()
            }

            callback.invoke(result)
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            callback.invoke(null)
        } finally {
            httpURLConnection?.disconnect()
        }
    }
}