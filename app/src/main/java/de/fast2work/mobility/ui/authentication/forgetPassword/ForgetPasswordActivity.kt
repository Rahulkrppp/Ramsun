package de.fast2work.mobility.ui.authentication.forgetPassword

import android.os.Bundle
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import de.fast2work.mobility.R
import de.fast2work.mobility.databinding.ActivityForgetPasswordBinding
import de.fast2work.mobility.ui.authentication.verifyotp.VerifyOtpActivity
import de.fast2work.mobility.ui.core.BaseVMBindingActivity
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.dialog.DialogUtil
import de.fast2work.mobility.utility.extension.getAndroidDeviceId
import de.fast2work.mobility.utility.extension.getDrawableFromAttr
import de.fast2work.mobility.utility.extension.getTrimText
import de.fast2work.mobility.utility.extension.isValidEmail

/**
 * Activity used for update user password
 * */
@AndroidEntryPoint
class ForgetPasswordActivity : BaseVMBindingActivity<ActivityForgetPasswordBinding, ForgetPasswordViewModel>(
    ForgetPasswordViewModel::class.java) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBindingView(ActivityForgetPasswordBinding.inflate(layoutInflater))
    }

    override fun initComponents() {
        setToolbar()
        setThemeForTextInputLayout(binding.tlEmail)
        setThemeForView(binding.btnRequestOtp)
    }

    /**
     * This method contains code for all the click listeners in the activity
     *
     */

    override fun setClickListener() {
        binding.apply {
            btnRequestOtp.setOnClickListener {
                if (isValid() /*|| isPhoneNumberValid(telEmail.getTrimText())*/){
                    binding.tvEmailError.isVisible=false
                    viewModel.callForgotPasswordApi(telEmail.getTrimText(), getAndroidDeviceId())
                }/*else{
                    binding.tvEmailError.isVisible=true
                    binding.tvEmailError.text= getString(R.string.please_enter_the_email_address_or_number)
                }*/
            }
        }
    }

    /**
     * This method contains code for api callback to perform defined operations
     *
     */
    override fun attachObservers() {
        viewModel.forgotPasswordLiveData.observe(this) {
            if (it.isSuccess) {
                DialogUtil.showDialog(supportFragmentManager, getString(R.string.email_sent), getString(R.string.we_have_sent_you_a_reset_password_code_on_your_registered_email_address), getString(R.string.continue_), "",   il = object : DialogUtil.IL {
                    override fun onSuccess() {
                        startActivity(VerifyOtpActivity.newInstance(this@ForgetPasswordActivity, email = binding.telEmail.getTrimText()))
                    }

                    override fun onCancel(isNeutral: Boolean) {

                    }
                }, image = getDrawableFromAttr(R.attr.imgEmailSent), isCancelShow = false)
            }
        }

        viewModel.errorLiveData.observe(this){
            showErrorMessage(it)
        }
    }


    private fun setToolbar() {
        binding.toolbar.let {
            overrideToolbar(it, ToolbarConfig().apply {
                showBackButton = true
                showWhiteBg = false
            })

            it.ivBack.clickWithDebounce {
                finish()
            }
        }
    }

    private fun isValid(): Boolean {
        binding.tvEmailError.isVisible=false
        when {
            binding.telEmail.getTrimText().isEmpty() -> {
                binding.tvEmailError.isVisible=true
                binding.tvEmailError.text= getString(R.string.please_enter_the_email_address)
                return false
            }
            !binding.telEmail.getTrimText().isValidEmail() -> {
                binding.tvEmailError.isVisible=true
                binding.tvEmailError.text=getString(R.string.please_enter_a_valid_email_address)
                return false
            }

        }

        return true
    }

    private fun isPhoneNumberValid(phoneNumber: String): Boolean {
        val phoneRegex = Regex("^(?:[0-9] ?){6,14}[0-9]$")
        return phoneRegex.matches(phoneNumber)
    }
}