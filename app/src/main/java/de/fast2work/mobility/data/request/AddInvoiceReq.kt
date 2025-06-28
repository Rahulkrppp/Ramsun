package de.fast2work.mobility.data.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddInvoiceReq(
    @field:SerializedName("type") var type: String? = "",
    @field:SerializedName("categoryId") var categoryId: String? = "",
    @field:SerializedName("subCategoryId") var subCategoryId: String? = "",
    @field:SerializedName("paidBy") var paidBy: String? = "",
    @field:SerializedName("expenseType") var expenseType: String? = "",
    @field:SerializedName("fileType") var fileType: String? = "",
    @field:SerializedName("processType") var processType: String? = "",
    @field:SerializedName("employeeRemarks") var employeeRemarks: String? = "",
    @field:SerializedName("cardId") var cardId: String? = "",
    @field:SerializedName("noOfKws") var noOfKws: String? = "",
    @field:SerializedName("noOfKms") var noOfKms: String? = ""


    ) : Parcelable
