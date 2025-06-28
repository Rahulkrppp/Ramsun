package de.fast2work.mobility.utility.extension

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.StrictMode
import android.util.Log
import de.fast2work.mobility.utility.util.IConstants
import de.fast2work.mobility.utility.util.ImageUtil
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL


/**
 * Get the Video Information
 * 0 landscape/portrait
 * 1 width
 * 2 height
 * 3 video bitrate
 * @return
 */
fun String.getVideoInfo(): IntArray {
    val mediaMetadataRetriever = MediaMetadataRetriever()
    mediaMetadataRetriever.setDataSource(this)

    val height = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)!!.toInt()
    val width = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!.toInt()

    val rotation = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)!!.toInt()

    val output = IntArray(4)
    if (width < height) {
        output[0] = IConstants.PORTRAIT
        output[1] = width
        output[2] = height
    } else if (width > height && rotation == 90) {
        output[0] = IConstants.PORTRAIT
        output[1] = height
        output[2] = width
    } else {
        output[0] = IConstants.LANDSCAPE
        output[1] = width
        output[2] = height
    }
    output[3] = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)!!.toInt()
    return output
}

/**
 * Get the Video Information
 * 0 height
 * 1 width
 * @return
 */
fun String.getVideoHeightWidth(): IntArray {
    val output = IntArray(2)
    val mediaMetadataRetriever = MediaMetadataRetriever()
    mediaMetadataRetriever.setDataSource(this)

    output[0] = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)!!.toInt()
    output[1] = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!.toInt()
    output[0] = if (output[0] % 2 != 0) output[0] + 1
    else output[0]

    output[1] = if (output[1] % 2 != 0) output[1] + 1
    else output[1]

    return output
}

/**
 * Get the Video Information
 * 0 width
 * 1 height
 * @return
 */
fun String.getVideoWidthHeight(): IntArray {
    val output = IntArray(2)
    val mediaMetadataRetriever = MediaMetadataRetriever()
    mediaMetadataRetriever.setDataSource(this)

    output[1] = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)!!.toInt()
    output[0] = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!.toInt()

    output[0] = if (output[0] % 2 != 0) output[0] + 1
    else output[0]

    output[1] = if (output[1] % 2 != 0) output[1] + 1
    else output[1]

    return output
}

/**
 * Get the Video Information
 * 0 landscape/portrait
 * 1 width
 * 2 height
 * @return
 */
fun String.getVideoHeightWidthOrientation(): IntArray {
    val mediaMetadataRetriever = MediaMetadataRetriever()
    mediaMetadataRetriever.setDataSource(this)

    val height = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)!!.toInt()
    val width = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!.toInt()

    val rotation = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)!!.toInt()

    val output = IntArray(3)
    if (width < height) {
        output[0] = IConstants.PORTRAIT
        output[1] = width
        output[2] = height
    } else if (width > height && rotation == 90) {
        output[0] = IConstants.PORTRAIT
        output[1] = height
        output[2] = width
    } else {
        output[0] = IConstants.LANDSCAPE
        output[1] = width
        output[2] = height
    }

    output[1] = if (output[1] % 2 != 0) output[1] + 1
    else output[1]

    output[2] = if (output[2] % 2 != 0) output[2] + 1
    else output[2]

    return output
}

fun String.videoRotation(): Int {
    val mediaMetadataRetriever = MediaMetadataRetriever()
    mediaMetadataRetriever.setDataSource(this)
    return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)!!.toInt()
}

fun Context.deleteTrimmedVideoFile() {
    try {
        val parentFolder = this.getExternalFilesDir(null)
        if (parentFolder?.listFiles() != null && parentFolder.listFiles()!!.isNotEmpty()) {
            parentFolder.listFiles()!!.forEach {
                if (it.name.startsWith(IConstants.TRIM_VIDEO_PREFIX)) {
                    Log.e("Deleted file", it.path)
                    it.delete()
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * This function contains code to remove any file if exists
 *
 */
fun File.deleteImageFile() {
    if (this.exists()) {
        this.delete()
    }
}

/**
 * return IntArray size of 2
 * 0= width 1=height
 */
fun String.getImageInfo(): IntArray {
    val uri = URI(this)
    val output = IntArray(2)
    val options: BitmapFactory.Options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(File(uri.path).absolutePath, options)
    output[0] = options.outWidth
    output[1] = options.outHeight

    output[0] = if (output[0] % 2 != 0) output[0] + 1
    else output[0]

    output[1] = if (output[1] % 2 != 0) output[1] + 1
    else output[1]

    return output
}

/**
 * return IntArray size of 2
 * 0 landscape/portrait
 * 1= width
 * 2=height
 */
fun String.getImageWidthHeightOrientation(): IntArray {
    val uri = URI(this)
    val output = IntArray(3)
    val options: BitmapFactory.Options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(File(uri.path).absolutePath, options)
    output[1] = options.outWidth
    output[2] = options.outHeight

    if (options.outWidth < options.outHeight) {
        output[0] = IConstants.PORTRAIT
    } else {
        output[0] = IConstants.LANDSCAPE
    }

    output[1] = if (output[1] % 2 != 0) output[1] + 1
    else output[1]

    output[2] = if (output[2] % 2 != 0) output[2] + 1
    else output[2]

    return output
}

fun Activity.getLocalBitmapUri(bmp: Bitmap): Uri? {
    var bmpUri: Uri? = null
    try {
        val file: File = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png")
        val out = FileOutputStream(file)
        bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
        out.close()
        bmpUri = Uri.fromFile(file)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return bmpUri
}

/**
 * This method contains code to get video thumbnail
 *
 * @param context
 * @return
 */
fun String.getVideoThumbnail(context: Context): String {
//    val fileName = StringUtils.substringAfterLast(this, "/")
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(this)
    val bitmapThumbnail = retriever.getFrameAtTime(0)
//    return ImageUtil.bitmapToFile(context,bitmapThumbnail, Constants.VIDEO_THUMB_DIRECTORY, StringUtils.substringBeforeLast(fileName, "."))!!.absolutePath
    return ImageUtil.bitmapToFile(context, bitmapThumbnail!!, IConstants.VIDEO_THUMB_DIRECTORY)!!.absolutePath
}

/**
 * This method contains to download file from URL
 *
 * @param fileName
 * @return
 */
fun URL.downloadFile(fileName: String) : File {
    this.openStream().use { inp ->
        BufferedInputStream(inp).use { bis ->
            FileOutputStream(fileName).use { fos ->
                val data = ByteArray(1024)
                var count: Int
                while (bis.read(data, 0, 1024).also { count = it } != -1) {
                    fos.write(data, 0, count)
                }
            }
        }
    }
    return File(fileName)
}

fun String.getFileSizeFromUrl(): String {
    val KB = 1024
    val MB = KB * 1024
    val GB = MB * 1024
    val TB = GB * 1024

    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)
    var fileSizeInBytes : Double = 0.0
  /*  try {
        val urlConnection = URL(this).openConnection() as HttpURLConnection
        urlConnection.requestMethod = "HEAD"
        urlConnection.connect()
        Log.e("========","fileSizeInBytes00:: $fileSizeInBytes")
        *//*fileSizeInBytes = if (fileSizeInBytes >0.0){
            urlConnection.contentLength.toDouble()
        }else{
            52.0
        }*//*
        fileSizeInBytes= urlConnection.contentLengthLong.toDouble()
        Handler().postDelayed({
            Log.e("========","fileSizeInBytes:: $fileSizeInBytes")
            urlConnection.disconnect()
        },2000)



    } catch (e: IOException) {
        e.printStackTrace()
    }*/

    var urlConnection: HttpURLConnection? = null
     try {
        val url = URL(this)
        urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.requestMethod = "HEAD"
        urlConnection.connect()
    // Print all headers for debugging
    for ((key, value) in urlConnection.headerFields) {
        println("$key: $value")
    }

    val contentLength = urlConnection.contentLengthLong
    if (contentLength == -1L) {
        // Try a GET request to read the entire content length
        urlConnection.disconnect()
        urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.requestMethod = "GET"
        urlConnection.connect()
        val inputStream = urlConnection.inputStream
        fileSizeInBytes = inputStream.readBytes().size.toDouble()
        inputStream.close()
//        fileSizeInBytes
    } else {
        fileSizeInBytes= contentLength.toDouble()
    }
} catch (e: Exception) {
    e.printStackTrace()
         fileSizeInBytes= 0.0
} finally {
    urlConnection?.disconnect()
}
    Log.e("========","fileSizeInBytes:: $fileSizeInBytes")
    return when {
        fileSizeInBytes < KB -> "$fileSizeInBytes Bytes"
        fileSizeInBytes < MB -> String.format("%.2f KB", fileSizeInBytes / KB)
        fileSizeInBytes < GB -> String.format("%.2f MB", fileSizeInBytes / MB)
        fileSizeInBytes < TB -> String.format("%.2f GB", fileSizeInBytes / GB)
        else -> String.format("%.2f TB", fileSizeInBytes / TB)
    }/* (fileSizeInBytes/1000)*/
  //  return (fileSizeInBytes/1024).toDouble()
}