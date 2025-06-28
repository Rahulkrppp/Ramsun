package de.fast2work.mobility.ui.core

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.databinding.CustomToolbarBinding
import de.fast2work.mobility.ui.dashboard.DashboardActivity
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.customview.toolbar.ToolbarHelper
import de.fast2work.mobility.utility.dialog.progress.ProgressDialogFragment
import de.fast2work.mobility.utility.extension.SNACKBAR_TYPE_ALERT
import de.fast2work.mobility.utility.extension.SNACKBAR_TYPE_SUCCESS
import de.fast2work.mobility.utility.extension.backgroundColorTint
import de.fast2work.mobility.utility.extension.backgroundColorTintForAlpha
import de.fast2work.mobility.utility.extension.buttonTextColor
import de.fast2work.mobility.utility.extension.setThemeForTextInputLayout
import de.fast2work.mobility.utility.extension.showSnackBar
import de.fast2work.mobility.utility.extension.textColor
import de.fast2work.mobility.utility.preference.EasyPref
import de.fast2work.mobility.utility.util.LocalConfig.co2_management
import de.fast2work.mobility.utility.util.LocalConfig.mobility_budget
import org.greenrobot.eventbus.EventBus
import kotlin.math.log

/**
 * This class contains common things used in fragment
 *
 */
@AndroidEntryPoint
abstract class BaseFragment : Fragment() {

    /**
     * Used when we don't want to create/inflate view if same fragment instance is used.
     */
    var isFirstTimeLoad: Boolean = false
    private var mFragmentNavigation: FragmentNavigation? = null
    lateinit var baseActivity: BaseActivity
    private val progressDialogFragment: ProgressDialogFragment by lazy { ProgressDialogFragment.newInstance() }
    var tenantInfoData = BaseApplication.tenantSharedPreference.getTenantPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageBottombar()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onLoad()
    }
 /*   override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }*/
    /**
     * On load
     *
     */
    open fun onLoad() {

    }

    /**
     * On network disconnected
     *
     */
    open fun onNetworkDisconnected() {
        startActivity(NoInternetActivity.createIntent(requireContext()))
    }

    /**
     * Show error message
     *
     * @param message
     */
    fun showErrorMessage(message: String) {
        message.showSnackBar(requireActivity())
    }

    /**
     * Show success message
     *
     * @param message
     */
    fun showSuccessMessage(message: String) {
        message.showSnackBar(requireActivity(), SNACKBAR_TYPE_SUCCESS)
    }

    /**
     * Show success message
     *
     * @param message
     */
    fun showAlertMessage(message: String) {
        message.showSnackBar(requireActivity(), SNACKBAR_TYPE_ALERT)
    }

    /**
     * Show toast
     *
     * @param message
     */
    fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    /**
     * This interface is used for push and pop fragments in the stack
     *
     */
    interface FragmentNavigation {
        fun pushFragment(fragment: Fragment)

        fun popFragment(depth: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentNavigation) {
            mFragmentNavigation = context
        }
        baseActivity = context as BaseActivity
    }

    /**
     * This method is used to push fragment on stack
     *
     * @param fragment
     */
    fun pushFragment(fragment: Fragment) {
        if (mFragmentNavigation != null) {
            mFragmentNavigation?.pushFragment(fragment)
        }
    }

    /**
     *
     * This fragment is used to pop fragment form the stack
     *
     * @param depth
     */
    fun popFragment(depth: Int = 1) {
        if (mFragmentNavigation != null) {
            mFragmentNavigation?.popFragment(depth)
        }
    }

    open fun onBackPressed(): Boolean {

        return false
    }





    /**
     *
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
     * This method is used to shoe and hide progress dialog
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
            progressDialogFragment.show(activity?.supportFragmentManager!!, ProgressDialogFragment.FRAGMENT_TAG)
        } else {
            try {
                progressDialogFragment.dismissAllowingStateLoss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val toolbarHelper by lazy { ToolbarHelper() }
    private var customToolbarBinding: CustomToolbarBinding? = null
    fun overrideToolbar(binding: CustomToolbarBinding, toolbarConfig: ToolbarConfig) {
        this.customToolbarBinding = binding
        toolbarHelper.setCustomToolbarBinding(binding)
        toolbarHelper.setToolbarConfig(toolbarConfig)
        setThemeForNotificationIcon()
    }

    private fun setThemeForNotificationIcon(){
        val drawable = ContextCompat.getDrawable(requireActivity(), R.drawable.bg_notification_count) as GradientDrawable

        // Change the fill color of the drawable
        val newFillColor = if (BaseApplication.themeValue == Configuration.UI_MODE_NIGHT_YES) {
            ContextCompat.getColor(requireActivity(),R.color.color_primary_dark)
        }else{
            Color.parseColor(tenantInfoData?.brandingInfo?.primaryColor)
        }
        drawable.setColor(newFillColor)

        // Set the updated drawable to the ImageView
        customToolbarBinding?.tvNotificationCount?.setBackgroundDrawable(drawable)
    }

    fun hideTabs() {
        if (baseActivity is DashboardActivity) {
            Log.e("", "hideTabs:${tenantInfoData?.tenantInfo?.enabledServices.equals(co2_management,true)} ", )
             if (tenantInfoData?.tenantInfo?.enabledServices.equals(co2_management,true)) {
                (baseActivity as DashboardActivity).binding.bottomBarCo2.clBottomBar.visibility = View.GONE
            }else{
                (baseActivity as DashboardActivity).binding.bottomBar.clBottomBar.visibility = View.GONE
            }
        }
    }

    fun showTabs() {
        if (baseActivity is DashboardActivity) {
            Log.e("", "showTabs:${tenantInfoData?.tenantInfo?.enabledServices.equals(co2_management,true)} ", )
            if (tenantInfoData?.tenantInfo?.enabledServices.equals(co2_management,true)){
                (baseActivity as DashboardActivity).binding.bottomBarCo2.clBottomBar.visibility = View.VISIBLE
                (baseActivity as DashboardActivity).binding.bottomBar.clBottomBar.visibility = View.GONE
            }else{
                (baseActivity as DashboardActivity).binding.bottomBar.clBottomBar.visibility = View.VISIBLE
                (baseActivity as DashboardActivity).binding.bottomBarCo2.clBottomBar.visibility = View.GONE
            }

        }
    }
    private fun manageBottombar() {
        if (activity is DashboardActivity) {
            showTabs()
        }
    }

    fun toggleDrawer() {
        if (activity is DashboardActivity) {
            (activity as DashboardActivity).showHideMenuView()
        }
    }

    fun setThemeForTextInputLayout(textInputLayout: TextInputLayout) {
        textInputLayout.setThemeForTextInputLayout(tenantInfoData?.brandingInfo?.primaryColor)
    }

    fun setThemeForView(view: View) {
        view.backgroundColorTint(tenantInfoData?.brandingInfo?.primaryColor)
    }

    fun setThemeForTextView(textView: AppCompatTextView) {
        textView.textColor(tenantInfoData?.brandingInfo?.secondaryColor)
    }

    fun setThemeForButtonTextView(textView: AppCompatTextView) {
        textView.buttonTextColor(tenantInfoData?.brandingInfo?.primaryColor)
    }

    fun setBackgroundColorTintForAlpha(view: View) {
        view.backgroundColorTintForAlpha(tenantInfoData?.brandingInfo?.primaryColor)
    }

    fun switchTab(index: Int, bundle: Bundle? = null) {
        if (activity is DashboardActivity) {
            (activity as DashboardActivity).switchTab(index, bundle)
        }
    }

}