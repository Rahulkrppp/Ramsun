package de.fast2work.mobility.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class UserStatusModel(

    @SerializedName("first_name")
    var firstName : String = "",

    @SerializedName("last_name")
    var lastName : String = "",

    @SerializedName("full_name")
    var fullName : String = "",

    @SerializedName("user_status_id")
    var statusID : String = "",

    @SerializedName("user_id")
    val userID : String = "",

    @SerializedName("status_type")
    var statusType : String = "",

    @SerializedName("status_text")
    var statusText : String = "",

    @SerializedName("text_data")
    var text_data : String = "",

    @SerializedName("twilio_id1")
    var twilioID : String="",

    @SerializedName("twilio_identity1")
    var twilioIdentity : String="",

    @SerializedName("image")
    var statusImage : String = "",

    @SerializedName("video")
    var videoPath : String = "",


    @SerializedName("profile_image")
    var userProfileImage : String = "",

    @SerializedName("added_date")
    var statusDate : String = "",

    var isStorySeen: Boolean = false,
    var lastStoryPointIndex: Int = 0
    ) : Parcelable
