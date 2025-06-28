package de.fast2work.mobility.data.request

import com.google.gson.annotations.SerializedName

data class VerifyOtpReq(
    @SerializedName("email")
    var email : String = "",
    @SerializedName("otp")
    var otp : String = "",
    @SerializedName("deviceType")
    val deviceType : String = "android",
    @SerializedName("deviceId")
    var deviceId: String = ""
)