package de.fast2work.mobility.data.response


import com.google.gson.annotations.SerializedName

data class CityResponse(
    @SerializedName("data")
    val data: ArrayList<City>? = arrayListOf()
){
    data class City(
        @SerializedName("cityId")
        val cityId: Int? = 0,
        @SerializedName("stateId")
        val stateId: Int? = 0,
        @SerializedName("countryId")
        val countryId: Int? = 0,
        @SerializedName("cityName")
        val cityName: String? = "",
        @SerializedName("status")
        val status: String? = ""
    )
}