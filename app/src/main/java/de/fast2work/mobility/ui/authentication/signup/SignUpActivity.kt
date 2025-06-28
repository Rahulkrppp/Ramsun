package de.fast2work.mobility.ui.authentication.signup

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import de.fast2work.mobility.R
import de.fast2work.mobility.data.request.SignUpReq
import de.fast2work.mobility.databinding.ActivitySignUpBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingActivity
import de.fast2work.mobility.utility.customview.AsteriskPasswordTransformationMethod
import de.fast2work.mobility.utility.customview.countrypicker.CountryPicker
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.dialog.DialogUtil
import de.fast2work.mobility.utility.extension.CustomTypefaceSpan
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.getString
import de.fast2work.mobility.utility.extension.getTrimText
import de.fast2work.mobility.utility.extension.isValidEmail
import de.fast2work.mobility.utility.extension.isValidPassword
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.preference.EasyPref
import de.fast2work.mobility.utility.util.IConstantsIcon
import de.fast2work.mobility.utility.util.LocalConfig


class SignUpActivity :
    BaseVMBindingActivity<ActivitySignUpBinding, SignUpViewModule>(SignUpViewModule::class.java) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBindingView(ActivitySignUpBinding.inflate(layoutInflater))

    }

    override fun attachObservers() {
        viewModel.signUpLiveData.observe(this) {
            DialogUtil.showDialog(supportFragmentManager,
                "",
                getString(R.string.your_account_has_been_created_a_verification_link_has_been_sent_to_your_email_please_verify_your_email_to_proceed),
                getString(R.string.ok),
                "",
                il = object : DialogUtil.IL {
                    override fun onSuccess() {

                    }

                    override fun onCancel(isNeutral: Boolean) {

                    }
                },
                null,
                isCancelShow = false,
                isTitleShow = View.GONE
            )
        }
        viewModel.staticPageLiveData.observe(this) { response ->
            if (response?.data != null) {
                viewModel.staticPage = response.data!!
                viewModel.employeeReportPdfFile= response.data!!.pageContentPdf.toString()
                viewModel.employeeReportPdfFileDE= response.data!!.pageContentPdfDe.toString()


            } else {
                //binding?.loadingView?.error(response.settings?.message)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initComponents() {
        setToolbar()
        setBoldAndColorSpannable(
            binding.tvTermsConditions,
            getString(R.string.terms_condition_sign_up)
        )

        val spannableNote = SpannableString(binding!!.tvNote.text.toString())
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
        spannableNote.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvNote.text = spannableNote
        setBoldAndColorSpannable1(binding.tvSignup, getString(R.string.login))
        setThemeForView(binding.btnSignUp)
        /*binding.ivCountryCodeImageSet.setImageBitmap(
            CountryPicker.loadImageFromAssets(
                this,
                "DE".toBlankString()
            )
        )*/
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
        if (BaseApplication.sharedPreference.getPref("term",false )){
            viewModel.termsAccepted = true
            binding.ivAccepted.isVisible = true
        }else{
            viewModel.termsAccepted = false
            binding.ivAccepted.isVisible = false
        }
    }

    override fun setClickListener() {
        binding.apply {
            imgPasswordToggle.setOnClickListener {
                handleTogglePasswordImage()
            }
            imgConfirmPasswordToggle.setOnClickListener {
                handleToggleConfirmImage()
            }
            btnSignUp.clickWithDebounce {
                if (isValid()) {
                    BaseApplication.sharedPreference.setPref("term", false)
                    val signUpReqParam = SignUpReq().apply {
                        this.firstName = telFirstName.text.toString()
                        this.lastName = telLastName.text.toString()
                        this.email = telEmail.text.toString()
                        //this.mobileNo = telPhoneNo.text.toString()
                        this.password = telPassword.text.toString()
                        this.isTermsAccepted = viewModel.termsAccepted
                        //this.countryCode = viewModel.countryCode
                    }
                    viewModel.callCreateAccountApi(signUpReqParam)
                }
            }

        /*    tlPhoneCode.clickWithDebounce {
                openCountrySheetBottomSheet()
            }
            telPhoneCode.clickWithDebounce {
                openCountrySheetBottomSheet()
            }*/
          /*  ivCountryCodeImageSet.clickWithDebounce {
                openCountrySheetBottomSheet()
            }*/

        }
    }

    /**
     * This method contains openCountrySheetBottomSheet
     *
     */


    private fun setToolbar() {
        binding.customToolbar.let {
            overrideToolbar(it, ToolbarConfig().apply {
                showBackButton = true
                showWhiteBg = false
            })

            it.ivBack.clickWithDebounce {
                BaseApplication.sharedPreference.setPref("term", false)
                finish()
            }

        }
    }

    /**
     * This method contains code for all the valid msg display in our app
     *
     */
    private fun isValid(): Boolean {
        var valid = true
        binding.apply {

            tvFirstError.isVisible = false
            tvLastError.isVisible = false
            tvEmailError.isVisible = false
            //tvPhoneError.isVisible = false
            tvPasswordError.isVisible = false
            tvConfirmPasswordError.isVisible = false

            if (telFirstName.getTrimText().isEmpty()) {
                tvFirstError.isVisible = true
                tvFirstError.text = getString(R.string.first_name_is_required)
                valid = false
            }

            if (telLastName.getTrimText().isEmpty()) {
                tvLastError.isVisible = true
                tvLastError.text = getString(R.string.the_last_name_is_required)
                valid = false
            }

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
            /* if (viewModel.countryCode.isEmpty()){
                 tvPhoneError.isVisible = true
                 tvPhoneError.text = "Please set country code"
                 valid = false
             }else*/ /*if (telPhoneNo.getTrimText().isEmpty()) {
            tvPhoneError.isVisible = true
            tvPhoneError.text = getString(R.string.a_phone_number_is_required)
            valid = false
        }*/

            if (!binding.telPassword.getTrimText().isValidPassword()) {
                binding.tvPasswordError.isVisible = true
                binding.tvPasswordError.text =
                    getString(R.string.please_enter_the_password_in_a_valid_format)
                valid = false
            }
            if (binding.telPassword.getTrimText().isEmpty()) {
                binding.tvPasswordError.isVisible = true
                binding.tvPasswordError.text = getString(R.string.please_enter_the_new_password)
                valid = false
            }
            if (binding.tetConfirmPassword.getTrimText() != binding.telPassword.getTrimText()) {
                binding.tvConfirmPasswordError.isVisible = true
                binding.tvConfirmPasswordError.text =
                    getString(R.string.please_enter_the_confirm_password)
                valid = false
            }
            if (binding.tetConfirmPassword.getTrimText().isEmpty()) {
                binding.tvConfirmPasswordError.isVisible = true
                binding.tvConfirmPasswordError.text =
                    getString(R.string.confirm_password_cannot_be_empty)
                valid = false
            } else if (!viewModel.termsAccepted) {
                showErrorMessage(getString(R.string.please_read_and_accept_the_terms_and_conditions_first))
                valid = false
            }

        }
        return valid

    }

    private fun handleTogglePasswordImage() {
        if (viewModel.isTogglePassShow) {
            viewModel.isTogglePassShow = false
            binding.imgPasswordToggle.setImageResource(R.drawable.ic_eye_on)
            binding.telPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            viewModel.isTogglePassShow = true
            binding.imgPasswordToggle.setImageResource(R.drawable.ic_eye_off)
            binding.telPassword.transformationMethod =
                AsteriskPasswordTransformationMethod() //PasswordTransformationMethod.getInstance()
        }
        binding.telPassword.setSelection(binding.telPassword.length())
    }

    private fun handleToggleConfirmImage() {
        if (viewModel.isToggleConfirmShow) {
            viewModel.isToggleConfirmShow = false
            binding.imgConfirmPasswordToggle.setImageResource(R.drawable.ic_eye_on)
            binding.tetConfirmPassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
        } else {
            viewModel.isToggleConfirmShow = true
            binding.imgConfirmPasswordToggle.setImageResource(R.drawable.ic_eye_off)
            binding.tetConfirmPassword.transformationMethod =
                AsteriskPasswordTransformationMethod() //PasswordTransformationMethod.getInstance()
        }
        binding.tetConfirmPassword.setSelection(binding.tetConfirmPassword.length())
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
                            getString(R.string.terms_condition_sign_up) -> {

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
                        ResourcesCompat.getFont(this, R.font.poppins_medium_500)!!
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
                            getString(R.string.login) -> {

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