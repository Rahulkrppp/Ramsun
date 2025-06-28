package de.fast2work.mobility.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.*
import de.fast2work.mobility.R
import de.fast2work.mobility.data.eventbus.UpdateNotificationCount
import de.fast2work.mobility.data.eventbus.UpdateTheme
import de.fast2work.mobility.data.model.NotificationDTicketData
import de.fast2work.mobility.data.remote.ApiService
import de.fast2work.mobility.data.remote.cit.HBBaseResponse
import de.fast2work.mobility.data.remote.cit.HBSuccessCallback
import de.fast2work.mobility.data.remote.cit.RetrofitCallback
import de.fast2work.mobility.data.response.ActiveTicket
import de.fast2work.mobility.data.response.NotificationData
import de.fast2work.mobility.data.response.PushNotification
import de.fast2work.mobility.ui.authentication.SplashActivity
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.NoInternetActivity
import de.fast2work.mobility.ui.dashboard.DashboardActivity
import de.fast2work.mobility.utility.extension.isLoggedIn
import de.fast2work.mobility.utility.extension.isNetworkAvailable
import de.fast2work.mobility.utility.preference.EasyPref
import de.fast2work.mobility.utility.preference.EasyPref.Companion.FCM_KEY
import de.fast2work.mobility.utility.util.IConstants.Companion.BUNDLE_PUSH_NOTIFICATION
import de.fast2work.mobility.utility.util.IConstantsIcon
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject
import kotlin.random.Random

/**
 * This class is for receiving push notifications from firebase and creating building the notification to show to the user as per the
 * requirement
 *
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private val TAG = "MyFirebaseMsgService"
        private const val channelId = "mobility.app.notifications"
        private var notificationId = 10000
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("=====","fcm token:: $token")
//        BaseApplication.sharedPreference.setPref(USER_FCM_KEY_REGISTERED, false)
        BaseApplication.sharedPreference.setPref(FCM_KEY, token)
//        if (isLoggedIn()) {
//            updateTokenOnServer(token)
//        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        printMessage(remoteMessage)
        Log.e("=====","remoteMessage:: ${remoteMessage.data}")

        val pushNotification = buildNotificationModel(remoteMessage)
//        if(!BaseApplication.isAppInForeground /*|| pushNotification.notificationType.equals(IConstants.PUSH_NOTIFICATION_TYPE_MESSAGE)*/)
        if (pushNotification.refType == NotificationData.INVOICE_DETAIL || pushNotification.refType == NotificationData.UPLOAD_INVOICE || pushNotification.refType == NotificationData.INVOICE_CO2_REQUIRED || pushNotification.refType == NotificationData.D_TICKET) {
            if(BaseApplication.isAppInForeground) {
                showNotificationInTray(applicationContext, pushNotification, Intent(this, DashboardActivity::class.java).putExtra(BUNDLE_PUSH_NOTIFICATION, pushNotification).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))

            }else showNotificationInTray(applicationContext, pushNotification, Intent(this, SplashActivity::class.java).putExtra(BUNDLE_PUSH_NOTIFICATION, pushNotification))
        }else{
            EventBus.getDefault().post(UpdateTheme(pushNotification.refId))
        }
        val activityTicket= ActiveTicket()
        activityTicket.ticketId= pushNotification.ticketId
        activityTicket.couponId= pushNotification.couponId
        activityTicket.orderId= pushNotification.orderId
        activityTicket.subscriptionId= pushNotification.subscriptionId
        activityTicket.subscriptionExpiredAt= pushNotification.subscriptionExpiredAt
        BaseApplication.sharedPreference.setPref(EasyPref.activeTicket, activityTicket)
        BaseApplication.sharedPreference.setPref(EasyPref.D_TICKET, IConstantsIcon.ACTIVATED)
        EventBus.getDefault().post(NotificationDTicketData(pushNotification.refType,pushNotification.ticketId))
//        EventBus.getDefault().post(pushNotification)
        EventBus.getDefault().post(UpdateNotificationCount(BaseApplication.notificationCount.value?.toInt()?.plus(1)))

//        if(BaseApplication.isAppInForeground) {
//            EventBus.getDefault().post(UpdateNotificationCount())
//        }else {
//            if (BaseApplication.isUnreadNotificationAvailable) {
//                EventBus.getDefault().post(UpdateNotificationCount())
//            }
//        }
    }

    //{"content-available":"1","notification_code":"FACILITY_MAKE_VERIFIED","notification_count":"0","msgcnt":"0","facility_id":"6478","id":"b5a0c819",
    // "body":"Your facility, Gdhd , has been verified!","type":"Facility","badge":"0","title":"Facility Verified","message":"Your facility, Gdhd , has been verified!"}
    @SuppressLint("LogNotTimber")
    private fun printMessage(remoteMessage: RemoteMessage) {
        Log.e(TAG, "onMessageReceived() called with: remoteMessage = [$remoteMessage]")
        Log.e(TAG, "MessageId = [" + remoteMessage.messageId + "]")
        Log.e(TAG, "From = [" + remoteMessage.from + "]")
        Log.e(TAG, "DATA")
        val d = remoteMessage.data
        val set = d.keys
        val iterator = set.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            val value = d[key]
            Log.e(TAG, "$key = [$value]")
        }
    }

    private fun showNotificationInTray(context: Context, pushNotification: PushNotification, mainIntent: Intent) {
        val notificationBuilder: NotificationCompat.Builder = getNotificationBuilder(pushNotification.title, pushNotification.body)
        notificationId = Random.nextInt()
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, notificationId, mainIntent, PendingIntent.FLAG_IMMUTABLE)
        notificationBuilder.setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "General Notification",
                NotificationManager.IMPORTANCE_HIGH)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            val att = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).setContentType(
                AudioAttributes.CONTENT_TYPE_SPEECH).build()
            channel.setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI, att)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
        notificationId++
    }

    private fun getNotificationBuilder(title: String?, body: String?, messageTitle: String? = ""): NotificationCompat.Builder {

        val builder = NotificationCompat.Builder(BaseApplication.application.applicationContext, channelId)
        if (messageTitle != null && messageTitle.isNotEmpty()) {
            builder.setContentTitle(messageTitle)
        } else {
            builder.setContentTitle(title)
        }
        builder.setContentText(body)
        builder.setStyle(NotificationCompat.BigTextStyle().bigText(body))
        builder.setSmallIcon(R.drawable.ic_notification)
        builder.setAutoCancel(true)
        builder.setTicker(body)
        builder.setDefaults(Notification.DEFAULT_LIGHTS and Notification.DEFAULT_SOUND)
        builder.setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
        builder.setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))

        return builder
    }

    /**
     * This method is for building notifications generated from the backend(Knit) for example: Connection request received
     */
    @SuppressLint("LogNotTimber")
    private fun buildNotificationModel(remoteMessage: RemoteMessage): PushNotification {
        var pushNotification = PushNotification()
        try {
            val d = remoteMessage.data
            val jsonObject = JSONObject(d as Map<*, *>)
            Log.e("Notification", jsonObject.toString())
            pushNotification = Gson().fromJson(jsonObject.toString(), PushNotification::class.java)

            if (remoteMessage.notification != null) {
                pushNotification.title = remoteMessage.notification!!.title!!
                pushNotification.body = remoteMessage.notification!!.body!!
            }else{
                if (d.containsKey("message")) {
                    pushNotification.body = d["message"]!!
                }
                if (d.containsKey("title")) {
                    pushNotification.title = d["title"]!!
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


//        if (remoteMessage.notification != null) {
//            val d = remoteMessage.data
//            val jsonObject = JSONObject(d as Map<*, *>)
//            Log.e("Notification", jsonObject.toString())
//            pushNotification = Gson().fromJson(jsonObject.toString(), PushNotification::class.java)
//            if (d.containsKey("others")) {
//                var other = JsonParser.parseString(jsonObject.get("others") as String)
//
//                if (other is JsonArray) {
//                    other = other.get(0) as JsonObject
//                }
//                if (other is JsonObject) {
//                    pushNotification = Gson().fromJson(other.toString(), PushNotification::class.java)
//                }
//            }
//            pushNotification.title = remoteMessage.notification!!.title!!
//            pushNotification.message = remoteMessage.notification!!.body!!
//
//        } else {
//            try {
//                val d = remoteMessage.data
//                val jsonData = JSONObject(d as Map<*, *>)
//                Log.e("Notification", jsonData.toString())
//                pushNotification = Gson().fromJson(jsonData.toString(), PushNotification::class.java)
//                if (d.containsKey("others")) {
//
//                    var other = JsonParser.parseString(jsonData.get("others") as String)
//
//                    if (other is JsonArray) {
//                        other = other.get(0) as JsonObject
//                    }
//                    if (other is JsonObject) {
//                        pushNotification = Gson().fromJson(other.toString(), PushNotification::class.java)
//                    }
//                }
//                if (d.containsKey("message")) {
//                    pushNotification.message = d["message"]!!
//                }
//                if (d.containsKey("title")) {
//                    pushNotification.title = d["title"]!!
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }

        return pushNotification
    }

    /**
     *
     * This method is for updating the notification on the server whenever the FCM token is refreshed we send it to the api
     */
    private fun updateTokenOnServer(token: String) {
//        val model = TokenUpdateReq().apply {
//            this.token = token
//        }
//
//        GlobalScope.launch(Dispatchers.IO) {
//
//            /**
//             * Update FCM token in twilio
//             * */
//            val response = ApiClient.apiService.callUpdateToken(model.toFieldStringBodyMap())
//            if (conversationRepository.isClientCreated) {
//                conversationRepository.getConversationsClient().registerFCMToken(token)
//            }
//            callApi(response, object : HBSuccessCallback<WSGenericResponse<JsonElement>> {
//                override fun onSuccess(response: WSGenericResponse<JsonElement>) {
//                    MainApplication.sharedPreference.setPref(USER_FCM_KEY_REGISTERED, true)
//                }
//
//                override fun onFailure(code: String?, message: String?) {
//
//                }
//            })
//        }

    }

    /**
     * This method is to check whether internet is available or not
     */
    private fun validateNetwork(onNetworkConnected: () -> Unit) {
        if (applicationContext.isNetworkAvailable()) {
            onNetworkConnected()
        } else {
            ContextCompat.startActivity(applicationContext, NoInternetActivity.createIntent(applicationContext), null)
        }
    }

//    suspend fun <T, SC : RetrofitCallback<T>> callApi(response: Response<T>, successCallback: SC?) {
//        withContext(Dispatchers.IO) {
//            validateNetwork {
//                try {
//                    val body = response.body()
//
//                    if (body != null) {
//                        if (body is HBBaseResponse && successCallback != null) {
//                            val settings: Settings = (body as HBBaseResponse).settings!!
//                            when (settings.success) {
//                                Settings.SUCCESS -> successCallback.onSuccess(body)
//                                Settings.INVALID_USER -> successCallback.onSuccess(body)
//                                else -> (successCallback as HBSuccessCallback<*>).onFailure(settings.success, settings.message)
//                            }
//                        } else if (body is JsonElement && successCallback != null) {
//                            successCallback.onSuccess(body)
//                        }
//                    } else {
//                        if (successCallback != null && successCallback is HBSuccessCallback<*>) {
//                            successCallback.onFailure(response.code().toString(), response.message().toString())
//                        }
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    successCallback?.onFailure("", applicationContext.getString(R.string.alert_error_something_went_wrong))
//                }
//            }
//        }
//    }

    suspend fun <T, SC : RetrofitCallback<T>> safeApiCall(result: Response<T>, retrofitCallback: SC) {
        withContext(Dispatchers.IO) {
            validateNetwork {
                try {
                    val body = result.body()
                    if (body != null) {
                        when (body) {
                            is HBBaseResponse -> {
                                when ((body as HBBaseResponse).responseCode) {
                                    HBBaseResponse.SUCCESS -> retrofitCallback.onSuccess(body)
                                    HBBaseResponse.ERROR -> retrofitCallback.onFailure(
                                        (body as HBBaseResponse).responseCode, (body as HBBaseResponse).responseMessage)
                                    else -> (retrofitCallback as HBSuccessCallback<*>).onFailure(
                                        (body as HBBaseResponse).responseCode, (body as HBBaseResponse).responseMessage)
                                }
                            }
                            is JsonElement -> {
                                retrofitCallback.onSuccess(body)
                            }
                            else -> {
                                retrofitCallback.onFailure(result.code(), result.message())
                            }
                        }
                    } else {
                        if (retrofitCallback is HBSuccessCallback<*>) {
                            if (result.errorBody() != null) {
                                val responseModel = Gson().fromJson(result.errorBody()!!.string(), HBBaseResponse::class.java)
                                retrofitCallback.onFailure(result.code(), responseModel.responseMessage)
                            }else {
                                retrofitCallback.onFailure(result.code(), applicationContext.getString(R.string.something_went_wrong))
                            }
                        }
                    }
                } catch (e: Exception) {
                    if (!e.message.isNullOrEmpty()) {
                        retrofitCallback.onFailure(0, e.message)
                    }
                    else {
                        retrofitCallback.onFailure(0, e.message)
                    }
                }
            }
        }
    }
}