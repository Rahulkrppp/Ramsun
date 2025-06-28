package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SurveyStatisticsResp(
    @field:SerializedName("paramType")
    val paramType: String="",
    @field:SerializedName("paramUnit")
    val paramUnit: String="",
    @field:SerializedName("weeklyTotal")
    val weeklyTotal: String="0.0",
    @field:SerializedName("yearlyTotal")
    val yearlyTotal: Double?=null,
) : Parcelable
