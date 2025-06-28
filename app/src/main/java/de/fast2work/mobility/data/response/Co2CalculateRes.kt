package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Co2CalculateRes  (
    @field:SerializedName("co2_kg") val co2Kg: Double? = null,

) : Parcelable