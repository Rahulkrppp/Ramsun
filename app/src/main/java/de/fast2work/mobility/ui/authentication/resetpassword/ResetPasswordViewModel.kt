package de.fast2work.mobility.ui.authentication.resetpassword

import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.remote.cit.WSListResponse
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.ResetPasswordReq
import de.fast2work.mobility.di.helper.AuthenticationRepoHelper
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.utility.helper.SingleLiveData
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This class contains code for reset password activity
 *
 */
@HiltViewModel
class ResetPasswordViewModel @Inject constructor(private val repository: AuthenticationRepoHelper): BaseViewModel() {
    val resetPasswordLiveData = SingleLiveData<WSObjectResponse<JsonElement>>()

    var isTogglePassShow = true
    var isToggleConfirmShow = true

    fun callResetPasswordApi(email: String, otp: String, password: String, confirmPassword: String,token: String, deviceId:String) {
        viewModelScope.launch {
            val param = ResetPasswordReq().apply {
                this.email = email
                this.otp = otp
                this.password = password
                this.confirmPassword = confirmPassword
                this.type = "forgot_password"
                this.token = token
                this.deviceId = deviceId
            }
            invalidateLoading(true)
            repository.callResetPasswordApi(param, onResult = {
                invalidateLoading(false)
                resetPasswordLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }
}