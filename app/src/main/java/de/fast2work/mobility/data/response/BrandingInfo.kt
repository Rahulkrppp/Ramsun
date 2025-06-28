package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class BrandingInfo(

    @SerializedName("logo") var logo: String? = null,
    @SerializedName("favIcon") var favIcon: String? = null,
    @SerializedName("pwaIcon512") var pwaIcon512: String? = null,
    @SerializedName("pwaIcon192") var pwaIcon192: String? = null,
    @SerializedName("pwaIcon180") var pwaIcon180: String? = null,
    @SerializedName("primaryColor") var primaryColor: String? = "#274072",
    @SerializedName("secondaryColor") var secondaryColor: String? = "#49C087",
    @SerializedName("tenantLoginImage") var tenantLoginImage: String? = null,
    @SerializedName("title_EN") var titleEN: String? = null,
    @SerializedName("subTitle_EN") var subTitleEN: String? = null,
    @SerializedName("streamLine_EN") var streamLineEN: String? = null,
    @SerializedName("title_DE") var titleDE: String? = null,
    @SerializedName("subTitle_DE") var subTitleDE: String? = null,
    @SerializedName("streamLine_DE") var streamLineDE: String? = null,

    ) : Parcelable