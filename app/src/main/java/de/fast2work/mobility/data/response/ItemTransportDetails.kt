package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemTransportDetails(
    @field:SerializedName("key")
    var key: String,
    @field:SerializedName("label")
    var label: String,
    var isSelected:Boolean=false
) : Parcelable