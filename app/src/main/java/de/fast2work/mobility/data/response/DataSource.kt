package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataSource(
    @field:SerializedName("mbm_geocode")
    val geocode_ng: GeocodeNg? = null
) : Parcelable