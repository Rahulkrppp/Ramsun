package de.fast2work.mobility.data.request

import com.google.gson.annotations.SerializedName

data class ContactUsReq (@SerializedName("subject")
                         var subject : String = "",
                         @SerializedName("message")
                         var message : String = "",
                         @SerializedName("deviceType")
                         val deviceType : String = "android",
                         @SerializedName("deviceId")
                         var deviceId: String = ""
    )