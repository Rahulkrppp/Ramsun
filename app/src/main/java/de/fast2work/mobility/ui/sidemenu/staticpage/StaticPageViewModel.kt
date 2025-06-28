package de.fast2work.mobility.ui.sidemenu.staticpage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.remote.cit.WSListResponse
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.response.StaticPage
import de.fast2work.mobility.di.helper.SideMenuRepoHelper
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.ui.dashboard.DashBoardRepository
import de.fast2work.mobility.ui.sidemenu.SideMenuRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *  Static page view model
 */
@HiltViewModel
class StaticPageViewModel @Inject constructor(private val repository: SideMenuRepository) : BaseViewModel() {

    var pageCodeToSend = ""
    var pageTitle = ""
    var employeeReportPdfFile = ""
    var isFromMyProfile: Boolean = false
    var isFrom: Boolean = false
    var pageIndex=0
    var staticPageLiveData = MutableLiveData<WSObjectResponse<StaticPage>>()

    fun callStaticPageApi(pageIndex:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            invalidateLoading(true)
            repository.callStaticPageApi(pageIndex, onResult = {
                invalidateLoading(false)
                staticPageLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                staticPageLiveData.postValue(WSObjectResponse())
            })
        }
    }
}