package de.fast2work.mobility.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class CategoryInfoRes(

	@field:SerializedName("categoryData")
	val categoryData: ArrayList<BudgetGroupInfoItem> = arrayListOf(),

	@field:SerializedName("budgetGroupData")
	val budgetGroupData: ArrayList<BudgetGroupDataItem> = arrayListOf()
) : Parcelable



