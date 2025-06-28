package de.fast2work.mobility.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SourceDestination(
    /*val addressline: String,
    val callback: String,
    val city: String,
    val distance: Double,
    val from_lat: Double,
    val from_lng: Double,
    val headline: String,
    val house_number: String,
    val icon_url: String,
    val postcode: String,
    val subline: String,
    val to_lat: Double,
    val to_lng: Double*/
    val addressline: String,
    val display_place: String,
    val lat: String,
    val long: String,
    val compressed_address: String,
) : Parcelable