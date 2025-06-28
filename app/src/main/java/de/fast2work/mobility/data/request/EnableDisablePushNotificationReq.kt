package de.fast2work.mobility.data.request

import com.google.gson.annotations.SerializedName

data class EnableDisablePushNotificationReq(
    @SerializedName("enableNotification")
    var enableNotification : String = "",
)