package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class InvoiceDataList(
    @field:SerializedName("data")
    val data: ArrayList<InvoiceApiResponse> = arrayListOf(),
    @field:SerializedName("totalCount")
    val totalCount: Int = 0,
    @field:SerializedName("totalPages")
    var totalPages: Int = 0,
    @field:SerializedName("currentPage")
    val currentPage: Int = 0,
) : Parcelable
