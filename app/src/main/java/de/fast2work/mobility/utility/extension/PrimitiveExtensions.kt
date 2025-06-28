package de.fast2work.mobility.utility.extension

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.util.Patterns
import androidx.annotation.RequiresApi
import de.fast2work.mobility.ui.core.BaseActivity
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.utility.util.Media
import de.fast2work.mobility.utility.util.Media.MIME_TYPE_IMAGE
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.math.RoundingMode
import java.net.URLConnection
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.min

/**
 * checks whether email is valid or not
 *
 * @return
 */
fun String.isValidEmail(): Boolean {
    val pattern = Patterns.EMAIL_ADDRESS
    val matcher = pattern.matcher(this)
    return matcher.matches()
}

/**
 * checks password is passing all the custom validation or not
 *
 * @return
 */
fun String.isValidPassword(): Boolean {
    return length in 8..15 && Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*#?&()^])[A-Za-z\\d@\$!%*#?&()^]{6,}$").matcher(this).find()
}

fun String.isValidFirst(): Boolean {
    return  Pattern.compile("^[A-Za-z0-9]+\$").matcher(this).find()
}

/**
 * Contains code to get phone number
 *
 * @return
 */
fun String.getPhoneNoToPassInApi(): String {
    val fullPhoneNumber = when {
        this.isEmpty() -> ""
        this.startsWith("966") -> this
        this.startsWith("+966") -> this.replace("+", "")
        else -> "966$this"
    }

    return fullPhoneNumber.replace("+", "").replace(" ", "")
}
/**
 * Contains code to get phone number with country code
 *
 * @return
 */
fun String.getPhoneNoWithCountryCode(isMasked: Boolean = false, isFormatted: Boolean = false): String {
    var fullPhoneNumber = when {
        this.isEmpty() -> ""
        this.startsWith("966") -> "+$this"
        this.startsWith("+966") -> this
        else -> "+966$this"
    }

    if (fullPhoneNumber.length == 13) {
        // if saudi phone no with country code then length must be 13
        if (isMasked) {
            //no need to format masked number so there is else if condition

            val firstUnMasked = fullPhoneNumber.substring(0, 4) // this will return +9661
            val lastUnMasked = fullPhoneNumber.substring(9, fullPhoneNumber.length) // this will return +9661
            fullPhoneNumber = "${firstUnMasked}*****${lastUnMasked}"

        }

        if (isFormatted) {

            val phoneNoWithoutCountry = fullPhoneNumber.replace("+966", "")
            val threeDigitChunks = arrayOf(phoneNoWithoutCountry.substring(0, 3), phoneNoWithoutCountry.substring(3, 5), phoneNoWithoutCountry.substring(5, 9))


            fullPhoneNumber = "+966 " + threeDigitChunks.joinToString(" ")
        }
    }

    return fullPhoneNumber.replace("X", "*")
}

/**
 * Checks whether mobile number is valid or not
 *
 * @return
 */
fun String.isValidMobileNumber(): Boolean {
    return this.length == 9 && this.startsWith("5")
}

/**
 * checks is there any special character available or not
 *
 * @return
 */
fun String.isSpecialCharacterAvailable(): Boolean {
    val regex: Pattern = Pattern.compile("(?=.*[!@#\${}]).*")

    if (regex.matcher(this).find()) {
        return true
    }
    return false
}

/**
 * Checks User name is valid or not
 *
 * @return
 */
fun String.isValidUserName(): Boolean {
    val regex: Pattern = Pattern.compile("^[[A-Z]|[a-z]][[A-Z]|[a-z]|\\d|[_]]{7,29}\$")

    if (regex.matcher(this).find()) {
        return true
    }
    return false
}

/**
 * check whether zipcode is valid or not
 *
 * @return
 */
fun String.isValidZipCode(): Boolean {
    val regex: Pattern = Pattern.compile("^[a-zA-Z0-9]{5,20}\$")

    if (regex.matcher(this).find()) {
        return true
    }
    return false
}

/**
 * Checks upper and lower case available or not
 *
 * @return
 */
fun String.isOneUpperAndLowercaseAvailable(): Boolean {
    val regex: Pattern = Pattern.compile("(?=.*[A-Z])(?=.*[a-z]).*")

    if (regex.matcher(this).find()) {
        return true
    }
    return false
}

/**
 * Check if any digit is available or not
 *
 * @return
 */
fun String.isOneDigitAvailable(): Boolean {
    val regex: Pattern = Pattern.compile("(?=.*[0-9]).*")

    if (regex.matcher(this).find()) {
        return true
    }
    return false
}

/**
 * Coverts miliseconds into seconds
 *
 * @return
 */
fun Long.milliSecToSec(): String {
    val seconds = (this / 1000 % 60)
    val minutes = (this / 1000 / 60)
    return "${String.format(Locale.ENGLISH, "%02d", minutes)}:${String.format(Locale.ENGLISH, "%02d", seconds)}"
}

private fun decode(encodedString: String): String {
    return String(android.util.Base64.decode(encodedString, android.util.Base64.DEFAULT))
//    return String(Base64.getUrlDecoder().decode(encodedString))
}

/**
 * This method is used to get file name
 *
 * @param fileUri
 * @return
 */
fun ContentResolver.getFileName(fileUri: Uri): String {
    var name = ""
    val returnCursor = this.query(fileUri, null, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }
    return name
}

/**
 * This method is used to get file size
 *
 * @return
 */
fun File.getFileSize(): Double {
    // Get length of file in bytes
    val fileSizeInBytes: Long = this.length()
    // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
    //  Convert the KB to MegaBytes (1 MB = 1024 KBytes)
//    val fileSizeInMB = fileSizeInKB / 1024
    return (fileSizeInBytes.toDouble() / 1000)/1000
}
/**
 * This method is used to get file size
 *
 * @return
 */
fun Uri.getFileSize(context: BaseActivity): Double {
    val returnCursor: Cursor = context.contentResolver.query(this, null, null, null, null)!!
    val sizeIndex: Int = returnCursor.getColumnIndex(OpenableColumns.SIZE)
    returnCursor.moveToFirst()
    val kbSize = returnCursor.getInt(sizeIndex) / 1000 // in KB
    return kbSize / 1024.0 // in MB
}
/**
 * This method is used to get file size
 *
 * @return
 */
fun Uri.getFileSize(context: Activity): Double {
    val returnCursor: Cursor = context.contentResolver.query(this, null, null, null, null)!!
    val sizeIndex: Int = returnCursor.getColumnIndex(OpenableColumns.SIZE)
    returnCursor.moveToFirst()
    return returnCursor.getDouble(sizeIndex) / 1000000
//    return kbSize / 1024.0 // in MB
}


/**
 * sha256 hashing
 * @return String
 */
fun String.toSha256(): String {
    var hash: ByteArray? = null
    var hashCode = ""
    try {
        val digest = MessageDigest.getInstance("SHA-256")
        hash = digest.digest(this.toByteArray())
    } catch (e: NoSuchAlgorithmException) {
        //Log.e("SHA", "Can't calculate SHA-256")
    }
    if (hash != null) {
        val hashBuilder = StringBuilder()
        for (i in hash.indices) {
            val hex = Integer.toHexString(hash[i].toInt())
            if (hex.length == 1) {
                hashBuilder.append("0")
                hashBuilder.append(hex[hex.length - 1])
            } else {
                hashBuilder.append(hex.substring(hex.length - 2))
            }
        }
        hashCode = hashBuilder.toString()
    }
    return hashCode
}

/**
 * encode string into Base64
 * @param source String
 * @return String
 */
fun String.toBase64Encode(): String {
    val data = this.toByteArray(charset("UTF-8"))
    return android.util.Base64.encodeToString(data, android.util.Base64.NO_WRAP)
}

fun Double.toAmountFormatValue():String{
    val formatter = DecimalFormat("###,###,##0.00")
    return formatter.format(this)
}

fun Float.toAmountFormatValue():String{
    val formatter = DecimalFormat("###,###,##0.00")
    return formatter.format(this)
}

/**
 * checks whether file is video file or not
 *
 * @return
 */
fun String.isVideoFile(): Boolean {
    val mimeType: String = URLConnection.guessContentTypeFromName(this)
    return mimeType.startsWith("video")
}

/**
 * checks whether file is image file or not
 *
 * @return
 */
fun String.isImageFile(): Boolean {
    val mimeType = URLConnection.guessContentTypeFromName(this)
    return mimeType != null && mimeType.startsWith("image")
}

fun printErrorLog(temp: String?, message: String?) {
    val tag = getTagName(temp)
    Log.e(tag, message ?: "Null")
}

fun printDebugLog(temp: String?, message: String?) {
    val tag = getTagName(temp)
    Log.d(tag, message ?: "Null")
}

private fun getTagName(temp: String?): String? {
    var tag = temp
    if (tag?.length ?: 0 > 22) {
        tag = tag?.substring(0, 22)
    }
    return tag
}

@RequiresApi(Build.VERSION_CODES.O)
fun Uri.getFileFromContentUri(context: Context): File? {
    val cR: ContentResolver = context.contentResolver
    val mimeType = cR.getType(this)
    Log.e("==========","mime :: $mimeType")
    var tempFile: File? = null
    var fileName = ""

    this.let { returnUri ->
        context.contentResolver.query(returnUri, null, null, null)
    }?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        fileName = cursor.getString(nameIndex)
    }

    try {
        val pfd = BaseApplication.application.contentResolver.openFileDescriptor(this, "r")
        if (pfd != null) {
            val fd = pfd.fileDescriptor
            val fileInputStream = FileInputStream(fd)
            val extension = if (mimeType == Media.MIME_TYPE_PDF) {
                ".${Media.EXTENSION_PDF}"
            } else {
                if (mimeType?.startsWith(MIME_TYPE_IMAGE) == true) {
                    ".${Media.EXTENSION_JPEG}"
                }else{
                    ".${Media.EXTENSION_TEXT}"
                }
            }
            val attachment = ByteArray(fileInputStream.available())
            fileInputStream.read(attachment)
            val folder = context.getExternalDirectoryPath()
            folder.mkdirs()
            tempFile = if (fileName.isNotEmpty()) {
                File(folder, fileName)
            }else {
                File(folder, Date().time.toString() + extension)
            }
            tempFile.createNewFile()
            val out = FileOutputStream(tempFile)
            out.write(attachment)
            fileInputStream.close()
            out.close()
        }
    }catch (e: Exception){
        e.printStackTrace()
    }
    return tempFile
}

