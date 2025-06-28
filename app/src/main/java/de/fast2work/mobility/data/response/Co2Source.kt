package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Co2Source(
    @field:SerializedName("data")
    val data: DataSource? =null,
    @field:SerializedName("notifications")
    val notifications: ArrayList<String> = arrayListOf()
) : Parcelable