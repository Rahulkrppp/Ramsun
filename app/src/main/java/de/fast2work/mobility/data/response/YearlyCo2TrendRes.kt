package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class YearlyCo2TrendRes (
    @SerializedName("vehicle_type") var vehicleType: String? = null,
    @SerializedName("vehicle_sub_type") var vehicleSubType: String? = null,
    @SerializedName("vehicle_type_label") var vehicleTypeLabel: String? = null,
    @SerializedName("total_co2_value") var totalCo2Value: Double? = null,
    @SerializedName("color_code") var colorCode: String? = null,
    @SerializedName("icon_url") var iconUrl: String? = null,
    @SerializedName("month_wise_emissions") var monthWiseEmissions: ArrayList<MonthWiseEmissionsRes> = arrayListOf()
    ) : Parcelable