package de.fast2work.mobility.data.remote.cit

import com.google.gson.annotations.SerializedName

open class HBBaseResponse {
    @SerializedName("responseCode")
    var responseCode: Int = 0// 0 -> Failure, 1->Success, 101-> Network error(showing network exception/no network)

    @SerializedName("responseStatus")
    var responseStatus = "Success"

    @SerializedName("responseMessage")
    var responseMessage = ""

    val isSuccess: Boolean
        get() = responseCode == SUCCESS

    val isNetworkError: Boolean
        get() = responseCode == NETWORK_ERROR

    val isAuthenticationError: Boolean
        get() = responseCode == AUTHENTICATION_ERROR

    val isAPIError: Boolean
        get() = responseCode == ERROR

    companion object {

        const val AUTHENTICATION_ERROR = 401

        //IF Network error
        const val NETWORK_ERROR = 501

        const val SUCCESS = 200

        const val ERROR = 400

    }
}
