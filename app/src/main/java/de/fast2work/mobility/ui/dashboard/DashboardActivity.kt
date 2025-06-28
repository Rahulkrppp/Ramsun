package de.fast2work.mobility.ui.dashboard

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import de.fast2work.mobility.BuildConfig
import de.fast2work.mobility.R
import de.fast2work.mobility.data.eventbus.UpdateNotificationCount
import de.fast2work.mobility.data.eventbus.UpdateTheme
import de.fast2work.mobility.data.response.ActiveTicket
import de.fast2work.mobility.data.response.NotificationData
import de.fast2work.mobility.data.response.PushNotification
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.data.response.User
import de.fast2work.mobility.databinding.ActivityDashboardBinding
import de.fast2work.mobility.ui.authentication.changepassword.ChangePasswordFragment
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseFragment
import de.fast2work.mobility.ui.core.BaseVMBindingActivity
import de.fast2work.mobility.ui.home.HomeFragment
import de.fast2work.mobility.utility.dialog.DialogUtil
import de.fast2work.mobility.utility.extension.getAndroidDeviceId
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.getString
import de.fast2work.mobility.utility.extension.hideKeyboard
import de.fast2work.mobility.utility.extension.loadCircleCropImage
import de.fast2work.mobility.utility.extension.parcelable
import de.fast2work.mobility.utility.extension.performLogout
import de.fast2work.mobility.utility.extension.setTint
import de.fast2work.mobility.utility.preference.EasyPref
import de.fast2work.mobility.utility.preference.EasyPref.Companion.USER_DATA
import de.fast2work.mobility.utility.util.FragNavController
import de.fast2work.mobility.utility.util.FragNavLogger
import de.fast2work.mobility.utility.util.FragNavSwitchController
import de.fast2work.mobility.utility.util.FragNavTransactionOptions
import de.fast2work.mobility.utility.util.IConstants
import de.fast2work.mobility.utility.util.IConstants.Companion.BUNDLE_PUSH_NOTIFICATION
import de.fast2work.mobility.utility.util.IConstantsIcon
import de.fast2work.mobility.utility.util.LocalConfig.co2_management
import de.fast2work.mobility.utility.util.LocalConfig.mobility_budget
import de.fast2work.mobility.utility.util.UniqueTabHistoryStrategy
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.hypot
import kotlin.math.max


const val INDEX_HOME = FragNavController.TAB1
const val INDEX_INVOICE = FragNavController.TAB2
const val INDEX_FILE = FragNavController.TAB3
const val INDEX_SETTING = FragNavController.TAB4
const val INDEX_BTG = FragNavController.TAB5

const val INDEX_HOME_CO2 = FragNavController.TAB6
const val INDEX_SETTING_CO2 = FragNavController.TAB7
const val INDEX_CENTER_CO2 = FragNavController.TAB8
const val INDEX_CONTACT_CO2 = FragNavController.TAB9
const val INDEX_BTG_CO2 = FragNavController.TAB10

const val INDEX_SURVEY = FragNavController.TAB11

/**
 *  Activity used for base of fragments
 */
@AndroidEntryPoint
class DashboardActivity :
    BaseVMBindingActivity<ActivityDashboardBinding, DashBoardViewModel>(DashBoardViewModel::class.java),
    BaseFragment.FragmentNavigation, FragNavController.TransactionListener,
    FragNavController.RootFragmentListener {
    private val fragNavController: FragNavController =
        FragNavController(supportFragmentManager, R.id.container)
    private var isOpen = false
    var tenant: TenantInfoModel? = null
    private var userData: User? = null

    companion object {
        fun newInstance(activity: Context, pushNotification: PushNotification) =
            Intent(activity, DashboardActivity::class.java).apply {
                putExtra(BUNDLE_PUSH_NOTIFICATION, pushNotification)
            }
    }

    private var myReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            viewModel.callNotificationCountApi()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBindingView(ActivityDashboardBinding.inflate(layoutInflater))

        tenant = (BaseApplication.tenantSharedPreference.getTenantPrefModel(
            EasyPref.TENANT_DATA,
            TenantInfoModel::class.java
        ))
        userData = BaseApplication.sharedPreference.getPrefModel(USER_DATA, User::class.java)
        initNavigationDrawer(savedInstanceState)
        setOnBackPress()
        if (BaseApplication.themeValue == 16 || BaseApplication.themeValue == 32) {
//            if (tenant?.tenantInfo?.enabledServices.equals(co2_management, true)) {
//                selectTabAtPosition(INDEX_HOME_CO2)
//            } else {
//                selectTabAtPosition(INDEX_HOME)
//
//            }
            if (tenant?.tenantInfo?.enabledServices.equals(mobility_budget, true)) {
                binding.bottomBar.cbSetting.isVisible = true
                binding.bottomBar.cbSurvey.isVisible = false
                binding.bottomBarCo2.clBottomBar.isVisible = false
                binding.bottomBar.clBottomBar.isVisible = true
                binding.layoutNavDrawer.tvInvoice.isVisible = true
                binding.layoutNavDrawer.divider3.isVisible = true
                selectTabAtPosition(INDEX_HOME)
            } else if (tenant?.tenantInfo?.enabledServices.equals(co2_management, true)) {
                binding.bottomBarCo2.clBottomBar.isVisible = true
                binding.bottomBar.clBottomBar.isVisible = false
                binding.layoutNavDrawer.tvInvoice.isVisible = false
                binding.layoutNavDrawer.divider3.isVisible = false
                selectTabAtPosition(INDEX_HOME_CO2)
            } else {
                binding.bottomBarCo2.clBottomBar.isVisible = false
                binding.bottomBar.cbSetting.isVisible = false
                binding.bottomBar.cbSurvey.isVisible = true
                selectTabAtPosition(INDEX_SURVEY)
                selectTabAtPosition(INDEX_HOME)
                binding.layoutNavDrawer.tvInvoice.isVisible = true
                binding.layoutNavDrawer.divider3.isVisible = true

            }
        }


    }




    override val numberOfRootFragments: Int = 11

    override fun attachObservers() {
        viewModel.logoutLiveData.observe(this) {
            performLogout()
        }
        viewModel.tenantInfoLiveData.observe(this) {
            if (it.isSuccess) {
                recreate()
            }
        }
        viewModel.notificationCountListLiveData.observe(this) {
            Log.e("===============", "count : ${it.data?.unreadNotificationCount}")
            if (it.isSuccess) {
                BaseApplication.notificationCount.postValue(it.data?.unreadNotificationCount)
                EventBus.getDefault()
                    .post(UpdateNotificationCount(it.data?.unreadNotificationCount))
            }

//            if ((it.data?.unreadNotificationCount ?: 0) > 0) {
//                    BaseApplication.isUnreadNotificationAvailable = true
//                    EventBus.getDefault().post(UpdateNotificationCount(it.data?.unreadNotificationCount))
//                } else {
//                    BaseApplication.isUnreadNotificationAvailable = false
//                    EventBus.getDefault().post(UpdateNotificationCount(it.data?.unreadNotificationCount))
//                }
        }
    }

    override fun initComponents() {
        setBottomBar()
        viewModel.callCreateFCMTokenApi(getAndroidDeviceId())
        viewModel.callNotificationCountApi()
        getDataFromIntent()

    }

    override fun setClickListener() {
        binding.apply {

        }
    }

    private fun initNavigationDrawer(savedInstanceState: Bundle?) {
        fragNavController.apply {
            transactionListener = this@DashboardActivity
            rootFragmentListener = this@DashboardActivity
            createEager = true
            fragNavLogger = object : FragNavLogger {
                override fun error(message: String, throwable: Throwable) {
                    //Log.e("TAG", message, throwable)
                }
            }

            defaultTransactionOptions = FragNavTransactionOptions.newBuilder().build()
            fragmentHideStrategy = FragNavController.DETACH

            navigationStrategy = UniqueTabHistoryStrategy(object : FragNavSwitchController {
                override fun switchTab(index: Int, transactionOptions: FragNavTransactionOptions?) {
                    //Log.e("switchTab", index.toString())
                    selectTabAtPosition(index)
                    fragNavController.switchTab(index)
                }
            })
        }
        if (tenant?.tenantInfo?.enabledServices.equals(mobility_budget, true)) {
            binding.bottomBar.cbSetting.isVisible = true
            binding.bottomBar.cbSurvey.isVisible = false
            fragNavController.initialize(INDEX_HOME, savedInstanceState)
        } else if (tenant?.tenantInfo?.enabledServices.equals(co2_management, true)) {
            fragNavController.initialize(INDEX_HOME_CO2, savedInstanceState)
        } else {
            binding.bottomBar.cbSetting.isVisible = false
            binding.bottomBar.cbSurvey.isVisible = true
            fragNavController.initialize(INDEX_HOME, savedInstanceState)
        }

    }

    private fun getDataFromIntent() {

        if (intent != null && intent.hasExtra(BUNDLE_PUSH_NOTIFICATION)) {
            viewModel.pushNotification =
                intent.parcelable<PushNotification>(BUNDLE_PUSH_NOTIFICATION)/*if (viewModel.pushNotification?.notificationCode != Notification.CHAT_MESSAGE) {
                   // viewModel.callNotificationMarkAsReadApi(viewModel.pushNotification?.notificationId.toString())
                }*/

        }
    }

    /**
     * This method contains code for manageNotificationRedirectionIfAny
     *
     */


    override fun getRootFragment(index: Int): Fragment {
        when (index) {
            INDEX_HOME -> {
                return HomeFragment.newInstance(viewModel.pushNotification)
            }

            INDEX_INVOICE -> {
                return HomeFragment.newInstance(viewModel.pushNotification)
            }

            INDEX_SETTING -> {
                return HomeFragment.newInstance(viewModel.pushNotification)
            }

            INDEX_FILE -> {
                return HomeFragment.newInstance(viewModel.pushNotification)
            }

            INDEX_BTG -> {
                return HomeFragment.newInstance(viewModel.pushNotification)
            }

            /**
             * co2 tab
             */
            INDEX_HOME_CO2 -> {
                return HomeFragment.newInstance(viewModel.pushNotification)
            }

            INDEX_SETTING_CO2 -> {
                return HomeFragment.newInstance(viewModel.pushNotification)
            }

            INDEX_CENTER_CO2 -> {
                return HomeFragment.newInstance(viewModel.pushNotification)
            }

            INDEX_CONTACT_CO2 -> {
                return HomeFragment.newInstance(viewModel.pushNotification)
            }

            INDEX_BTG_CO2 -> {
                return HomeFragment.newInstance(viewModel.pushNotification)
            }
            /**
             * Both
             */
            INDEX_SURVEY -> {
                return HomeFragment.newInstance(viewModel.pushNotification)
            }

        }
        throw IllegalStateException("Need to send an index that we know")
    }

    private fun setBottomBar() {
        binding.apply {
            bottomBar.cbHome.clickWithDebounce {
                fragNavController.clearStack()
                selectBottomTab(INDEX_HOME)
            }
            bottomBar.cbInvoice.clickWithDebounce {
                fragNavController.clearStack()
                selectBottomTab(INDEX_INVOICE)
            }
            bottomBar.ivFile.clickWithDebounce {
                fragNavController.clearStack()
                selectBottomTab(INDEX_FILE)
            }
            bottomBar.cbBtg.clickWithDebounce {
                fragNavController.clearStack()
                openBtg()
                // selectBottomTab(INDEX_BTG)
            }
            bottomBar.cbSetting.clickWithDebounce {
                fragNavController.clearStack()
                selectBottomTab(INDEX_SETTING)
            }
            bottomBarCo2.cbHomeCo2.clickWithDebounce {
                fragNavController.clearStack()
                selectBottomTab(INDEX_HOME_CO2)
            }
            bottomBarCo2.cbBtg.clickWithDebounce {
                fragNavController.clearStack()
                openBtg()
            }
            bottomBarCo2.ivFile.clickWithDebounce {
                fragNavController.clearStack()

                selectBottomTab(INDEX_CENTER_CO2)
            }
            bottomBarCo2.cbContactCo2.clickWithDebounce {
                fragNavController.clearStack()
                selectBottomTab(INDEX_CONTACT_CO2)
            }
            bottomBarCo2.cbSettingCo2.clickWithDebounce {
                fragNavController.clearStack()
                selectBottomTab(INDEX_SETTING_CO2)
            }
            bottomBar.cbSurvey.clickWithDebounce {
                fragNavController.clearStack()
                selectBottomTab(INDEX_SURVEY)
            }
            layoutNavDrawer.ivClose.clickWithDebounce {
                showHideMenuView()
            }
            layoutNavDrawer.clUserdata.clickWithDebounce {
                fragNavController.clearStack()
                showHideMenuView()
              /* if (tenant?.tenantInfo?.enabledServices.equals(mobility_budget,true)){
                    selectBottomTab(INDEX_PROFILE)
                }else if (tenant?.tenantInfo?.enabledServices.equals(co2_management,true)){
                    selectBottomTab(INDEX_PROFILE_CO2)
                }else{
                    pushFragment(ProfileFragment())
                }*/
            }
            layoutNavDrawer.tvDashboard.clickWithDebounce {
                fragNavController.clearStack()
                showHideMenuView()
                if (tenant?.tenantInfo?.enabledServices.equals(co2_management, true)) {
                    selectBottomTab(INDEX_HOME_CO2)
                } else {
                    selectBottomTab(INDEX_HOME)
                }
            }
            layoutNavDrawer.tvInvoice.clickWithDebounce {
                fragNavController.clearStack()
                showHideMenuView()
                selectBottomTab(INDEX_INVOICE)
            }
            layoutNavDrawer.tvChangePassword.clickWithDebounce {
                showHideMenuView()
                pushFragment(ChangePasswordFragment())
            }


            layoutNavDrawer.tvLogOut.clickWithDebounce {
                showHideMenuView()
                DialogUtil.showDialog(supportFragmentManager,
                    getString(R.string.logging_out),
                    getString(R.string.alert_logout),
                    getString(R.string.logout),
                    getString(R.string.cancel),
                    object : DialogUtil.IL {
                        override fun onSuccess() {
                            viewModel.callLogoutApi(true, getAndroidDeviceId())
                        }

                        override fun onCancel(isNeutral: Boolean) {
                        }
                    },
                    isCancelShow = false
                )
            }
            layoutNavDrawer.tvSetting.clickWithDebounce {
                fragNavController.clearStack()
                showHideMenuView()
                if (tenant?.tenantInfo?.enabledServices.equals(co2_management, true)) {
                    selectBottomTab(INDEX_SETTING_CO2)
                } else if (tenant?.tenantInfo?.enabledServices.equals(mobility_budget, true)) {
                    selectBottomTab(INDEX_SETTING)
                } else {
                }

            }
            layoutNavDrawer.tvDTicket.clickWithDebounce {
                showHideMenuView()
//                pushFragment(MyCo2SavingsFragment())
            }
            layoutNavDrawer.tvVersion.clickWithDebounce {
                //pushFragment(CalculateCo21Fragment.newInstance(surveyId = 8,"geed"))
            }
        }
    }

    private fun selectBottomTab(newPos: Int) {
        if (viewModel.currentTabPosition == newPos) {
            fragNavController.clearStack()
            return
        }
        viewModel.currentTabPosition = newPos
        fragNavController.switchTab(newPos)
        selectTabAtPosition(newPos)
    }

    private fun selectTabAtPosition(index: Int) {

        viewModel.currentTabPosition = index
        when (index) {
            INDEX_HOME -> {
                setBottomTabSelected(binding.bottomBar.cbHome, null)
            }

            INDEX_INVOICE -> {
                setBottomTabSelected(binding.bottomBar.cbInvoice, null)
            }

            INDEX_FILE -> {
                setBottomTabSelected(null, binding.bottomBar.ivFile)
            }

            INDEX_BTG -> {
                //setBottomTabSelected(binding.bottomBar.cbBtg,null)
            }

            INDEX_SETTING -> {
                setBottomTabSelected(binding.bottomBar.cbSetting, null)
            }
            /**
             * c02 tab
             */
            INDEX_HOME_CO2 -> {
                setBottomTabSelected(binding.bottomBarCo2.cbHomeCo2, null)
            }

            INDEX_SETTING_CO2 -> {
                setBottomTabSelected(binding.bottomBarCo2.cbSettingCo2, null)

            }

            INDEX_CENTER_CO2 -> {
                setBottomTabSelected(null, binding.bottomBarCo2.ivFile)
            }

            INDEX_CONTACT_CO2 -> {
                setBottomTabSelected(binding.bottomBarCo2.cbContactCo2, null)
            }

            INDEX_BTG_CO2 -> {
                //setBottomTabSelected(binding.bottomBarCo2.cbBtg,null)
            }

            INDEX_SURVEY -> {
                setBottomTabSelected(binding.bottomBar.cbSurvey, null)
            }
        }
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    private fun setBottomTabSelected(checkBox: AppCompatCheckBox?, imageView: AppCompatImageView?) {
        binding.apply {
            bottomBar.cbHome.isSelected = false
            bottomBar.cbFile.isSelected = false
            bottomBar.ivFile.isSelected = false
            //bottomBar.cbBtg.isSelected = false
            bottomBar.cbSetting.isSelected = false

            bottomBar.cbHome.setTextColor(getColorFromAttr(R.attr.colorBgBottomBarIconUnSelected))
            bottomBar.cbHome.compoundDrawableTintList =
                ColorStateList.valueOf(getColorFromAttr(R.attr.colorBgBottomBarIconUnSelected))

            bottomBar.cbInvoice.setTextColor(getColorFromAttr(R.attr.colorBgBottomBarIconUnSelected))
            bottomBar.cbInvoice.compoundDrawableTintList =
                ColorStateList.valueOf(getColorFromAttr(R.attr.colorBgBottomBarIconUnSelected))

            /*bottomBar.cbBtg.setTextColor(getColorFromAttr(R.attr.colorBgBottomBarIconUnSelected))
            bottomBar.cbBtg.compoundDrawableTintList = ColorStateList.valueOf(getColorFromAttr(R.attr.colorBgBottomBarIconUnSelected))*/

            bottomBar.cbSetting.setTextColor(getColorFromAttr(R.attr.colorBgBottomBarIconUnSelected))
            bottomBar.cbSetting.compoundDrawableTintList =
                ColorStateList.valueOf(getColorFromAttr(R.attr.colorBgBottomBarIconUnSelected))

//            binding.bottomBar.ivFile.setTint(tenantInfoData?.brandingInfo?.secondaryColor.toString())
            binding.bottomBar.ivFile.setTint("#274072")

            /**
             * co2 bar
             *
             */
            bottomBarCo2.cbHomeCo2.isSelected = false
            //bottomBarCo2.cbBtg.isSelected = false
            bottomBarCo2.ivFile.isSelected = false
            bottomBarCo2.cbContactCo2.isSelected = false
            bottomBarCo2.cbSettingCo2.isSelected = false

            bottomBarCo2.cbHomeCo2.setTextColor(getColorFromAttr(R.attr.colorBgBottomBarIconUnSelected))
            bottomBarCo2.cbHomeCo2.compoundDrawableTintList =
                ColorStateList.valueOf(getColorFromAttr(R.attr.colorBgBottomBarIconUnSelected))

            /*bottomBarCo2.cbBtg.setTextColor(getColorFromAttr(R.attr.colorBgBottomBarIconUnSelected))
            bottomBarCo2.cbBtg.compoundDrawableTintList = ColorStateList.valueOf(getColorFromAttr(R.attr.colorBgBottomBarIconUnSelected))*/

            bottomBarCo2.cbContactCo2.setTextColor(getColorFromAttr(R.attr.colorBgBottomBarIconUnSelected))
            bottomBarCo2.cbContactCo2.compoundDrawableTintList =
                ColorStateList.valueOf(getColorFromAttr(R.attr.colorBgBottomBarIconUnSelected))

            bottomBarCo2.cbSettingCo2.setTextColor(getColorFromAttr(R.attr.colorBgBottomBarIconUnSelected))
            bottomBarCo2.cbSettingCo2.compoundDrawableTintList =
                ColorStateList.valueOf(getColorFromAttr(R.attr.colorBgBottomBarIconUnSelected))

//            binding.bottomBarCo2.ivFile.setTint(tenantInfoData?.brandingInfo?.secondaryColor.toString())
            binding.bottomBar.ivFile.setTint("#274072")

            /**
             * Both
             *
             */
            bottomBar.cbSurvey.isSelected = false
            bottomBar.cbSurvey.setTextColor(getColorFromAttr(R.attr.colorBgBottomBarIconUnSelected))
            bottomBar.cbSurvey.compoundDrawableTintList =
                ColorStateList.valueOf(getColorFromAttr(R.attr.colorBgBottomBarIconUnSelected))

        }

        if (checkBox != null) {
            if (BaseApplication.themeValue == Configuration.UI_MODE_NIGHT_YES) {
                checkBox.setTextColor(getColorFromAttr(R.attr.colorBgBottomBarIconSelected))
                checkBox.compoundDrawableTintList =
                    ColorStateList.valueOf(getColorFromAttr(R.attr.colorBgBottomBarIconSelected))
            } else {
                checkBox.setTextColor(Color.parseColor(tenantInfoData?.brandingInfo?.primaryColor.toString()))
                checkBox.compoundDrawableTintList =
                    ColorStateList.valueOf(Color.parseColor(tenantInfoData?.brandingInfo?.primaryColor.toString()))
            }
        }
        if (imageView != null) {
//            binding.bottomBar.ivFile.setTint(tenantInfoData?.brandingInfo?.secondaryColor.toString())
            binding.bottomBar.ivFile.setTint("#274072")
        }
    }

    fun switchTab(index: Int, bundle: Bundle? = null) {
        if (numberOfRootFragments <= index) {
            return
        }

        if (bundle != null) {
            fragNavController.getStack(index)?.peek()?.arguments = bundle
        }

        when (index) {
            INDEX_HOME -> {
                binding.bottomBar.cbHome.performClick()
            }

            INDEX_INVOICE -> {
                binding.bottomBar.cbInvoice.performClick()
            }

            INDEX_FILE -> {
                fragNavController.clearStack()
                binding.bottomBar.ivFile.performClick()
            }

            INDEX_BTG -> {
                //binding.bottomBar.cbBtg.performClick()
            }

            INDEX_SETTING -> {
                binding.bottomBar.cbSetting.performClick()
            }
            /**
             * co2
             */
            INDEX_HOME_CO2 -> {
                binding.bottomBarCo2.cbHomeCo2.performClick()
            }

            INDEX_SETTING_CO2 -> {
                binding.bottomBarCo2.cbSettingCo2.performClick()
            }

            INDEX_CENTER_CO2 -> {
                binding.bottomBarCo2.ivFile.performClick()

            }

            INDEX_CONTACT_CO2 -> {
                binding.bottomBarCo2.cbContactCo2.performClick()
            }

            INDEX_BTG_CO2 -> {
                //binding.bottomBarCo2.cbBtg.performClick()
            }

            INDEX_SURVEY -> {
                binding.bottomBar.cbSurvey.performClick()
            }
        }

    }

    override fun onFragmentTransaction(
        fragment: Fragment?,
        transactionType: FragNavController.TransactionType
    ) {
        supportActionBar?.setDisplayHomeAsUpEnabled(fragNavController.isRootFragment.not())
    }

    override fun onTabTransaction(fragment: Fragment?, index: Int) {
        supportActionBar?.setDisplayHomeAsUpEnabled(fragNavController.isRootFragment.not())
    }

    override fun pushFragment(fragment: Fragment) {
        fragNavController.pushFragment(fragment)
    }

    override fun popFragment(depth: Int) {
        fragNavController.popFragments(depth)
    }

    private var isBackPressed = false
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        isBackPressed = false
    }

    private fun handleBackPress() {
        val currentFragment = fragNavController.currentFrag
        hideKeyboard(this@DashboardActivity)
        if (userData?.isProfileComplete == "0") {
            finishAffinity()
        } else {
            if (isOpen) {
                showHideMenuView()
            } else if (currentFragment != null && currentFragment is BaseFragment && currentFragment.onBackPressed()) {
                //showHideChatNotification(viewModel.unreadChatNotification == 0)
                return
            } else if (fragNavController.popFragment().not()) {
                if (isBackPressed) {
                    finishAffinity()
                } else {
                    isBackPressed = true
                    showToast(getString(R.string.press_again_to_exit))
                    handler.postDelayed(runnable, 1000)
                }
            }
        }
    }

    private fun setOnBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }

        })
    }

    fun showHideMenuView() {
        setUserProfileData()
        if (!isOpen) {
            val x = binding.clMain.left
            val y = binding.clMain.top

            val startRadius = 0
            val endRadius =
                hypot(binding.clMain.width.toDouble(), binding.clMain.height.toDouble()).toInt()

            val anim = ViewAnimationUtils.createCircularReveal(
                binding.layoutNavDrawer.clDrawerMain,
                x,
                y,
                startRadius.toFloat(),
                endRadius.toFloat()
            )

            binding.llMenu.visibility = View.VISIBLE
            anim.start()
            isOpen = true
        } else {
            val x = binding.clMain.left
            val y = binding.layoutNavDrawer.clDrawerMain.top

            val startRadius = max(binding.clMain.width, binding.clMain.height)
            val endRadius = 0

            val anim = ViewAnimationUtils.createCircularReveal(
                binding.layoutNavDrawer.clDrawerMain,
                x,
                y,
                startRadius.toFloat(),
                endRadius.toFloat()
            )
            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {

                }

                override fun onAnimationEnd(animator: Animator) {
                    binding.llMenu.visibility = View.GONE
                }

                override fun onAnimationCancel(animator: Animator) {

                }

                override fun onAnimationRepeat(animator: Animator) {

                }
            })
            anim.start()
            isOpen = false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUserProfileData() {
        val userData = BaseApplication.sharedPreference.getPrefModel(USER_DATA, User::class.java)
        binding.apply {
            layoutNavDrawer.tvVersion.text =
                getString(R.string.version) + BuildConfig.VERSION_NAME + "(${BuildConfig.VERSION_CODE})"
            if (userData != null) {
                if (userData.firstName.isNullOrEmpty()) {
                    layoutNavDrawer.tvUserName.text = getString(R.string.user)
                } else {
                    layoutNavDrawer.tvUserName.text = getString(
                        R.string.user_first_last_name,
                        userData.firstName.toString() + " " + userData.lastName
                    )
                }
                layoutNavDrawer.ivProfile.loadCircleCropImage(
                    this@DashboardActivity,
                    userData.profilePic.toString(),
                    R.drawable.placeholder,
                    catchImage = true
                )

            } else {
                layoutNavDrawer.ivProfile.elevation = 0f
                layoutNavDrawer.ivProfile.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@DashboardActivity,
                        R.drawable.placeholder
                    )
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        if (BaseApplication.themeValueChanged) {
//            if (BaseApplication.themeValue == 16 || BaseApplication.themeValue == 32) {
//                selectTabAtPosition(INDEX_HOME)
//            }
//        }
        Log.e("NISARG", "onResume: NISARG CALLED")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(
                myReceiver,
                IntentFilter(IConstants.NOTIFICATION_COUNT),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(myReceiver, IntentFilter(IConstants.NOTIFICATION_COUNT))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myReceiver)
    }

    /**
     * Notification count update
     *
     * @param UpdateTheme
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateTheme(updateTheme: UpdateTheme) {
        if (updateTheme.tenantName.toString().isNotEmpty()) {
            viewModel.callGetTenantThemeApi(updateTheme.tenantName.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun openBtg() {
        try {
            var intent = packageManager.getLaunchIntentForPackage("de.fast2work.biketourguide")
            if (intent != null) {
                // We found the activity now start the activity
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                // Bring user to the market or let them choose an app?
                intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setData(Uri.parse("market://details?id=" + "de.fast2work.biketourguide"))
                startActivity(intent)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "$e", Toast.LENGTH_LONG).show()
        }
    }
}
