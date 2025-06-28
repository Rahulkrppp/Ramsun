package de.fast2work.mobility.data.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DayDataReq(
    @SerializedName("day")
    var day:Int=0,
    @SerializedName("inOffice")
  var inOffice:String="",
    @SerializedName("transportMode")
    var transportMode:String="",
    @SerializedName("transportType")
    var transportType:String="",
    @SerializedName("isWfh")
    var isWfh:String="",

) : Parcelable
