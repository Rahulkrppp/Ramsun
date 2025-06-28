package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GeocodeNg(
   /* @field:SerializedName("ocd")
    val ocd: ArrayList<Ocd> = arrayListOf(),*/

    @field:SerializedName("results")
    val results: ArrayList<SourceDestination> = arrayListOf(),

    @field:SerializedName("state")
    val state: Int
) : Parcelable