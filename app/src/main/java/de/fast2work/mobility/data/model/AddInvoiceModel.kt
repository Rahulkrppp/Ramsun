package de.fast2work.mobility.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class AddInvoiceModel(
    @field:SerializedName("type") var type: String? = "",
    @field:SerializedName("categoryId") var categoryId: Int? = null,
    @field:SerializedName("subCategoryId") var subCategoryId: Int? = null,
    @field:SerializedName("paidBy") var paidBy: String? = "",
    @field:SerializedName("expenseType") var expenseType: String? = "",
    @field:SerializedName("fileType") var fileType: String? = "",
    @field:SerializedName("processType") var processType: String? = "",
    @field:SerializedName("employeeRemarks") var employeeRemarks: String? = "",
    @field:SerializedName("benefitId") var benefitId: Int? = 0,
    @field:SerializedName("benefitCode") var benefitCode: String? = null,
    @field:SerializedName("cardId") var cardId: String? = "",
    @field:SerializedName("power") var power: String? = "",
    @Transient var file: File? = null,

    @Transient  var selectedFileImageList:ArrayList<File> = arrayListOf(),

    var postion:Int = 0
    ) : Parcelable