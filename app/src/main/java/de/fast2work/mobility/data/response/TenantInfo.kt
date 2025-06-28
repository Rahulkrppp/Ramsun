package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class TenantInfo(

    @SerializedName("tenantId") var tenantId: Int = 0,
    @SerializedName("tenantName") var tenantName: String = "",
    @SerializedName("tenantDomain") var tenantDomain: String = "",
    @SerializedName("tenantCode") var tenantCode: String = "",
    @SerializedName("systemCode") var systemCode: String = "",
    @SerializedName("accessKey") var accessKey: String = "",
    @SerializedName("nipNumber") var nipNumber: String = "",
    @SerializedName("tenantEmail") var tenantEmail: String = "",
    @SerializedName("tenantMobileCountryCode") var tenantMobileCountryCode: String = "",
    @SerializedName("tenantMobileNo") var tenantMobileNo: String = "",
    @SerializedName("usersCount") var usersCount: Int = 0,
    @SerializedName("industry") var industry: String = "",
    @SerializedName("sizeOfCompany") var sizeOfCompany: Int = 0,
    @SerializedName("countryId") var countryId: Int = 0,
    @SerializedName("cityId") var cityId: Int = 0,
    @SerializedName("zipCode") var zipCode: String = "",
    @SerializedName("addressLine1") var addressLine1: String = "",
    @SerializedName("twoFactorEnabled") var twoFactorEnabled: String = "",
    @SerializedName("status") var status: String = "",
    @SerializedName("enabledServices") var enabledServices: String = "",

    ) : Parcelable

