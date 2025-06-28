package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LastTwoYearMonthRes(
    @SerializedName("month") var month: Int? = null,
    @SerializedName("month_name") var monthName: String? = null,
    @SerializedName("co2_value") var co2Value: Double? = null,
    @SerializedName("last_year_co2_value") var lastYearCo2Value: Double? = null,
    @SerializedName("current_year") var currentYear: Int? = null,
    @SerializedName("last_year") var lastYear: Int? = null,
) : Parcelable
