package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BudgetGroupInfoItem(

    @field:SerializedName("allocatedBudget") val allocatedBudget: Float = 0f,

    @field:SerializedName("unusedBudget") val unusedBudget: Float = 0f,

    @field:SerializedName("categoryIcon") val categoryIcon: String ? = null,

    @field:SerializedName("inreviewInvoices") val inreviewInvoices: Float = 0f,

    @field:SerializedName("rejectedInvoices") val rejectedInvoices: Int = 0,

    @field:SerializedName("usedBudget") val usedBudget: Float = 0f,

    @field:SerializedName("approvedInvoices") val approvedInvoices: Int = 0,

    @field:SerializedName("currencyId") val currencyId: Int = 0,

    @field:SerializedName("currencySymbol") val currencySymbol: String ?= null,

    @field:SerializedName("currencyName") val currencyName: String = "",

    @field:SerializedName("currencyCode") val currencyCode: String = "",

    @field:SerializedName("pendingInvoices") val pendingInvoices: Int = 0,

    @field:SerializedName("categoryName") val categoryName: String? = "",

    @field:SerializedName("colorCode") val colorCode: String = "",

    @field:SerializedName("categoryId") val categoryId: Int = 0,

    @field:SerializedName("totalInvoices") val totalInvoices: Int = 0,
    @field:SerializedName("v1AmountInfo") val amountInformation: amountInfo? = null,
) : Parcelable{
    fun getBudgetProgressValue():Float{
        if(unusedBudget>0){
            return ((unusedBudget*100)/allocatedBudget)
        }
        return 0f
    }
}

@Parcelize
data class amountInfo(

    @field:SerializedName("availableAmount") var availableAmount: Double = 0.0,
    @field:SerializedName("usedAmount") var usedAmount: Double = 0.0,
    @field:SerializedName("pendingAmount") var pendingAmount: Double = 0.0,
    @field:SerializedName("colors") val colors: colorInfo ? = null,
):Parcelable

@Parcelize
data class colorInfo(
    @field:SerializedName("available") val availableColor: String = "#58AC5D",
    @field:SerializedName("used") val usedColor: String = "#6CAFBE",
    @field:SerializedName("pending") val pendingColor: String = "#5E70A0",
):Parcelable
