package de.fast2work.mobility.utility.customview.toolbar

import android.view.View
import androidx.core.content.ContextCompat
import de.fast2work.mobility.R
import de.fast2work.mobility.databinding.CustomToolbarBinding
import de.fast2work.mobility.utility.extension.getColorFromAttr

class ToolbarHelper {

    private var binding: CustomToolbarBinding? = null
    private var config: ToolbarConfig? = null

    fun setCustomToolbarBinding(binding: CustomToolbarBinding?) {
        this.binding = binding
    }

    fun setToolbarConfig(toolbarConfig: ToolbarConfig?) {
        this.config = toolbarConfig

        if (binding != null && toolbarConfig != null) {
            hideAllItemInToolbar(binding!!)
            manageIconVisibility(binding!!, toolbarConfig)
        }
    }

    private fun hideAllItemInToolbar(binding: CustomToolbarBinding) {
       /* binding.ivBack.visibility = View.GONE
        binding.ivMenu.visibility = View.GONE
        binding.ivBell.visibility = View.GONE
        binding.ivOptions.visibility = View.GONE
        binding.ivSearch.visibility = View.GONE
        binding.tvCenterTitle.visibility = View.GONE
        binding.clFilter.visibility = View.GONE
        binding.ivMapView.visibility = View.GONE
        binding.tvNotificationCount.visibility = View.GONE
        binding.clSearch.visibility = View.GONE
        binding.ivListIcon.visibility = View.GONE
        binding.ivFilterDot.visibility = View.GONE
        binding.ivWallet.visibility = View.GONE
        binding.ivSetting.visibility = View.GONE
        binding.ivLogo.visibility = View.GONE
        binding.edtSearch.hint = binding.root.context.getString(R.string.search_here)*/
        binding.ivKnitLogo.visibility = View.GONE
        binding.ivBack.visibility = View.GONE
        binding.ivMenu.visibility = View.GONE
        binding.ivBackWhite.visibility = View.GONE
        binding.tvCenterTitle.visibility = View.GONE
        binding.ivSearch.visibility = View.GONE
        binding.ivChat.visibility = View.GONE
        binding.ivNotification.visibility = View.GONE
        binding.tvFavoriteCount.visibility = View.GONE
        binding.ivFilter.visibility = View.GONE
        binding.ivMap.visibility = View.GONE
        binding.ivWallet.visibility = View.GONE
        binding.ivReport.visibility = View.GONE
        binding.ivUser.visibility = View.GONE
        binding.clSearch.visibility = View.GONE
        binding.ivOption.visibility = View.GONE
        binding.ivNotificationCount.visibility = View.GONE
        binding.ivFilterHappening.visibility = View.GONE
        binding.view.visibility = View.GONE
    }

    private fun manageIconVisibility(binding: CustomToolbarBinding, toolbarConfig: ToolbarConfig) {

       /* if (toolbarConfig.showBackButton) {
            binding.ivBack.visibility = View.VISIBLE
        }

        if (toolbarConfig.showOptionsButton) {
            binding.ivOptions.visibility = View.VISIBLE
        }

        if (toolbarConfig.showMenuButton) {
            binding.ivMenu.visibility = View.VISIBLE
        }
        if (toolbarConfig.showSearchIcon) {
            binding.ivSearch.visibility = View.VISIBLE
        }

        if (toolbarConfig.showBellIcon) {
            binding.ivBell.visibility = View.VISIBLE
        }

        if (toolbarConfig.showMapViewIcon) {
            binding.ivMapView.visibility = View.VISIBLE
        }

        if (toolbarConfig.showFilterIcon) {
            binding.clFilter.visibility = View.VISIBLE
        }

        if (toolbarConfig.showListIcon) {
            binding.ivListIcon.visibility = View.VISIBLE
        }

        if (toolbarConfig.showLogoIcon) {
            binding.ivLogo.visibility = View.VISIBLE
        }

        if (toolbarConfig.notificationCount > 0) {
            binding.tvNotificationCount.visibility = View.VISIBLE
            binding.tvNotificationCount.text = toolbarConfig.notificationCount.toString()
        }

        if (toolbarConfig.centerTitle.isNotEmpty()) {
            binding.tvCenterTitle.visibility = View.VISIBLE
            binding.tvCenterTitle.text = toolbarConfig.centerTitle
        }
        if (toolbarConfig.showFilterDot) {
            binding.ivFilterDot.visibility = View.VISIBLE
        }

        if (toolbarConfig.showWalletIcon) {
            binding.ivWallet.visibility = View.VISIBLE
        }

        if (toolbarConfig.showSettingIcon) {
            binding.ivSetting.visibility = View.VISIBLE
        }*/

        if (toolbarConfig.showLogoIcon) {
            binding.ivKnitLogo.visibility = View.VISIBLE
        }

        if (toolbarConfig.showBackButton) {
            binding.ivBack.visibility = View.VISIBLE
        }

        if (toolbarConfig.showMenuButton) {
            binding.ivMenu.visibility = View.VISIBLE
        }

        if (toolbarConfig.showBackWhiteButton) {
            binding.ivBackWhite.visibility = View.VISIBLE
        }

        if (toolbarConfig.centerTitle.isNotEmpty()) {
            binding.tvCenterTitle.visibility = View.VISIBLE
            binding.tvCenterTitle.text = toolbarConfig.centerTitle
        }

        if (toolbarConfig.showSearchIcon) {
            binding.ivSearch.visibility = View.VISIBLE
        }

        if (toolbarConfig.showChatIcon) {
            binding.ivChat.visibility = View.VISIBLE
        }

        if (toolbarConfig.showNotificationIcon) {
            binding.ivNotification.visibility = View.VISIBLE
        }
        if (toolbarConfig.showNotificationCountIcon) {
            binding.ivNotificationCount.visibility = View.VISIBLE
        }

        if (toolbarConfig.favoriteCount.isNotEmpty()) {
            binding.tvFavoriteCount.visibility = View.VISIBLE
            binding.tvFavoriteCount.text = toolbarConfig.favoriteCount
        }

        if (toolbarConfig.showFilterIcon) {
            binding.ivFilter.visibility = View.VISIBLE
        }

        if (toolbarConfig.showWalletIcon) {
            binding.ivWallet.visibility = View.VISIBLE
        }

        if (toolbarConfig.showReportIcon) {
            binding.ivReport.visibility = View.VISIBLE
        }

        if (toolbarConfig.showProfileImage) {
            binding.ivUser.visibility = View.VISIBLE
        }

        if (toolbarConfig.showMapViewIcon) {
            binding.ivMap.visibility = View.VISIBLE
        }

        if (toolbarConfig.showOptionsButton) {
            binding.ivOption.visibility = View.VISIBLE
        }
        if (toolbarConfig.showFilterHappening) {
            binding.ivFilterHappening.visibility = View.VISIBLE
        }
        if (toolbarConfig.showViewLine){
            binding.view.visibility = View.VISIBLE
        }


        if (toolbarConfig.showWhiteBg) {
            binding.cvMain.setBackgroundColor(binding.root.context.getColorFromAttr(R.attr.colorNavBar))
      } else {
          binding.cvMain.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.transparent))
        }
    }
}