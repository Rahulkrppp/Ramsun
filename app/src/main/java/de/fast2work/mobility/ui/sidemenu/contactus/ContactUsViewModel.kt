package de.fast2work.mobility.ui.sidemenu.contactus

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.ContactUsReq
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.ui.sidemenu.SideMenuRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactUsViewModel @Inject constructor(private val repository: SideMenuRepository) : BaseViewModel() {

    var contactUsLiveData = MutableLiveData<WSObjectResponse<JsonElement>>()

    /**
     * This function contains code for callContactUsdApi api call
     *
     * @param email
     * @param password
     */
    fun callContactUsdApi(subject: String, message: String, deviceId:String) {
        viewModelScope.launch {
            val param = ContactUsReq().apply {
                this.subject = subject
                this.message = message
                this.deviceId = deviceId
            }
            invalidateLoading(true)
            repository.callContactUsdApi(param, onResult = {
                invalidateLoading(false)
                contactUsLiveData.postValue(it)
                Log.e("", "callBudgetGroupApi=========:$it ")
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }
}
