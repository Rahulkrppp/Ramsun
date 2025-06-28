package de.fast2work.mobility.data.request

import com.google.gson.annotations.SerializedName

data class NotificationDeleteReq(
    @SerializedName("notificationIds")
    var notificationIds : ArrayList<Int> = arrayListOf(),
    @SerializedName("deviceType")
    val deviceType : String = "android",
    @SerializedName("deviceId")
    var deviceId: String = ""
)