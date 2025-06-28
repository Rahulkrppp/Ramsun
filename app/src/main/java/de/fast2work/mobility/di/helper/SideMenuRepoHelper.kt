package de.fast2work.mobility.di.helper

import com.google.gson.JsonElement
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.ContactUsReq
import de.fast2work.mobility.data.request.NotificationDeleteReq
import de.fast2work.mobility.data.request.NotificationReadUnReadReq
import de.fast2work.mobility.data.response.Notification
import de.fast2work.mobility.data.response.NotificationReadUnread
import de.fast2work.mobility.data.response.StaticPage

interface SideMenuRepoHelper {
    suspend fun callStaticPageApi(pageIndex: Int, onResult: (response: WSObjectResponse<StaticPage>) -> Unit, onFailure: (message: String) -> Unit)
    suspend fun callContactUsdApi(model: ContactUsReq, onResult: (response: WSObjectResponse<JsonElement>) -> Unit, onFailure: (message: String) -> Unit)
    suspend fun callNotificationListApi( onResult: (response: WSObjectResponse<Notification>) -> Unit, onFailure: (message: String) -> Unit)
    suspend fun callNotificationMarkAllReadApi( onResult: (response: WSObjectResponse<JsonElement>) -> Unit, onFailure: (message: String) -> Unit)


    suspend fun callDeleteNotificationApi(
        model: NotificationDeleteReq,
        onResult: (response: WSObjectResponse<Int>) -> Unit,
        onFailure: (message: String) -> Unit
    )

    suspend fun callNotificationReadUnReadApi(
        notificationId: Int?,
        model: NotificationReadUnReadReq,
        onResult: (response: WSObjectResponse<NotificationReadUnread>) -> Unit,
        onFailure: (message: String) -> Unit,
    )

    suspend fun callNotificationCountApi(
        onResult: (response: WSObjectResponse<Notification>) -> Unit,
        onFailure: (message: String) -> Unit
    )
}