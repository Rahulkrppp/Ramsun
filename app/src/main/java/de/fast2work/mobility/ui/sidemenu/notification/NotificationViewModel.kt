package de.fast2work.mobility.ui.sidemenu.notification

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.ContactUsReq
import de.fast2work.mobility.data.request.NotificationDeleteReq
import de.fast2work.mobility.data.request.NotificationReadUnReadReq
import de.fast2work.mobility.data.response.Notification
import de.fast2work.mobility.data.response.NotificationItem
import de.fast2work.mobility.data.response.NotificationReadUnread
import de.fast2work.mobility.data.response.PushNotification
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.ui.sidemenu.SideMenuRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(private val repository: SideMenuRepository) : BaseViewModel(){
    var notificationListApiLiveData = MutableLiveData<WSObjectResponse<Notification>>()
    var notificationMarkAllReadApiApiLiveData = MutableLiveData<WSObjectResponse<JsonElement>>()
    var notificationDeleteLiveData = MutableLiveData<WSObjectResponse<Int>>()
    var notificationReadUnreadLiveData = MutableLiveData<WSObjectResponse<NotificationReadUnread>>()

    var notificationArrayList:ArrayList<NotificationItem> = arrayListOf()
    var notificationData:NotificationItem = NotificationItem()
    var notificationCountListLiveData = MutableLiveData<WSObjectResponse<Notification>>()


    /**
     * This function contains code for callNotificationListApi api call
     *
     * @param email
     * @param password
     */
    fun callNotificationListApi() {
        viewModelScope.launch {

            invalidateLoading(true)
            repository.callNotificationListApi( onResult = {
                invalidateLoading(false)
                notificationListApiLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

    /**
     * This function contains code for callNotificationMarkAllReadApi api call
     *
     * @param email
     * @param password
     */
    fun callNotificationMarkAllReadApi() {
        viewModelScope.launch {

            invalidateLoading(true)
            repository.callNotificationMarkAllReadApi( onResult = {
                invalidateLoading(false)
                notificationMarkAllReadApiApiLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }


    fun callDeleteNotificationApi(notificationIdList :ArrayList<Int>, deviceId:String) {
        viewModelScope.launch {
            val params= NotificationDeleteReq().apply {
                this.notificationIds = notificationIdList
                this.deviceId = deviceId
            }
            invalidateLoading(false)
            repository.callDeleteNotificationApi(params,onResult = {
                invalidateLoading(false)
                notificationDeleteLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }


    fun callNotificationReadUnReadApi(notificationId: Int?, isRead: String) {
        viewModelScope.launch {
            val params= NotificationReadUnReadReq().apply {
                this.isRead = isRead
            }
            invalidateLoading(true)
            repository.callNotificationReadUnReadApi(notificationId,params,onResult = {
                invalidateLoading(false)
                notificationReadUnreadLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }

    /**
     * This function contains code for callNotificationCountApi api call
     *
     */
    fun callNotificationCountApi() {
        viewModelScope.launch {
            invalidateLoading(false)
            repository.callNotificationCountApi( onResult = {
                if (it.data?.data?.isEmpty() == true){
                    invalidateLoading(false)
                }
                notificationCountListLiveData.postValue(it)
            }, onFailure = {
                invalidateLoading(false)
                errorLiveData.postValue(it)
            })
        }
    }
}