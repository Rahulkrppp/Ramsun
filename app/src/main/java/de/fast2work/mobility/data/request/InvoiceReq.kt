package de.fast2work.mobility.data.request

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class InvoiceReq(

	@field:SerializedName("supplierName")
	var supplierName: ArrayList<String> = arrayListOf(),

	@field:SerializedName("readStatus")
	var readStatus: ArrayList<String> = arrayListOf(),

	@field:SerializedName("approvalStatus")
	var approvalStatus: ArrayList<String> = arrayListOf(),

	@field:SerializedName("filterFrom")
	var filterFrom: String? = null,

	@field:SerializedName("payPeriodStartMonth") var payPeriodStartMonth: String? = null,

	@field:SerializedName("budgetGroupId") var budgetGroupId: String? = null,

	@field:SerializedName("filterTo")
	var filterTo: String? = null,

	@field:SerializedName("pageNo")
	var pageNo: Int? = null,

	@field:SerializedName("sortOrder")
	var sortOrder: String? = null,

	@field:SerializedName("limit")
	var limit: Int? = null,

	@field:SerializedName("sortBy")
	var sortBy: String? = null,

	@field:SerializedName("keyword")
	var keyword: String? = null,

	@field:SerializedName("validationStatus")
	var validationStatus: ArrayList<String> = arrayListOf(),

	@field:SerializedName("filterParams")
	var filterParams: ArrayList<FilterParamsItem> = arrayListOf(),

	@field:SerializedName("categoryId")
	var categoryId: ArrayList<Int> = arrayListOf(),

	@SerializedName("deviceType")
	val deviceType : String = "android",

	@SerializedName("deviceId")
	var deviceId: String = "",

	@SerializedName("refType")
	var refType: String = ""
) : Parcelable


