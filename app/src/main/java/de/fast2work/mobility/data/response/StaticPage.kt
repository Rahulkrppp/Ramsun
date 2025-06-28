package de.fast2work.mobility.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class StaticPage(

	@field:SerializedName("pageContentDe")
	val pageContentDe: String? = "",

	@field:SerializedName("pageContentPdf")
	val pageContentPdf: String? = "",

	@field:SerializedName("pageContentPdfDe")
	val pageContentPdfDe: String? = "",

	@field:SerializedName("pageNameDe")
	val pageNameDe: String? = "",

	@field:SerializedName("pageContent")
	val pageContent: String? = "",

	@field:SerializedName("pageId")
	val pageId: Int? = 0,

	@field:SerializedName("pageName")
	val pageName: String? = "",

	@field:SerializedName("pageCode")
	val pageCode: String? = "",

	@field:SerializedName("status")
	val status: String? = ""
) : Parcelable
