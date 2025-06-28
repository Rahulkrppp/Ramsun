package de.fast2work.mobility.ui.co2

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.R
import de.fast2work.mobility.data.remote.ApiService
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.response.Co2Source
import de.fast2work.mobility.data.response.Co2SurveyGetDataResp
import de.fast2work.mobility.data.response.SourceDestination
import de.fast2work.mobility.db.ModelStops
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.ui.dashboard.DashBoardRepository
import de.fast2work.mobility.utility.customview.countrypicker.CountryPicker
import de.fast2work.mobility.utility.helper.SingleLiveData
import de.fast2work.mobility.utility.preference.EasyPref
import de.fast2work.mobility.utility.util.ResourceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.log

@HiltViewModel
class CalculateCo21ViewModel  @Inject constructor(
    private val repository: DashBoardRepository,
    private val resourceProvider: ResourceProvider,
):BaseViewModel() {






    var selectedLocationIndex = 0
    var selectedDay = resourceProvider.getString(R.string.mo)
    var isPreviousDaySelected = false
    var isChangeSelected = false
    var surveyIdLocal:Int=0

    @Named("sourceDestinationApi")
    @Inject
    lateinit var apiSourceDestination: ApiService

    var getStoredUserSurveyListLiveData = MutableSharedFlow<WSObjectResponse<Co2SurveyGetDataResp>>()

    var co2SurveyLiveData = MutableLiveData<WSObjectResponse<JsonElement>>()
    var sourceDestinationLiveData = SingleLiveData<Co2Source>()
    val dayList= arrayListOf<String>()
    var isSelectedProfileAddress=false
    var sourceDestination:SourceDestination? = null
    var fromAddressLocal=""
    var fromLatitudeLocal=""
    var fromLongitudeLocal=""
    var toAddressLocal=""
    var toLatitudeLocal=""
    var toLongitudeLocal=""
    var isProfileDialogShow=false

    var notificationCount : MutableLiveData<Int> = MutableLiveData()
    fun callSourceDestinationApi(search:String) {
        viewModelScope.launch {
            invalidateLoading(false)
            withContext(Dispatchers.IO) {

                coroutineScope {
                    val result = async {
                        try {
                            /*val param = HashMap<String, String>()
                            param[API_KEY] = "yZGwNZdcJsA0nm9G64B5t9QG30V4GDie"*/
                            val response = apiSourceDestination.callSourceDistanceApi(search,true,"4eac728f-62d8-416f-850c-49e833f6d6e7")
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


    val co2JsonData = JSONObject()
    val dayData = JSONArray()
    fun onSubmitClickListeners(modeOfTransportList:List<ModelStops>){

        viewModelScope.launch {
            co2JsonData.put("surveyId",surveyIdLocal)
            co2JsonData.put("sourceAddress",fromAddressLocal)
            co2JsonData.put("destinationAddress",toAddressLocal)
            co2JsonData.put("sourcelatitude",fromLatitudeLocal)
            co2JsonData.put("sourcelongitude",fromLongitudeLocal)

            co2JsonData.put("destinationlatitude",toLatitudeLocal)
            co2JsonData.put("destinationlongitude",toLongitudeLocal)

            buildDayWiseData(0,modeOfTransportList)
            delay(100)
            buildDayWiseData(1,modeOfTransportList)
            delay(100)
            buildDayWiseData(2,modeOfTransportList)
            delay(100)
            buildDayWiseData(3,modeOfTransportList)
            delay(100)
            buildDayWiseData(4,modeOfTransportList)
            delay(100)
            buildDayWiseData(5,modeOfTransportList)
            delay(100)
            buildDayWiseData(6,modeOfTransportList)


            invalidateLoading(true)

            delay(3000)
            repository.callCo2SurveyApiNew(JsonParser.parseString(co2JsonData.toString()).asJsonObject, onResult = {
                invalidateLoading(false)
                co2SurveyLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
            Log.e("DATA====>",(co2JsonData.toString()))
        }



    }
    fun getDayNumber(name:String):Int{

        var dayNumber:Int=0
        if (BaseApplication.languageSharedPreference.getLanguagePref(EasyPref.CURRENT_LANGUAGE, "").equals("de", true)){
            when (name) {
                "So" -> {
                    dayNumber = 0
                }

                "Mo" -> {
                    dayNumber = 1
                }

                "Di" -> {
                    dayNumber = 2
                }

                "Mi" -> {
                    dayNumber = 3
                }

                "Do" -> {
                    dayNumber = 4
                }

                "Fr" -> {
                    dayNumber = 5
                }

                "Sa" -> {
                    dayNumber = 6
                }
            }
        }else {
            when (name) {
                "Su" -> {
                    dayNumber = 0

                }

               "Mo" -> {
                    dayNumber = 1
                }

                "Tu" -> {
                    dayNumber = 2
                }

                "We" -> {
                    dayNumber = 3
                }

                "Th" -> {
                    dayNumber = 4
                }

                "Fr" -> {
                    dayNumber = 5
                }

                "Sa" -> {
                    dayNumber = 6
                }
            }
        }
        Log.e("","==========${dayNumber} ${name}").toString()
        return dayNumber
    }
    fun getDayName(number:Int):String{
        var dayName=""
        if (BaseApplication.languageSharedPreference.getLanguagePref(EasyPref.CURRENT_LANGUAGE, "").equals("de", true)){
            when(number){
                0 -> {
                    dayName = "So"
                    // resourceProvider.getString(R.string.su)
                }
                1 -> {
                    dayName ="Mo"
                }
                2 -> {
                    dayName =  "Di"
                    //resourceProvider.getString(R.string.tu)
                }
                3 -> {
                    dayName =  "Mi"
                }
                4 -> {
                    dayName = "Do"
                }
                5 -> {
                    dayName =   "Fr"
                }
                6 -> {
                    dayName =   "Sa"
                }
            }
        }else{
            when(number){
                0 -> {
                    dayName = "Su"
                    // resourceProvider.getString(R.string.su)
                }
                1 -> {
                    dayName ="Mo"
                }
                2 -> {
                    dayName =  "Tu"
                }
                3 -> {
                    dayName =  "We"
                }
                4 -> {
                    dayName = "Th"
                }
                5 -> {
                    dayName =   "Fr"
                }
                6 -> {
                    dayName =   " Sa"
                }
            }
        }

        return dayName
    }
    fun dayWiseJourney(name:String,list:List<ModelStops>):List<ModelStops>{
        return list.filter { d->d.transportDay==name }
    }
    fun buildDayWiseData(dayNumber:Int,list:List<ModelStops>){
        viewModelScope.launch {
                val filterList = dayWiseJourney(getDayName(dayNumber), list)
                if (filterList.isNotEmpty()) {
                    val day = JSONObject()
                    day.put("day", getDayNumber(filterList[0].transportDay))
                    if (filterList[0].workingMode.isNotEmpty()) {
                        day.put("workFrom", filterList[0].workingMode)

                        day.put("transportType", filterList[0].transportMode)
                        day.put(
                            "journeyInfo",
                            if (filterList[0].workingMode.equals(
                                    resourceProvider.getString(R.string.office),
                                    true
                                )
                            ) dayWiseJourney(filterList) else JSONArray()
                        )
                        //day.put("journeyInfo",  dayWiseJourney(filterList))
                        dayData.put(day)
                        co2JsonData.put("dayWiseCommuteInfo", dayData)
                    }
                }
        }
    }
    fun dayWiseJourney(list:List<ModelStops>):JSONArray{
        val journeyData = JSONArray()
        for(data in list){
            val daySingle = JSONObject()
            if(data.transportMode.lowercase()=="single") {
                daySingle.put(
                    "transportMode",
                    if (list[0].transportMeans != null) list[0].transportMeans?.key else ""
                )
                daySingle.put("transportChildMode", if(list[0].transportDetails!=null)list[0].transportDetails?.key else "")
            }
            else {
                daySingle.put(
                    "transportMode",
                    if (data.transportMeans != null) data.transportMeans?.key else ""
                )
                daySingle.put("transportChildMode", if(data.transportDetails!=null)data.transportDetails?.key else "")
            }


            daySingle.put("sourceAddress", data.transportStartAddress)
            daySingle.put("sourcelatitude", data.transportStartAddressLatitude)
            daySingle.put("sourcelongitude", data.transportStartAddressLongitude)
            daySingle.put("destinationAddress", data.transportStopAddress)
            daySingle.put("destinationlatitude", data.transportStopAddressLatitude)
            daySingle.put("destinationlongitude", data.transportStopAddressLongitude)
            daySingle.put("isShared", data.isShared)
            daySingle.put("noOfPeople", if(data.numberOfPeople.isNotEmpty()) data.numberOfPeople.toInt() else 0)
            journeyData.put(daySingle)
        }
        Log.e("PRINT DATA===",journeyData.toString())
        return journeyData
    }


    fun callGetStoredUserSurveyListApi(surveyId:Int) {

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