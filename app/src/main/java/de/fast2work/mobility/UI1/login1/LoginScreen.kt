package de.fast2work.mobility.UI1.login1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import de.fast2work.mobility.R
import de.fast2work.mobility.databinding.ActivityLoginBinding
import de.fast2work.mobility.databinding.ActivityLoginScreenBinding
import de.fast2work.mobility.ui.core.BaseVMBindingActivity

class LoginScreen: BaseVMBindingActivity<ActivityLoginScreenBinding, LoginScreenViewModel>(LoginScreenViewModel::class.java) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBindingView(ActivityLoginScreenBinding.inflate(layoutInflater))
    }

    override fun attachObservers() {

    }

    override fun initComponents() {

    }

    override fun setClickListener() {

    }
}