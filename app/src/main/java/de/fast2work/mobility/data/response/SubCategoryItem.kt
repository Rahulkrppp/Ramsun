package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubCategoryItem(

    @field:SerializedName("subCategoryId")
    val subCategoryId: Int? = 0,

    @field:SerializedName("subCategoryName")
    val subCategoryName: String? = "",
    var isSelect:Boolean=false
) : Parcelable
