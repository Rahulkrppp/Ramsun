package de.fast2work.mobility.ui.authentication.changepassword

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.HideReturnsTransformationMethod
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import de.fast2work.mobility.R
import de.fast2work.mobility.data.eventbus.UpdateNotificationCount
import de.fast2work.mobility.databinding.FragmentChangePasswordBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.sidemenu.notification.NotificationFragment
import de.fast2work.mobility.utility.customview.AsteriskPasswordTransformationMethod
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.dialog.DialogUtil
import de.fast2work.mobility.utility.extension.getAndroidDeviceId
import de.fast2work.mobility.utility.extension.getDrawableFromAttr
import de.fast2work.mobility.utility.extension.getTrimText
import de.fast2work.mobility.utility.extension.isValidPassword
import de.fast2work.mobility.utility.extension.performLogout
import de.fast2work.mobility.utility.preference.EasyPref
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Activity used for change user password
 * */
@AndroidEntryPoint
class ChangePasswordFragment : BaseVMBindingFragment<FragmentChangePasswordBinding,ChangePasswordViewModel>(ChangePasswordViewModel::class.java) {
  
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return generateBinding(FragmentChangePasswordBinding.inflate(inflater), container)
    }


    /**
     * This method contains code to handle initial operation required for login activity
     *
     */
    override fun initComponents() {
        setThemeForTextInputLayout(binding!!.tlPassword)
        setThemeForTextInputLayout(binding!!.tlConfirmPassword)
        setThemeForTextInputLayout(binding!!.tlCurrentPassword)
        binding!!.tetConfirmPassword.transformationMethod = AsteriskPasswordTransformationMethod()
        binding!!.tetPassword.transformationMethod = AsteriskPasswordTransformationMethod()
        binding!!.tetCurrentPassword.transformationMethod = AsteriskPasswordTransformationMethod()
        val spannableNote = SpannableString(binding!!.tvNote.text.toString())
        val startIndex = if (BaseApplication.languageSharedPreference.getLanguagePref(EasyPref.CURRENT_LANGUAGE, "").equals("de", true)) {
            binding!!.tvNote.text.toString().indexOf("Hinweis:")
        }else{
            binding!!.tvNote.text.toString().indexOf("Note:")
        }

        val endIndex =if (BaseApplication.languageSharedPreference.getLanguagePref(EasyPref.CURRENT_LANGUAGE, "").equals("de", true)) {
            startIndex + 8// Length of "Note:" plus one space
        }else{
            startIndex + 5
        }
//        val endIndex = startIndex + 5 // Length of "Note:" plus one space

        spannableNote.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding!!.tvNote.text = spannableNote
        setToolbar()
        setThemeForView(binding!!.btnSetPassword)
    }

    /**
     * This method contains code for all click listeners in the activity
     *
     */
    override fun setClickListener() {
        binding!!.apply {
            imgPasswordToggle.clickWithDebounce {
                handleTogglePasswordImage()
            }
            imgConfirmPasswordToggle.clickWithDebounce {
                handleToggleConfirmImage()
            }
            imgCurrentPasswordToggle.clickWithDebounce {
                handleToggleCurrentPasswordImage()
            }
            btnSetPassword.clickWithDebounce {
                if (isValid()){
                    viewModel.callChangePasswordApi(binding!!.tetCurrentPassword.getTrimText(),binding!!.tetPassword.getTrimText(), binding!!.tetConfirmPassword.getTrimText(), requireActivity().getAndroidDeviceId())
                }
            }
        }
    }
    private fun setToolbar() {
        binding!!.toolbar.let {
            overrideToolbar(it, ToolbarConfig().apply {
                showBackButton = true
                showWhiteBg = true
                showNotificationIcon = false
                centerTitle=getString(R.string.title_change_password)
                showViewLine = true
            })

            it.ivNotification.clickWithDebounce {
                pushFragment(NotificationFragment())
            }

            it.ivBack.clickWithDebounce {
               popFragment()
            }
        }
    }
    /**
     * This method handles api call response for all the apis
     *
     */
    override fun attachObservers() {

        BaseApplication.notificationCount.observe(this){
            /*if (it > 0) {
                binding!!.toolbar.tvNotificationCount.visibility = View.VISIBLE
                binding!!.toolbar.ivNotification.visibility = View.VISIBLE
                binding!!.toolbar.tvNotificationCount.text = it.toString()
            } else {
                binding!!.toolbar.tvNotificationCount.visibility = View.GONE
                binding!!.toolbar.ivNotification.visibility = View.VISIBLE
            }*/
        }

        viewModel.changePasswordLiveData.observe(this){
            if(it.isSuccess){
                DialogUtil.showDialog(childFragmentManager, getString(R.string.password_changed), getString(R.string.your_password_has_been_updated_successfully_login_with_the_new_password_to_continue), getString(R.string.continue_to_login), "",   il = object : DialogUtil.IL {
                    override fun onSuccess() {
                        popFragment()
                        requireActivity().performLogout()
                    }

                    override fun onCancel(isNeutral: Boolean) {

                    }
                },image= requireActivity().getDrawableFromAttr(R.attr.imgPasswordChanged), isCancelShow = false)
            }
        }
        viewModel.errorLiveData.observe(this) {
            showErrorMessage(it)
        }

    }
    /**
     * This method contains code for all the password show or hide display in our app
     *
     */
    private fun handleTogglePasswordImage() {
        if (viewModel.isTogglePassShow) {
            viewModel.isTogglePassShow = false
            binding!!.imgPasswordToggle.setImageResource(R.drawable.ic_eye_on)
            binding!!.tetPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            viewModel.isTogglePassShow = true
            binding!!.imgPasswordToggle.setImageResource(R.drawable.ic_eye_off)
            binding!!.tetPassword.transformationMethod = AsteriskPasswordTransformationMethod() //PasswordTransformationMethod.getInstance()
        }
        binding!!.tetPassword.setSelection(binding!!.tetPassword.length())
    }

    private fun handleToggleCurrentPasswordImage() {
        if (viewModel.isTogglePassShow) {
            viewModel.isTogglePassShow = false
            binding!!.tetCurrentPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding!!.imgCurrentPasswordToggle.setImageResource(R.drawable.ic_eye_on)
        } else {
            viewModel.isTogglePassShow = true
            binding!!.tetCurrentPassword.transformationMethod = AsteriskPasswordTransformationMethod() //PasswordTransformationMethod.getInstance()
            binding!!.imgCurrentPasswordToggle.setImageResource(R.drawable.ic_eye_off)
        }
        binding!!.tetCurrentPassword.setSelection(binding!!.tetCurrentPassword.length())
    }
    private fun handleToggleConfirmImage() {
        if (viewModel.isToggleConfirmShow) {
            viewModel.isToggleConfirmShow = false
            binding!!.tetConfirmPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding!!.imgConfirmPasswordToggle.setImageResource(R.drawable.ic_eye_on)
        } else {
            viewModel.isToggleConfirmShow = true
            binding!!.tetConfirmPassword.transformationMethod = AsteriskPasswordTransformationMethod() //PasswordTransformationMethod.getInstance()
            binding!!.imgConfirmPasswordToggle.setImageResource(R.drawable.ic_eye_off)

        }
        binding!!.tetConfirmPassword.setSelection(binding!!.tetConfirmPassword.length())
    }

    private fun isValid(): Boolean {
        var valid =true
        binding!!.tvPasswordError.isVisible=false
        binding!!.tvConfirmPasswordError.isVisible=false
        binding!!.tvCurrentPasswordError.isVisible=false
        if (binding!!.tetCurrentPassword.getTrimText().isEmpty()){
            binding!!.tvCurrentPasswordError.isVisible=true
            binding!!.tvCurrentPasswordError.text= getString(R.string.please_enter_the_current_password)
            valid=false
        }
        if (!binding!!.tetPassword.getTrimText().isValidPassword()){
            binding!!.tvPasswordError.isVisible=true
            binding!!.tvPasswordError.text= getString(R.string.please_enter_the_password_in_a_valid_format)
            valid=false
        }
        if (binding!!.tetPassword.getTrimText().isEmpty()){
            binding!!.tvPasswordError.isVisible=true
            binding!!.tvPasswordError.text= getString(R.string.please_enter_the_new_password)
            valid=false
        }
        if (binding!!.tetConfirmPassword.getTrimText() != binding!!.tetPassword.getTrimText()){
            binding!!.tvConfirmPasswordError.isVisible=true
            binding!!.tvConfirmPasswordError.text= getString(R.string.please_enter_the_password_the_same_as_the_new_password_field)
            valid=false
        }
        if (binding!!.tetConfirmPassword.getTrimText().isEmpty()){
            binding!!.tvConfirmPasswordError.isVisible=true
            binding!!.tvConfirmPasswordError.text=getString(R.string.please_enter_the_new_password)
            valid=false
        }
        return valid
    }

    /**
     * Notification count update
     *
     * @param updateNotificationCount
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun notificationCountUpdate(updateNotificationCount: UpdateNotificationCount) {
        /*if ( updateNotificationCount.pushNotificationCount!!>0) {
            binding!!.toolbar.tvNotificationCount.visibility = View.VISIBLE
            binding!!.toolbar.ivNotification.visibility = View.VISIBLE
            binding!!.toolbar.tvNotificationCount.visibility = updateNotificationCount.pushNotificationCount?:0
        } else {
            binding!!.toolbar.tvNotificationCount.visibility = View.GONE
            binding!!.toolbar.ivNotification.visibility = View.VISIBLE
        }*/
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        hideTabs()
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}