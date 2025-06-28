package de.fast2work.mobility.ui.authentication.resetpassword

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.HideReturnsTransformationMethod
import android.text.style.StyleSpan
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import de.fast2work.mobility.R
import de.fast2work.mobility.databinding.ActivityResetPasswordBinding
import de.fast2work.mobility.ui.authentication.login.LoginActivity
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingActivity
import de.fast2work.mobility.utility.customview.AsteriskPasswordTransformationMethod
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.dialog.DialogUtil
import de.fast2work.mobility.utility.extension.getAndroidDeviceId
import de.fast2work.mobility.utility.extension.getDrawableFromAttr
import de.fast2work.mobility.utility.extension.getTrimText
import de.fast2work.mobility.utility.extension.isValidPassword
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.preference.EasyPref

/**
 * Activity used for reset user password
 * */
@AndroidEntryPoint
class ResetPasswordActivity : BaseVMBindingActivity<ActivityResetPasswordBinding, ResetPasswordViewModel>(ResetPasswordViewModel::class.java) {
    private var email: String = ""
    private var otp: String = ""
    private var token: String = ""

    companion object {
        const val INTENT_EMAIL = "email"
        const val INTENT_OTP = "otp"
        const val INTENT_TOKEN = "token"

        fun newInstance(activity: Activity, email: String = "", otp: String, token: String) = Intent(activity,
            ResetPasswordActivity::class.java).apply {
            putExtra(INTENT_EMAIL, email)
            putExtra(INTENT_OTP, otp)
            putExtra(INTENT_TOKEN, token)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBindingView(ActivityResetPasswordBinding.inflate(layoutInflater))

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button press here
                // For example, you can navigate back or show a confirmation dialog
                startActivity(Intent(this@ResetPasswordActivity, LoginActivity::class.java).addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                finish()
            }
        }

        // Add the onBackPressedCallback to the onBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun initComponents() {
        setToolbar()
        setThemeForTextInputLayout(binding.tlPassword)
        setThemeForTextInputLayout(binding.tlConfirmPassword)
        setThemeForView(binding.btnSetPassword)

        email = intent.getStringExtra(INTENT_EMAIL).toString()
        otp = intent.getStringExtra(INTENT_OTP).toString()
        token = intent.getStringExtra(INTENT_TOKEN).toString()

        binding.tetConfirmPassword.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.tetPassword.transformationMethod = AsteriskPasswordTransformationMethod()
        val spannableNote = SpannableString(binding.tvNote.text.toString())
        val startIndex = if (BaseApplication.languageSharedPreference.getLanguagePref(EasyPref.CURRENT_LANGUAGE, "").equals("de", true)) {
            binding.tvNote.text.toString().indexOf("Hinweis:")
        }else{
            binding.tvNote.text.toString().indexOf("Note:")
        }

        val endIndex =if (BaseApplication.languageSharedPreference.getLanguagePref(EasyPref.CURRENT_LANGUAGE, "").equals("de", true)) {
            startIndex + 8// Length of "Note:" plus one space
        }else{
            startIndex + 5
        }
//        val endIndex = startIndex + 5 // Length of "Note:" plus one space

        spannableNote.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvNote.text = spannableNote

    }

    /**
     * This method contains code for all click listeners in the activity
     *
     */
    override fun setClickListener() {
        binding.apply {
            imgPasswordToggle.setOnClickListener {
                handleTogglePasswordImage()
            }
            imgConfirmPasswordToggle.setOnClickListener {
                handleToggleConfirmImage()
            }
            btnSetPassword.setOnClickListener {
                if (isValid()) {
                    viewModel.callResetPasswordApi(email, otp, tetPassword.getTrimText(), tetConfirmPassword.getTrimText(), token, getAndroidDeviceId())
                }
            }
        }
    }

    private fun setToolbar() {
        binding.toolbar.let {
            overrideToolbar(it, ToolbarConfig().apply {
                showBackButton = true
                showWhiteBg = false
            })

            it.ivBack.clickWithDebounce {
                startActivity(Intent(this@ResetPasswordActivity, LoginActivity::class.java).addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                finish()
            }
        }
    }
    override fun attachObservers() {
        viewModel.resetPasswordLiveData.observe(this) {
            if (it.isSuccess) {
                callSuccessDialog()
            }
        }
        viewModel.errorLiveData.observe(this){
            showErrorMessage(it.toString())
        }
    }

    private fun callSuccessDialog() {
        DialogUtil.showDialog(supportFragmentManager, getString(R.string.password_changed),
            getString(R.string.your_password_has_been_updated_successfully_login_with_the_new_password_to_continue),
            getString(R.string.continue_to_login), "", il = object : DialogUtil.IL {
                override fun onSuccess() {
                    startActivity(Intent(this@ResetPasswordActivity, LoginActivity::class.java).addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                    finish()
                }

                override fun onCancel(isNeutral: Boolean) {

                }
            }, image = getDrawableFromAttr(R.attr.imgPasswordChanged), isCancelShow = false)
    }

    private fun handleTogglePasswordImage() {
        if (viewModel.isTogglePassShow) {
            viewModel.isTogglePassShow = false
            binding.imgPasswordToggle.setImageResource(R.drawable.ic_eye_on)
            binding.tetPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            viewModel.isTogglePassShow = true
            binding.imgPasswordToggle.setImageResource(R.drawable.ic_eye_off)
            binding.tetPassword.transformationMethod = AsteriskPasswordTransformationMethod() //PasswordTransformationMethod.getInstance()
        }
        binding.tetPassword.setSelection(binding.tetPassword.length())
    }

    private fun handleToggleConfirmImage() {
        if (viewModel.isToggleConfirmShow) {
            viewModel.isToggleConfirmShow = false
            binding.imgConfirmPasswordToggle.setImageResource(R.drawable.ic_eye_on)
            binding.tetConfirmPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            viewModel.isToggleConfirmShow = true
            binding.imgConfirmPasswordToggle.setImageResource(R.drawable.ic_eye_off)
            binding.tetConfirmPassword.transformationMethod = AsteriskPasswordTransformationMethod() //PasswordTransformationMethod.getInstance()
        }
        binding.tetConfirmPassword.setSelection(binding.tetConfirmPassword.length())
    }

    private fun isValid(): Boolean {
        var valid=true
        binding.tvPasswordError.isVisible = false
        binding.tvConfirmPasswordError.isVisible = false
        if (!binding.tetPassword.getTrimText().isValidPassword()){
            binding.tvPasswordError.isVisible = true
            binding.tvPasswordError.text = getString(R.string.please_enter_the_password_in_a_valid_format)
            valid=false
        }
        if (binding.tetPassword.getTrimText().isEmpty()){
            binding.tvPasswordError.isVisible = true
            binding.tvPasswordError.text = getString(R.string.please_enter_the_new_password)
            valid=false
        }
        if ( binding.tetConfirmPassword.getTrimText() != binding.tetPassword.getTrimText()){
            binding.tvConfirmPasswordError.isVisible = true
            binding.tvConfirmPasswordError.text = getString(R.string.please_enter_the_password_the_same_as_the_new_password_field)
            valid=false
        }
        if (binding.tetConfirmPassword.getTrimText().isEmpty()){
            binding.tvConfirmPasswordError.isVisible = true
            binding.tvConfirmPasswordError.text = getString(R.string.please_enter_the_new_password)
            valid=false
        }
        /*when {
            binding.tetPassword.getTrimText().isEmpty() -> {
                binding.tvPasswordError.isVisible = true
                binding.tvPasswordError.text = getString(R.string.please_enter_the_new_password)
                return false
            }

            !binding.tetPassword.getTrimText().isValidPassword() -> {
                binding.tvPasswordError.isVisible = true
                binding.tvPasswordError.text = getString(R.string.please_enter_the_password_in_a_valid_format)
                return false
            }

            binding.tetConfirmPassword.getTrimText().isEmpty() -> {
                binding.tvConfirmPasswordError.isVisible = true
                binding.tvConfirmPasswordError.text = getString(R.string.please_enter_the_new_password)
                return false
            }

            binding.tetConfirmPassword.getTrimText() != binding.tetPassword.getTrimText() -> {
                binding.tvConfirmPasswordError.isVisible = true
                binding.tvConfirmPasswordError.text = getString(R.string.please_enter_the_password_the_same_as_the_new_password_field)
                return false
            }
        }*/
        return valid
    }
}