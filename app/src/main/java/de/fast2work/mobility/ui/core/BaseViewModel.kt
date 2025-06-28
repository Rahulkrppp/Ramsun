package de.fast2work.mobility.ui.core

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.fast2work.mobility.data.FormValidation
import de.fast2work.mobility.utility.helper.ConnectionLiveData

/**
 * This class contains code for our base view model
 *
 */
abstract class BaseViewModel : ViewModel() {
    val validationLiveData: MutableLiveData<ArrayList<FormValidation>> = MutableLiveData()
    val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val errorLiveData: MutableLiveData<String> = MutableLiveData()
    val networkLiveData = ConnectionLiveData(BaseApplication.application.applicationContext)

    fun postValidation(validations: ArrayList<FormValidation>) {
        validationLiveData.postValue(validations)
    }

    fun invalidateLoading(show: Boolean) {
        loadingLiveData.postValue(show)
    }
}
