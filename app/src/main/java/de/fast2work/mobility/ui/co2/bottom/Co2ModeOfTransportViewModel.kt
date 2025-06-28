package de.fast2work.mobility.ui.co2.bottom

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.remote.cit.WSListResponse
import de.fast2work.mobility.data.response.ModeOfTransport
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.ui.dashboard.DashBoardRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import javax.inject.Inject

@HiltViewModel
class Co2ModeOfTransportViewModel  @Inject constructor(private val repository: DashBoardRepository):BaseViewModel() {
    var modeOfTransportLiveData = MutableLiveData<WSListResponse<ModeOfTransport>>()

    var modeOfTransportList: ArrayList<ModeOfTransport> = arrayListOf()
    fun callModeOfTransportApi(isLoading:Boolean) {
        viewModelScope.launch {
            invalidateLoading(isLoading)
            repository.callModeOfTransportApi( onResult = {
                invalidateLoading(false)
                modeOfTransportLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

    fun callModeOfTransportRecpictApi(isLoading: Boolean) {
        viewModelScope.launch {
            invalidateLoading(isLoading)
            repository.callModeOfTransportReceiptApi(onResult = {
                invalidateLoading(false)
                modeOfTransportLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

}