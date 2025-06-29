package de.fast2work.mobility.UI1.login1

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import de.fast2work.mobility.R
import de.fast2work.mobility.data.request.LoginRequest
import de.fast2work.mobility.data.request.SignUpReq
import de.fast2work.mobility.databinding.ActivityLoginBinding
import de.fast2work.mobility.databinding.ActivityLoginScreenBinding
import de.fast2work.mobility.ui.core.BaseVMBindingActivity

class LoginScreen: BaseVMBindingActivity<ActivityLoginScreenBinding, LoginScreenViewModel>(LoginScreenViewModel::class.java) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBindingView(ActivityLoginScreenBinding.inflate(layoutInflater))
    }

    override fun attachObservers() {
        viewModel.loginLiveData.observe(this) {
            Log.e("", "attachObservers: ${it.data}",)
        }
    }

    override fun initComponents() {

    }

    override fun setClickListener() {
        binding.btnLogin.clickWithDebounce {
            val signUpReqParam = LoginRequest().apply {
                this.email = binding.telEmail.text.toString()
                this.password = binding.telPassword.text.toString()
                this.password = binding.telPassword.text.toString()

                //this.countryCode = viewModel.countryCode
            }
            viewModel.callCreateAccountApi(signUpReqParam)
        }
    }
}