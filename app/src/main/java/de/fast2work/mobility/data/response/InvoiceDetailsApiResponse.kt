package de.fast2work.mobility.data.response


import com.google.gson.annotations.SerializedName

data class InvoiceDetailsApiResponse(
    @SerializedName("approvalStatus")
    val approvalStatus: String? = "",
    @SerializedName("currencyId")
    val currencyId: Int? = 0,
    @SerializedName("currencySymbol")
    val currencySymbol: String? = "",
    @field:SerializedName("currencyName")
    val currencyName: String = "",
    @field:SerializedName("currencyCode")
    val currencyCode: String? = "",
    @SerializedName("grossAmount")
    val grossAmount: String? = "",
    @SerializedName("invoiceDate")
    val invoiceDate: String? = null,
    @SerializedName("invoiceFile")
    val invoiceFile: String? = "",
    @SerializedName("invoiceName")
    val invoiceName: String? = "",
    @SerializedName("invoiceId")
    val invoiceId: Int? = 0,
    @SerializedName("invoiceNo")
    var invoiceNo: String? = null,
    @SerializedName("invoiceTransactions")
    val invoiceTransactions: ArrayList<InvoiceTransaction> = arrayListOf(),
    @SerializedName("netAmount")
    val netAmount: String? = "",
    @SerializedName("ocrStatus")
    val ocrStatus: String? = "",
    @SerializedName("patternId")
    val patternId: String? = "",
    @SerializedName("processType")
    val processType: String? = "",
    @SerializedName("pythonScannedData")
    val pythonScannedData: PythonScannedData? = PythonScannedData(),
    @SerializedName("readStatus")
    val readStatus: String? = "",
    @SerializedName("rejectionRemarks")
    val rejectionRemarks: String? = "",
    @SerializedName("employeeRemarks")
    val employeeRemarks: String? = "",
    @SerializedName("serviceDate")
    val serviceDate: String? = "",
    @SerializedName("supplierAddress")
    val supplierAddress: String? = "",
    @SerializedName("supplierName")
    val supplierName: String? = "",
    @SerializedName("supplierVatNo")
    val supplierVatNo: String? = null,
    @SerializedName("systemCode")
    val systemCode: String? = "",
    @SerializedName("taxAmount")
    val taxAmount: String? = "",
    @SerializedName("validateTag")
    val validateTag: ValidateTag? = ValidateTag(),
    @SerializedName("validationStatus")
    val validationStatus: String? = "",
    @SerializedName("uploadedDate")
    var uploadedDate: String? = "",
    @SerializedName("invoiceCo2Type")
    var invoiceCo2Type: String? = "",
    @SerializedName("captureCo2")
    val captureCo2: Boolean = false,
    @SerializedName("vehicleType")
    val vehicleType: String? = null,
    @SerializedName("vehicleSubType")
    val vehicleSubType: String? = null,

    @SerializedName("vehicleTypeLabel")
    val vehicleTypeLabel: String? = null,
    @SerializedName("vehicleSubTypeLabel")
    val vehicleSubTypeLabel: String? = null,

    @SerializedName("distanceInKm")
    val distanceInKm: String? = null,
    @SerializedName("noOfTrips")
    val noOfTrips: String? = null,
    @SerializedName("fuelType")
    val fuelType: String? = null,
    @SerializedName("fuelTypeLabel")
    val fuelTypeLabel: String? = null,
    @SerializedName("fuelQuantity")
    val fuelQuantity: String? = null,
    @SerializedName("co2Value")
    val co2Value: Double? = null,
    @SerializedName("generateCo2Block")
    val generateCo2Block: Boolean? = false,

    @SerializedName("distanceDataType")
    var distanceDataType : String = "",

    @SerializedName("fromAddress")
    var fromAddress : String = "",
    @SerializedName("fromLatitude") var fromLatitude: String? = null,
    @SerializedName("fromLongitude") var fromLongitude: String? = null,
    @SerializedName("toAddress") var toAddress: String? = null,
    @SerializedName("toLatitude") var toLatitude: String? =null,
    @SerializedName("toLongitude") var toLongitude: String? = null,
    @SerializedName("taxBenefitInvoice") var taxBenefitInvoice: String? = null,

    @SerializedName("invoiceTaxInfo") var invoiceTaxInfo: InvoiceTaxInfo? = InvoiceTaxInfo(),
    @SerializedName("invoiceRefDocList") var invoiceRefDocList: ArrayList<InvoiceRefDocListRes> = arrayListOf(),

    ) {
    data class InvoiceTransaction(
        @SerializedName("budgetGroupId")
        val budgetGroupId: Int? = 0,
        @SerializedName("categoryId")
        val categoryId: Int? = 0,
        @SerializedName("description")
        val description: String? = null,
        @SerializedName("endDate")
        val endDate: String? = "",
        @SerializedName("expenseType")
        val expenseType: String? = null,
        @SerializedName("grossAmount")
        val grossAmount: String? = "",
        @SerializedName("invoiceTransactionId")
        val invoiceTransactionId: Int? = 0,
        @SerializedName("isDateRange")
        val isDateRange: Boolean? = false,
        @SerializedName("itemNo")
        val itemNo: String? = "",
        @SerializedName("lineItems")
        val lineItems: ArrayList<LineItem?>? = arrayListOf(),
        @SerializedName("netAmount")
        val netAmount: String? = "",
        @SerializedName("paidBy")
        val paidBy: String? = "",
        @SerializedName("startDate")
        val startDate: String? = "",
        @SerializedName("subCategoryId")
        val subCategoryId: Int? = 0,
        @SerializedName("taxAmount")
        val taxAmount: String? = "",
        @SerializedName("taxPercentage")
        val taxPercentage: String? = "",
        @SerializedName("userId")
        val userId: Int? = 0,
        @SerializedName("categoryName")
        var categoryName: String? = "",
        @SerializedName("subCategoryName")
        var subCategoryName: String? = "",
        @SerializedName("contractCode")
        var contractCode:String? = null,
        @SerializedName("productId")
        var productId:String? = null
        ,
        var visibility:Boolean=false

    ) {
        data class LineItem(
            @SerializedName("acceptedInBudget")
            val acceptedInBudget: String? = "",
            @SerializedName("acceptedOverBudget")
            val acceptedOverBudget: String? = "",
            @SerializedName("acceptedOverBudgetManual")
            val acceptedOverBudgetManual: String? = "",
            @SerializedName("endDate")
            val endDate: String? = "",
            @SerializedName("excessOverBudget")
            val excessOverBudget: String? = "",
            @SerializedName("grossAmount")
            val grossAmount: String? = "",
            @SerializedName("invoiceLineItemId")
            val invoiceLineItemId: Int? = 0,
            @SerializedName("invoiceTransactionId")
            val invoiceTransactionId: Int? = 0,
            @SerializedName("isExported")
            val isExported: String? = "",
            @SerializedName("netAmount")
            val netAmount: String? = "",
            @SerializedName("payPeriodId")
            val payPeriodId: Int? = 0,
            @SerializedName("remainingBudget")
            val remainingBudget: String? = "",
            @SerializedName("startDate")
            val startDate: String? = "",
            @SerializedName("taxAmount")
            val taxAmount: String? = "",
            @SerializedName("taxPercentage")
            val taxPercentage: String? = "",

            )

    }


    data class InvoiceTaxInfo(
        @SerializedName("type")
        val type: String? = "",

        @SerializedName("noOfKws")
        val noOfKws: String? = null,

         @SerializedName("noOfKms")
         val noOfKms: String? = null,

        @SerializedName("benefitCode")
        val benefitCode: String? = null,

        @SerializedName("benefitId")
        val benefitId: Int? = 0,
    )


    class PythonScannedData

    data class ValidateTag(
        @SerializedName("top")
        val top: Top? = Top(),
        @SerializedName("0_0")
        val x00: X00? = X00()
    ) {
        data class Top(
            @SerializedName("invoiceNo")
            val invoiceNo: Boolean? = false,
            @SerializedName("supplierVatNo")
            val supplierVatNo: Boolean? = false
        )

        data class X00(
            @SerializedName("startDate")
            val startDate: Boolean? = false,
            @SerializedName("subCategoryId")
            val subCategoryId: Boolean? = false,
            @SerializedName("taxPercentage")
            val taxPercentage: Boolean? = false
        )
    }
}