package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TagsItem(
    @field:SerializedName("tagId")
    val tagId: Int? = null,

    @field:SerializedName("tagName")
    val tagName: String? = null,

    @field:SerializedName("userId")
    val userId: Int? = null
) : Parcelable
