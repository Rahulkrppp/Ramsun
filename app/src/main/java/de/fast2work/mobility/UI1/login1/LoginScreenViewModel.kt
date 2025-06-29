package de.fast2work.mobility.UI1.login1

import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.LoginRequest
import de.fast2work.mobility.data.request.LogoutReq
import de.fast2work.mobility.data.request.SignUpReq
import de.fast2work.mobility.data.response.User
import de.fast2work.mobility.di.helper.AuthenticationRepoHelper
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.utility.helper.SingleLiveData
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(private val authenticationRepo: AuthenticationRepoHelper) : BaseViewModel() {
    var loginLiveData = SingleLiveData<WSObjectResponse<User>>()

    fun callCreateAccountApi(signUpReq: LoginRequest) {
        viewModelScope.launch {

            invalidateLoading(true)
            authenticationRepo.callLoginApi(signUpReq, onResult = {
                invalidateLoading(false)
                loginLiveData.postValue(it)

                //}
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }
}