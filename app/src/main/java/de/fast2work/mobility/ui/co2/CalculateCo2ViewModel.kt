package de.fast2work.mobility.ui.co2

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.model.DayModel
import de.fast2work.mobility.data.remote.ApiService
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.Co2SurveryReq
import de.fast2work.mobility.data.request.DayDataReq
import de.fast2work.mobility.data.response.Co2Source
import de.fast2work.mobility.data.response.Co2SurveyGetDataResp
import de.fast2work.mobility.data.response.SourceDestination
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.ui.dashboard.DashBoardRepository
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.helper.SingleLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class CalculateCo2ViewModel @Inject constructor(
    private val repository: DashBoardRepository,
) : BaseViewModel() {


    var isOfficeSelected = false
    var isWorkfromHomeSelected = false
    var isTakeFromPreviousDay = false
    var isSingleMode = true
    var isMultiMode = false

    @Named("sourceDestinationApi")
    @Inject
    lateinit var apiSourceDestination: ApiService

    var sourceDestinationLiveData = SingleLiveData<Co2Source>()
    var co2SurveyLiveData = MutableLiveData<WSObjectResponse<JsonElement>>()
    var getStoredUserSurveyListLiveData =
        MutableSharedFlow<WSObjectResponse<Co2SurveyGetDataResp>>()


    var isSelectedProfileAddress = false

    var isSelectedInOfficeMo = false
    var isSelectedInOfficeTu = false
    var isSelectedInOfficeWe = false
    var isSelectedInOfficeTh = false
    var isSelectedInOfficeFr = false
    var isSelectedInOfficeSa = false
    var isSelectedInOfficeSu = false

    var isSelectedInHomeMo = false
    var isSelectedInHomeTu = false
    var isSelectedInHomeWe = false
    var isSelectedInHomeTh = false
    var isSelectedInHomeFr = false
    var isSelectedInHomeSa = false
    var isSelectedInHomeSu = false

    var sixDaySelection = false
    var searchList: ArrayList<SourceDestination> = arrayListOf()
    var sourceDestination: SourceDestination? = null
    var allDayDateSaveList: ArrayList<DayModel> = arrayListOf()
    var fromAddressLocal = ""
    var fromLatitudeLocal = ""
    var fromLongitudeLocal = ""

    var toAddressLocal = ""
    var toLatitudeLocal = ""
    var toLongitudeLocal = ""
    var dayDataSendList: ArrayList<DayDataReq> = arrayListOf()
    var isViewSurvey = false
    var surveyIdLocal: Int = 0
    var co2SurveyGetDataResp: Co2SurveyGetDataResp? = null
    val dayZero = 6
    val dayOne = 0
    val dayTwo = 1
    val dayThree = 2
    val dayFour = 3
    val dayFive = 4
    val daySix = 5
    var keyboardShowHide = false
    var notificationCount: MutableLiveData<Int> = MutableLiveData()
    fun callSourceDestinationApi(search: String) {
        viewModelScope.launch {
            invalidateLoading(false)
            withContext(Dispatchers.IO) {

                coroutineScope {
                    val result = async {
                        try {
                            /*val param = HashMap<String, String>()
                            param[API_KEY] = "yZGwNZdcJsA0nm9G64B5t9QG30V4GDie"*/
                            val response = apiSourceDestination.callSourceDistanceApi(
                                search,
                                true,
                                "4eac728f-62d8-416f-850c-49e833f6d6e7"
                            )
                            if (response.isSuccessful) {
                                sourceDestinationLiveData.postValue(response.body())
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

    fun callCo2SurveyApi() {
        val gson = Gson()
        dayDataSendList.clear()
        dayDataSendList.add(DayDataReq())
        dayDataSendList.add(DayDataReq())
        dayDataSendList.add(DayDataReq())
        dayDataSendList.add(DayDataReq())
        dayDataSendList.add(DayDataReq())
        dayDataSendList.add(DayDataReq())
        dayDataSendList.add(DayDataReq())
        /**
         * day one
         */
        dayDataSendList[0].day = 1
        if (isSelectedInOfficeMo) {
            dayDataSendList[0].inOffice = "yes"
            dayDataSendList[0].transportMode =
                allDayDateSaveList[0].modeOfTransport?.key.toBlankString()
            allDayDateSaveList[0].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                if (itemTransportDetails.isSelected) {
                    dayDataSendList[0].transportType = itemTransportDetails.key
                }
            }
            if (allDayDateSaveList[0].modeOfTransport?.items?.size == 0) {
                dayDataSendList[0].transportType = ""
            }
        } else {
            dayDataSendList[0].inOffice = "no"
            dayDataSendList[0].transportMode = ""
            dayDataSendList[0].transportType = ""

        }
        if (isSelectedInHomeMo) {
            dayDataSendList[0].isWfh = "yes"
        } else {
            dayDataSendList[0].isWfh = "no"
        }

        /**
         * day two
         */
        dayDataSendList[1].day = 2
        if (isSelectedInOfficeTu) {
            dayDataSendList[1].inOffice = "yes"
            dayDataSendList[1].transportMode =
                allDayDateSaveList[1].modeOfTransport?.key.toBlankString()
            allDayDateSaveList[1].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                if (itemTransportDetails.isSelected) {
                    dayDataSendList[1].transportType = itemTransportDetails.key

                }
            }
            if (allDayDateSaveList[1].modeOfTransport?.items?.size == 0) {
                dayDataSendList[1].transportType = ""
            }
        } else {
            dayDataSendList[1].inOffice = "no"
            dayDataSendList[1].transportMode = ""
            dayDataSendList[1].transportType = ""
        }
        if (isSelectedInHomeTu) {
            dayDataSendList[1].isWfh = "yes"
        } else {
            dayDataSendList[1].isWfh = "no"
        }

        /**
         * day three
         */
        dayDataSendList[2].day = 3
        if (isSelectedInOfficeWe) {
            dayDataSendList[2].inOffice = "yes"
            dayDataSendList[2].transportMode =
                allDayDateSaveList[2].modeOfTransport?.key.toBlankString()
            allDayDateSaveList[2].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                if (itemTransportDetails.isSelected) {
                    dayDataSendList[2].transportType = itemTransportDetails.key
                }
            }
            if (allDayDateSaveList[2].modeOfTransport?.items?.size == 0) {
                dayDataSendList[2].transportType = ""
            }
        } else {
            dayDataSendList[2].inOffice = "no"
            dayDataSendList[2].transportMode = ""
            dayDataSendList[2].transportType = ""
        }
        if (isSelectedInHomeWe) {
            dayDataSendList[2].isWfh = "yes"
        } else {
            dayDataSendList[2].isWfh = "no"
        }
        /**
         * day four
         */
        dayDataSendList[3].day = 4
        if (isSelectedInOfficeTh) {
            dayDataSendList[3].inOffice = "yes"
            dayDataSendList[3].transportMode =
                allDayDateSaveList[3].modeOfTransport?.key.toBlankString()
            allDayDateSaveList[3].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                if (itemTransportDetails.isSelected) {
                    dayDataSendList[3].transportType = itemTransportDetails.key
                }
            }
            if (allDayDateSaveList[3].modeOfTransport?.items?.size == 0) {
                dayDataSendList[3].transportType = ""
            }
        } else {
            dayDataSendList[3].inOffice = "no"
            dayDataSendList[3].transportMode = ""
            dayDataSendList[3].transportType = ""
        }
        if (isSelectedInHomeTh) {
            dayDataSendList[3].isWfh = "yes"
        } else {
            dayDataSendList[3].isWfh = "no"
        }
        /**
         * day five
         */
        dayDataSendList[4].day = 5
        if (isSelectedInOfficeFr) {
            dayDataSendList[4].inOffice = "yes"
            dayDataSendList[4].transportMode =
                allDayDateSaveList[4].modeOfTransport?.key.toBlankString()
            allDayDateSaveList[4].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                if (itemTransportDetails.isSelected) {
                    dayDataSendList[4].transportType = itemTransportDetails.key
                }
            }
            if (allDayDateSaveList[4].modeOfTransport?.items?.size == 0) {
                dayDataSendList[4].transportType = ""
            }
        } else {
            dayDataSendList[4].inOffice = "no"
            dayDataSendList[4].transportMode = ""
            dayDataSendList[4].transportType = ""
        }
        if (isSelectedInHomeFr) {
            dayDataSendList[4].isWfh = "yes"
        } else {
            dayDataSendList[4].isWfh = "no"
        }

        /**
         * day six
         */
        dayDataSendList[5].day = 6
        if (isSelectedInOfficeSa) {
            dayDataSendList[5].inOffice = "yes"
            dayDataSendList[5].transportMode =
                allDayDateSaveList[5].modeOfTransport?.key.toBlankString()
            allDayDateSaveList[5].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                if (itemTransportDetails.isSelected) {
                    dayDataSendList[5].transportType = itemTransportDetails.key
                }
            }
            if (allDayDateSaveList[5].modeOfTransport?.items?.size == 0) {
                dayDataSendList[5].transportType = ""
            }
        } else {
            dayDataSendList[5].inOffice = "no"
            dayDataSendList[5].transportMode = ""
            dayDataSendList[5].transportType = ""
        }
        if (isSelectedInHomeSa) {
            dayDataSendList[5].isWfh = "yes"
        } else {
            dayDataSendList[5].isWfh = "no"
        }

        /**
         * day seven
         */
        dayDataSendList[6].day = 0
        if (isSelectedInOfficeSu) {
            dayDataSendList[6].inOffice = "yes"
            dayDataSendList[6].transportMode =
                allDayDateSaveList[6].modeOfTransport?.key.toBlankString()
            allDayDateSaveList[6].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                if (itemTransportDetails.isSelected) {
                    dayDataSendList[6].transportType = itemTransportDetails.key
                }
            }
            if (allDayDateSaveList[6].modeOfTransport?.items?.size == 0) {
                dayDataSendList[6].transportType = ""
            }
        } else {
            dayDataSendList[6].inOffice = "no"
            dayDataSendList[6].transportMode = ""
            dayDataSendList[6].transportType = ""
        }
        if (isSelectedInHomeSu) {
            dayDataSendList[6].isWfh = "yes"
        } else {
            dayDataSendList[6].isWfh = "no"
        }

        val listString: String =
            gson.toJson(dayDataSendList, object : TypeToken<ArrayList<DayDataReq>?>() {}.type)
        viewModelScope.launch {
            /*val param= HashMap<String, RequestBody>()
            param["surveyId"] = 5.toBlankString().getRequestBody()
            param["fromAddress"] = fromAddressLocal.getRequestBody()
            param["fromLatitude"] = fromLatitudeLocal.toBlankString().getRequestBody()
            param["fromLongitude"] = fromLongitudeLocal.toBlankString().getRequestBody()
            param["toAddress"] = toAddressLocal.getRequestBody()
            param["toLatitude"] = toLatitudeLocal.toBlankString().getRequestBody()
            param["toLongitude"] = toLongitudeLocal.toBlankString().getRequestBody()
            param["dayData"] =listString.getRequestBody()*/
            val param = Co2SurveryReq().apply {
                this.surveyId = surveyIdLocal
                this.fromAddress = fromAddressLocal
                this.fromLatitude = fromLatitudeLocal
                this.fromLongitude = fromLongitudeLocal
                this.toAddress = toAddressLocal
                this.toLatitude = toLatitudeLocal
                this.toLongitude = toLongitudeLocal
                this.dayData = dayDataSendList
            }
            invalidateLoading(true)

            repository.callCo2SurveyApi(param, onResult = {
                invalidateLoading(false)
                co2SurveyLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

    fun String?.getRequestBody(): RequestBody {
        val MEDIA_TYPE_TEXT = "text/plain".toMediaTypeOrNull()
        return (this ?: "").toRequestBody(MEDIA_TYPE_TEXT)
    }

    /**
     * This function contains code for callGetStoredUserSurveyListApi api call
     *
     */
    fun callGetStoredUserSurveyListApi(surveyId: Int) {

        viewModelScope.launch {
            invalidateLoading(true)

            repository.callGetStoredUserSurveyListApi(surveyId, onResult = {
                invalidateLoading(false)
                viewModelScope.launch {
                    getStoredUserSurveyListLiveData.run { emit(it) }
                }
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }
}