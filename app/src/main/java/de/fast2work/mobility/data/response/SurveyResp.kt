package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SurveyResp(
    @field:SerializedName("createdDate")
    val createdDate: String="",
    @field:SerializedName("endDate")
    val endDate: String="",
    @field:SerializedName("savedCo2")
    val savedCo2: String="",
    @field:SerializedName("startDate")
    val startDate: String="",
    @field:SerializedName("surveyId")
    val surveyId: Int=0,
    @field:SerializedName("surveyName")
    val surveyName: String="",
    @field:SerializedName("systemCode")
    val systemCode: String="",
    @field:SerializedName("surveyStatus")
    val status: String?=null,
    @field:SerializedName("surveyStatusMessage")
    val statusMessage: String="",
    @field:SerializedName("employeeReportFile")
    val reportUrl: String="",
    @field:SerializedName("employeeEnReportFile")
    val employeeEnReportFile: String="",
    @field:SerializedName("employeeEnReportPdfFile")
    val employeeEnReportPdfFile: String="",
    @field:SerializedName("employeeReportPdfFile")
    val employeeReportPdfFile: String=""

) : Parcelable