package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FuelTypeRes(
    @field:SerializedName("code") val code: String = "",
    @field:SerializedName("label") val label: String = "",
    var isSelected:Boolean=false
    ) : Parcelable
