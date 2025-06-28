package de.fast2work.mobility.ui.authentication.url

import android.content.Intent
import android.content.res.Resources
import android.graphics.Typeface


import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import de.fast2work.mobility.R
import de.fast2work.mobility.databinding.ActivityTenantLoginBinding
import de.fast2work.mobility.ui.authentication.login.LoginActivity
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingActivity
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.getTrimText
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.preference.EasyPref
import de.fast2work.mobility.utility.util.IConstants.Companion.TAG
import java.net.URI
import java.util.concurrent.Executor

/**
 * This class contains code for TenantLoginActivity
 */
@AndroidEntryPoint
class TenantLoginActivity : BaseVMBindingActivity<ActivityTenantLoginBinding, TenantViewModel>(TenantViewModel::class.java) {
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBindingView(ActivityTenantLoginBinding.inflate(layoutInflater))


//        val biometricManager = BiometricManager.from(this)
//
//        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
//                BiometricManager.Authenticators.DEVICE_CREDENTIAL
//
//        when (biometricManager.canAuthenticate(authenticators)) {
//            BiometricManager.BIOMETRIC_SUCCESS -> {
//                showBiometricPrompt()
//            }
//            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
//                // No biometric features available
//            }
//            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
//                // Biometric hardware currently unavailable
//            }
//            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
//                // User has not enrolled any biometrics
//            }
//            else -> {
//                // Unknown or other error
//            }
//        }
//        val biometricManager = BiometricManager.from(this)
//        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG
//
//        when (biometricManager.canAuthenticate(authenticators)) {
//            BiometricManager.BIOMETRIC_SUCCESS -> {
//                // Face or fingerprint is ready
//                showBiometricPrompt()
//            }
//            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
//                // User has not enrolled any biometric (face or fingerprint)
//            }
//            else -> {
//                // Hardware unavailable or unsupported
//            }
//        }

//        checkBiometricSupport()


    }
    private fun checkBiometricSupport() {
        val biometricManager = BiometricManager.from(this)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL

        when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
//                showBiometricPrompt(authenticators)
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Ask user to enroll fingerprint/face
            }
            else -> {
                // Unsupported or unavailable
            }
        }
    }
    private fun showBiometricPrompt() {
        
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Face or Fingerprint Unlock")
            .setSubtitle("Use your biometric credential to authenticate")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .setNegativeButtonText("Cancel")
            .build()
//        val promptInfo = BiometricPrompt.PromptInfo.Builder()
//            .setTitle("Face or Fingerprint Unlock")
//            .setSubtitle("Use your biometric credential to authenticate")
//            .setAllowedAuthenticators(
//                BiometricManager.Authenticators.BIOMETRIC_STRONG or
//                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
//            )
//            .setNegativeButtonText("Cancel")
//            .build()

        val biometricPrompt = BiometricPrompt(this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // ✅ Auth successful (via face or fingerprint)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // ❌ Auth error
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // ❗ Not recognized
                }
            }
        )

        biometricPrompt.authenticate(promptInfo)
    }

    /**
     * This method handles api call response for all the apis
     *
     */
    override fun attachObservers() {
        viewModel.tenantInfoLiveData.observe(this) {
            if (it.isSuccess) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        viewModel.errorLiveData.observe(this){
            showErrorMessage(it.toBlankString())
        }

    }

    /**
     * This method contains code to handle initial operation required for login activity
     *
     */
    override fun initComponents() {
        setThemeForTextInputLayout(binding.tlUrl,true)
        val spannableNote = SpannableString(binding.tvNote.text.toBlankString())
        val startIndex = if (BaseApplication.languageSharedPreference.getLanguagePref(EasyPref.CURRENT_LANGUAGE, "").equals("de", true)) {
            binding.tvNote.text.toBlankString().indexOf("Hinweis:")
        }else{
            binding.tvNote.text.toBlankString().indexOf("Note:")
        }

        val endIndex =if (BaseApplication.languageSharedPreference.getLanguagePref(EasyPref.CURRENT_LANGUAGE, "").equals("de", true)) {
            startIndex + 8// Length of "Note:" plus one space
        }else{
            startIndex + 5
        }

        spannableNote.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvNote.text = spannableNote


    }
    /**
     * This method contains code for all the clickListener in our app
     *
     */
    override fun setClickListener() {
        binding.btnConnect.setOnClickListener {
            if (isValid()){
//                viewModel.callGetTenantThemeApi(getCompanyName(binding.telUrl.getTrimText()).toBlankString())
                viewModel.callGetTenantThemeApi(binding.telUrl.getTrimText())
            }
        }
    }
    /**
     * This method contains code for all the valid msg display in our app
     *
     */
    private fun isValid(): Boolean {
        binding.tvUrlError.isVisible=false
        when {
            binding.telUrl.getTrimText().isEmpty() -> {
                binding.tvUrlError.isVisible=true
                binding.tvUrlError.text= getString(R.string.please_enter_the_platform_url)
                return false
            }
        }

        return true
    }

}