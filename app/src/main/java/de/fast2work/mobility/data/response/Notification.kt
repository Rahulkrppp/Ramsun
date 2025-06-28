package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notification(
    @field:SerializedName("data")
    val data: ArrayList<NotificationItem> = arrayListOf(),

    @field:SerializedName("unreadNotificationCount")
    val unreadNotificationCount: Int = 0,

) : Parcelable
