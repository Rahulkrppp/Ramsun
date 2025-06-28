package de.fast2work.mobility.ui.savingandrecpit.co2saving

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.remote.cit.WSListResponse
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.response.SurveyDataList
import de.fast2work.mobility.data.response.SurveyResp
import de.fast2work.mobility.data.response.SurveyStatisticsResp
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.ui.dashboard.DashBoardRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyCo2SavingsViewModel  @Inject constructor(private val repository: DashBoardRepository):BaseViewModel() {

    var surveyListLiveData = MutableLiveData<WSObjectResponse<SurveyDataList>>()
    var inProgressSurveyListLiveData = MutableLiveData<WSObjectResponse<SurveyResp>>()
    var getSurveyStatisticsListLiveData = MutableLiveData<WSListResponse<SurveyStatisticsResp>>()
    var getSurveyStatusByUserId = MutableLiveData<WSListResponse<SurveyResp>>()

    var hasLoadedAllData = false
    var currentPage = 1
    var isLoaded = false
    var surveyList:ArrayList<SurveyResp> = arrayListOf()
    var surveyId=0
    /**
     * This function contains code for callGetUserSurveyApi api call
     *
     */
    fun callGetUserSurveyApi(pageNo:Int,limit:Int) {
        viewModelScope.launch {
            invalidateLoading(true)
            repository.callGetUserSurveyApi(pageNo,limit, onResult = {
                invalidateLoading(false)
                surveyListLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }


    /**
     * This function contains code for callInProgressSurveyApi api call
     *
     */
    fun callInProgressSurveyApi() {
        viewModelScope.launch {
            invalidateLoading(true)
            repository.callInProgressSurveyApi( onResult = {
                invalidateLoading(false)
                inProgressSurveyListLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

    /**
     * This function contains code for callGetUserSurveyApi api call
     *
     */
    fun callGetSurveyStatisticsByUserIdApi() {
        viewModelScope.launch {
            invalidateLoading(true)
            repository.callGetSurveyStatisticsByUserIdApi( onResult = {
                invalidateLoading(false)
                getSurveyStatisticsListLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

    /**
     * This function contains code for callGetSurveyStatusByUserId api call
     *
     */
    fun callGetSurveyStatusByUserId(surveyId:Int) {
        viewModelScope.launch {
            invalidateLoading(false)
            repository.callGetSurveyStatusByUserId(surveyId,onResult = {
                invalidateLoading(false)
                getSurveyStatusByUserId.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }
}