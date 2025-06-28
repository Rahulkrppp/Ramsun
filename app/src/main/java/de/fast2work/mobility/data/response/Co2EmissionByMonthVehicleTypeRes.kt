package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Co2EmissionByMonthVehicleTypeRes(
    @SerializedName("month") var month: Int? = null,
    @SerializedName("month_name") var monthName: String? = null,
    @SerializedName("total_co2_value") var totalCo2Value: Double? = null,
    @SerializedName("vehicle_wise_emissions") var vehicleWiseEmissions: ArrayList<Co2VehicleWiseEmissions>? = null,
) : Parcelable
