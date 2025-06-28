package de.fast2work.mobility.data.request

import com.google.gson.annotations.SerializedName

data class ChangePasswordReq(@SerializedName("oldPassword")
                             var oldPassword : String = "",
                             @SerializedName("newPassword")
                             var newPassword : String = "",
                             @SerializedName("confirmNewPassword")
                             var confirmNewPassword : String = "",
                             @SerializedName("deviceType")
                             val deviceType : String = "android",
                             @SerializedName("deviceId")
                             var deviceId: String = ""
    )
