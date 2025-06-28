package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TenantInfoModel(
    @SerializedName("tenantInfo") var tenantInfo: TenantInfo? = null,
    @SerializedName("brandingInfo") var brandingInfo: BrandingInfo? = null,
) : Parcelable

