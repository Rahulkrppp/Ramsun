package de.fast2work.mobility.ui.savingandrecpit.co2receipt

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.remote.cit.WSListResponse
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.ChartReq
import de.fast2work.mobility.data.response.ChartRes
import de.fast2work.mobility.data.response.Co2EmissionByMonthVehicleTypeRes
import de.fast2work.mobility.data.response.LastTwoYearMonthRes
import de.fast2work.mobility.data.response.LastTwoYearVehicleTypeRes
import de.fast2work.mobility.data.response.UniqueVehiclesRes
import de.fast2work.mobility.ui.chart.LastTwoYearVehicleTypeFragment
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.ui.dashboard.DashBoardRepository
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class Co2ReceiptViewModel @Inject constructor(private val repository: DashBoardRepository) : BaseViewModel(){

    var co2ReceiptLiveData = MutableLiveData<WSObjectResponse<ChartRes>>()

    var co2EmissionByMonthList:ArrayList<Co2EmissionByMonthVehicleTypeRes> = arrayListOf()
    var lastTwoYearMonthList :ArrayList<LastTwoYearMonthRes> = arrayListOf()
    var lastTwoYearVehicleTypeList :ArrayList<LastTwoYearVehicleTypeRes> = arrayListOf()
    var co2EmissionByMonthVehicleTypeList:ArrayList<Co2EmissionByMonthVehicleTypeRes> = arrayListOf()
    var selectedName = "all"
    var selectedNameModes = "all"
    var selectedYear:Int?=0
    var uniqueVehiclesList:ArrayList<UniqueVehiclesRes> = arrayListOf()

    /**
     * This function contains code for callChartCo2Api api call
     *
     */
    fun callChartCo2Api(invoice: ChartReq,isLoader:Boolean=false) {
        viewModelScope.launch {
                invalidateLoading(isLoader)

            repository.callChartCo2Api(invoice, onResult = {
                co2ReceiptLiveData.postValue(it)
                invalidateLoading(false)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }


}