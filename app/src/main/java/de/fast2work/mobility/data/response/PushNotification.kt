package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PushNotification(

    @SerializedName("title")
    var title: String = "",

    @SerializedName("body")
    var body: String = "",

    @SerializedName("refType")
    var refType: String? = "",

    @SerializedName("refId")
    val refId: String? = "",

    val couponId: String = "",
    val orderId: String = "",
    val subscriptionExpiredAt: String = "",
    val subscriptionId: String = "",
    val ticketId: String = "",
    var status: String = "",
    var eligibleForBuy: Boolean = false,

) : Parcelable


