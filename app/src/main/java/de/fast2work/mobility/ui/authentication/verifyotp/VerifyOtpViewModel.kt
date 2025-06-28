package de.fast2work.mobility.ui.authentication.verifyotp

import android.os.CountDownTimer
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.ForgotPasswordReq
import de.fast2work.mobility.data.request.Login2FAReq
import de.fast2work.mobility.data.request.LoginRequest
import de.fast2work.mobility.data.request.VerifyOtpReq
import de.fast2work.mobility.data.response.User
import de.fast2work.mobility.data.response.VerifyMobileOtp
import de.fast2work.mobility.di.helper.AuthenticationRepoHelper
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.helper.SingleLiveData
import de.fast2work.mobility.utility.preference.EasyPref
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This method contains api call and variables
 *
 */
@HiltViewModel
class VerifyOtpViewModel @Inject constructor(private val repository: AuthenticationRepoHelper): BaseViewModel() {
    var verifyOtpLiveData = SingleLiveData<WSObjectResponse<VerifyMobileOtp>>()
    var resendOtpLiveData = SingleLiveData<WSObjectResponse<JsonElement>>()
    var login2FALiveData = SingleLiveData<WSObjectResponse<User>>()
    var resendLogin2FALiveData = SingleLiveData<WSObjectResponse<User>>()
    var timer: CountDownTimer? = null
    val countDownInterval: Long = 1000

    var email: String = ""
    var loginEmail: String = ""
    var twoFactorEnabled: String = ""
    var token: String = ""
    var password: String = ""


    fun callVerifyOtpApi(email: String, otp:String, deviceId: String) {
        viewModelScope.launch {
            val param = VerifyOtpReq().apply {
                this.otp = otp
                this.email = email
                this.deviceId = deviceId
            }
            invalidateLoading(true)
            repository.callVerifyOtpApi(param, onResult = {
                invalidateLoading(false)
                verifyOtpLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

    fun callReSendOtpApi(email: String, deviceId: String) {
        viewModelScope.launch {
            val param = ForgotPasswordReq().apply {
                this.email = email
                this.deviceId = deviceId
            }
            invalidateLoading(true)
            repository.callForgotPasswordApi(param, onResult = {
                invalidateLoading(false)
                resendOtpLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

    fun call2FALoginApi(email: String, otp: String, token: String,deviceId:String) {
        viewModelScope.launch {
            val param = Login2FAReq().apply {
                this.email = email
                this.otp = otp
                this.token = token
                this.deviceId = deviceId
            }
            invalidateLoading(true)
            repository.call2FALoginApi(param, onResult = {
                invalidateLoading(false)
                login2FALiveData.postValue(it)
                BaseApplication.sharedPreference.setPref(EasyPref.USER_DATA, it.data!!)
                BaseApplication.sharedPreference.setPref(EasyPref.USER_ACCESS_TOKEN, it.data?.accessToken.toBlankString())
                BaseApplication.sharedPreference.setPref(EasyPref.PARAM_LANGUAGE, it.data?.preferredLanguage.toBlankString())
                BaseApplication.sharedPreference.setPref(EasyPref.CURRENCY_FORMAT, it.data?.currencyFormat.toBlankString())
                BaseApplication.sharedPreference.setPref(EasyPref.NOTIFICATION_ENABLED, it.data?.notificationsEnabled.toBlankString())
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

    fun callResend2FAApi(email: String, password: String) {
        viewModelScope.launch {
            val param = LoginRequest().apply {
                this.email = email
                this.password = password
            }
            invalidateLoading(true)
            repository.callLoginApi(param, onResult = {
                invalidateLoading(false)
                resendLogin2FALiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }
}