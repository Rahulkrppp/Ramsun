package de.fast2work.mobility.ui.dashboard

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.CreateTokenReq
import de.fast2work.mobility.data.request.LogoutReq
import de.fast2work.mobility.data.response.Notification
import de.fast2work.mobility.data.response.PushNotification
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.helper.SingleLiveData
import de.fast2work.mobility.utility.preference.EasyPref
import kotlinx.coroutines.launch
import org.json.JSONArray
import javax.inject.Inject

@HiltViewModel
class DashBoardViewModel @Inject constructor(private val repository: DashBoardRepository) : BaseViewModel() {

    var logoutLiveData = MutableLiveData<WSObjectResponse<JsonElement>>()
    var createTokenLiveData = MutableLiveData<WSObjectResponse<JsonElement>>()
    var notificationCountListLiveData = MutableLiveData<WSObjectResponse<Notification>>()

    var currentTabPosition: Int = 0
    var pushNotification: PushNotification? = null
    var tenantInfoLiveData = SingleLiveData<WSObjectResponse<TenantInfoModel>>()


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
    /**
     * This function contains code for callCreateFCMTokenApi api call
     *
     */
    fun callCreateFCMTokenApi(deviceId:String) {
        viewModelScope.launch {
            val param = CreateTokenReq().apply {
                this.fcmToken = BaseApplication.sharedPreference.getPref(EasyPref.FCM_KEY, "")
                this.deviceId = deviceId
            }

            invalidateLoading(false)
            repository.callCreateFCMTokenApi(param, onResult = {
                invalidateLoading(false)
                createTokenLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                //errorLiveData.postValue(it)
            })
        }
    }

    /**
     * This function contains code for callNotificationCountApi api call
     *
     */
    fun callNotificationCountApi() {
        viewModelScope.launch {
            invalidateLoading(false)
            repository.callNotificationCountApi( onResult = {
                if (it.data?.data?.isEmpty() == true){
                    invalidateLoading(false)
                }
                notificationCountListLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }


    /**
     * This function contains code for callGetTenantThemeApi api call
     *
     */
    fun callGetTenantThemeApi(tenantName: String) {
        viewModelScope.launch {
            invalidateLoading(true)
            repository.callGetTenantThemeApi(tenantName, onResult = {
                invalidateLoading(false)
                tenantInfoLiveData.postValue(it)
                BaseApplication.tenantSharedPreference.setTenantPref(EasyPref.TENANT_DATA, it.data!!)
                BaseApplication.tenantSharedPreference.setTenantPref(EasyPref.ACCESS_KEY, it.data?.tenantInfo?.accessKey.toBlankString())
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }
}