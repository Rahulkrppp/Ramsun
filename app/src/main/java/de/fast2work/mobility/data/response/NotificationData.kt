package de.fast2work.mobility.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class NotificationData(

	@field:SerializedName("refType")
	val refType: String? = "",

	@field:SerializedName("refId")
	val refId: String? = "",

) : Parcelable{
	companion object {
		const val INVOICE_DETAIL = "invoice"
		const val UPLOAD_INVOICE = "invoice_upload_reminder"
		const val INVOICE_CO2_REQUIRED = "invoice_co2_required"
		const val D_TICKET = "dticket"

	}
}
