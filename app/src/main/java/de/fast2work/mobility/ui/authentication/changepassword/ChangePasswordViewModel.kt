package de.fast2work.mobility.ui.authentication.changepassword

import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import de.fast2work.mobility.data.FormValidation
import de.fast2work.mobility.data.remote.cit.WSListResponse
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.utility.extension.isValidPassword
import de.fast2work.mobility.utility.helper.SingleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.ChangePasswordReq
import de.fast2work.mobility.data.request.ResetPasswordReq
import de.fast2work.mobility.ui.authentication.AuthenticationRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Change password view model
 *
 */
@HiltViewModel
class ChangePasswordViewModel @Inject constructor(private val repository: AuthenticationRepository) : BaseViewModel() {

    var changePasswordLiveData = SingleLiveData<WSObjectResponse<JsonElement>>()

     var isTogglePassShow = true
     var isToggleConfirmShow = true
    /**
     * Call change password api
     * this method handles api call for change password
     * @param oldPassword
     * @param newPassword
     */
    fun callChangePasswordApi(oldPs: String, newPs: String,confirmPs: String, deviceId:String) {
        viewModelScope.launch {
            val param = ChangePasswordReq().apply {
                this.oldPassword = oldPs
                this.newPassword = newPs
                this.confirmNewPassword = confirmPs
                this.deviceId = deviceId
            }
            invalidateLoading(true)
            repository.callChangePasswordApi(param, onResult = {
                invalidateLoading(false)
                changePasswordLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

}