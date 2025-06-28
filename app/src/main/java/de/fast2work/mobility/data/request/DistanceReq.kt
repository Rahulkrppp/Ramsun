package de.fast2work.mobility.data.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DistanceReq (
    @SerializedName("fromAddress")
    var fromAddress : String = "",

    @SerializedName("toAddress")
    var toAddress : String = "",
) : Parcelable