package de.fast2work.mobility.data.response

import com.google.gson.annotations.SerializedName


data class InvoiceRefDocListRes(
    @SerializedName("url")
    val url: String? = "",
    @SerializedName("id")
    val id: String? = "",
)
