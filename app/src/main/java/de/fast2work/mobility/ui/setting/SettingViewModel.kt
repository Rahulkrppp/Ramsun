package de.fast2work.mobility.ui.setting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.EnableDisablePushNotificationReq
import de.fast2work.mobility.data.request.LogoutReq
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.ui.dashboard.DashBoardRepository
import de.fast2work.mobility.utility.preference.EasyPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val repository: DashBoardRepository) : BaseViewModel() {

    var isClickToggle=false
    private var enableNotificationFlag="0"
    var enableDisablePushNotificationLiveData = MutableLiveData<WSObjectResponse<Int>>()
    var deleteAccountLiveData = MutableLiveData<WSObjectResponse<JsonElement>>()
    var logoutLiveData = MutableLiveData<WSObjectResponse<JsonElement>>()


    fun callEnableDisableNotificationApi(userId:Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            enableNotificationFlag = if (isClickToggle){
                "1"
            }else{
                "0"
            }
            val params = EnableDisablePushNotificationReq().apply {
                this.enableNotification = enableNotificationFlag
            }
            invalidateLoading(true)
            repository.callEnableDisableNotificationApi(userId, params, onResult = {
                invalidateLoading(false)
                enableDisablePushNotificationLiveData.postValue(it)
                if (enableNotificationFlag == "1"){
                    BaseApplication.sharedPreference.setPref(EasyPref.NOTIFICATION_ENABLED, "1")
                }else{
                    BaseApplication.sharedPreference.setPref(EasyPref.NOTIFICATION_ENABLED, "0")
                }
            }, onFailure = {
                invalidateLoading(false)
                enableDisablePushNotificationLiveData.postValue(WSObjectResponse())
            })
        }
    }

    fun callDeleteAccountApi() {
        viewModelScope.launch(Dispatchers.IO) {
            invalidateLoading(true)
            repository.callDeleteAccountApi(onResult = {
                invalidateLoading(false)
                deleteAccountLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
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