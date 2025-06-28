package de.fast2work.mobility.ui.category

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.InvoiceReq
import de.fast2work.mobility.data.response.BudgetGroupInfoItem
import de.fast2work.mobility.data.response.CategoryInfoRes
import de.fast2work.mobility.data.response.InvoiceApiResponse
import de.fast2work.mobility.data.response.InvoiceDataList
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.ui.dashboard.DashBoardRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel  @Inject constructor(private val repository: DashBoardRepository) : BaseViewModel()  {

    var invoiceListLiveData = MutableLiveData<WSObjectResponse<InvoiceDataList>>()
    var categoryInfoLiveData = MutableLiveData<WSObjectResponse<CategoryInfoRes>>()
    var budgetGroupInfoItem = BudgetGroupInfoItem()
     var invoiceList: ArrayList<InvoiceApiResponse> = arrayListOf()
    var categoryTypeName=""
    var budgetGopId=""
    var categoryId=0
    var categoryDate=""


    /**
     * This function contains code for calInvoiceListApi api call
     *
     */
    fun calInvoiceListApi(payPeriodStartMonth:String) {
        viewModelScope.launch {
             val param = InvoiceReq().apply {
               this.budgetGroupId=budgetGopId
               this.payPeriodStartMonth=payPeriodStartMonth
                 val category :ArrayList<Int> = arrayListOf()
                 category.add(budgetGroupInfoItem.categoryId)
                this.refType="category"
               this.categoryId=category
               this.pageNo=1
           }
          invalidateLoading(true)
            repository.calInvoiceListApi(param, onResult = {
              invalidateLoading(false)
                invoiceListLiveData.postValue(it)
            }, onFailure = {
              invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

    /**
     * This function contains code for callCategoryInfoApi api call
     *
     */
    fun callCategoryInfoApi(categoryDate:String) {
        viewModelScope.launch {

            invalidateLoading(true)
            repository.callCategoryInfoApi(budgetGroupInfoItem.categoryId.toString(),budgetGopId,categoryDate, onResult = {
                invalidateLoading(false)
                categoryInfoLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }
}