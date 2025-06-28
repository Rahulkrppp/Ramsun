package de.fast2work.mobility.ui.authentication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import dagger.hilt.android.AndroidEntryPoint
import de.fast2work.mobility.BuildConfig
import de.fast2work.mobility.R
import de.fast2work.mobility.UI1.login1.LoginScreen
import de.fast2work.mobility.data.response.AppUpdate
import de.fast2work.mobility.data.response.PushNotification
import de.fast2work.mobility.databinding.ActivitySplashBinding
import de.fast2work.mobility.ui.authentication.login.LoginViewModel
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingActivity
import de.fast2work.mobility.ui.dashboard.DashboardActivity
import de.fast2work.mobility.utility.dialog.DialogUtil
import de.fast2work.mobility.utility.extension.getDrawableFromAttr
import de.fast2work.mobility.utility.extension.isLoggedIn
import de.fast2work.mobility.utility.extension.parcelable
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.preference.EasyPref
import de.fast2work.mobility.utility.util.IConstants.Companion.BUNDLE_PUSH_NOTIFICATION

/**
 * This class contains code for splash screen
 *
 */
@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseVMBindingActivity<ActivitySplashBinding, LoginViewModel>(LoginViewModel::class.java) {
    private val SPLASH_DURATION = 500L
    private var appUpdate:AppUpdate?=null
    var isFirst=false
    companion object {
        fun newInstance(context: Context, pushNotification: PushNotification? = null) = Intent(context, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(BUNDLE_PUSH_NOTIFICATION, pushNotification)
        }
    }    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBindingView(ActivitySplashBinding.inflate(layoutInflater))
        makeFullScreen()

    }

    /**
     * This method contains code for
     *
     */
    override fun initComponents() {
        Handler(Looper.getMainLooper()).postDelayed({
            redirectToLoginActivity()
        }, SPLASH_DURATION)
        //setDefaultLanguage()
        //viewModel.callAppVersionApi()
       // moveForward()
    }

    private fun setDefaultLanguage(){
        val lang = Resources.getSystem().configuration.locale.language

        if (BaseApplication.languageSharedPreference.getLanguagePref(EasyPref.CURRENT_LANGUAGE, "").isEmpty()) {
            if (lang == getString(R.string.de)) {
                BaseApplication.languageSharedPreference.setLanguagePref(EasyPref.CURRENT_LANGUAGE, getString(R.string.de))
            } else {
                BaseApplication.languageSharedPreference.setLanguagePref(EasyPref.CURRENT_LANGUAGE, getString(R.string.en))
            }
        }else{
            BaseApplication.languageSharedPreference.setLanguagePref(EasyPref.CURRENT_LANGUAGE, BaseApplication.languageSharedPreference.getLanguagePref(EasyPref.CURRENT_LANGUAGE, ""))
        }
    }

    private fun moveForward() {
            if (isLoggedIn()){
                if (intent != null && intent.hasExtra(BUNDLE_PUSH_NOTIFICATION)) {
                    startActivity(DashboardActivity.newInstance(this, intent.parcelable<PushNotification>(BUNDLE_PUSH_NOTIFICATION)!!))
                    finish()
                }else{
                    redirectToDashBoardActivity()
                }
            }else {
                redirectToLoginActivity()
            }
    }

    /**
     * This method contains code to redirection to activity
     *
     */
    private fun redirectToLoginActivity() {
        BaseApplication.tenantSharedPreference.clearAll()
        val intent = Intent(this, LoginScreen::class.java)
        startActivity(intent)
        finish()
    }

    private fun redirectToDashBoardActivity() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }


    override fun setClickListener() {

    }

    override fun onResume() {
        super.onResume()

    }

    /**
     * This method contains code for all the api call response
     *
     */
    override fun attachObservers() {
        viewModel.appVersionLiveData.observe(this){
            if (it.data != null){
                if (isVersionGreater(it.data?.appVersion.toBlankString(), BuildConfig.VERSION_NAME)) {
                    appUpdate=it.data
                    checkAppUpdate(it.data!!)
                }else{
                    Handler(Looper.getMainLooper()).postDelayed({
                        moveForward()
                        isFirst=false
                    }, SPLASH_DURATION)
                }
            }else{
                Handler(Looper.getMainLooper()).postDelayed({
                    moveForward()
                    isFirst=false
                }, SPLASH_DURATION)
            }
        }
        viewModel.errorLiveData.observe(this){
            redirectToLoginActivity()
        }
    }
    private fun checkAppUpdate(model:AppUpdate) {
        var notNow=""
        var title=""
        var des=""
        if (model.forceUpdate.equals("no", true)) {
            notNow=getString(R.string.not_now)
            title= getString(R.string.update_available)
            des= getString(R.string.we_ve_made_some_exciting_updates_to_enhance_your_experience)
        }else{
            title=getString(R.string.app_update_required)
            des=getString(R.string.we_have_added_new_features_and_fixed_bugs_to_enhance_your_experience)
        }
         DialogUtil.showDialog(supportFragmentManager,
                title,
                des,
                getString(
                    R.string.update_app
                ),notNow
                ,   il = object : DialogUtil.IL {
                    override fun onSuccess() {
                        isFirst=true
                            redirectToPlayStore(this@SplashActivity)

                    }
                    override fun onCancel(isNeutral: Boolean) {
                        isFirst=false
                        moveForward()
                    }
                }, image = getDrawableFromAttr(R.attr.imgAppUpdate), isCancelShow = false )
    }

    fun redirectToPlayStore(context: Context) {
        val appPackageName = context.packageName
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (e: android.content.ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }

    private fun compareVersions(version1: String, version2: String): Int {
        val version1Parts = version1.split(".")
        val version2Parts = version2.split(".")

        val maxLength = maxOf(version1Parts.size, version2Parts.size)

        for (i in 0 until maxLength) {
            val v1Part = version1Parts.getOrNull(i)?.toIntOrNull() ?: 0
            val v2Part = version2Parts.getOrNull(i)?.toIntOrNull() ?: 0

            when {
                v1Part > v2Part -> return 1
                v1Part < v2Part -> return -1
            }
        }
        return 0
    }

    private fun isVersionGreater(version1: String, version2: String): Boolean {
        return compareVersions(version1, version2) > 0
    }


}