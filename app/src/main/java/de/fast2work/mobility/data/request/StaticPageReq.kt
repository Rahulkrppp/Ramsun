package de.fast2work.mobility.data.request

import com.google.gson.annotations.SerializedName

class StaticPageReq {
    companion object {
        const val ABOUTUS = "aboutus"
        const val TERMSOFUSE = "terms_of_use"
        const val PRIVACYPOLICY = "privacypolicy"
        const val DECLARATION = "declaration"

        //        val COVID_19_GUIDELINES = "covidguidelines"
        const val END_USER_AGREEMENT = "eula"
        const val DELETE_ACCOUNT_POLICY = "deleteaccountpolicy"
        const val REFUND_POLICY = "refundpolicy"
        const val CANCELLATION_POLICY = "cancellationpolicy"
    }

    @SerializedName("page_code")
    var pageCode = ""
}