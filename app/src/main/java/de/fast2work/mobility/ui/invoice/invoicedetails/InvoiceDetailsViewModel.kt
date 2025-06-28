package de.fast2work.mobility.ui.invoice.invoicedetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.remote.cit.WSListResponse
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.InvoiceCalculateCo2Req
import de.fast2work.mobility.data.request.InvoiceFuelTypeReq
import de.fast2work.mobility.data.response.Co2CalculateRes
import de.fast2work.mobility.data.response.DistanceRes
import de.fast2work.mobility.data.response.FuelTypeRes
import de.fast2work.mobility.data.response.InvoiceDetailsApiResponse
import de.fast2work.mobility.data.response.ModeOfTransport
import de.fast2work.mobility.data.response.SourceDestination
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.ui.dashboard.DashBoardRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class InvoiceDetailsViewModel @Inject constructor(private val repository: DashBoardRepository) :
    BaseViewModel() {
    var invoiceDetailsLiveData = MutableLiveData<WSObjectResponse<InvoiceDetailsApiResponse>>()
    var invoiceCalculateCo2LiveData = MutableLiveData<WSObjectResponse<Co2CalculateRes>>()
    var fuelTypeListLiveData = MutableLiveData<WSListResponse<FuelTypeRes>>()
    var modeOfTransportLiveData = MutableLiveData<WSListResponse<ModeOfTransport>>()
    var distanceLiveData = MutableLiveData<WSObjectResponse<DistanceRes>>()
    val distanceApiErrorLiveData: MutableLiveData<String> = MutableLiveData()

    var modeOfTransportList: ArrayList<ModeOfTransport> = arrayListOf()
    var fuelTypeList: ArrayList<FuelTypeRes> = arrayListOf()
    var transportMode = ""
    var transportType = ""
    var fuelType = ""
    var invoiceDetailsData: InvoiceDetailsApiResponse = InvoiceDetailsApiResponse()


    var sourceDestination: SourceDestination? = null

    var fromAddressLocal = ""
    var fromLatitudeLocal = ""
    var fromLongitudeLocal = ""

    var toAddressLocal = ""
    var toLatitudeLocal = ""
    var toLongitudeLocal = ""
    var distanceTypeData = ""


    /**
     * This function contains code for callCategoryListApi api call
     *
     */
    fun callInvoiceDetailsApi(invoiceId: String) {
        viewModelScope.launch {
            invalidateLoading(true)
            repository.callInvoiceDetailsApi(invoiceId, "employee", onResult = {
                invoiceDetailsLiveData.postValue(it)
                invalidateLoading(false)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

    /**
     * This function contains code for callInvoiceCalculateCo2Api api call
     *
     */
    fun callInvoiceCalculateCo2Api(invoice: InvoiceCalculateCo2Req) {
        viewModelScope.launch {
            invalidateLoading(true)
            repository.callInvoiceCalculateCo2Api(invoice, onResult = {
                invoiceCalculateCo2LiveData.postValue(it)
                invalidateLoading(false)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }


    /**
     * This function contains code for callInvoiceCalculateCo2FuelTypeApi api call
     *
     */
    fun callInvoiceCalculateCo2FuelTypeApi(invoice: InvoiceFuelTypeReq) {
        viewModelScope.launch {
            invalidateLoading(true)
            repository.callInvoiceCalculateCo2FuelTypeApi(invoice, onResult = {
                invoiceCalculateCo2LiveData.postValue(it)
                invalidateLoading(false)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

    /**
     * This function contains code for callGetFuelTypeId api call
     *
     */
    fun callGetFuelTypeId() {
        viewModelScope.launch {
            invalidateLoading(true)
            repository.callGetFuelTypeId(onResult = {
                fuelTypeListLiveData.postValue(it)
                invalidateLoading(false)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }


    fun callModeOfTransportApi(isLoading: Boolean) {
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

    fun callDistanceApi(fromAddressLocal: String, toAddressLocal: String,mode:String) {
        viewModelScope.launch {

            val params = HashMap<String, RequestBody>()
            /*params["fromAddress"]=fromAddressLocal.getRequestBody()
            params["toAddress"]=toAddressLocal.getRequestBody()*/
            params["fromLatitude"] = fromLatitudeLocal.getRequestBody()
            params["fromLongitude"] = fromLongitudeLocal.getRequestBody()
            params["toLatitude"] = toLatitudeLocal.getRequestBody()
            params["toLongitude"] = toLongitudeLocal.getRequestBody()
            params["modeType"] = mode.getRequestBody()

            invalidateLoading(true)

            repository.callDistanceApi(params, onResult = {
                invalidateLoading(false)
                distanceLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                distanceApiErrorLiveData.postValue(it)
            })
        }
    }

    fun String?.getRequestBody(): RequestBody {
        val MEDIA_TYPE_TEXT = "text/plain".toMediaTypeOrNull()
        return (this ?: "").toRequestBody(MEDIA_TYPE_TEXT)
    }
}