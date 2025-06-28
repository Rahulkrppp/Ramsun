package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppUpdate(
    @SerializedName("appVersionId") var appVersionId: String? = null,
    @SerializedName("appVersion") var appVersion: String? = "",
    @SerializedName("appType") var appType: String? = "",
    @SerializedName("forceUpdate") var forceUpdate: String? = "",
    @SerializedName("createdAt") var createdAt: String? = "",
    @SerializedName("updatedAt") var updatedAt: String? = "",
    @SerializedName("status") var status: String? = "",

    ) : Parcelable
