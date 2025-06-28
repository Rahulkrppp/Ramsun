package de.fast2work.mobility.data.response

import com.google.gson.annotations.SerializedName

data class CountryList(
    @SerializedName("mc_country") var name: String? = null,
    @SerializedName("mc_dial_code") var iso: String? = null,
    @SerializedName("mc_country_code") var countryCode: String? = null,
    @SerializedName("states") var states: ArrayList<State>? = null,
) {
    data class State(
        @SerializedName("code") var code: String? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("subdivision") var subdivision: String? = null,
    )
}
