package de.fast2work.mobility.ui.authentication.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import de.fast2work.mobility.R
import de.fast2work.mobility.data.request.StaticPageReq
import de.fast2work.mobility.databinding.ActivityLoginBinding
import de.fast2work.mobility.ui.authentication.forgetPassword.ForgetPasswordActivity
import de.fast2work.mobility.ui.authentication.signup.SignUpActivity
import de.fast2work.mobility.ui.authentication.url.TenantLoginActivity
import de.fast2work.mobility.ui.authentication.verifyotp.VerifyOtpActivity
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingActivity
import de.fast2work.mobility.ui.dashboard.DashboardActivity
import de.fast2work.mobility.ui.sidemenu.staticpage.StaticPageActivity
import de.fast2work.mobility.utility.customview.AsteriskPasswordTransformationMethod
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.extension.CustomTypefaceSpan
import de.fast2work.mobility.utility.extension.getAndroidDeviceId
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.getTrimText
import de.fast2work.mobility.utility.extension.isValidEmail
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.preference.EasyPref.Companion.FCM_KEY
import de.fast2work.mobility.utility.util.LocalConfig


/**
 * Activity used for login with Username / Email and google
 * */
@AndroidEntryPoint
class LoginActivity :
    BaseVMBindingActivity<ActivityLoginBinding, LoginViewModel>(LoginViewModel::class.java) {

    companion object {
        fun newInstance(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }

        private var isTogglePassShow = true
        fun newTaskIntent(context: Context): Intent {
            val intentX = Intent(context, LoginActivity::class.java)
            intentX.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            return intentX
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBindingView(ActivityLoginBinding.inflate(layoutInflater))
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        // setTheme()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getNotificationPermission()
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            redirectToTenantLoginActivity()
        }
    }


    /**
     * This method contains code to handle initial operation required for login activity
     *
     */

    @SuppressLint("SetTextI18n")
    override fun initComponents() {
        setToolbar()
        setThemeForTextInputLayout(binding.tlEmail)
        setThemeForTextInputLayout(binding.tlPassword)
        setThemeForView(binding.btnLogin)
        setThemeForTextView(binding.tvForgot)
        setBoldAndColorSpannable(
            binding.tvTermsConditions,
            getString(R.string.terms_of_use),
            getString(R.string.title_privacy_policy)
        )
        setBoldAndColorSpannable1(binding.tvSignup, getString(R.string.create_now))
        binding.telPassword.transformationMethod = AsteriskPasswordTransformationMethod()
        if (tenantInfoData?.tenantInfo?.enabledServices.equals(LocalConfig.co2_management, true)) {
            binding.tvSubtitle.text =
                getString(R.string.to) + tenantInfoData?.tenantInfo?.tenantName.toBlankString() + " " + getString(
                    R.string.steigum_de_business
                )
            setBoldAndColorSpannableUrl(binding.tvSubtitle, getString(R.string.steigum_de))
        } else if (tenantInfoData?.tenantInfo?.enabledServices.equals(
                LocalConfig.mobility_budget,
                true
            )
        ) {
            binding.tvSubtitle.text =
                getString(R.string.to) + tenantInfoData?.tenantInfo?.tenantName.toBlankString() + " " + getString(
                    R.string.mobility_budget
                )
        } else {
            binding.tvSubtitle.text =
                getString(R.string.to) + tenantInfoData?.tenantInfo?.tenantName.toString() + " " + getString(
                    R.string.mobility_sustainability_platform
                )

        }

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            BaseApplication.sharedPreference.setPref(FCM_KEY, it)
        }

    }

    private fun setToolbar() {
        binding.toolbar.let {
            overrideToolbar(it, ToolbarConfig().apply {
                showBackButton = true
                showWhiteBg = false
            })

            it.ivBack.clickWithDebounce {
                redirectToTenantLoginActivity()
            }
        }
    }

    private fun redirectToTenantLoginActivity() {
        BaseApplication.tenantSharedPreference.clearAll()
        val intent = Intent(this, TenantLoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * This function checks notification permission to get notification in our app (e.g. Notification permission is required in Android 13 in above devices)
     *
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun getNotificationPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) -> {
                // You can use the API that requires the permission.
            }

            else -> {
                // The registered ActivityResultCallback gets the result of this request
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _: Boolean ->
        }

    /**
     * This method contains code for all the clickListener in our app
     *
     */
    override fun setClickListener() {
        binding.apply {
            imgPasswordToggle.setOnClickListener {
                handleToggleImage()
            }
            btnLogin.setOnClickListener {
                if (isValid()) {
                    viewModel.callLoginApi(
                        telEmail.text.toString(),
                        telPassword.text.toString(),
                        getAndroidDeviceId()
                    )
                }
            }
            tvForgot.setOnClickListener {
                startActivity(Intent(this@LoginActivity, ForgetPasswordActivity::class.java))
            }
        }
    }

    /**
     * This method handles api call response for all the apis
     *
     */
    override fun attachObservers() {

        viewModel.loginLiveData.observe(this) {
            if (it.isSuccess) {
                if (it.data?.twoFactorEnabled.toString().equals("Yes", true)) {
                    startActivity(
                        VerifyOtpActivity.newInstance(
                            this@LoginActivity,
                            it.data?.email.toString(),
                            binding.telEmail.getTrimText(),
                            binding.telPassword.getTrimText(),
                            it.data?.twoFactorEnabled.toString(),
                            it.data?.token.toString()
                        )
                    )
                } else {
                    startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                }
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
    private fun handleToggleImage() {
        if (isTogglePassShow) {
            isTogglePassShow = false
            binding.imgPasswordToggle.setImageResource(R.drawable.ic_eye_on)
            binding.telPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            isTogglePassShow = true
            binding.imgPasswordToggle.setImageResource(R.drawable.ic_eye_off)
            binding.telPassword.transformationMethod =
                AsteriskPasswordTransformationMethod() //PasswordTransformationMethod.getInstance()
        }
        binding.telPassword.setSelection(binding.telPassword.length())
    }

    /**
     * This method contains code for all the valid msg display in our app
     *
     */
    private fun isValid(): Boolean {
        var valid = true
        binding.tvEmailError.isVisible = false
        binding.tvPasswordError.isVisible = false
        if (!binding.telEmail.getTrimText().isValidEmail()) {
            binding.tvEmailError.isVisible = true
            binding.tvEmailError.text = getString(R.string.please_enter_a_valid_email_address)
            valid = false
        }
        if (binding.telEmail.getTrimText().isEmpty()) {
            binding.tvEmailError.isVisible = true
            binding.tvEmailError.text = getString(R.string.please_enter_the_email_address)
            valid = false
        }
        if (binding.telPassword.getTrimText().isEmpty()) {
            binding.tvPasswordError.isVisible = true
            binding.tvPasswordError.text = getString(R.string.please_enter_the_password)
            valid = false
        }
        return valid
    }

    /**
     * This method contains code for all the setBoldAndColorSpannable
     *
     */
    private fun setBoldAndColorSpannable(textView: TextView, vararg portions: String) {
        val label = textView.text.toString()
        val spannableString1 = SpannableString(label)
        for (portion in portions) {
            val startIndex = label.indexOf(portion)
            val endIndex = startIndex + portion.length
            try {
                spannableString1.setSpan(object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        when (portion) {
                            getString(R.string.terms_of_use) -> {
                                startActivity(
                                    StaticPageActivity.newInstance(
                                        this@LoginActivity,
                                        StaticPageReq.TERMSOFUSE
                                    )
                                )
                            }

                            getString(R.string.title_privacy_policy) -> {
                                startActivity(
                                    StaticPageActivity.newInstance(
                                        this@LoginActivity,
                                        StaticPageReq.PRIVACYPOLICY
                                    )
                                )
                            }

                        }
                    }

                    override fun updateDrawState(ds: TextPaint) {// override updateDrawState
                        ds.isUnderlineText = true // set to false to remove underline
                    }

                }, startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

                spannableString1.setSpan(
                    ForegroundColorSpan(getColorFromAttr(R.attr.colorTerm)), startIndex, endIndex,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
//                spannableString1.setSpan(CustomTypefaceSpan("", ResourcesCompat.getFont(this, R.font.roboto_medium_500)!!), startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                textView.movementMethod = LinkMovementMethod.getInstance()
                textView.highlightColor = Color.TRANSPARENT
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        textView.text = spannableString1
    }


    /**
     * This method contains code for all the setBoldAndColorSpannable
     *
     */
    private fun setBoldAndColorSpannable1(textView: TextView, vararg portions: String) {
        val label = textView.text.toString()
        val spannableString1 = SpannableString(label)
        for (portion in portions) {
            val startIndex = label.indexOf(portion)
            val endIndex = startIndex + portion.length
            try {
                spannableString1.setSpan(object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        when (portion) {
                            getString(R.string.create_now) -> {
                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        SignUpActivity::class.java
                                    )
                                )
                            }


                        }
                    }

                    override fun updateDrawState(ds: TextPaint) {// override updateDrawState
                        ds.isUnderlineText = false // set to false to remove underline
                    }

                }, startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

                spannableString1.setSpan(
                    ForegroundColorSpan(getColorFromAttr(com.google.android.material.R.attr.editTextColor)),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )

                spannableString1.setSpan(
                    CustomTypefaceSpan(
                        "",
                        ResourcesCompat.getFont(this, R.font.poppins_semi_bold_600)!!
                    ), startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                textView.movementMethod = LinkMovementMethod.getInstance()
                textView.highlightColor = Color.TRANSPARENT
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        textView.text = spannableString1
    }

    /**
     * This method contains code for all the setBoldAndColorSpannableUrl
     *
     */
    private fun setBoldAndColorSpannableUrl(textView: TextView, vararg portions: String) {
        val label = textView.text.toString()
        val spannableString1 = SpannableString(label)
        for (portion in portions) {
            val startIndex = label.indexOf(portion)
            val endIndex = startIndex + portion.length
            try {
                spannableString1.setSpan(object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        when (portion) {
                            getString(R.string.steigum_de) -> {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://www.steigum.de/en/")
                                    )
                                )
                            }
                        }
                    }

                    override fun updateDrawState(ds: TextPaint) {// override updateDrawState
                        ds.isUnderlineText = true // set to false to remove underline
                    }

                }, startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

                if (BaseApplication.themeValue == Configuration.UI_MODE_NIGHT_YES) {
                    spannableString1.setSpan(
                        ForegroundColorSpan(getColorFromAttr(com.google.android.material.R.attr.colorPrimary)),
                        startIndex,
                        endIndex,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                } else {
                    spannableString1.setSpan(
                        ForegroundColorSpan(Color.parseColor(tenantInfoData?.brandingInfo?.primaryColor)),
                        startIndex,
                        endIndex,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                }
                textView.movementMethod = LinkMovementMethod.getInstance()
                textView.highlightColor = Color.TRANSPARENT
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        textView.text = spannableString1
    }
}