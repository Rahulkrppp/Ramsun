package de.fast2work.mobility.ui.core

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import com.google.android.material.internal.EdgeToEdgeUtils
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.databinding.CustomToolbarBinding
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.customview.toolbar.ToolbarHelper
import de.fast2work.mobility.utility.dialog.progress.ProgressDialogFragment
import de.fast2work.mobility.utility.extension.SNACKBAR_TYPE_SUCCESS
import de.fast2work.mobility.utility.extension.backgroundColorTint
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.setThemeForTextInputLayout
import de.fast2work.mobility.utility.extension.showSnackBar
import de.fast2work.mobility.utility.extension.textColor
import de.fast2work.mobility.utility.preference.EasyPref
import java.util.Locale
import javax.inject.Singleton


/**
 *  Activity used for Base of application
 */
@AndroidEntryPoint
@Singleton
abstract class BaseActivity : AppCompatActivity() {

    private val progressDialogFragment: ProgressDialogFragment by lazy { ProgressDialogFragment.newInstance() }
    private lateinit var baseApplication: BaseApplication
    private lateinit var currentLanguage: String
    var exitWithAnimation = true
    private var isActive = false
    var noInternetOnApp = false
    var tenantInfoData = BaseApplication.tenantSharedPreference.getTenantPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)


    public override fun onStart() {
        super.onStart()
        isActive = true
    }

    /**
     * This method is used to change the status bar color
     *
     * @param drawable
     */
    fun setStatusBarGradiant(drawable: Drawable?) {
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
//        window.setBackgroundDrawable(drawable)
    }

    public override fun onStop() {
        isActive = false
        super.onStop()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseApplication = (application as BaseApplication)

    }

    /**
     * This method is used to show and hide progress dialog e.g. User have to pass true or false value
     *
     * @param isShow
     */
    fun showProgressDialog(isShow: Boolean) {
        if (isShow) {
            try {
                progressDialogFragment.dismissAllowingStateLoss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            progressDialogFragment.show(supportFragmentManager, ProgressDialogFragment.FRAGMENT_TAG)
        } else {
            try {
                progressDialogFragment.dismissAllowingStateLoss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * This method is used to handle more than clicks at time
     *
     * @param debounceTime
     * @param action
     */
    fun View.clickWithDebounce(debounceTime: Long = 700L, action: (view: View) -> Unit) {
        this.setOnClickListener(object : View.OnClickListener {
            private var lastClickTime: Long = 0
            override fun onClick(v: View) {
                if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
                else action(v)

                lastClickTime = SystemClock.elapsedRealtime()
            }
        })
    }
    /**
     * On network disconnected
     *
     */
    open fun onNetworkDisconnected() {
        startActivity(NoInternetActivity.createIntent(this))
    }

    /**
     * Show error message
     *
     * @param message
     */
    fun showErrorMessage(message: String) {
        message.showSnackBar(this)
    }

    /**
     * Show success message
     *
     * @param message
     */
    fun showSuccessMessage(message: String) {
        message.showSnackBar(this, SNACKBAR_TYPE_SUCCESS)
    }

    /**
     * Show toast
     *
     * @param message
     */
    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * This function is used to make activity ful screen
     *
     */
    fun makeFullScreen() {
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    /**
     * This function is used to toolbar
     *
     */
    private val toolbarHelper by lazy { ToolbarHelper() }
    private var customToolbarBinding: CustomToolbarBinding? = null
    fun overrideToolbar(binding: CustomToolbarBinding, toolbarConfig: ToolbarConfig) {
        this.customToolbarBinding = binding
        toolbarHelper.setCustomToolbarBinding(binding)
        toolbarHelper.setToolbarConfig(toolbarConfig)
    }
    /**
     * This function is used to setStatusBarColor
     *
     */
    fun setStatusBarColor(@ColorInt color: Int) {
        window.statusBarColor = color
    }

    fun setThemeForTextInputLayout(textInputLayout: TextInputLayout, isFromTenant : Boolean = false) {
        textInputLayout.setThemeForTextInputLayout(tenantInfoData?.brandingInfo?.primaryColor,isFromTenant)
    }

    fun setThemeForView(view: View) {
        view.backgroundColorTint(tenantInfoData?.brandingInfo?.primaryColor)
    }

    fun setThemeForTextView(textView: AppCompatTextView) {
        textView.textColor(tenantInfoData?.brandingInfo?.secondaryColor)
    }
    override fun attachBaseContext(newBase: Context?) {

        val newOverride = Configuration(newBase?.resources?.configuration)
        val fontScale = newOverride.fontScale
        val newFontSize = when {
            fontScale > 1.5 -> 1.3f // Adjust the threshold as needed
            fontScale < 0.8 -> 1.0f // Adjust the threshold as needed
            else -> fontScale
        }
        newOverride.fontScale = newFontSize
        newOverride.locale = Locale(BaseApplication.languageSharedPreference.getLanguagePref(EasyPref.CURRENT_LANGUAGE,""))

        BaseApplication.themeValue = newOverride.uiMode - 1

        applyOverrideConfiguration(newOverride)

        super.attachBaseContext(newBase)
    }
}