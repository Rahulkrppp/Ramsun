package de.fast2work.mobility.data.request

import com.google.gson.annotations.SerializedName

data class NotificationReadUnReadReq(
    @SerializedName("isRead")
    var isRead : String = "0"
)