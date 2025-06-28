package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LastTwoYearVehicleTypeRes(
    @SerializedName("vehicle_type") var vehicleType: String? = null,
    @SerializedName("vehicle_sub_type") var vehicleSubType: String? = null,
    @SerializedName("vehicle_type_label") var vehicleTypeLabel: String? = null,
    @SerializedName("co2_value") var co2Value: Double? = null,
    @SerializedName("last_year_co2_value") var lastYearCo2Value: Double? = null,
    @SerializedName("current_year") var currentYear: Int? = null,
    @SerializedName("last_year") var lastYear: Int? = null,
) : Parcelable
