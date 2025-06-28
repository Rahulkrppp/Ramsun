package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BudgetGroupDataItem(

    @field:SerializedName("budgetGroupName")
    val budgetGroupName: String? = null,

    @field:SerializedName("budgetGroupId")
    val budgetGroupId: Int? = null
) : Parcelable
