package de.fast2work.mobility.ui.invoice

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.CategoryReq
import de.fast2work.mobility.data.request.InvoiceReq
import de.fast2work.mobility.data.response.CategoryApiResponse
import de.fast2work.mobility.data.response.InvoiceDataList
import de.fast2work.mobility.data.response.InvoiceDetailsApiResponse
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.ui.dashboard.DashBoardRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvoiceViewModel @Inject constructor(private val repository: DashBoardRepository) :
    BaseViewModel() {

    var hasLoadedAllInvoiceData = false
    var currentInvoicePage = 1
    var isLoading = false
    var isCategoryScreen=false

    var invoiceListLiveData = MutableLiveData<WSObjectResponse<InvoiceDataList>>()

    /**
     * This function contains code for callInvoiceListApi api call
     *
     */
    fun callInvoiceListApi(param: InvoiceReq,isLoader:Boolean=false) {
        viewModelScope.launch {
            invalidateLoading(isLoader)
            repository.calInvoiceListApi(param, onResult = {
                invalidateLoading(false)
                invoiceListLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

    var categoryListLiveData = MutableLiveData<WSObjectResponse<CategoryApiResponse>>()

    /**
     * This function contains code for callCategoryListApi api call
     *
     */
    fun callCategoryListApi(param: HashMap<String, Any?>) {
        viewModelScope.launch {
            repository.callCategoryListApi(param, onResult = {
                categoryListLiveData.postValue(it)
            }, onFailure = {
                errorLiveData.postValue(it)
            })
        }
    }
}