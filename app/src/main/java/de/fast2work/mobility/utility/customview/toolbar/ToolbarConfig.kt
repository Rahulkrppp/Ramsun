package de.fast2work.mobility.utility.customview.toolbar

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ToolbarConfig : Parcelable {
    var showBackButton: Boolean = false
    var showMenuButton: Boolean = false
    var showBellIcon: Boolean = false
    var showSearchIcon: Boolean = false
    var centerTitle: String = ""
    var showFilterIcon: Boolean = false
    var showMapViewIcon: Boolean = false
    var notificationCount: Int = 0
    var showOptionsButton: Boolean = false
    var showListIcon: Boolean = false
    var showFilterDot: Boolean = false
    var showWhiteBg: Boolean = true
    var showWalletIcon: Boolean = false
    var showSettingIcon: Boolean = false
    var showLogoIcon: Boolean = false
    var showBackWhiteButton: Boolean = false
    var showChatIcon: Boolean = false
    var showNotificationIcon: Boolean = false
    var showNotificationCountIcon: Boolean = false
    var favoriteCount: String = ""
    var showReportIcon: Boolean = false
    var showProfileImage: Boolean = false
    var showFilterHappening: Boolean = false
    var showViewLine: Boolean = false

}