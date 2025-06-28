package de.fast2work.mobility.utility.extension

import android.content.Context
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.utility.preference.EasyPref
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.net.URLEncoder
import java.util.*

fun Any.toFieldStringMap(): HashMap<String, String> {
    /*val classAny = this::class
    val paramMap = HashMap<String, Any>()
    for (field in classAny.members) {
        paramMap[field.name] = field.
    }
    return paramMap*/
    val gson = Gson()
    val json = gson.toJson(this)
    val jsonObject = JSONObject(json)
    val param = HashMap<String, String>()
    try {
        val iterator = jsonObject.keys()
        while (iterator.hasNext()) {
            val key = iterator.next()
            val value = jsonObject.get(key)

            if (value != null && value is String) {
                param[key] = value
            }
        }
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    return param
}

fun Any.toFieldRequestBodyMap(): HashMap<String, RequestBody> {
    val gson = Gson()
    val json = gson.toJson(this)
    val jsonObject = JSONObject(json)
    val param = HashMap<String, RequestBody>()
    try {
        val iterator = jsonObject.keys()
        while (iterator.hasNext()) {
            val key = iterator.next()
            val value = jsonObject.get(key) // {"path":"/emulated/0/"}

            if (value == null) {
                continue
            }
            val map = parseFileJsonIfRequired(key, value)
            map?.let {
                param.putAll(it)
            }
        }
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    return param
}

fun isLoggedIn(): Boolean {
    return !TextUtils.isEmpty(BaseApplication.sharedPreference.getPref(EasyPref.USER_ACCESS_TOKEN, ""))
}

/**
 * this method returns multipart key and value for file input
 *
 * @param keyName
 * @param file
 * @return
 */
fun getMultipartBody(keyName: String, file: File?): MultipartBody.Part? {
    if (keyName.isEmpty()) {
        Log.w("REQUEST MULTI PART BODY", "KEYNAME MUST HAVE VALUE")
        return null
    }
    file?.apply {
        val requestBody = this.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(keyName, this.name, requestBody)
    }
    return null
}
fun Context.getExternalDirectoryPath() : File{
    return File(externalCacheDir , "/Mobility/")
}
fun getMultipartBodyGIF(keyName: String, file: File?): MultipartBody.Part? {
    if (keyName.isEmpty()) {
        Log.w("REQUEST MULTI PART BODY", "KEYNAME MUST HAVE VALUE")
        return null
    }
    file?.apply {
        val requestBody = this.asRequestBody("image/gif".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(keyName, this.name, requestBody)
    }
    return null
}

/**
 * this method returns multipart key and value for file input
 *
 * @param keyName
 * @param file
 * @return
 */
fun getMultipartBody(keyName: String, file: File?, mimeType: String?): MultipartBody.Part? {
    if (keyName.isEmpty()) {
        Log.w("REQUEST MULTI PART BODY", "KEYNAME MUST HAVE VALUE")
        return null
    }
    file?.apply {
        val requestBody = this.asRequestBody(mimeType!!.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(keyName, this.name, requestBody)
    }
    return null
}

/**
 * This method is used to get mime type for file
 *
 * @param fallback
 * @return
 */
fun File.getMimeType(fallback: String = "image/*"): String {
    return MimeTypeMap.getFileExtensionFromUrl(toString())
        ?.run { MimeTypeMap.getSingleton().getMimeTypeFromExtension(lowercase()) }
        ?: fallback // You might set it to */*
}

fun getMultipartBodyForCSV(keyName: String, file: File?): MultipartBody.Part? {
    if (keyName.isEmpty()) {
        Log.w("REQUEST MULTI PART BODY", "KEYNAME MUST HAVE VALUE")
        return null
    }
    file?.apply {
        val requestBody = this.asRequestBody("text/csv".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(keyName, this.name, requestBody)
    }
    return null
}

/**
 * this method returns multipart key and value for csv file input
 *
 * @param keyName
 * @param file
 * @return
 */
fun getMultipartBodyFromInputStreamCSV(keyName: String, inputStream: ByteArray?): MultipartBody.Part? {
    if (keyName.isEmpty()) {
        Log.w("REQUEST MULTI PART BODY", "KEYNAME MUST HAVE VALUE")
        return null
    }

    inputStream?.let {
        val body = inputStream.toRequestBody("text/csv".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(keyName, "contacts_${Calendar.getInstance().timeInMillis}.csv", body)
    }
    return null
}

/**
 * this method returns multipart key and value for gif input
 *
 * @param keyName
 * @param file
 * @return
 */
fun getMultipartBodyForGif(keyName: String, file: File?): MultipartBody.Part? {
    if (keyName.isEmpty()) {
        Log.w("REQUEST MULTI PART BODY", "KEYNAME MUST HAVE VALUE")
        return null
    }
    file?.apply {
        val requestBody = this.asRequestBody("image/gif".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(keyName, this.name, requestBody)
    }
    return null
}

fun parseFileJsonIfRequired(key: String, valueFromParam: Any?): HashMap<String, RequestBody>? {
    var value = valueFromParam
    if (value is JSONObject && value.has("path")) {
        val path = value.getString("path")
        value = File(path)

        return getKeyValueMap(key, value)

    } else if (value is JSONArray && value.length() > 0) {
        val param = HashMap<String, RequestBody>()

        for (i in 0 until value.length()) {
            try {
                val map = parseFileJsonIfRequired("$key[$i]", value.get(i))
                map?.let {
                    param.putAll(it)
                }
            } catch (e: JSONException) {
            }
        }
        return param
    } else {
        return getKeyValueMap(key, value)
    }
}

fun getKeyValueMap(key: String, value: Any?): HashMap<String, RequestBody>? {
    val body = getRequestBody(value)
    val reqKey = getRequestKey(key, value)
    if (body != null) {
        val param = HashMap<String, RequestBody>()
        param[reqKey] = body
        return param
    }
    return null
}

fun <T> getRequestBody(t: T): RequestBody? {
    return when (t) {
        is File -> getFileRequestBody(t as File)
        else -> getStringRequestBody(t.toString())
    }
}

fun <T> getRequestKey(key: String, value: T): String {
    return when (value) {
        is File -> getFileUploadKey(key, value)
        else -> key
    }
}

fun getFileRequestBody(file: File): RequestBody? {
    try {
        val mimeType = file.getMimeType()
        if (mimeType != null) {
            val MEDIA_TYPE = mimeType.toMediaTypeOrNull()
            return RequestBody.create(MEDIA_TYPE, file)
        }
    } catch (e: Exception) {
    }
    return null
}

fun getStringRequestBody(value: String?): RequestBody? {
    try {
        val MEDIA_TYPE_TEXT = "text/plain".toMediaTypeOrNull()
        return (value ?: "").toRequestBody(MEDIA_TYPE_TEXT)
    } catch (e: Exception) {

    }
    return null
}

fun getFileUploadKey(key: String, file: File): String {
    return "" + key + "\"; filename=\"" + URLEncoder.encode(file.name, "utf-8")
}

fun getDocumentMultipartBodyFromInputStream(keyName: String, fileName:String, inputStream: InputStream?, mineType:String): MultipartBody.Part? {
    if (keyName.isEmpty()) {
        Log.w("REQUEST MULTI PART BODY", "KEYNAME MUST HAVE VALUE")
        return null
    }

    inputStream?.let {
        val body = inputStream.readBytes().toRequestBody(mineType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(keyName, fileName, body)
    }
    return null
}

fun getContactMultipartBody(keyName: String, file: File?, mimeType: String?): MultipartBody.Part? {
    if (keyName.isEmpty()) {
        Log.w("REQUEST MULTI PART BODY", "KEYNAME MUST HAVE VALUE")
        return null
    }
    file?.apply {
        val requestBody = this.asRequestBody(mimeType!!.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(keyName, this.name, requestBody)
    }
    return null
}