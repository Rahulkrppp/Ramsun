package de.fast2work.mobility.data.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import de.fast2work.mobility.utility.util.IConstants
import kotlinx.parcelize.Parcelize

@Parcelize
data class CityReq(
    @field:SerializedName("limit") var limit: Int = IConstants.PAGE_LIMIT_20,
    @field:SerializedName("pageNo") var pageNo: Int = 1,
    @field:SerializedName("sortBy") var sortBy: String? = "cityId",
    @field:SerializedName("sortOrder") var sortOrder: String? = "asc",
    @field:SerializedName("keyword") var keyword: String? = "",
    @field:SerializedName("selectedCityId") var selectedCityId: Int = 0,


    ) : Parcelable
