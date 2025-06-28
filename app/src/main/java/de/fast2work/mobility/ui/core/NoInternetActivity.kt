package de.fast2work.mobility.ui.core

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import de.fast2work.mobility.databinding.ActivityNoInternetBinding
import de.fast2work.mobility.utility.helper.ConnectionLiveData
import dagger.hilt.android.AndroidEntryPoint
import de.fast2work.mobility.utility.extension.backgroundColorTint
import de.fast2work.mobility.utility.extension.imageTickTint

/**
 *  Activity used for no internet alert
 */
@AndroidEntryPoint
class NoInternetActivity : BaseBindingActivity<ActivityNoInternetBinding>() {

    companion object {
        fun createIntent(context: Context): Intent {
            val intentX = Intent(context, NoInternetActivity::class.java)
            intentX.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            return intentX
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setBindingView(R.layout.activity_no_internet)
        setBindingView(ActivityNoInternetBinding.inflate(layoutInflater))
        makeFullScreen()
        binding.ivNoData.imageTickTint(tenantInfoData?.brandingInfo?.primaryColor)
        binding.tvOpenSetting.backgroundColorTint(tenantInfoData?.brandingInfo?.primaryColor)
        binding.tvCloseApp.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.tvOpenSetting.setOnClickListener {
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }
        /**
         * This live Data Fires when user is disconnected from data or wifi
         */
        ConnectionLiveData(this).observe(this) { isConnected ->
            if (isConnected) {
                finish()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun initComponents() {

    }

    override fun setClickListener() {

    }
}