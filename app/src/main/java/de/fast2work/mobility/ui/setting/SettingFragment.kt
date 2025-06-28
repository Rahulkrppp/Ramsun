package de.fast2work.mobility.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.Fragment
import de.fast2work.mobility.R
import de.fast2work.mobility.data.eventbus.UpdateNotificationCount
import de.fast2work.mobility.data.request.StaticPageReq
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.data.response.User
import de.fast2work.mobility.databinding.FragmentSettingBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.dashboard.INDEX_CONTACT_CO2
import de.fast2work.mobility.ui.dashboard.INDEX_SETTING
import de.fast2work.mobility.ui.dashboard.INDEX_SETTING_CO2

import de.fast2work.mobility.ui.profile.bottom.PreferredLanguageBottomSheetFragment
import de.fast2work.mobility.ui.sidemenu.contactus.ContactUsFragment
import de.fast2work.mobility.ui.sidemenu.notification.NotificationFragment
import de.fast2work.mobility.ui.sidemenu.staticpage.StaticPageFragment
import de.fast2work.mobility.utility.customview.countrypicker.CountryPicker
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.dialog.DialogUtil
import de.fast2work.mobility.utility.extension.getAndroidDeviceId
import de.fast2work.mobility.utility.extension.getString
import de.fast2work.mobility.utility.extension.performLogout
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.preference.EasyPref
import de.fast2work.mobility.utility.preference.EasyPref.Companion.NOTIFICATION_ENABLED
import de.fast2work.mobility.utility.util.LocalConfig
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Locale


/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : BaseVMBindingFragment<FragmentSettingBinding, SettingViewModel>(SettingViewModel::class.java) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return generateBinding(FragmentSettingBinding.inflate(inflater), container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        local()
        //val actionBar= SupportA
    }
    override fun attachObservers() {
        if (!tenantInfoData?.tenantInfo?.enabledServices.equals(LocalConfig.BOTH, true)) {
            BaseApplication.notificationCount.observe(this){
                if (it > 0) {
                    binding!!.toolbar.tvNotificationCount.visibility = View.VISIBLE
                    binding!!.toolbar.ivNotification.visibility = View.VISIBLE
                    binding!!.toolbar.tvNotificationCount.text = it.toBlankString()
                } else {
                    binding!!.toolbar.tvNotificationCount.visibility = View.GONE
                    binding!!.toolbar.ivNotification.visibility = View.VISIBLE
                }
            }
        }

        viewModel.enableDisablePushNotificationLiveData.observe(this) {
            //showSuccessMessage(it.responseMessage)
            binding?.ivNotificationToggle?.isSelected = BaseApplication.sharedPreference.getPref(NOTIFICATION_ENABLED, "") == "1"
        }

        viewModel.deleteAccountLiveData.observe(this) {
            if (it.isSuccess){
                    DialogUtil.showDialog(supportFragmentManager = parentFragmentManager, getString(R.string.request_received), getString(R.string.request_received_your_account_will_be_deleted_soon),
                        getString(R.string.okay), null, object : DialogUtil.IL {
                            override fun onSuccess() {
                                requireActivity().performLogout()
                            }

                            override fun onCancel(isNeutral: Boolean) {
                            }
                        }, isCancelShow = false, isTitleShow = View.VISIBLE)
            }
        }
        viewModel.logoutLiveData.observe(this){
            requireContext().performLogout()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun initComponents() {
//        tenant=(BaseApplication.tenantSharedPreference.getTenantPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java) )
        setToolbar()
        binding?.ivNotificationToggle?.isSelected = BaseApplication.sharedPreference.getPref(NOTIFICATION_ENABLED, "") == "1"
        if (BaseApplication.languageSharedPreference.getLanguagePref(EasyPref.CURRENT_LANGUAGE, "").equals("de", true)){
            binding!!.ivChangeLanguageImage.setImageBitmap(CountryPicker.loadImageFromAssets(requireContext(), "DE"))
        }else{
            binding!!.ivChangeLanguageImage.setImageBitmap(CountryPicker.loadImageFromAssets(requireContext(), "GB"))
        }
    }

    override fun setClickListener() {
        binding?.apply {
            tvAboutUs.clickWithDebounce {
                pushFragment(StaticPageFragment.newInstance(StaticPageReq.ABOUTUS, false))
            }
            tvTermsConditions.clickWithDebounce {
                pushFragment(StaticPageFragment.newInstance(StaticPageReq.TERMSOFUSE, false))
            }
            tvPrivacyPolicy.clickWithDebounce {
                pushFragment(StaticPageFragment.newInstance(StaticPageReq.PRIVACYPOLICY, false))
            }
            tvDeclaration.clickWithDebounce {
                pushFragment(StaticPageFragment.newInstance(StaticPageReq.DECLARATION, false))
            }
            tvContactUs.clickWithDebounce {
                if (tenantInfoData?.tenantInfo?.enabledServices.equals(LocalConfig.co2_management,true)){
                    switchTab(INDEX_CONTACT_CO2)
                }else{
                    pushFragment(ContactUsFragment())
                }
            }
            ivNotificationToggle.setOnClickListener {
                setNotificationToggle()
            }
            tvPushNotification.setOnClickListener {
                setNotificationToggle()
            }
            tvChangeLanguage.clickWithDebounce {
                openLanguageBottomSheet(true, getString(R.string.select_language))
            }
            tvDeleteAccount.clickWithDebounce {
                showDeleteAccountDialog()
            }
            tvLogOut.clickWithDebounce {
                DialogUtil.showDialog(childFragmentManager, getString(R.string.logging_out), getString(R.string.alert_logout),
                    getString(R.string.logout), getString(R.string.cancel), object : DialogUtil.IL {
                        override fun onSuccess() {
                            viewModel.callLogoutApi(true, requireActivity().getAndroidDeviceId())
                        }

                        override fun onCancel(isNeutral: Boolean) {
                        }
                    }, isCancelShow = false)
            }
        }
    }

    private fun setNotificationToggle() {
        binding?.apply {
            if (viewModel.isClickToggle){
                viewModel.isClickToggle=false
                ivNotificationToggle.isSelected=false
            }else{
                viewModel.isClickToggle=true
                ivNotificationToggle.isSelected=true
            }

            viewModel.callEnableDisableNotificationApi(
                BaseApplication.sharedPreference.getPrefModel(EasyPref.USER_DATA, User::class.java)?.userId)
        }
    }

    private fun setToolbar() {
        binding!!.toolbar.let {
            overrideToolbar(it, ToolbarConfig().apply {
                if (tenantInfoData?.tenantInfo?.enabledServices.equals(LocalConfig.BOTH, true)) {
                    showBackButton = true
                    showMenuButton = false
                    showNotificationIcon = false
                } else{
                    showBackButton = false
                    showMenuButton = true
                    showNotificationIcon = true
                }

                showWhiteBg = true
                showLogoIcon = true
                showViewLine = true
            })

            it.ivMenu.clickWithDebounce {
                toggleDrawer()
            }
            it.ivNotification.clickWithDebounce {
                pushFragment(NotificationFragment())
            }
            it.ivBack.clickWithDebounce {
                popFragment()
            }
        }
    }
    private fun openLanguageBottomSheet(languageClick: Boolean, tittle: String) {
        var lang=""
        lang=BaseApplication.languageSharedPreference.getLanguagePref(EasyPref.CURRENT_LANGUAGE,"")
        if (lang.isEmpty()){
            lang=getString(R.string.en)
        }
        val dialog = PreferredLanguageBottomSheetFragment.newInstance(languageClick, tittle,lang)
        dialog.sendClickListener = {
            dialog.dismiss()
            if(it.language == getString(R.string.english)){
                language("en")
                recreate(requireActivity())
            }else{
                language("de")
                recreate(requireActivity())
            }
        }
        dialog.show(childFragmentManager, "")
    }
    fun language(language: String){
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = getResources()
        val configuration = resources.getConfiguration()
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.getDisplayMetrics())

        BaseApplication.languageSharedPreference.setLanguagePref(EasyPref.CURRENT_LANGUAGE,language)
    }
    private fun local(){
        val lang=BaseApplication.languageSharedPreference.getLanguagePref(EasyPref.CURRENT_LANGUAGE,"")
        language(lang)

    }

    private fun showDeleteAccountDialog(){
        DialogUtil.showDialog(supportFragmentManager = parentFragmentManager, getString(R.string.delete_account_), getString(R.string.alert_delete_account),
            getString(R.string.delete), getString(R.string.cancel), object : DialogUtil.IL {
                override fun onSuccess() {
                    viewModel.callDeleteAccountApi()
                }

                override fun onCancel(isNeutral: Boolean) {
                }
            }, isCancelShow = false)
    }

    /**
     * Notification count update
     *
     * @param updateNotificationCount
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun notificationCountUpdate(updateNotificationCount: UpdateNotificationCount) {
        if (!tenantInfoData?.tenantInfo?.enabledServices.equals(LocalConfig.BOTH, true)) {
            if (updateNotificationCount.pushNotificationCount!! > 0) {
                binding!!.toolbar.tvNotificationCount.visibility = View.VISIBLE
                binding!!.toolbar.ivNotification.visibility = View.VISIBLE
                binding!!.toolbar.tvNotificationCount.visibility = updateNotificationCount.pushNotificationCount?:0
            } else {
                binding!!.toolbar.tvNotificationCount.visibility = View.GONE
                binding!!.toolbar.ivNotification.visibility = View.VISIBLE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (tenantInfoData?.tenantInfo?.enabledServices.equals(LocalConfig.BOTH, true)) {
           hideTabs()
        }
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}