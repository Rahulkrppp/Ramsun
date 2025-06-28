package de.fast2work.mobility.ui.sidemenu

import com.google.gson.JsonElement
import de.fast2work.mobility.data.remote.ApiService
import de.fast2work.mobility.data.remote.cit.HBBaseResponse
import de.fast2work.mobility.data.remote.cit.HBSuccessCallback
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.ContactUsReq
import de.fast2work.mobility.data.request.NotificationDeleteReq
import de.fast2work.mobility.data.request.NotificationReadUnReadReq
import de.fast2work.mobility.data.response.Notification
import de.fast2work.mobility.data.response.NotificationReadUnread
import de.fast2work.mobility.data.response.StaticPage
import de.fast2work.mobility.data.response.User
import de.fast2work.mobility.di.helper.DashBoardRepoHelper
import de.fast2work.mobility.di.helper.SideMenuRepoHelper
import de.fast2work.mobility.ui.core.BaseRepository
import de.fast2work.mobility.utility.extension.toBlankString
import javax.inject.Inject

class SideMenuRepository @Inject constructor(val apiService: ApiService) : BaseRepository(), SideMenuRepoHelper {
    override suspend fun callStaticPageApi(pageIndex: Int, onResult: (response: WSObjectResponse<StaticPage>) -> Unit, onFailure: (message: String) -> Unit) {
        try {
            safeApiCall(apiService.callStaticPageApi(pageIndex), object : HBSuccessCallback<WSObjectResponse<StaticPage>> {
                override fun onSuccess(response: WSObjectResponse<StaticPage>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }

                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callContactUsdApi(model: ContactUsReq, onResult: (response: WSObjectResponse<JsonElement>) -> Unit, onFailure: (message: String) -> Unit) {
        try {
            safeApiCall(apiService.callContactUsdApi(model), object : HBSuccessCallback<WSObjectResponse<JsonElement>> {
                override fun onSuccess(response: WSObjectResponse<JsonElement>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }

                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callNotificationListApi(onResult: (response: WSObjectResponse<Notification>) -> Unit, onFailure: (message: String) -> Unit) {
        try {
            safeApiCall(apiService.callNotificationListApi(), object : HBSuccessCallback<WSObjectResponse<Notification>> {
                override fun onSuccess(response: WSObjectResponse<Notification>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }

                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callNotificationMarkAllReadApi(onResult: (response: WSObjectResponse<JsonElement>) -> Unit, onFailure: (message: String) -> Unit) {
        try {
            safeApiCall(apiService.callNotificationMarkAllReadApi(), object : HBSuccessCallback<WSObjectResponse<JsonElement>> {
                override fun onSuccess(response: WSObjectResponse<JsonElement>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }

                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }


    override suspend fun callDeleteNotificationApi(
        model: NotificationDeleteReq,
        onResult: (response: WSObjectResponse<Int>) -> Unit,
        onFailure: (message: String) -> Unit,
    ) {
        try {
            safeApiCall(apiService.callDeleteNotificationApi(model), object : HBSuccessCallback<WSObjectResponse<Int>> {
                override fun onSuccess(response: WSObjectResponse<Int>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }

                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }


    override suspend fun callNotificationReadUnReadApi(
        notificationId: Int?,
        model: NotificationReadUnReadReq,
        onResult: (response: WSObjectResponse<NotificationReadUnread>) -> Unit,
        onFailure: (message: String) -> Unit,
    ) {
        try {
            safeApiCall(apiService.callNotificationReadUnReadApi(notificationId,model), object :
                HBSuccessCallback<WSObjectResponse<NotificationReadUnread>> {
                override fun onSuccess(response: WSObjectResponse<NotificationReadUnread>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }

                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callNotificationCountApi(
        onResult: (response: WSObjectResponse<Notification>) -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        try {
            safeApiCall(apiService.callNotificationCountApi(), object : HBSuccessCallback<WSObjectResponse<Notification>> {
                override fun onSuccess(response: WSObjectResponse<Notification>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }

                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

}