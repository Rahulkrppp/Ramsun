package de.fast2work.mobility.ui.authentication.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.LoginRequest
import de.fast2work.mobility.data.request.SignUpReq
import de.fast2work.mobility.data.response.StaticPage
import de.fast2work.mobility.data.response.User
import de.fast2work.mobility.di.helper.AuthenticationRepoHelper
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.ui.dashboard.DashBoardRepository
import de.fast2work.mobility.ui.sidemenu.SideMenuRepository
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.helper.SingleLiveData
import de.fast2work.mobility.utility.preference.EasyPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModule @Inject constructor(private val repository: AuthenticationRepoHelper,private val repository1: SideMenuRepository) : BaseViewModel() {

    var signUpLiveData = SingleLiveData<WSObjectResponse<JsonElement>>()
    var staticPageLiveData = MutableLiveData<WSObjectResponse<StaticPage>>()

    var isTogglePassShow = true
    var isToggleConfirmShow = true
    var countryCode="+49"
    var staticPage=StaticPage()
    var termsAccepted=false
    var employeeReportPdfFile = ""
    var employeeReportPdfFileDE = ""
    /**
     * This function contains code for callCreateAccountApi
     *
     * @param email
     * @param password
     */
    fun callCreateAccountApi(signUpReq: SignUpReq) {
        viewModelScope.launch {

            invalidateLoading(true)
            repository.callCreateAccountApi(signUpReq, onResult = {
                invalidateLoading(false)
                signUpLiveData.postValue(it)

                //}
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

    fun callStaticPageApi(pageIndex:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            invalidateLoading(true)
            repository1.callStaticPageApi(pageIndex, onResult = {
                invalidateLoading(false)
                staticPageLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                staticPageLiveData.postValue(WSObjectResponse())
            })
        }
    }
}