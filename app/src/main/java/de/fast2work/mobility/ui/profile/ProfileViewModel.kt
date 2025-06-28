package de.fast2work.mobility.ui.profile

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.remote.ApiService
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.CityReq
import de.fast2work.mobility.data.request.LogoutReq
import de.fast2work.mobility.data.response.AssignedBudgetGroups
import de.fast2work.mobility.data.response.CityResponse
import de.fast2work.mobility.data.response.Co2Source
import de.fast2work.mobility.data.response.DistanceRes
import de.fast2work.mobility.data.response.SourceDestination
import de.fast2work.mobility.data.response.User
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.ui.dashboard.DashBoardRepository
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.helper.SingleLiveData
import de.fast2work.mobility.utility.preference.EasyPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: DashBoardRepository) : BaseViewModel() {

    @Named("sourceDestinationApi")
    @Inject
    lateinit var apiSourceDestination: ApiService

    var sourceDestinationLiveData = SingleLiveData<Co2Source>()
    var logoutLiveData = MutableLiveData<WSObjectResponse<JsonElement>>()
    var getUserProfileLiveData = MutableLiveData<WSObjectResponse<User>>()
    var updateUserProfileLiveData = MutableLiveData<WSObjectResponse<User>>()
    var distanceLiveData = MutableLiveData<WSObjectResponse<DistanceRes>>()
    var cityLiveData = MutableLiveData<WSObjectResponse<CityResponse>>()

    var permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.CAMERA)
    } else {
        arrayOf(Manifest.permission.CAMERA)
    }
    var isCameraSelected = false
    var imageFile: File? = null
    var selectedProfileImage: File? = null
    var assignedBudgetList:ArrayList<AssignedBudgetGroups> = arrayListOf()
    var countryCode=""
    var languageCode=""
    var amountCode=""
    var sourceDestination: SourceDestination? = null

    var fromAddressLocal=""
    var fromLatitudeLocal=""
    var fromLongitudeLocal=""

    var toAddressLocal=""
    var toLatitudeLocal=""
    var toLongitudeLocal=""
    var cityId = 0

    val distanceApiErrorLiveData: MutableLiveData<String> = MutableLiveData()

    /**
     * This function contains code for callGetUserProfileApi api call
     *
     */
    fun callGetUserProfileApi(isShowLoader: Boolean = false, userId: String) {
        viewModelScope.launch {

            invalidateLoading(isShowLoader)
            repository.callGetUserProfileApi(userId, onResult = {
                invalidateLoading(false)
                getUserProfileLiveData.postValue(it)
                Log.e("", "callBudgetGroupApi=========:$it ")
                BaseApplication.sharedPreference.setPref(EasyPref.CURRENCY_FORMAT, it.data?.currencyFormat.toBlankString())
                BaseApplication.sharedPreference.setPref(EasyPref.PARAM_LANGUAGE, it.data?.preferredLanguage.toBlankString())
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

    /**
     * This function contains code for callUpdateProfileApi api call
     *

     */
    fun callUpdateProfileApi(params: HashMap<String, RequestBody>, file: MultipartBody.Part?) {
        viewModelScope.launch {
            invalidateLoading(true)
            repository.callUpdateProfileApi(params,file, onResult = {
                invalidateLoading(false)
                BaseApplication.sharedPreference.setPref(EasyPref.USER_DATA, it.data!!)
              //  BaseApplication.sharedPreference.setPref(EasyPref.USER_ACCESS_TOKEN, it.data?.accessToken.toBlankString())
                updateUserProfileLiveData.postValue(it)
                Log.e("================", "user data=========:$it ")
                BaseApplication.sharedPreference.setPref(EasyPref.CURRENCY_FORMAT, it.data?.currencyFormat.toBlankString())
                BaseApplication.sharedPreference.setPref(EasyPref.PARAM_LANGUAGE, it.data?.preferredLanguage.toBlankString())
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

    fun callDistanceApi(fromAddressLocal:String,toAddressLocal:String) {
        viewModelScope.launch {

            val params = HashMap<String, RequestBody>()
            /*params["fromAddress"]=fromAddressLocal.getRequestBody()
            params["toAddress"]=toAddressLocal.getRequestBody()*/
            params["fromLatitude"]=fromLatitudeLocal.getRequestBody()
            params["fromLongitude"]=fromLongitudeLocal.getRequestBody()
            params["toLatitude"]=toLatitudeLocal.getRequestBody()
            params["toLongitude"]=toLongitudeLocal.getRequestBody()

            invalidateLoading(true)

            repository.callDistanceApi(params, onResult = {
                invalidateLoading(false)
                distanceLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                distanceApiErrorLiveData.postValue(it)
            })
        }
    }
    fun String?.getRequestBody(): RequestBody {
        val MEDIA_TYPE_TEXT = "text/plain".toMediaTypeOrNull()
        return (this ?: "").toRequestBody(MEDIA_TYPE_TEXT)
    }

    fun callCityListApi(pageNo: Int, limit: Int, keyword: String) {
        viewModelScope.launch {
            invalidateLoading(false)
            val params = CityReq().apply {
                this.pageNo = pageNo
                this.limit = limit
                this.keyword = keyword
                this.selectedCityId = cityId
            }

            repository.callCityListApi(params, onResult = {
                invalidateLoading(false)
                cityLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

    fun callSourceDestinationApi(search:String) {
        viewModelScope.launch {
            invalidateLoading(false)
            withContext(Dispatchers.IO) {

                coroutineScope {
                    val result = async {
                        try {
                            val response = apiSourceDestination.callSourceDistanceApi(search,true,"4eac728f-62d8-416f-850c-49e833f6d6e7")
                            if (response.isSuccessful) {
                                sourceDestinationLiveData.postValue(response.body())
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        invalidateLoading(false)
                    }
                    result.await()

                }
            }
        }
    }

    /**
     * This function contains code for callLogoutApi api call
     *
     */
    fun callLogoutApi(isShowLoader: Boolean = false, deviceId: String) {
        viewModelScope.launch {
            val param = LogoutReq().apply {
                this.deviceId = deviceId
            }

            invalidateLoading(isShowLoader)
            repository.callLogoutApi(param, onResult = {
                //invalidateLoading(false)
                logoutLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }
}