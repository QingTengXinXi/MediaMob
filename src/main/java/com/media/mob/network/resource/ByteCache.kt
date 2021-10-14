package com.media.mob.network.resource

import com.jakewharton.disklrucache.DiskLruCache
import com.media.mob.helper.digest
import java.io.File
import java.io.OutputStream

class ByteCache constructor(
    file: File,
    version: Int = 1
) {

    private var diskCache: DiskLruCache? = null

    init {
        val cacheDirectory = File(file.path + File.separator + "mobCache")

        if (!cacheDirectory.exists()) {
            cacheDirectory.mkdirs()
        }

        try {
            diskCache = DiskLruCache.open(cacheDirectory, version, 1, 20 * 1024 * 1024L)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    @Synchronized
    fun load(url: String): ByteArray? {
        val snapshot = diskCache?.get(url.digest())

        if (snapshot != null) {
            return snapshot.getInputStream(0).use { inputStream ->
                inputStream.readBytes()
            }
        }
        return null
    }

    fun save(
        key: String,
        byteArray: ByteArray
    ) {
        val editor = diskCache?.edit(key.digest())

        var outputStream: OutputStream? = null

        if (editor != null) {
            try {

                outputStream = editor.newOutputStream(0)

                outputStream.write(byteArray)
                outputStream.flush()
            } catch (throwable: Throwable) {
                try {
                    editor.abort()
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }
            } finally {
                try {
                    editor.commit()
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }
                try {
                    outputStream?.close()
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }
        }
    }
}
