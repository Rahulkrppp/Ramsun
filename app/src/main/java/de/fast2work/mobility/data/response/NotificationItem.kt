package de.fast2work.mobility.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class NotificationItem(

	@field:SerializedName("notification")
	val notification: String? = null,

	@field:SerializedName("notificationForId")
	val notificationForId: Int? = null,

	@field:SerializedName("notificationTime")
	val notificationTime: String? = null,

	@field:SerializedName("isRead") var isRead: String? = null,

	@field:SerializedName("notificationData")
	val notificationData: NotificationData? = null,

	@field:SerializedName("readTime")
	val readTime: String? = null,

	@field:SerializedName("notificationId")
	val notificationId: Int? = null,

	@field:SerializedName("notificationType")
	val notificationType: String? = null


) : Parcelable
