package de.fast2work.mobility.utility.customview.countrypicker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.fast2work.mobility.data.response.CountryList
import java.io.IOException

object CountryPicker {

    fun getCountryListFromJson(context: Context): ArrayList<CountryList> {
        val jsonFileString = getJsonDataFromAsset(context, "CountryCode.json")
        val listPersonType = object : TypeToken<ArrayList<CountryList>>() {}.type
        return Gson().fromJson(jsonFileString, listPersonType)
    }

    private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }


    fun loadImageFromAssets(context: Context, filename: String): Bitmap? {
        val imageName = "flags/$filename.png"
        val assetManager = context.assets

        return try {
            // Open the InputStream
            val inputStream = assetManager.open(imageName)

            // Decode the InputStream into a Bitmap
            BitmapFactory.decodeStream(inputStream)

        } catch (e: IOException) {
            e.printStackTrace()
            null // Handle the exception by returning null or log the error
        }
    }

}