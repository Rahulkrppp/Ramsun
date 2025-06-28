package de.fast2work.mobility.data.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SignUpReq(
    @field:SerializedName("firstName") var firstName:String? = null,
    @field:SerializedName("lastName") var lastName:String? = null,
    @field:SerializedName("email") var email:String? = null,
    @field:SerializedName("mobileNo") var mobileNo:String? = null,
    @field:SerializedName("countryCode") var countryCode:String? = null,
    @field:SerializedName("password") var password:String? = null,
    @field:SerializedName("isTermsAccepted") var isTermsAccepted:Boolean? = null,
) : Parcelable
