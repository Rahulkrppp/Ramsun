package de.fast2work.mobility.data.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class InvoiceFuelTypeReq (@SerializedName("fuelType")
                          var fuelType : String = "",
                               @SerializedName("invoiceId")
                               var invoiceid : Int? = 0,

                          @SerializedName("fuelQuantity")
                          var fuelQuantity : Double = 0.0) : Parcelable