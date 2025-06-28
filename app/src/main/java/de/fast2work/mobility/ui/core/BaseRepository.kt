package de.fast2work.mobility.ui.core

import android.provider.Settings.Global
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.JsonElement
import de.fast2work.mobility.R
import de.fast2work.mobility.data.remote.cit.HBBaseResponse
import de.fast2work.mobility.data.remote.cit.HBSuccessCallback
import de.fast2work.mobility.data.remote.cit.RetrofitCallback
import de.fast2work.mobility.utility.extension.isNetworkAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * This class contains code used common in all the repository
 *
 */
abstract class BaseRepository {

    private var applicationContext = BaseApplication.application.baseContext

    /**
     * This method checks whether user is connected to Internet or not
     *
     * @param onNetworkConnected
     */
    private fun validateNetwork(onNetworkConnected: () -> Unit) {
        if (applicationContext.isNetworkAvailable()) {
            onNetworkConnected()
        }
        else {
            ContextCompat.startActivity(applicationContext, NoInternetActivity.createIntent(applicationContext), null)
        }
    }

    /**
     * This method is used to make network call and parse response as per our base response
     *
     * @param T
     * @param SC
     * @param result
     * @param retrofitCallback
     */
    suspend fun <T, SC : RetrofitCallback<T>> safeApiCall(result: Response<T>, retrofitCallback: SC) {
            withContext(Dispatchers.IO) {
                    validateNetwork {
                        try {
                            val body = result.body()
                            if (body != null) {
                                when (body) {
                                    is HBBaseResponse -> {
                                        when ((body as HBBaseResponse).responseCode) {
                                            HBBaseResponse.SUCCESS -> retrofitCallback.onSuccess(body)
                                            HBBaseResponse.ERROR -> retrofitCallback.onFailure(
                                                (body as HBBaseResponse).responseCode, (body as HBBaseResponse).responseMessage)
                                            else -> (retrofitCallback as HBSuccessCallback<*>).onFailure(
                                                (body as HBBaseResponse).responseCode, (body as HBBaseResponse).responseMessage)
                                        }
                                    }
                                    is JsonElement -> {
                                        retrofitCallback.onSuccess(body)
                                    }
                                    else -> {
                                        retrofitCallback.onFailure(result.code(), result.message())
                                    }
                                }
                            } else {
                                if (retrofitCallback is HBSuccessCallback<*>) {
                                    if (result.errorBody() != null) {
                                        val responseModel = Gson().fromJson(result.errorBody()!!.string(), HBBaseResponse::class.java)
                                        retrofitCallback.onFailure(result.code(), responseModel.responseMessage)
                                    }else {
                                        retrofitCallback.onFailure(result.code(), applicationContext.getString(R.string.something_went_wrong))
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            if (!e.message.isNullOrEmpty()) {
                                retrofitCallback.onFailure(0, e.message)
                            }
                            else {
                                retrofitCallback.onFailure(0, e.message)
                            }
                        }
                    }
            }
    }
}