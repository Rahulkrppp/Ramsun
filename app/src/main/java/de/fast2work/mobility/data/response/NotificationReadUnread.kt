package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationReadUnread(
    @SerializedName("notificationId") var notificationId: Int? = null,
    @SerializedName("notification") var notification: String? = null,
    @SerializedName("notificationForId") var notificationForId: Int? = null,
    @SerializedName("notificationTime") var notificationTime: String? = null,
    @SerializedName("isRead") var isRead: String? = null,
    @SerializedName("notificationType") var notificationType: String? = null,
    @SerializedName("notificationCode") var notificationCode: String? = null,
    @SerializedName("notificationData") var notificationData: String? = null,
    @SerializedName("readTime") var readTime: String? = null,
    @SerializedName("deletedTime") var deletedTime: String? = null,
    @SerializedName("createdBy") var createdBy: Int? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("updatedBy") var updatedBy: Int? = null,
    @SerializedName("updatedAt") var updatedAt: String? = null,
    @SerializedName("deletedAt") var deletedAt: String? = null,

    ) : Parcelable