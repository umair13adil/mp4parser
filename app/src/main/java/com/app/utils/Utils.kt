package com.app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Umair_Adil on 23/07/2016.
 */
object Utils {

    fun getFileExtension(f: File): String? {
        val i = f.name.lastIndexOf('.')
        return if (i > 0) {
            f.name.substring(i + 1)
        } else
            null
    }

    enum class SupportedFileFormat private constructor(val filesuffix: String) {
        AAC("aac"),
        MP4("mp4")
    }

    val outputPath: String
        get() {
            val path = Environment.getExternalStorageDirectory().toString() + File.separator + Constants.APP_FOLDER + File.separator

            val folder = File(path)
            if (!folder.exists())
                folder.mkdirs()

            return path
        }

    fun copyFileToExternalStorage(resourceId: Int, resourceName: String, context: Context): File {
        val pathSDCard = outputPath + resourceName
        try {
            val inputStream = context.resources.openRawResource(resourceId)
            inputStream.toFile(pathSDCard)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return File(pathSDCard)
    }

    fun InputStream.toFile(path: String) {
        File(path).outputStream().use { this.copyTo(it) }
    }

    fun getConvertedFile(folder: String, fileName: String): File {
        val f = File(folder)

        if (!f.exists())
            f.mkdirs()

        return File(f.path + File.separator + fileName)
    }

    fun refreshGallery(path: String, context: Context) {

        val file = File(path)
        try {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(file)
            mediaScanIntent.data = contentUri
            context.sendBroadcast(mediaScanIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getTimeStamp(): String {
        return SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
    }

    fun copyAssets(context: Context) {
        val bufferSize = 1024
        val assetManager = context.assets
        val assetFiles = assetManager.list("")

        assetFiles.forEach {
            try {
                val inputStream = assetManager.open(it)
                val outputStream = FileOutputStream(File(outputPath, it))

                try {
                    inputStream.copyTo(outputStream, bufferSize)
                } finally {
                    inputStream.close()
                    outputStream.flush()
                    outputStream.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
