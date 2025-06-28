package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Co2VehicleWiseEmissions(
    @SerializedName("vehicle_type") var vehicleType: String? = null,
    @SerializedName("vehicle_sub_type") var vehicleSubType: String? = null,
    @SerializedName("vehicle_type_label") var vehicleTypeLabel: String? = null,
    @SerializedName("co2_value") var co2Value: Double? = null,
    @SerializedName("color_code") var colorCode: String? = null
) : Parcelable

