package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class KmsVehicleTypeRes(
    @SerializedName("vehicle_type") var vehicleType: String? = null,
    @SerializedName("vehicle_sub_type") var vehicleSubType: String? = null,
    @SerializedName("vehicle_type_label") var vehicleTypeLabel: String? = null,
    @SerializedName("km_value") var kmValue: Double? = null,
    @SerializedName("color_code") var colorCode: String? = null
) : Parcelable
