package de.fast2work.mobility.di

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.dc.retroapi.builder.RetrofitClientBuilder
import com.dc.retroapi.interceptor.DataConverterInterceptor
import com.dc.retroapi.interceptor.ResponseInterceptor
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.fast2work.mobility.BuildConfig
import de.fast2work.mobility.data.remote.ApiService
import de.fast2work.mobility.data.remote.cit.HBBaseResponse
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.response.RefreshToken
import de.fast2work.mobility.data.response.User
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.dashboard.DashboardActivity
import de.fast2work.mobility.utility.extension.getAndroidDeviceId
import de.fast2work.mobility.utility.extension.performLogout
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.preference.EasyPref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Named
import javax.inject.Qualifier


@Qualifier
annotation class MeetUp

@Module
@InstallIn(SingletonComponent::class)
class ApiClientModule {

    @Provides //actual instance is created here
    fun provideBaseUrl() = BuildConfig.BASE_URL

    @Provides
    fun provideApplication(@ApplicationContext appContext: Context): Context {
        return appContext.applicationContext
    }

    @Provides
    fun provideApiService(baseUrl: String, context: Context): ApiService {
        return RetrofitClientBuilder().baseUrl(baseUrl).readTimeout(1000 * 10).writeTimeout(1000 * 120)
            .addConverterFactory(GsonConverterFactory.create()).addInterceptor(Interceptor { chain ->
                    val original = chain.request()
                    val url = original.url.newBuilder()
                        .addQueryParameter("deviceType", "android")
                        .addQueryParameter("deviceId", context.getAndroidDeviceId())
                        .build()
                    val builder1 = original.newBuilder().url(url)
                builder1.header("Content-Type", "application/json")
                if (original.url.pathSegments.contains("appVersion") || original.url.pathSegments.contains("staticpages")){

                }else{
                    builder1.header("access-key", BaseApplication.tenantSharedPreference.getPref(EasyPref.ACCESS_KEY, ""))
                    builder1.header("authorization", "Bearer " + BaseApplication.sharedPreference.getPref(EasyPref.USER_ACCESS_TOKEN, ""))
                }
                //builder1.header("Accept-Language", BaseApplication.sharedPreference.getPref(EasyPref.PARAM_LANGUAGE, ""))
                builder1.header("ngrok-skip-browser-warning", "1")
                val request = builder1.build()
                chain.proceed(request)
            }).addInterceptor(DataConverterInterceptor()).addResponseInterceptor(object : ResponseInterceptor.OnResponseCallback {
            override fun onNetworkResponse(response: Response) {
                try {
                    val responseBody = response.body
                    if (responseBody != null) {
                        val resString: String = responseBody.string()

                        try {
                            val baseResponse: HBBaseResponse = Gson().fromJson(resString, HBBaseResponse::class.java)
                            Log.e("", "onNetworkResponse: ${baseResponse.isAuthenticationError}", )

                            if (baseResponse.isAuthenticationError) {
//                                BaseApplication.application.performLogout()
                                CoroutineScope(Dispatchers.IO).launch {
                                    val postData = HashMap<String,String>()
                                    val headerata = HashMap<String,String>()

                                    headerata.put("access-key",BaseApplication.tenantSharedPreference.getPref(EasyPref.ACCESS_KEY, ""))
                                    headerata.put("authorization","Bearer " + BaseApplication.sharedPreference.getPref(EasyPref.USER_ACCESS_TOKEN, ""))
                                    postData.put("refreshToken", BaseApplication.sharedPreference.getPrefModel(EasyPref.USER_DATA, User::class.java)?.refreshToken.toBlankString())
                                    //Log.e("", "callApi: $data")
                                    val call1: Call<WSObjectResponse<RefreshToken>> =provideSourceApiService().callGenerateNewAccessToken(headerata,postData)
                                    call1.enqueue(object : Callback<WSObjectResponse<RefreshToken>>{

                                        override fun onResponse(call: Call<WSObjectResponse<RefreshToken>>, response: retrofit2.Response<WSObjectResponse<RefreshToken>>) {
                                            BaseApplication.sharedPreference.setPref(EasyPref.USER_ACCESS_TOKEN,response.body()?.data?.accessToken.toBlankString())
                                            val userData = BaseApplication.sharedPreference.getPrefModel(EasyPref.USER_DATA, User::class.java)
                                            userData?.refreshToken=response.body()?.data?.refreshToken.toBlankString()
                                            BaseApplication.sharedPreference.setPref(EasyPref.USER_DATA, userData!!)
                                            Log.e("", "callApi: ${response.body()?.data?.accessToken}")
                                            BaseApplication.application.callApi()
                                        }

                                        override fun onFailure(call: Call<WSObjectResponse<RefreshToken>>?, t: Throwable?) {
                                            // Handle failure of the first API call
                                            Log.e("", "callApi: ${t}")
                                        }
                                    })
                                }

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        if (response.networkResponse?.isSuccessful == false) {
                            Toast.makeText(BaseApplication.application.applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }

            }
        }).addLogInterceptor(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else  HttpLoggingInterceptor.Level.BODY).create(
            ApiService::class.java)
    }

    @Provides
    @Named("sourceDestinationApi")
    fun provideSourceApiService(): ApiService {
        return RetrofitClientBuilder().baseUrl("https://api.steigum.de/api/v4/steigum/").readTimeout(1000 * 120).writeTimeout(
            1000 * 120).addConverterFactory(ScalarsConverterFactory.create()).addConverterFactory(
            GsonConverterFactory.create()).addLogInterceptor(
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE).create(ApiService::class.java)
    }

    @Provides
    @Named("provideDTicketApiService")
    fun provideDTicketApiService(): ApiService {
        return RetrofitClientBuilder().baseUrl("https://api.themobilitybox.com/v7/").readTimeout(1000 * 120).writeTimeout(
            1000 * 120).addConverterFactory(ScalarsConverterFactory.create()).addConverterFactory(
            GsonConverterFactory.create()).addLogInterceptor(
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE).create(ApiService::class.java)
    }
}
