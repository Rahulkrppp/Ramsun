package de.fast2work.mobility.ui.authentication.forgetPassword

import android.devicelock.DeviceId
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.FormValidation
import de.fast2work.mobility.data.remote.cit.HBBaseResponse
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.ForgotPasswordReq
import de.fast2work.mobility.data.request.LoginRequest
import de.fast2work.mobility.di.helper.AuthenticationRepoHelper
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.utility.extension.isValidEmail
import de.fast2work.mobility.utility.helper.SingleLiveData
import de.fast2work.mobility.utility.preference.EasyPref
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Forget password view model
 *
 */
@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(private val authenticationRepo: AuthenticationRepoHelper): BaseViewModel() {
    var forgotPasswordLiveData = SingleLiveData<HBBaseResponse>()

    fun callForgotPasswordApi(email: String, deviceId: String) {
        viewModelScope.launch {
            val param = ForgotPasswordReq().apply {
                this.email = email
                this.deviceId = deviceId
            }
            invalidateLoading(true)
            authenticationRepo.callForgotPasswordApi(param, onResult = {
                invalidateLoading(false)
                forgotPasswordLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }
}