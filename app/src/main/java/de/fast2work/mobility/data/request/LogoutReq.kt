package de.fast2work.mobility.data.request

import com.google.gson.annotations.SerializedName

data class LogoutReq(
    @SerializedName("deviceType")
    val deviceType : String = "android",
    @SerializedName("deviceId")
    var deviceId: String = ""
)