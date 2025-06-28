package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UniqueVehiclesRes(
    @field:SerializedName("vehicle_type")
    val vehicleType: String? = "",
    @field:SerializedName("vehicle_sub_type")
    val vehicleSubType: String? = "",
    @field:SerializedName("vehicle_type_label")
    val vehicleTypeLabel: String? = "",
    @field:SerializedName("icon_url")
    val iconUrl: String? = "",
    @field:SerializedName("color_code")
    val colorCode: String? = "",
) : Parcelable
