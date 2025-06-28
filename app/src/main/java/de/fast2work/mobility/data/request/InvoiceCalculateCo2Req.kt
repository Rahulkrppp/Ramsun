package de.fast2work.mobility.data.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class InvoiceCalculateCo2Req (
    @SerializedName("invoiceId")
    var invoiceid : Int? = 0,

    @SerializedName("vehicleType")
    var vehicleType : String = "",

    @SerializedName("vehicleSubType")
    var vehicleSubType : String = "",

    @SerializedName("distanceInKm")
    var distanceInKm : Double = 0.0,

    @SerializedName("distanceDataType")
    var distanceDataType : String = "",

    @SerializedName("fromAddress")
    var fromAddress : String = "",
    @SerializedName("fromLatitude") var fromLatitude: String = "",
    @SerializedName("fromLongitude") var fromLongitude: String = "",
    @SerializedName("toAddress") var toAddress: String = "",
    @SerializedName("toLatitude") var toLatitude: String = "",
    @SerializedName("toLongitude") var toLongitude: String = "",

) : Parcelable