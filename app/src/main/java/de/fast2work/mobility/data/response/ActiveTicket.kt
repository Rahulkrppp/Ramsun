package de.fast2work.mobility.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ActiveTicket(
    var couponId: String? = null,
    var orderId: String = "",
    var subscriptionExpiredAt: String = "",
    var subscriptionId: String = "",
    var ticketId: String? = null,
    val refId: String = "",
    val title: String = "",
    val refType: String = "",
    var status: String = "",
    var eligibleForBuy: Boolean = false,
    val body: String = ""
) : Parcelable
