package de.fast2work.mobility.data.request

import com.google.gson.annotations.SerializedName

data class UpdateProfileReq(
    @SerializedName("firstName") var firstName: String = "",
    @SerializedName("lastName") var lastName: String = "",
    @SerializedName("countryCode") var countryCode: String = "",
    @SerializedName("mobileNo") var mobileNo: String = "",
    @SerializedName("preferredLanguage") var preferredLanguage: String = "",
    @SerializedName("currencyFormat") var currencyFormat: String = "",
    @SerializedName("addressLine1") var addressLine1: String = "",
    @SerializedName("fromAddress") var fromAddress: String = "",
    @SerializedName("fromLatitude") var fromLatitude: String = "",
    @SerializedName("fromLongitude") var fromLongitude: String = "",
    @SerializedName("toAddress") var toAddress: String = "",
    @SerializedName("toLatitude") var toLatitude: String = "",
    @SerializedName("toLongitude") var toLongitude: String = "",
    @SerializedName("distance") var distance: String = "",
    @SerializedName("countryId") var countryId: Int = 1,
    @SerializedName("cityId") var cityId: Int = 0,
    @SerializedName("zipCode") var zipCode: Int = 0,
    @SerializedName("systemCode") var systemCode: String = "",

    )