package de.fast2work.mobility.data.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class InvoiceApiResponse(
    @SerializedName("approvalStatus")
    val approvalStatus: String? = "",
    @SerializedName("approvedAmount")
    val approvedAmount: String? = "",
    @SerializedName("budgetGroupId")
    val budgetGroupId: Int? = 0,
    @SerializedName("budgetGroupName")
    val budgetGroupName: String? = "",
    @SerializedName("categoryId")
    val categoryId: Int? = 0,
    @SerializedName("categoryName")
    val categoryName: ArrayList<String> = arrayListOf(),
    @SerializedName("description")
    val description: String? = "",
    @SerializedName("expenseType")
    val expenseType: String? = "",
    @SerializedName("grossAmount")
    val grossAmount: String? = "",
    @SerializedName("invoiceId")
    val invoiceId: Int? = 0,
    @SerializedName("invoiceNo")
    val invoiceNo: String? = "",
    @SerializedName("overBudgetAmount")
    val overBudgetAmount: String? = "",
    @SerializedName("paidBy")
    val paidBy: String? = "",
    @SerializedName("readStatus")
    val readStatus: String? = "",
    @SerializedName("subCategoryId")
    val subCategoryId: Int? = 0,
    @SerializedName("subCategoryName")
    val subCategoryName: String? = "",
    @SerializedName("supplierName")
    val supplierName: String? = "",
    @SerializedName("type")
    val type: String? = "",
    @SerializedName("uploadedBy")
    val uploadedBy: String? = "",
    @SerializedName("uploadedOn")
    val uploadedOn: String? = "",
    @SerializedName("validationStatus")
    val validationStatus: String? = "",
    @field:SerializedName("currencySymbol")
    val currencySymbol: String ?= null,
    @field:SerializedName("currencyName")
    val currencyName: String = "",
    @field:SerializedName("currencyCode")
    val currencyCode: String = "",
    @field:SerializedName("currencyId")
    val currencyId: String = "",
    @field:SerializedName("invoiceDate")
    val invoiceDate: String? = "",
    @SerializedName("invoiceCo2Type")
    var invoiceCo2Type: String? = "",
    @SerializedName("captureCo2")
    val captureCo2: Boolean = false
) : Parcelable