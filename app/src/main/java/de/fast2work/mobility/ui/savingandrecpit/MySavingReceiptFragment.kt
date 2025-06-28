package de.fast2work.mobility.ui.savingandrecpit


import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.fast2work.mobility.R
import de.fast2work.mobility.data.eventbus.UpdateNotificationCount
import de.fast2work.mobility.data.response.User
import de.fast2work.mobility.databinding.FragmentMySavingReceiptBinding
import de.fast2work.mobility.databinding.LayoutCustomTabConnectionsBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.savingandrecpit.adapter.MyCo2Adapter
import de.fast2work.mobility.ui.sidemenu.notification.NotificationFragment
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.extension.backgroundColorTint
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.preference.EasyPref
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MySavingReceiptFragment :
    BaseVMBindingFragment<FragmentMySavingReceiptBinding, MySavingReceiptViewModel>(
        MySavingReceiptViewModel::class.java
    ) {
    private var mAdapter: MyCo2Adapter? = null
    private var arrayList: ArrayList<String> = ArrayList()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return generateBinding(FragmentMySavingReceiptBinding.inflate(inflater), container)
    }
    override fun attachObservers() {
        BaseApplication.notificationCount.observe(this) {
            if (it > 0) {
                binding!!.toolbar.tvNotificationCount.visibility = View.VISIBLE
                binding!!.toolbar.ivNotification.visibility = View.VISIBLE
                binding!!.toolbar.tvNotificationCount.text = it.toBlankString()
            } else {
                binding!!.toolbar.tvNotificationCount.visibility = View.GONE
                binding!!.toolbar.ivNotification.visibility = View.VISIBLE
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isFirstTimeLoad) {
            initComponents1()
        }
    }

    override fun initComponents() {

    }
     fun initComponents1() {
        arrayList.add(getString(R.string.my_co2_saving))
        arrayList.add(getString(R.string.co2_receipt))
        setToolbar()
        initTabLayout()
    }

    private fun initTabLayout() {
        val userData = BaseApplication.sharedPreference.getPrefModel(EasyPref.USER_DATA, User::class.java)
        binding?.apply {
            mAdapter = MyCo2Adapter(childFragmentManager, lifecycle)

            viewPager.adapter = mAdapter
            viewPager.isSaveEnabled = false
            viewPager.offscreenPageLimit = 2

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = arrayList[position]
                viewPager.setCurrentItem(tab.position, true)
            }.attach()
            val count = 1


            for (i in 0..count) {
                val tab = tabLayout.getTabAt(i)
                tab.let {
                    val tabView = getTabView(i)
                    it!!.customView = tabView
                    if (userData?.lockCo2SurveyDashboard==true) {
                        if (i == 1) {
                            it.select()
                        }
                    }else if (userData?.lockCo2ReceiptDashboard==true){
                        if (i == 0) {
                            it.select()
                        }
                    }else{
                        if (i == 0) {
                            it.select()
                        }
                    }

                }
            }

            //	Tab Config
            tabLayout.tabMode = TabLayout.MODE_FIXED
            viewPager.isUserInputEnabled = false

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val customView = (tab!!.customView as ConstraintLayout).getChildAt(0)
                    if (customView is AppCompatTextView) {
                        customView.typeface = ResourcesCompat.getFont(requireActivity(), R.font.poppins_semi_bold_600)
                    }

                    if (toolbar.clSearch.visibility == View.VISIBLE) {
                       // hideSearchBar()
                    }
                    Log.e("", "onTabSelected:${tab.position} ", )
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    val customView = (tab!!.customView as ConstraintLayout).getChildAt(0)
                    if (customView is AppCompatTextView) {
                        customView.typeface = ResourcesCompat.getFont(requireActivity(), R.font.poppins_medium_500)
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }
            })
        }
    }

    private fun getTabView(position: Int): View {
        val tabBinding = LayoutCustomTabConnectionsBinding.inflate(requireActivity().layoutInflater, binding!!.root as ViewGroup, false)
        val item: String = arrayList[position]
        tabBinding.tvTitle.text = item
       // tabBinding.clMain.backgroundTintList = ColorStateList.valueOf(Color.parseColor(tenantInfoData?.brandingInfo?.primaryColor.toBlankString()))
        tabBinding.clMain.backgroundColorTint(tenantInfoData?.brandingInfo?.primaryColor)
        return tabBinding.root
    }

    override fun setClickListener() {
    }
    private fun setToolbar() {
        binding!!.toolbar.let {
            overrideToolbar(it, ToolbarConfig().apply {
                showMenuButton = true
                showNotificationIcon = true
                showLogoIcon = true
                showViewLine = true
            })

            it.ivMenu.clickWithDebounce {
                toggleDrawer()
            }
            it.ivNotification.clickWithDebounce {
                pushFragment(NotificationFragment())
            }

        }
    }
    override fun onStart() {
        super.onStart()
        showTabs()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    /**
     * Notification count update
     *
     * @param updateNotificationCount
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun notificationCountUpdate(updateNotificationCount: UpdateNotificationCount) {
        if ( updateNotificationCount.pushNotificationCount!!>0) {
            binding!!.toolbar.tvNotificationCount.visibility = View.VISIBLE
            binding!!.toolbar.ivNotification.visibility = View.VISIBLE
            binding!!.toolbar.tvNotificationCount.visibility = updateNotificationCount.pushNotificationCount?:0
        } else {
            binding!!.toolbar.tvNotificationCount.visibility = View.GONE
            binding!!.toolbar.ivNotification.visibility = View.VISIBLE
        }
    }
}