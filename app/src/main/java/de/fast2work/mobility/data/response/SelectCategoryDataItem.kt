package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectCategoryDataItem(

    @field:SerializedName("subCategory")
    val subCategory: ArrayList<SubCategoryItem> = arrayListOf(),

    @field:SerializedName("categoryIcon")
    val categoryIcon: String? = null,

    @field:SerializedName("categoryName")
    val categoryName: String? = null,

    @field:SerializedName("categoryId")
    val categoryId: Int? = null,

    var isExpanded : Boolean = false,

    @field:SerializedName("benefitId") var benefitId: Int? = 0,
    @field:SerializedName("benefitCode") var benefitCode: String? = null,

) : Parcelable