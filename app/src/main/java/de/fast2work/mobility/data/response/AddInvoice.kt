package de.fast2work.mobility.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class AddInvoice(

	@field:SerializedName("invoiceId")
	val invoiceId: Int? = null
) : Parcelable
