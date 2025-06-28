package de.fast2work.mobility.data.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Co2SurveryReq (

    @SerializedName("surveyId")
    var surveyId : Int = 0,

    @SerializedName("fromAddress")
    var fromAddress : String = "",

    @SerializedName("fromLatitude")
    var fromLatitude : String = "",

    @SerializedName("fromLongitude")
    var fromLongitude : String ="",
    @SerializedName("toAddress")
    var toAddress : String = "",
    @SerializedName("toLatitude")
    var toLatitude : String = "",

    @SerializedName("toLongitude")
    var toLongitude : String = "",
    @SerializedName("dayData")
    var dayData : ArrayList<DayDataReq> = arrayListOf(),

    ) : Parcelable