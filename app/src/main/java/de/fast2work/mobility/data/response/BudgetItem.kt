package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BudgetItem(
    @field:SerializedName("symbol") val symbol: String = "0.0",

    @field:SerializedName("currencySymbol") val currencySymbol: String = "",

    @field:SerializedName("currencyName") val currencyName: String = "",

    @field:SerializedName("currencyCode") val currencyCode: String = "",

    @field:SerializedName("budgetGroupName") val budgetGroupName: String,

    @field:SerializedName("systemCode") val systemCode: String,

    @field:SerializedName("totalUtilizedBudget")  val totalUtilizedBudget: Float? = 0f,

    @field:SerializedName("budgetGroupId") val budgetGroupId: Int,

    @field:SerializedName("currencyId") val currencyId: Int,

    @field:SerializedName("status") val status: String,

    @field:SerializedName("totalAllocatedBudget") val totalAllocatedBudget: Float = 0f,

    @field:SerializedName("totalRemainingBudget") val totalRemainingBudget: Float = 0f,
    @field:SerializedName("v1AmountInfo") val amountInformation: amountInfo? = null,
    var isSelect: Boolean = false,
    var selectedPos:Int=0
) : Parcelable {

    fun getBudgetProgressValue(): Float {
        if (totalRemainingBudget != null && totalRemainingBudget > 0) {
            return ((totalRemainingBudget * 100) / totalAllocatedBudget)
        }
        return 0f
    }

}


