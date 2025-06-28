package de.fast2work.mobility.data.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChartReq(
    @field:SerializedName("year") var year:Int? = null,
    @field:SerializedName("travel_type") var travelType:String? = null,
    @field:SerializedName("vehicle_type") var vehicleType:String? = null
) : Parcelable
