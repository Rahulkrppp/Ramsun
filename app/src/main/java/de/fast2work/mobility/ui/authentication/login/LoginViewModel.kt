package de.fast2work.mobility.ui.authentication.login

import android.os.Build
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.FormValidation
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.LoginRequest
import de.fast2work.mobility.data.response.AppUpdate
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.data.response.User
import de.fast2work.mobility.di.helper.AuthenticationRepoHelper
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.utility.extension.getAppVersion
import de.fast2work.mobility.utility.extension.getDeviceName
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.helper.SingleLiveData
import de.fast2work.mobility.utility.preference.EasyPref
import de.fast2work.mobility.utility.util.IConstants
import de.fast2work.mobility.utility.util.IConstantsIcon
import de.fast2work.mobility.utility.util.IConstantsIcon.Companion.DISABLE
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *  Login View model
 */
@HiltViewModel
class LoginViewModel @Inject constructor(private val authenticationRepo: AuthenticationRepoHelper) : BaseViewModel() {

    var loginLiveData = SingleLiveData<WSObjectResponse<User>>()
    var appVersionLiveData = SingleLiveData<WSObjectResponse<AppUpdate>>()


    /**
     * This function contains code for login api call
     *
     * @param email
     * @param password
     */
 /*   fun callLoginApi(email: String, password: String, deviceId:String) {
        viewModelScope.launch {
            val param = LoginRequest().apply {
                this.email = email
                this.password = password
                this.deviceId = deviceId
            }
            invalidateLoading(true)
            authenticationRepo.callLoginApi(param, onResult = {
                invalidateLoading(false)
                loginLiveData.postValue(it)
                //if (it.data?.twoFactorEnabled.toBlankString().equals("No", true)){
                    BaseApplication.sharedPreference.setPref(EasyPref.USER_DATA, it.data!!)
                    BaseApplication.sharedPreference.setPref(EasyPref.USER_ACCESS_TOKEN, it.data?.accessToken.toBlankString())
                    BaseApplication.sharedPreference.setPref(EasyPref.PARAM_LANGUAGE, it.data?.preferredLanguage.toBlankString())
                    BaseApplication.sharedPreference.setPref(EasyPref.CURRENCY_FORMAT, it.data?.currencyFormat.toBlankString())
                    BaseApplication.sharedPreference.setPref(EasyPref.NOTIFICATION_ENABLED, it.data?.notificationsEnabled.toBlankString())

                    BaseApplication.sharedPreference.setPref(EasyPref.activeTicket, it.data!!.activeTicket)
                if (it.data?.activeTicket!=null){
                    if (it.data!!.activeTicket.ticketId.isNullOrEmpty()) {
                        if (it.data!!.activeTicket.couponId.isNullOrEmpty()) {
                            BaseApplication.sharedPreference.setPref(EasyPref.D_TICKET, DISABLE)
                        } else {
                            BaseApplication.sharedPreference.setPref(
                                EasyPref.D_TICKET,
                                IConstantsIcon.PENDING
                            )
                        }

                    } else {
                        BaseApplication.sharedPreference.setPref(
                            EasyPref.D_TICKET,
                            IConstantsIcon.ACTIVATED
                        )
                    }
                }else{
                    BaseApplication.sharedPreference.setPref(EasyPref.D_TICKET, DISABLE)
                }
                //}
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }*/

    /**
     * This function contains code for callAppVersionApi
     *
     */
    fun callAppVersionApi() {
        viewModelScope.launch {

            invalidateLoading(false)
            authenticationRepo.callAppVersionApi("android", onResult = {
                invalidateLoading(false)
                appVersionLiveData.postValue(it)
                //}
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }
}