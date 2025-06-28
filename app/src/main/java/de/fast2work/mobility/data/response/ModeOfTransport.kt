package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModeOfTransport(
    @field:SerializedName("items")
    var items: ArrayList<ItemTransportDetails> = arrayListOf(),
    @field:SerializedName("key")
    var key: String="",
    @field:SerializedName("label")
    var label: String="",
    @field:SerializedName("icon")
    var icon: String="",

    @field:SerializedName("sharable")
    var isSharable: Boolean=false,

    @field:SerializedName("noOfPeople")
    var noOfPeople: Int=0,

    var isSelected:Boolean=false
) : Parcelable