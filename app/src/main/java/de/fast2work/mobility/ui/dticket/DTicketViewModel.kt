package de.fast2work.mobility.ui.dticket

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.remote.ApiService
import de.fast2work.mobility.data.remote.cit.WSListResponse
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.DTicketActivatedReq
import de.fast2work.mobility.data.request.DTicketCreatePaymentIntent
import de.fast2work.mobility.data.response.DTicketActivatedRes
import de.fast2work.mobility.data.response.DTicketCreatePaymentRes
import de.fast2work.mobility.data.response.DTicketRes
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.ui.dashboard.DashBoardRepository
import de.fast2work.mobility.utility.extension.DISPLAY_FORMAT
import de.fast2work.mobility.utility.extension.DISPLAY_FORMAT_NEW
import de.fast2work.mobility.utility.extension.SERVER_FORMAT
import de.fast2work.mobility.utility.extension.formatDate
import de.fast2work.mobility.utility.extension.millisToStringTime
import de.fast2work.mobility.utility.helper.SingleLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class DTicketViewModel @Inject constructor(private val repository: DashBoardRepository) :
    BaseViewModel() {

    var dTicketLiveData = MutableLiveData<WSListResponse<DTicketRes>>()
    var dTicketCreatePyamentLiveData = MutableLiveData<WSObjectResponse<DTicketCreatePaymentRes>>()
    var dTicketActivatedLiveData = MutableLiveData<DTicketActivatedRes>()
    var clickButtonPurchaseTicket = ""

    @Named("provideDTicketApiService")
    @Inject
    lateinit var apiDTicket: ApiService
    var setDate: String = ""
    var startDate:String=""
    var endDate:String=""
    /**
     * This function contains code for callChartCo2Api api call
     *
     */
    fun callDTicketApi(isLoader: Boolean = false) {
        viewModelScope.launch {
            invalidateLoading(isLoader)

            repository.callDTicketApi(onResult = {
                dTicketLiveData.postValue(it)
                invalidateLoading(false)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

    fun callDTicketApi(
        search: String,

    ) {
        viewModelScope.launch {
            invalidateLoading(false)
            withContext(Dispatchers.IO) {
                val dTicketReq = DTicketActivatedReq().apply {
                    this.reactivation_key = "request.reActivationKey"

                }


                coroutineScope {
                    val result = async {
                        try {
                            /*val param = HashMap<String, String>()
                            param[API_KEY] = "yZGwNZdcJsA0nm9G64B5t9QG30V4GDie"*/
                            val response = apiDTicket.callDTicketActivatedtApi(search, dTicketReq)
                            if (response.isSuccessful) {
                                dTicketActivatedLiveData.postValue(response.body())
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        invalidateLoading(false)
                    }
                    result.await()

                }
            }
        }
    }

    /**
     * This function contains code for callDTicketCreatePaymentIntentApi api call
     *
     */
    fun callDTicketCreatePaymentIntentApi(params: DTicketCreatePaymentIntent) {
        viewModelScope.launch {
            invalidateLoading(true)
            repository.callDTicketCreatePaymentIntentApi(params,onResult = {
                dTicketCreatePyamentLiveData.postValue(it)
                invalidateLoading(false)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }
}