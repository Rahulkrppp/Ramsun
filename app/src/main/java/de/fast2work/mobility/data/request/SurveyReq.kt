package de.fast2work.mobility.data.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SurveyReq(
    @field:SerializedName("pageNo")
    var pageNo: Int? = null,
    @field:SerializedName("limit")
    var limit: Int? = null,
) : Parcelable
