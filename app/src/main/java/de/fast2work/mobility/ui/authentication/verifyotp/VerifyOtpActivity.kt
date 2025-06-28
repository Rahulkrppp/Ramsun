package de.fast2work.mobility.ui.authentication.verifyotp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.window.OnBackInvokedDispatcher
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import de.fast2work.mobility.R
import de.fast2work.mobility.databinding.ActivityVerifyOtpBinding
import de.fast2work.mobility.ui.authentication.resetpassword.ResetPasswordActivity
import de.fast2work.mobility.ui.core.BaseVMBindingActivity
import de.fast2work.mobility.ui.dashboard.DashboardActivity
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.extension.getAndroidDeviceId
import de.fast2work.mobility.utility.util.LocalConfig.RESEND_OTP_DURATION
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * This class contains code for verify otp
 *
 */
@AndroidEntryPoint
class VerifyOtpActivity : BaseVMBindingActivity<ActivityVerifyOtpBinding, VerifyOtpViewModel>(VerifyOtpViewModel::class.java) {

    companion object {
        const val INTENT_EMAIL = "email"
        const val INTENT_LOGIN_EMAIL = "loginEmail"
        const val INTENT_TWO_FACTOR_ENABLED = "twoFactorEnabled"
        const val INTENT_TOKEN = "token"
        const val INTENT_PASSWORD = "password"

        fun newInstance(
            activity: Activity, email: String = "", loginEmail: String= "", password: String = "", twoFactorEnabled: String = "",
            token: String = "",
        ) = Intent(activity, VerifyOtpActivity::class.java).apply {
            putExtra(INTENT_EMAIL, email)
            putExtra(INTENT_LOGIN_EMAIL, loginEmail)
            putExtra(INTENT_PASSWORD, password)
            putExtra(INTENT_TWO_FACTOR_ENABLED, twoFactorEnabled)
            putExtra(INTENT_TOKEN, token)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBindingView(ActivityVerifyOtpBinding.inflate(layoutInflater))
    }

    /**
     * This class contains code for initial view
     *
     */
    override fun initComponents() {
        setToolbar()
        setThemeForView(binding.btnSubmit)
        setThemeForTextView(binding.tvResendOtp)

        viewModel.email = intent.getStringExtra(INTENT_EMAIL).toString()
        viewModel.loginEmail = intent.getStringExtra(INTENT_LOGIN_EMAIL).toString()
        viewModel.twoFactorEnabled = intent.getStringExtra(INTENT_TWO_FACTOR_ENABLED).toString()
        viewModel.token = intent.getStringExtra(INTENT_TOKEN).toString()
        viewModel.password = intent.getStringExtra(INTENT_PASSWORD).toString()

        setClickableSpan()
        setOtpTimer()
    }

    /**
     * This method contains all the screens in this activity
     *
     */
    override fun setClickListener() {
        binding.apply {
            btnSubmit.clickWithDebounce {
                if (customOtp.isValid()){
                    binding.tvOtpError.isVisible=false
                    if (viewModel.twoFactorEnabled.equals("Yes", true)) {
                        viewModel.call2FALoginApi(viewModel.email, customOtp.pairingCode, viewModel.token, getAndroidDeviceId())
                    } else {
                        viewModel.callVerifyOtpApi(viewModel.email, customOtp.pairingCode, getAndroidDeviceId())
                    }
                }else{
                    binding.tvOtpError.isVisible=true
                    binding.tvOtpError.text= getString(R.string.please_enter_the_otp)
                }
            }
            tvResendOtp.clickWithDebounce {
                setOtpTimer()
                if (viewModel.twoFactorEnabled.equals("Yes", true)) {
                    viewModel.callResend2FAApi(viewModel.loginEmail, viewModel.password)
                } else {
                    viewModel.callReSendOtpApi(viewModel.email, getAndroidDeviceId())
                }
            }
        }
    }

    private fun setClickableSpan() {

    }

    /**
     * This method handles all the api call response
     *
     */
    override fun attachObservers() {
        viewModel.verifyOtpLiveData.observe(this) {
            if (it.isSuccess) {
                startActivity(ResetPasswordActivity.newInstance(this, viewModel.email, binding.customOtp.pairingCode, it.data?.token.toString()))
            }
        }

        viewModel.login2FALiveData.observe(this) {
            if (it.isSuccess) {
                startActivity(Intent(this@VerifyOtpActivity, DashboardActivity::class.java))
            }
        }

        viewModel.errorLiveData.observe(this) {
            showErrorMessage(it)
        }

        viewModel.resendOtpLiveData.observe(this) { response ->
            if(response.isSuccess){
                binding.customOtp.clear()
//                showSuccessMessage(getString(R.string.otp_send_successfully))
            }
        }

        viewModel.resendLogin2FALiveData.observe(this) { response ->
            if(response.isSuccess){
                viewModel.token = response.data?.token.toString()
                viewModel.email = response.data?.email.toString()
                binding.customOtp.clear()
//                showSuccessMessage(getString(R.string.otp_send_successfully))
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
                finish()
            }
        }
    }

    private fun setOtpTimer() {
        startEndTimer(TimeUnit.SECONDS.toMillis(RESEND_OTP_DURATION))
    }

    /**
     * This function is used to start and stop the timer
     *
     * @param millisInFuture
     */
    private fun startEndTimer(millisInFuture: Long) {
        viewModel.timer = object : CountDownTimer(millisInFuture, viewModel.countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
//                binding.tvResendOtp.visibility = View.VISIBLE
                binding.tvResendOtp.isEnabled = false
//                binding.tvResendOtp.alpha = 0.5f
                timeString(millisUntilFinished)
            }

            override fun onFinish() {
//                binding.tvResendOtp.visibility = View.VISIBLE
//                binding.tvCounter.visibility = View.VISIBLE
                binding.tvResendOtp.isEnabled = true
//                binding.tvResendOtp.alpha = 1f
                binding.tvResendOtp.text = getString(R.string.resend_passcode)
            }
        }
        viewModel.timer?.start()
    }

    private fun timeString(millisUntilFinished: Long) {
        var millisUntilFinishedLocal: Long = millisUntilFinished
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinishedLocal)
        millisUntilFinishedLocal -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinishedLocal)

        binding.tvResendOtp.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}