package de.fast2work.mobility.utility.util


import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.provider.OpenableColumns
import android.util.TypedValue
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.Inet4Address
import java.net.MalformedURLException
import java.net.NetworkInterface
import java.net.SocketException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor

object CommonUtils {

    fun openGooglePlay(context: Context, appPackageName: String = context.packageName) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (e: Exception) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }

    fun getUUID(): String {
        return UUID.randomUUID().toString()
    }

    /**
     * generate 24 character nonce
     * @return String
     */
    fun generateNonce(): String {
        val str = "0123456789876543210abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        var c = ""

        for (i in 1..24) {
            c += str[floor(Math.random() * str.length).toInt()]
        }
        return c
    }

    fun getUTCOffset(): String {
        return SimpleDateFormat("ZZZZZ", Locale.ENGLISH).format(System.currentTimeMillis())
    }

    fun getLocalIpAddress(): String {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress() ?: ""
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        return ""
    }

    fun getResourceFromAttr(attr: Int, context: Context): Int {
        val typedValue = TypedValue()
        val theme: Resources.Theme = context.theme
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.resourceId
    }

    //    @RequiresApi(Build.VERSION_CODES.O)
    fun openPdfFile(context: Context, uri: Uri?, type: String) {
        val file = createFileFromContentUri(uri!!, context)
        val fileUri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        intent.setDataAndType(fileUri, type)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "No PDF viewer app found", Toast.LENGTH_SHORT).show()
        }
    }

//    @RequiresApi(Build.VERSION_CODES.O)
    /**
     * method to create file from the uri obtained
     */
    private fun createFileFromContentUri(fileUri: Uri, context: Context): File {

        var fileName = ""

        fileUri.let { returnUri ->
            context.contentResolver.query(returnUri, null, null, null)
        }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
        }
        val iStream: InputStream = context.contentResolver.openInputStream(fileUri)!!
        val folder = context.filesDir
        val f = File(folder, "BuZZWORk_CHAT")
        f.mkdir()
        val outputFile = File(f, fileName)
        copyStreamToFile(iStream, outputFile)
        iStream.close()
        return outputFile
    }

    private fun copyStreamToFile(inputStream: InputStream, outputFile: File) {
        inputStream.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
    }

    /*fun downloadFile(fileUrl: String, directory: File, downloadCompleted: Boolean = false) {
        var totalSize = 0
        try {
            //  Log.v(ContentValues.TAG, "downloadFile() invoked ")
            // Log.v(ContentValues.TAG, "downloadFile() fileUrl $fileUrl")
            // Log.v(ContentValues.TAG, "downloadFile() directory $directory")
            val url = URL(fileUrl)
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connect()
            val inputStream = urlConnection.inputStream
            val fileOutputStream = FileOutputStream(directory)
            totalSize = urlConnection.contentLength
            val buffer = ByteArray(MEGABYTE)
            var bufferLength = 0
            var total: Long = 0
            do {
                bufferLength = inputStream.read(buffer)
                total += bufferLength
                if (bufferLength == 1 || bufferLength == -1) {
                    break
                }
                if (totalSize > 0) // only if total length is known
                // publishProgress((total * 100 / totalSize).toInt());
                    fileOutputStream.write(buffer, 0, bufferLength)
            } while (true)

            fileOutputStream.close()

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            if (directory.exists() && directory.length() != totalSize.toLong()) {
                directory.delete()
            }
        }

    }*/

}