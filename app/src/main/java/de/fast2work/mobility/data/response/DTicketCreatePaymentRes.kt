package de.fast2work.mobility.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DTicketCreatePaymentRes(
    val order_reference_no: String = "",
    val payment_intent_id: Int = 0
) : Parcelable