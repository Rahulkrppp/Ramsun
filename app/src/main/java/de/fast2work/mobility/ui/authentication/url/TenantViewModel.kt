package de.fast2work.mobility.ui.authentication.url

import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.di.helper.AuthenticationRepoHelper
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.helper.SingleLiveData
import de.fast2work.mobility.utility.preference.EasyPref
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TenantViewModel @Inject constructor(private val authenticationRepo: AuthenticationRepoHelper) : BaseViewModel() {
    var tenantInfoLiveData = SingleLiveData<WSObjectResponse<TenantInfoModel>>()

    fun callGetTenantThemeApi(tenantName: String) {
        viewModelScope.launch {
            invalidateLoading(true)
            authenticationRepo.callGetTenantThemeApi(tenantName, onResult = {
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