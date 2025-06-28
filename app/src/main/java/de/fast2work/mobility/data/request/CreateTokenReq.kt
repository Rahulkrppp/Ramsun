package de.fast2work.mobility.data.request

import com.google.gson.annotations.SerializedName

data class CreateTokenReq(
    @SerializedName("fcmToken")
    var fcmToken : String = "",
    @SerializedName("deviceType")
    val deviceType : String = "android",
    @SerializedName("deviceId")
    var deviceId: String = ""
)