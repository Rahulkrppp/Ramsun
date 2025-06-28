package de.fast2work.mobility.ui.sidemenu.contactus

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import de.fast2work.mobility.R
import de.fast2work.mobility.data.eventbus.UpdateNotificationCount
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.databinding.FragmentContactUsBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.dashboard.INDEX_CONTACT_CO2
import de.fast2work.mobility.ui.sidemenu.notification.NotificationFragment
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.extension.getAndroidDeviceId
import de.fast2work.mobility.utility.extension.getString
import de.fast2work.mobility.utility.extension.getTrimText
import de.fast2work.mobility.utility.util.LocalConfig
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class ContactUsFragment : BaseVMBindingFragment<FragmentContactUsBinding, ContactUsViewModel>(ContactUsViewModel::class.java) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return generateBinding(FragmentContactUsBinding.inflate(inflater), container)
    }

    override fun attachObservers() {
        BaseApplication.notificationCount.observe(this){
            if (tenantInfoData?.tenantInfo?.enabledServices.equals(LocalConfig.co2_management,true)){
                if (it > 0) {
                    binding!!.toolbar.tvNotificationCount.visibility = View.VISIBLE
                    binding!!.toolbar.ivNotification.visibility = View.VISIBLE
                    binding!!.toolbar.tvNotificationCount.text = it.toString()
                } else {
                    binding!!.toolbar.tvNotificationCount.visibility = View.GONE
                    binding!!.toolbar.ivNotification.visibility = View.VISIBLE
                }
            }

        }
        viewModel.contactUsLiveData.observe(this) {
            showSuccessMessage(getString(R.string.thank_you_for_contacting_us_we_will_get_back_to_you_as_soon_as_possible))
            popFragment()
        }
    }

    override fun initComponents() {
        setToolbar()
        setTheme()
    }

    override fun setClickListener() {
        binding?.apply {
            btnSubmit.clickWithDebounce {
                if (isValid()) {
                    viewModel.callContactUsdApi(telSubject.getTrimText(), telMessageHere.getTrimText(), requireActivity().getAndroidDeviceId())
                }
            }
        }
    }

    private fun setToolbar() {
        binding!!.toolbar.let {
            overrideToolbar(it, ToolbarConfig().apply {
                showNotificationIcon = if (tenantInfoData?.tenantInfo?.enabledServices.equals(LocalConfig.co2_management,true)){
                    true
                }else{
                    false
                }
                showBackButton = true
                showWhiteBg = true
                showBackButton = true
                showViewLine = true

                centerTitle = getString(R.string.title_contact_us)
            })

            it.ivBack.clickWithDebounce {
                popFragment()
            }

            it.ivNotification.clickWithDebounce {
                pushFragment(NotificationFragment())
            }

        }
    }
    private fun setTheme() {
        binding?.apply {
            setThemeForTextInputLayout(tlSubject)
            setThemeForTextInputLayout(tlMessageHere)
            setThemeForView(binding!!.btnSubmit)
        }
    }
    private fun isValid(): Boolean {
        binding?.apply {
            tvSubjectError.isVisible = false
            tvMessageError.isVisible = false

            when {
                telSubject.getTrimText().isEmpty() -> {
                    tvSubjectError.isVisible = true
                    tvSubjectError.text = getString(R.string.subject_is_required)
                    return false
                }

                telMessageHere.getTrimText().isEmpty() -> {
                    tvMessageError.isVisible = true
                    tvMessageError.text = getString(R.string.message_is_required)
                    return false
                }

            }
        }
        return true
    }

    /**
     * Notification count update
     *
     * @param updateNotificationCount
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun notificationCountUpdate(updateNotificationCount: UpdateNotificationCount) {
        if (tenantInfoData?.tenantInfo?.enabledServices.equals(LocalConfig.co2_management,true)){
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
        Log.e("", "onStart: ${tenantInfoData?.tenantInfo?.enabledServices}", )
        if (tenantInfoData?.tenantInfo?.enabledServices.equals(LocalConfig.co2_management,true)){
            showTabs()
        }else{
            hideTabs()
        }
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}