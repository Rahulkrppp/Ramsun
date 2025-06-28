package de.fast2work.mobility.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.response.ActiveTicket
import de.fast2work.mobility.data.response.BudgetGroup
import de.fast2work.mobility.data.response.BudgetGroupInfo
import de.fast2work.mobility.data.response.BudgetGroupInfoItem
import de.fast2work.mobility.data.response.BudgetItem
import de.fast2work.mobility.data.response.Notification
import de.fast2work.mobility.data.response.PushNotification
import de.fast2work.mobility.data.response.TicketInfoRes
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.ui.dashboard.DashBoardRepository
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: DashBoardRepository) : BaseViewModel() {

    var budgetGroupListLiveData = MutableLiveData<WSObjectResponse<BudgetGroup>>()
    var budgetGroupInfoListLiveData = MutableLiveData<WSObjectResponse<BudgetGroupInfo>>()
    var activeTicketInfoLiveData = MutableLiveData<WSObjectResponse<TicketInfoRes>>()

    var categoryList:ArrayList<BudgetItem> = arrayListOf()
    var budgetManagementInfoList:ArrayList<BudgetGroupInfoItem> = arrayListOf()
    var categoryTypeName=""
    var budgetGroupId=""
    var categoryDate=""
    var pushNotification: PushNotification? = null


    /**
     * This function contains code for callBudgetGroupApi api call
     *
     */
    fun callBudgetGroupApi(isShowLoader: Boolean = false,categoryDate:String) {
        viewModelScope.launch {
           /* val param = BudgetGroupReq().apply {
                this.payPeriodStartMonth = "2024-04-01"

            }*/
           invalidateLoading(isShowLoader)
            repository.callBudgetGroupApi(categoryDate, onResult = {
                if (it.data?.data?.isEmpty() == true){
                    invalidateLoading(false)
                }
                budgetGroupListLiveData.postValue(it)
            }, onFailure = {
              invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

    /**
     * This function contains code for callBudgetGroupInfoApi api call
     *
     */
    fun callBudgetGroupInfoApi(isShowLoader: Boolean = false,budgetGroupId:String,categoryDate:String) {
        viewModelScope.launch {
          invalidateLoading(isShowLoader)
            repository.callBudgetGroupInfoApi(categoryDate,budgetGroupId, onResult = {
                invalidateLoading(false)
                budgetGroupInfoListLiveData.postValue(it)
            }, onFailure = {
               invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

    /**
     * This function contains code for callActiveTicketInfoUserId api call
     *
     */
    fun callActiveTicketInfoUserId() {
        viewModelScope.launch {
            invalidateLoading(true)
            repository.callActiveTicketInfoUserId( onResult = {
                    invalidateLoading(false)

                activeTicketInfoLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }
}