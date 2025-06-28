package de.fast2work.mobility.ui.home

//import de.fast2work.mobility.utility.extension.MMM_YYYY
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vesputi.mobilitybox_ticketing_android.models.Mobilitybox
import com.vesputi.mobilitybox_ticketing_android.models.MobilityboxTicketCode
import com.vesputi.mobilitybox_ticketing_android.models.MobilityboxTicketValidity
import com.vesputi.mobilitybox_ticketing_android.views.MobilityboxBottomSheetFragment
import de.fast2work.mobility.R
import de.fast2work.mobility.data.eventbus.UpdateNotificationCount
import de.fast2work.mobility.data.model.NotificationDTicketData
import de.fast2work.mobility.data.response.ActiveTicket
import de.fast2work.mobility.data.response.BudgetItem
import de.fast2work.mobility.data.response.NotificationData
import de.fast2work.mobility.data.response.PushNotification
import de.fast2work.mobility.data.response.User
import de.fast2work.mobility.databinding.FragmentHomeBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.dashboard.DashboardActivity
import de.fast2work.mobility.ui.dashboard.INDEX_CENTER_CO2
import de.fast2work.mobility.ui.dashboard.INDEX_CONTACT_CO2
import de.fast2work.mobility.ui.home.adapter.BudgetManagementAdapter
import de.fast2work.mobility.ui.home.adapter.CategoryAdapter
import de.fast2work.mobility.utility.chart.notimportant.charting.animation.Easing
import de.fast2work.mobility.utility.chart.notimportant.charting.data.PieData
import de.fast2work.mobility.utility.chart.notimportant.charting.data.PieDataSet
import de.fast2work.mobility.utility.chart.notimportant.charting.data.PieEntry
import de.fast2work.mobility.utility.chart.notimportant.charting.utils.ColorTemplate
import de.fast2work.mobility.utility.customview.ThreeItemsLinearLayoutManager
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.extension.MM_FORMAT
import de.fast2work.mobility.utility.extension.SERVER_FORMAT
import de.fast2work.mobility.utility.extension.YYYY_FORMAT
import de.fast2work.mobility.utility.extension.displayDate
import de.fast2work.mobility.utility.extension.formatCurrency
import de.fast2work.mobility.utility.extension.formatCurrencyNew
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.imageTickTint
import de.fast2work.mobility.utility.extension.parcelable
import de.fast2work.mobility.utility.extension.setTint
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.preference.EasyPref
import de.fast2work.mobility.utility.preference.EasyPref.Companion.USER_DATA
import de.fast2work.mobility.utility.util.IConstants.Companion.BUNDLE_PUSH_NOTIFICATION
import de.fast2work.mobility.utility.util.IConstantsIcon
import de.fast2work.mobility.utility.util.LocalConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.log
import kotlin.math.roundToInt


/**
 *  Fragment used for HomeFragment
 */
class HomeFragment : BaseVMBindingFragment<FragmentHomeBinding, HomeViewModel>(HomeViewModel::class.java) {

    private var categoryAdapter: CategoryAdapter? = null
    private var budgetManagementAdapter: BudgetManagementAdapter? = null
    private var userData: User? = User()
    private var clickRotation = false
    private var budgetItemData: BudgetItem?=null
    var isWidthSet=false
    var activeTicketData:ActiveTicket?=null

    //    var tenant:TenantInfoModel? =null
    companion object {

        var isAdChangeTimerRunning = false
        const val IS_CO2 = "co2"
        const val IS_PROFILE_COMPLETE = "isProfileComplete"


        fun newInstance(pushNotificationData: PushNotification? /*isProfileComplete: String*/) = HomeFragment().apply {
            this.arguments = Bundle().apply {
                pushNotificationData?.let { this.putParcelable(BUNDLE_PUSH_NOTIFICATION, it) }
                //this.putString(IS_PROFILE_COMPLETE,isProfileComplete)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return generateBinding(FragmentHomeBinding.inflate(inflater), container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isFirstTimeLoad) {
            isWidthSet = false
            initRecyclerView()
            initBudgetManagementRecyclerView()
            BaseApplication.sharedPreference.setPref("categoryPosition", 0)
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.callActiveTicketInfoUserId()
    }

    /**
     * This method contains setCalenderDate
     *
     */
    private fun setCalenderDate() {

        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH]
        viewModel.categoryDate = ("$year-0${month + 1}-01")
        viewModel.callBudgetGroupApi(true, viewModel.categoryDate)
        binding!!.tvCalendar.text = viewModel.categoryDate.displayDate(SERVER_FORMAT, SimpleDateFormat("MMM yyyy", if (BaseApplication.languageSharedPreference.getLanguagePref(EasyPref.CURRENT_LANGUAGE, "").equals("de", true)) Locale.GERMAN else Locale.ENGLISH))
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
        viewModel.budgetGroupListLiveData.observe(this) {
            /*binding!!.llBudgetValue.isVisible=true
            if (it.data?.data?.isNotEmpty() == true) {
                viewModel.categoryList.clear()
                it.data?.data?.filter { i -> i.status.lowercase() == "active" }?.let { it1 -> viewModel.categoryList.addAll(it1) }*//* binding!!.ivNext.alpha=0.5f
                    binding!!.ivNext.isEnabled=false*//*
                //binding?.ivNext?.alpha = 1.0f
                binding!!.ivNext.isVisible = categoryAdapter?.itemCount!! > 3
                if (viewModel.categoryList.size != 0) {
                    val selectedPosition = BaseApplication.sharedPreference.getPref("categoryPosition", 0)
                    viewModel.categoryList.forEachIndexed { index, budgetItem ->
                        budgetItemData = budgetItem
                        if (index == 0 && selectedPosition == 0) {
                            budgetItem.isSelect = true
                            setProgressbar(budgetItem)
                            viewModel.categoryTypeName = budgetItem.budgetGroupName
                            viewModel.budgetGroupId = budgetItem.budgetGroupId.toBlankString()
                            viewModel.callBudgetGroupInfoApi(false, budgetItem.budgetGroupId.toBlankString(), viewModel.categoryDate)
                        } else if (index == selectedPosition) {
                            budgetItem.isSelect = true
                            setProgressbar(budgetItem)
                            viewModel.categoryTypeName = budgetItem.budgetGroupName
                            viewModel.budgetGroupId = budgetItem.budgetGroupId.toBlankString()
                            viewModel.callBudgetGroupInfoApi(false, budgetItem.budgetGroupId.toBlankString(), viewModel.categoryDate)
                        }

                    }

                    binding!!.rvBudgetManagement.isVisible = true
                    binding!!.clNoData.isVisible = false

                }

                //BaseApplication.sharedPreference.setPref(CURRENCY_SYMBOL, it.data?.data?.get(0)?.symbol.toBlankString())
                categoryAdapter?.notifyDataSetChanged()
            } else {
                binding!!.rvBudgetManagement.isVisible = false
                binding!!.clNoData.isVisible = true
                binding!!.tvTotalBudget.text = "€0.0"
                binding!!.tvUnutilizedBudget.text = "€0.0"

            }
            manageNotificationRedirectionIfAny()*/
        }

        viewModel.budgetGroupInfoListLiveData.observe(this) {
            // Log.e("", "=========:${it.data?.data} ", )
            if (it.data?.data?.isNotEmpty() == true || it.data?.data?.size!! > 0) {
                binding!!.rvBudgetManagement.isVisible = true
                binding!!.clNoData.isVisible = false
                viewModel.budgetManagementInfoList.clear()
                viewModel.budgetManagementInfoList.addAll(it.data?.data!!)
                //Log.e("", "=========:${it.data?.data} ")
                budgetManagementAdapter?.notifyDataSetChanged()
            } else {
                binding!!.rvBudgetManagement.isVisible = false
                binding!!.clNoData.isVisible = true
            }
        }
        viewModel.errorLiveData.observe(this) {
            showErrorMessage(it)
        }
        viewModel.activeTicketInfoLiveData.observe(this){
            Log.e("", "attachObservers:${it.data?.activeTicket?.ticketId} ", )
             activeTicketData=it.data?.activeTicket
            val activityTicket=ActiveTicket()
            activityTicket.ticketId= activeTicketData?.ticketId
            activityTicket.couponId= activeTicketData?.couponId
            activityTicket.orderId= activeTicketData?.orderId.toString()
            activityTicket.subscriptionId= activeTicketData?.subscriptionId.toString()
            activityTicket.subscriptionExpiredAt= activeTicketData?.subscriptionExpiredAt.toString()
            activityTicket.eligibleForBuy= activeTicketData?.eligibleForBuy == true
            activityTicket.status= activeTicketData?.status.toString()
            /*BaseApplication.sharedPreference.setPref(EasyPref.activeTicket, activityTicket)
            if (activeTicketData?.eligibleForBuy==true){
                BaseApplication.sharedPreference.setPref(EasyPref.D_TICKET, IConstantsIcon.DISABLE)
            }else{
                when (activeTicketData?.status) {
                    "active" -> {
                        BaseApplication.sharedPreference.setPref(EasyPref.D_TICKET, IConstantsIcon.ACTIVATED)
                    }
                    "inactive" -> {
                        BaseApplication.sharedPreference.setPref(EasyPref.D_TICKET, IConstantsIcon.PENDING)

                    }
                    "cancelled" -> {
                        BaseApplication.sharedPreference.setPref(EasyPref.D_TICKET, IConstantsIcon.cancelled)
                    }
                    else -> {
                        BaseApplication.sharedPreference.setPref(EasyPref.D_TICKET, IConstantsIcon.DISABLE)
                    }
                }
            }*/
            setToolbar()
        }
    }

    /**
     * This method contains setProgressbar data
     *
     */
    private fun setProgressbar(budgetItem: BudgetItem) {
        binding?.apply {
            //semiCircleProgressView.setStrokeWidth(40f)
            //semiCircleProgressView.setProgress(budgetItem.getBudgetProgressValue())
            //semiCircleProgressView.setAnimation()
            tvTotalBudget.text = budgetItem.totalAllocatedBudget.formatCurrencyNew(budgetItem.symbol)
            tvUnutilizedBudget.text = budgetItem.totalRemainingBudget.formatCurrencyNew(budgetItem.symbol)

            tvAvailableAmount.setTextColor(Color.parseColor(budgetItem.amountInformation!!.colors!!.availableColor))
            tvUsedAmount.setTextColor(Color.parseColor(budgetItem.amountInformation!!.colors!!.usedColor))
            tvPendingAmount.setTextColor(Color.parseColor(budgetItem.amountInformation!!.colors!!.pendingColor))

            tvAvailableAmountValue.text = budgetItem.amountInformation!!.availableAmount.formatCurrencyNew(budgetItem.symbol)
            tvPendingAmountValue.text = budgetItem.amountInformation!!.pendingAmount.formatCurrencyNew(budgetItem.symbol)
            tvUsedAmountValue.text = budgetItem.amountInformation!!.usedAmount.formatCurrencyNew(budgetItem.symbol)

            setPieData(budgetItem)

        }
    }

    /**
     * This method contains code to setPieData progress chart sem circle
     *
     */
    private fun setPieData(budgetItem: BudgetItem){
        val pieEntries: ArrayList<PieEntry> = ArrayList<PieEntry>()
        val label = "type"


        if (!isWidthSet) {
            isWidthSet=true
            val width = (binding!!.PieData.getWidth() * 1.35f)
            val height = (binding!!.PieData.getHeight() * 1.35f)

            val params = binding!!.PieData.getLayoutParams()
            params.width = width.toInt()
            params.height = height.toInt()
            binding!!.PieData.setLayoutParams(params)
            binding!!.PieData.setExtraOffsets( 10f, 0f, 10f,  -150f)
        }
        binding!!.PieData.isDrawHoleEnabled = true
        binding!!.PieData.setHoleColor(Color.TRANSPARENT)
        binding!!.PieData.setTransparentCircleColor(Color.TRANSPARENT)

        binding!!.PieData.setTransparentCircleRadius(80f);
        binding!!.PieData.maxAngle = 180f
        binding!!.PieData.rotationAngle = 180f
        binding!!.PieData.setCenterTextOffset(0f,-20f)
        binding!!.PieData.animateY(1000,Easing.EaseInOutCubic)
        binding!!.PieData.legend.isEnabled = false
        binding!!.PieData.holeRadius=80f


        binding!!.PieData.setRotationEnabled(false)
        binding!!.PieData.setHighlightPerTapEnabled(false)
        //initializing data
        /*val colors = ArrayList<Int>()
        val typeAmountMap: MutableMap<String, Int> = HashMap()
        if (budgetItem.amountInformation?.availableAmount?.toInt() ==0 && budgetItem.amountInformation?.usedAmount?.toInt() ==0 && budgetItem.amountInformation?.pendingAmount?.toInt() ==0){
            typeAmountMap["availableAmount"] =100
            if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
                colors.add(Color.parseColor("#4D274072"))
            }else {
                colors.add(Color.parseColor("#D9D9D9"))
            }
        }else {
            typeAmountMap["availableAmount"] =
                budgetItem.amountInformation!!.availableAmount!!.roundToInt()
            typeAmountMap["usedAmount"] = budgetItem.amountInformation!!.usedAmount!!.roundToInt()
            typeAmountMap["pendingAmount"] =
                budgetItem.amountInformation!!.pendingAmount!!.roundToInt()

            colors.add(Color.parseColor(budgetItem.amountInformation!!.colors!!.availableColor))
            colors.add(Color.parseColor(budgetItem.amountInformation!!.colors!!.usedColor))
            colors.add(Color.parseColor(budgetItem.amountInformation!!.colors!!.pendingColor))
        }*/



        //initializing colors for the entries




        //input data and fit data into pie chart entry



        //collecting the entries with label name
        val pieDataSet: PieDataSet = PieDataSet(pieEntries, label)

        //setting text size of the value
        binding!!.PieData.setDrawCenterText(false)

        //binding!!.chart.setRotationAngle(0f)
        // enable rotation of the chart by touch
        // enable rotation of the chart by touch
        binding!!.PieData.setDrawSliceText(false); // To remove slice text
        binding!!.PieData.setDrawMarkers(false); // To remove markers when click
        binding!!.PieData.setDrawEntryLabels(false); // To remove labels from piece of pie
        binding!!.PieData.getDescription().setEnabled(false); //
        pieDataSet.setDrawValues(false)
        //providing color list for coloring different entries
        //pieDataSet.setColors(colors)

        //grouping the data set from entry to chart
        val pieData: PieData = PieData(pieDataSet)
        pieData.setValueTextSize(0f)
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(true)

        binding!!.PieData.setData(pieData)
        binding!!.PieData.invalidate()
    }


    /**
     * This method contains code to handle initial
     *
     */
    override fun initComponents() {
//        tenant=(BaseApplication.tenantSharedPreference.getTenantPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java) )


        if (tenantInfoData?.tenantInfo?.enabledServices.equals(LocalConfig.co2_management, true)) {
            binding!!.clLockHome.isVisible = true
            binding!!.clMainHomeInvoice.isVisible = false
            binding!!.swipeRefresh.isEnabled = false
            if (isFirstTimeLoad){
                switchTab(INDEX_CENTER_CO2)
            }
        } else {
            if (isFirstTimeLoad) {
                binding!!.llBudgetValue.isVisible=false
                setCalenderDate()
            }
            binding!!.swipeRefresh.isEnabled = true
            binding!!.clLockHome.isVisible = false
            binding!!.clMainHomeInvoice.isVisible = true
        }
        viewModel.pushNotification = if (requireArguments().containsKey(BUNDLE_PUSH_NOTIFICATION)) requireArguments().parcelable<PushNotification>(BUNDLE_PUSH_NOTIFICATION)
        else null


        //userData = BaseApplication.sharedPreference.getPrefModel(USER_DATA, User::class.java)
        setToolbar()
        if (userData?.firstName.isNullOrEmpty()) {
            binding?.tvWelcomeBack?.text = getString(R.string.welcome_, getString(R.string.user))
        } else {
            binding?.tvWelcomeBack?.text = getString(R.string.welcome_, userData?.firstName)
        }
        binding?.ivLockHome?.setTint(tenantInfoData?.brandingInfo?.primaryColor.toBlankString())
        binding?.btnContactAdmin?.backgroundTintList = ColorStateList.valueOf(Color.parseColor(tenantInfoData?.brandingInfo?.primaryColor.toBlankString()))
        binding!!.noData.ivNoData.imageTickTint(tenantInfoData?.brandingInfo?.primaryColor)
        binding!!.noData.ivNoDataBg.imageTickTint(tenantInfoData?.brandingInfo?.primaryColor)

    }


    /**
     * This method contains code for all the clickListener in our app
     *
     */
    override fun setClickListener() {
        binding?.apply {
            ivNext.clickWithDebounce {
                if (viewModel.categoryList.size > 0) {
                    if (clickRotation) {
                        scrollToPosition(0)
                        ivNext.rotation = 0f
                        clickRotation = false
                    } else {
                        clickRotation = true
                        ivNext.rotation = 180f
                        scrollToPosition(viewModel.categoryList.size - 1)
                    }
                }
            }
            tvCalendar.setOnClickListener {
                val pd = de.fast2work.mobility.utility.customview.MonthYearPickerDialog(viewModel.categoryDate.displayDate(SERVER_FORMAT, MM_FORMAT).toInt(), viewModel.categoryDate.displayDate(SERVER_FORMAT, YYYY_FORMAT).toInt())
                pd.setListener { _, p1, p2, _ ->
                    viewModel.categoryDate = "$p1-${String.format("%02d", p2)}-01"
                    //showToast("$p1-${String.format("%02d", p2)}-01")
                    viewModel.callBudgetGroupApi(true, viewModel.categoryDate)
                    binding!!.tvCalendar.text = viewModel.categoryDate.displayDate(SERVER_FORMAT, SimpleDateFormat("MMM yyyy", if (BaseApplication.languageSharedPreference.getLanguagePref(EasyPref.CURRENT_LANGUAGE, "").equals("de", true)) Locale.GERMAN else Locale.ENGLISH))
                }
                pd.show(parentFragmentManager, "MonthYearPickerDialog")
            }
            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                setCalenderDate()
                //viewModel.callBudgetGroupApi(true, viewModel.categoryDate)
            }
            btnContactAdmin.clickWithDebounce {
                switchTab(INDEX_CONTACT_CO2)
            }
            ivAddDTicket.clickWithDebounce {

            }
        }
    }

    private fun setToolbar() {
        Mobilitybox.setup(requireActivity().getSharedPreferences("AppPref", Context.MODE_PRIVATE))
        binding!!.toolbar.let {
            overrideToolbar(it, ToolbarConfig().apply {
                showMenuButton = true
                showNotificationIcon = true
                showLogoIcon = true
                showViewLine = true
                if (BaseApplication.sharedPreference.getPref(EasyPref.D_TICKET, "")==IConstantsIcon.ACTIVATED){
                   // showFilterHappening=true
                }else if (BaseApplication.sharedPreference.getPref(EasyPref.D_TICKET, "")==IConstantsIcon.cancelled){
                    //showFilterHappening=true
                }else{
                   // showFilterHappening=false
                }
            })

            it.ivMenu.clickWithDebounce {
                toggleDrawer()
            }
            it.ivNotification.clickWithDebounce {

            }
            it.ivFilterHappening.clickWithDebounce{
                MobilityboxTicketCode(BaseApplication.sharedPreference.getPrefModel(EasyPref.activeTicket, ActiveTicket::class.java)?.ticketId.toString()).fetchTicket({ ticket->
                    if(ticket.validity() == MobilityboxTicketValidity.VALID) {
                        val ticketBottomSheet = MobilityboxBottomSheetFragment.newInstance(
                            ticket,
                            BaseApplication.sharedPreference.getPrefModel(EasyPref.activeTicket, ActiveTicket::class.java)?.ticketId.toString()
                        )

                        ticketBottomSheet.show(requireActivity().supportFragmentManager, "ticket")
                    }
                    //ticketBottomSheet(childFragmentManager, "Ticket Inspection View")
                },{error->

                })
            }

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerView() {
        binding?.apply {
            categoryAdapter = CategoryAdapter(requireContext(), viewModel.categoryList) { view, model, position ->
                when (view.id) {
                    R.id.tv_item -> {
                        Log.e("", "initRecyclerView======: ${position}=======${viewModel.categoryList.size - 1}")
                        if (viewModel.categoryList.size - 1 == position) {
                            clickRotation = true
                            ivNext.rotation = 180f
                        } else {
                            ivNext.rotation = 0f
                            clickRotation = false
                        }
                        viewModel.categoryList.forEach {
                            it.isSelect = false
                        }
                        BaseApplication.sharedPreference.setPref("categoryPosition", position)
                        model.isSelect = true
                        setProgressbar(model)
                        viewModel.categoryTypeName = ""
                        viewModel.categoryTypeName = model.budgetGroupName
                        viewModel.budgetGroupId = model.budgetGroupId.toBlankString()
                        viewModel.callBudgetGroupInfoApi(true, model.budgetGroupId.toBlankString(), viewModel.categoryDate)
                        categoryAdapter?.notifyDataSetChanged()
                    }
                }
            }
            rvCtg.layoutManager = ThreeItemsLinearLayoutManager(requireContext())
            rvCtg.adapter = categoryAdapter
            binding!!.rvCtg.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    updateButtonStates()
                }
            })
        }
    }

    /**
     * This method contains code for scrollToPosition
     *
     */
    private fun scrollToPosition(position: Int) {
        binding!!.rvCtg.smoothScrollToPosition(position) // Use smoothScrollToPosition for smooth scrolling
        // Or use recyclerView.scrollToPosition(position) for immediate scrolling
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initBudgetManagementRecyclerView() {
        binding?.apply {
            budgetManagementAdapter = BudgetManagementAdapter(requireContext(), viewModel.budgetManagementInfoList) { view, model, position ->
                when (view.id) {
                    R.id.cl_mail -> {
                        Log.e("==========", "model: $model")
                    }
                }
            }
            rvBudgetManagement.layoutManager = LinearLayoutManager(activity)
            rvBudgetManagement.adapter = budgetManagementAdapter
        }
    }

    /**
     * This method contains code for manageNotificationRedirectionIfAny
     *
     */

    private fun manageNotificationRedirectionIfAny(): Boolean {
        if (viewModel.pushNotification != null) {
            if (!viewModel.pushNotification!!.refType.isNullOrEmpty()) {
                when (viewModel.pushNotification!!.refType) {
                    NotificationData.INVOICE_DETAIL -> {
                        requireArguments().clear()
                        return true
                    }

                    NotificationData.UPLOAD_INVOICE -> {
                        requireArguments().clear()
                        return true
                    }

                    NotificationData.INVOICE_CO2_REQUIRED -> {
                        requireArguments().clear()
                        return true
                    }
                    NotificationData.D_TICKET ->{
                        BaseApplication.sharedPreference.setPref(EasyPref.D_TICKET, IConstantsIcon.ACTIVATED)
                        val activityTicket=ActiveTicket()
                        activityTicket.ticketId= viewModel.pushNotification!!.ticketId
                        activityTicket.couponId= viewModel.pushNotification!!.couponId
                        activityTicket.orderId= viewModel.pushNotification!!.orderId
                        activityTicket.subscriptionId= viewModel.pushNotification!!.subscriptionId
                        activityTicket.subscriptionExpiredAt= viewModel.pushNotification!!.subscriptionExpiredAt
                        activityTicket.eligibleForBuy=viewModel.pushNotification!!.eligibleForBuy
                        activityTicket.status=viewModel.pushNotification!!.status

                        BaseApplication.sharedPreference.setPref(EasyPref.activeTicket, activityTicket)

                        if (activityTicket.eligibleForBuy){
                            BaseApplication.sharedPreference.setPref(EasyPref.D_TICKET, IConstantsIcon.DISABLE)
                        }else{
                            when (activityTicket.status) {
                                "active" -> {
                                    BaseApplication.sharedPreference.setPref(EasyPref.D_TICKET, IConstantsIcon.ACTIVATED)
                                }
                                "inactive" -> {
                                    BaseApplication.sharedPreference.setPref(EasyPref.D_TICKET, IConstantsIcon.PENDING)

                                }
                                "cancelled" -> {
                                    BaseApplication.sharedPreference.setPref(EasyPref.D_TICKET, IConstantsIcon.cancelled)
                                }
                                else -> {
                                    BaseApplication.sharedPreference.setPref(EasyPref.D_TICKET, IConstantsIcon.DISABLE)
                                }
                            }
                        }

                        requireArguments().clear()
                        Handler(Looper.getMainLooper()).postDelayed({
                        }, 1000)

                        return true
                    }
                }
            }
        }
        return false
    }


    override fun onStart() {
        super.onStart()
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
        if (updateNotificationCount.pushNotificationCount!! > 0) {
            binding!!.toolbar.tvNotificationCount.visibility = View.VISIBLE
            binding!!.toolbar.ivNotification.visibility = View.VISIBLE
            binding!!.toolbar.tvNotificationCount.visibility = updateNotificationCount.pushNotificationCount ?: 0
        } else {
            binding!!.toolbar.tvNotificationCount.visibility = View.GONE
            binding!!.toolbar.ivNotification.visibility = View.VISIBLE
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun notificationDTicket(notificationData: NotificationDTicketData) {
        if (notificationData.refType==NotificationData.D_TICKET){
            Log.e("", "notificationDTicket: ${notificationData}", )


            if (notificationData.ticketId.isEmpty()){
                binding!!.toolbar.ivFilterHappening.visibility = View.GONE
            }else{
                binding!!.toolbar.ivFilterHappening.visibility = View.VISIBLE
            }
        }
    }

    /**
     * This method contains code for scrollToPosition
     *
     */
    private fun updateButtonStates() {
        val layoutManager = binding!!.rvCtg.layoutManager as ThreeItemsLinearLayoutManager

        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

        val totalItemCount = categoryAdapter!!.itemCount

        Log.e("", "updateButtonStates:${lastVisiblePosition} === ${totalItemCount - 1}")
        if (lastVisiblePosition >= viewModel.categoryList.size - 1) {
            clickRotation = true
            binding!!.ivNext.rotation = 180f
        } else {
            binding!!.ivNext.rotation = 0f
            clickRotation = false
        }

    }
}