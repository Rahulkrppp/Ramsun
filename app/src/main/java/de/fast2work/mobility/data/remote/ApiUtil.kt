package de.fast2work.mobility.data.remote

import android.webkit.MimeTypeMap
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File


object ApiUtil {

    fun <T> toFieldStringBodyMap(model: T): LinkedHashMap<String, String> {
        val param = LinkedHashMap<String, String>()
        /*param[IConstants.LANG_ID] = PrefUtil.getPreference(MainApplication.appContext, PrefUtil.LANGUAGE, "EN")
                ?: "EN"*/
        val gson = Gson()
        val json = gson.toJson(model)
        val jsonObject: JSONObject?
        try {
            jsonObject = JSONObject(json)
            val iterator = jsonObject.keys()
            while (iterator.hasNext()) {
                val key = iterator.next() as String
                val value: Any? = jsonObject.get(key)

//                if (value is JSONObject && value.has("path")) {
//                    val path = value.optString("path")
//                    value = File(path)
//                    param[key] = value
//                } else {
                if (value != null && value is String) {
                    param[key] = value
                }
//                }
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
                var value = jsonObject.get(key) // {"path":"/emulated/0/"}
                if (value is JSONObject && value.has("path")) {
                    val path = value.getString("path")
                    value = File(path)
                }

                val body = getRequestBody(value)
                val reqKey = getRequestKey(key, value)
                if (body != null) {
                    param[reqKey] = body
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return param
    }

    private fun <T> getRequestKey(key: String, value: T): String {
        return when (value) {
            is File -> getFileUploadKey(key, value)
            else -> key
        }
    }

    private fun getFileUploadKey(key: String, file: File): String {
        return "" + key + "\"; filename=\"" + file.name
    }

    private fun <T> getRequestBody(t: T): RequestBody? {
        return when (t) {
            is String -> getStringRequestBody(t as String)
            is File -> getFileRequestBody(t as File)
            else -> null
        }
    }

    private fun getStringRequestBody(value: String?): RequestBody? {
        try {
            return (value ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getFileRequestBody(file: File): RequestBody? {
        try {
            val mimeType = getMimeType(file.absolutePath)
            if (mimeType != null) {
                return file.asRequestBody(mimeType.toMediaTypeOrNull())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getMimeType(url: String): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

}


