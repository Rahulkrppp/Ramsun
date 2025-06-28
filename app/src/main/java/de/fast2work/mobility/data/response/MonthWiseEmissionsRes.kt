package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MonthWiseEmissionsRes (
    @SerializedName("month") var month: Int? = null,
    @SerializedName("month_name") var monthName: String? = null,
    @SerializedName("co2_value") var co2Value: Double? = null,
) : Parcelable
