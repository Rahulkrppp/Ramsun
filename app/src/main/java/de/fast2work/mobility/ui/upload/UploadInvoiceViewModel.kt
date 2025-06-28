package de.fast2work.mobility.ui.upload

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.model.AddInvoiceModel
import de.fast2work.mobility.data.remote.cit.WSListResponse
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.response.AddInvoice
import de.fast2work.mobility.data.response.CardData
import de.fast2work.mobility.data.response.SelectCategoryDataItem
import de.fast2work.mobility.data.response.User
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.ui.dashboard.DashBoardRepository
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.preference.EasyPref
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UploadInvoiceViewModel @Inject constructor(private val repository: DashBoardRepository) : BaseViewModel() {

     var categoryCategorySubCategoryListLiveData = MutableLiveData<WSListResponse<SelectCategoryDataItem>>()
     var addInvoiceListLiveData = MutableLiveData<WSObjectResponse<AddInvoice>>()
     var cardListLiveData = MutableLiveData<WSObjectResponse<CardData>>()


    val userData = BaseApplication.sharedPreference.getPrefModel(EasyPref.USER_DATA, User::class.java)
    var selectCategoryDataItemArraylist:ArrayList<SelectCategoryDataItem> = arrayListOf()
    var addInvoice : AddInvoiceModel= AddInvoiceModel()
    var isFirstTimeLoad:Boolean=true

    var currentTabPosition: Int = 0


    /**
     * This function contains code for callCategoryCategorySubCategoryListApi api call
     *
     */
    fun callCategoryCategorySubCategoryListApi() {
        viewModelScope.launch {

            invalidateLoading(true)
            repository.callCategoryCategorySubCategoryListApi(userData?.userId.toBlankString(), onResult = {
                    invalidateLoading(false)
                categoryCategorySubCategoryListLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

    /**
     * This function contains code for callAddInvoiceApi api call
     *

     */
    fun callAddInvoiceApi(params: HashMap<String, RequestBody>, file: MultipartBody.Part?,fileList: ArrayList<MultipartBody.Part?>,isLastStep: Boolean = false) {
        viewModelScope.launch {

            invalidateLoading(true)
            repository.callAddInvoiceApi(params,file,fileList ,onResult = {
                invalidateLoading(false)
                addInvoiceListLiveData.postValue(it)

            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

    fun callCardMasterApi() {
        viewModelScope.launch {
            invalidateLoading(true)
            repository.callCardMasterApi(onResult = {
                invalidateLoading(false)
                cardListLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }
}