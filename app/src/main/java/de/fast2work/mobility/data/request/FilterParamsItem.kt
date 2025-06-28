package de.fast2work.mobility.data.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterParamsItem(

    @field:SerializedName("value")
    var value: String? = null,

    @field:SerializedName("key")
    var key: String? = null,

    @field:SerializedName("operator")
    var operator: String? = null
) : Parcelable