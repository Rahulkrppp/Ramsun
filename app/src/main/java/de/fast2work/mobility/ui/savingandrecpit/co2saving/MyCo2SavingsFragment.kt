package de.fast2work.mobility.ui.savingandrecpit.co2saving

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.SurveyStatisticsResp
import de.fast2work.mobility.data.response.User
import de.fast2work.mobility.databinding.FragmentMyCo2SavingsBinding
import de.fast2work.mobility.ui.co2.CalculateCo21Fragment
import de.fast2work.mobility.ui.co2.ViewSurveyFragment
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.dashboard.INDEX_CONTACT_CO2
import de.fast2work.mobility.ui.savingandrecpit.co2saving.adapter.MyCo2SurveyAdapter
import de.fast2work.mobility.ui.sidemenu.staticpage.StaticPageFragment
import de.fast2work.mobility.utility.extension.imageTickTint1
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.preference.EasyPref
import de.fast2work.mobility.utility.util.IConstants
import de.fast2work.mobility.utility.util.LocalConfig
import java.text.DecimalFormat
import java.text.NumberFormat


class MyCo2SavingsFragment : BaseVMBindingFragment<FragmentMyCo2SavingsBinding, MyCo2SavingsViewModel>(
    MyCo2SavingsViewModel::class.java) {
    private var surveyRespAdapter: MyCo2SurveyAdapter? = null
    private var surveyName:String=""

    companion object{
        const val PENDING="pending"
        const val IN_PROCESS="inprocess"
        const val SUCCESS="success"
        const val FAILED="failed"
        const val INVALID="invalid"
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return generateBinding(FragmentMyCo2SavingsBinding.inflate(inflater), container)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun attachObservers() {
        viewModel.surveyListLiveData.observe(this) {
//            if (it.data?.data?.size!! > 0) {
            viewModel.isLoaded = false
            viewModel.hasLoadedAllData = it.data?.currentPage == it.data?.totalPages
            if (it.data?.currentPage == 1) {
                viewModel.surveyList.clear()
            }
            it.data?.data?.let { it1 -> viewModel.surveyList.addAll(it1) }
            surveyRespAdapter?.notifyDataSetChanged()

            if (it.data?.data?.size!! > 0) {
                binding?.rvSurvey?.isVisible = true
                binding?.clNoData?.isVisible = false
            } else {
                binding?.rvSurvey?.isVisible = false
                binding?.clNoData?.isVisible = true
            }
        }


        viewModel.inProgressSurveyListLiveData.observe(this){
            if (it.data!=null && it.data.toBlankString().isNotEmpty()){
                if (it.data!!.surveyId!=0){
                    viewModel.surveyId= it.data!!.surveyId
                    surveyName=it.data!!.surveyName
                    binding!!.clNewSurvey.isVisible=true
                }else {
                    binding!!.clNewSurvey.isVisible=false
                }
            }else{
                binding!!.clNewSurvey.isVisible=false
            }
        }
        viewModel.getSurveyStatisticsListLiveData.observe(this){
            if (it.data!=null){
                setDataStatistics(it.data!!)
            }
        }
        viewModel.getSurveyStatusByUserId.observe(this){
            if (it.isSuccess){
                val data = it.data?.get(0)
                if (data?.status != null) {
                    when (data.status) {
                        SUCCESS -> {
                            if (BaseApplication.languageSharedPreference.getLanguagePref(
                                    EasyPref.CURRENT_LANGUAGE, "").equals("de", true)){
                                if (data.reportUrl.isNotEmpty()) {
                                    pushFragment(StaticPageFragment.newInstance(data.reportUrl, true, data.employeeReportPdfFile))
                                }
                            }else{
                                if (data.employeeEnReportFile.isNotEmpty() && data.employeeEnReportPdfFile.isNotEmpty()) {
                                    pushFragment(StaticPageFragment.newInstance(data.employeeEnReportFile, true, data.employeeEnReportPdfFile))
                                }else{
                                    pushFragment(StaticPageFragment.newInstance(data.reportUrl, true, data.employeeReportPdfFile))
                                }
                            }
                        }

                        PENDING, IN_PROCESS -> {
                            if (data.statusMessage.isEmpty() || data.statusMessage.isBlank()) {
                                showAlertMessage(getString(
                                    R.string.the_system_failed_to_generate_the_report_please_contact_admin_for_any_assistance))
                            } else {
                                showAlertMessage(data.statusMessage.toBlankString())
                            }
                        }

                        else -> {
                            if (data.statusMessage.isEmpty() || data.statusMessage.isBlank()) {
                                showErrorMessage(getString(
                                    R.string.the_system_failed_to_generate_the_report_please_contact_admin_for_any_assistance))
                            } else {
                                showErrorMessage(data.statusMessage.toBlankString())
                            }

                        }
                    }
                }else{
                    showErrorMessage(getString(
                        R.string.the_system_failed_to_generate_the_report_please_contact_admin_for_any_assistance))
                }
            }else{
                showErrorMessage(getString(
                    R.string.the_system_failed_to_generate_the_report_please_contact_admin_for_any_assistance))
            }
        }
    }

    private fun setDataStatistics(surveyStatisticsResp: ArrayList<SurveyStatisticsResp>) {
        binding?.apply {

            surveyStatisticsResp.forEachIndexed { index, surveyStatisticsResp ->
                if (surveyStatisticsResp.paramType.equals("co2",true)){
                    if (surveyStatisticsResp.yearlyTotal!=null){
                        tvDisplayCo2.text= (surveyStatisticsResp.yearlyTotal.toDouble()?:0.0).toString()
                           /* String.format("%.2f",surveyStatisticsResp.yearlyTotal)*/
                    }
                    tvDisplayMagrment.text=surveyStatisticsResp.paramUnit
                }
                if (surveyStatisticsResp.paramType.equals("distance",true)){
                    if (surveyStatisticsResp.yearlyTotal!=null){
                        tvDisplayDistance.text= (surveyStatisticsResp.yearlyTotal.toDouble()?:0.0).toString()
                            String.format("",surveyStatisticsResp.yearlyTotal)
                    }
                    tvDisplayType.text=surveyStatisticsResp.paramUnit
                   // tvMiles.text=surveyStatisticsResp.paramUnit
                }
                if (surveyStatisticsResp.paramType.equals("duration",true)){
                    if (surveyStatisticsResp.yearlyTotal!=null){
                        if (hours(surveyStatisticsResp.yearlyTotal)=="0"){
                            tvDisplayFitnessMinType.isVisible=false
                            tvDisplayFitnessMin.isVisible=false
                        }else{
                            tvDisplayFitnessMinType.isVisible=true
                            tvDisplayFitnessMin.isVisible=true
                            tvDisplayFitnessMin.text=hours(surveyStatisticsResp.yearlyTotal)
                        }

                        tvDisplayFitnessSec.text=minutes(surveyStatisticsResp.yearlyTotal)

                      // Log.e("", "setDataStatistics====: ${minutesToHours(surveyStatisticsResp.yearlyTotal)}", )
                    }
                    tvDisplayFitnessSecType.text=surveyStatisticsResp.paramUnit
                }
                if (surveyStatisticsResp.paramType.equals("microplastic",true)){
                    if (surveyStatisticsResp.yearlyTotal!=null){
                        tvDisplayMicro.text= (surveyStatisticsResp.yearlyTotal.toDouble()?:0.0).toString()
                    }
                    tvDisplayMicroType.text=surveyStatisticsResp.paramUnit
                }
                if (surveyStatisticsResp.paramType.equals("cost",true)){
                    if (surveyStatisticsResp.yearlyTotal!=null){
                        tvDisplayMoney.text= (surveyStatisticsResp.yearlyTotal.toDouble()?:0.0).toString()
                    }
                    tvDisplayMoneyType.text=surveyStatisticsResp.paramUnit
                }
            }

        }
    }

    override fun initComponents() {
        val userData = BaseApplication.sharedPreference.getPrefModel(EasyPref.USER_DATA, User::class.java)
        if (userData?.lockCo2SurveyDashboard==true) {
            binding!!.clLockChart.isVisible = true
            binding!!.swipeRefresh.isEnabled = false
            binding!!.clNewSurvey.isVisible=true
            binding!!.clNewSurvey.isEnabled=false
        }else{
            binding!!.clLockChart.isVisible = false
            binding!!.clMain.isVisible=true

            if (isFirstTimeLoad){
                viewModel.currentPage = 1
                viewModel.callGetSurveyStatisticsByUserIdApi()
                viewModel.callGetUserSurveyApi(viewModel.currentPage, IConstants.PAGE_LIMIT_20)
                initRecyclerView()
                //setToolbar()
                //binding!!.ivNoDataBg.imageTickTint(tenantInfoData?.brandingInfo?.primaryColor)
            }
            binding!!.ivNoData.imageTickTint1(tenantInfoData?.brandingInfo?.primaryColor)
            binding?.btnContactAdmin?.backgroundTintList = ColorStateList.valueOf(Color.parseColor(tenantInfoData?.brandingInfo?.primaryColor.toBlankString()))
        }
        binding!!.tvNewSurvey.setSelected(true)

    }
    override fun onResume() {
        super.onResume()
        if (!tenantInfoData?.tenantInfo?.enabledServices.equals(LocalConfig.co2_management, true)) {
            viewModel.callInProgressSurveyApi()
        }

    }
    override fun setClickListener() {
        binding?.apply {
            clNewSurvey.clickWithDebounce {
                //pushFragment(CalculateCo2Fragment.newInstance(surveyId = viewModel.surveyId))
                pushFragment(CalculateCo21Fragment.newInstance(surveyId = viewModel.surveyId,surveyName))
            }
            swipeRefresh.setOnRefreshListener {
                viewModel.currentPage = 1
                swipeRefresh.isRefreshing=false
                viewModel.isLoaded = false
                binding!!.clNewSurvey.isVisible=false
                viewModel.callGetUserSurveyApi(viewModel.currentPage, IConstants.PAGE_LIMIT_20)
                viewModel.callGetSurveyStatisticsByUserIdApi()
                viewModel.callInProgressSurveyApi()
            }
            btnContactAdmin.clickWithDebounce {
                switchTab(INDEX_CONTACT_CO2)
            }
        }


    }
  /*  private fun setToolbar() {
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
    }*/
    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerView() {
        binding?.apply {
            surveyRespAdapter = MyCo2SurveyAdapter(requireContext(), viewModel.surveyList
            ) { view, model, _ ->
                when (view.id) {
                    R.id.tv_view_survey ->{
                        //pushFragment(CalculateCo2Fragment.newInstance(true,model.surveyId))
                        pushFragment(ViewSurveyFragment.newInstance(model.surveyId,model.surveyName))
                    }
                    R.id.tv_view_report ->{
                        viewModel.callGetSurveyStatusByUserId(model.surveyId)
//                        if (model.status != null) {
//                            when (model.status) {
//                                SUCCESS -> {
//                                    pushFragment(StaticPageFragment.newInstance(model.reportUrl, true))
//                                }
//
//                                PENDING, IN_PROCESS -> {
//                                    if (model.statusMessage.isEmpty() || model.statusMessage.isBlank()) {
//                                        showAlertMessage(getString(
//                                            R.string.the_system_failed_to_generate_the_report_please_contact_admin_for_any_assistance))
//                                    } else {
//                                        showAlertMessage(model.statusMessage)
//                                    }
//                                }
//
//                                else -> {
//                                    if (model.statusMessage.isEmpty() || model.statusMessage.isBlank()) {
//                                        showErrorMessage(getString(
//                                            R.string.the_system_failed_to_generate_the_report_please_contact_admin_for_any_assistance))
//                                    } else {
//                                        showErrorMessage(model.statusMessage)
//                                    }
//
//                                }
//                            }
//                        }else{
//                            viewModel.callGetSurveyStatusByUserId(model.surveyId)
////                            showErrorMessage(getString(
////                                R.string.the_system_failed_to_generate_the_report_please_contact_admin_for_any_assistance))
//                        }
                    }
                }
            }
            rvSurvey.layoutManager = LinearLayoutManager(baseActivity)
            rvSurvey.adapter = surveyRespAdapter



            svMain.setOnScrollChangeListener { v: NestedScrollView, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
                if (scrollY > oldScrollY) {
                    Log.i("=============", "Scroll DOWN")
                }
                if (scrollY < oldScrollY) {
                    Log.i("=============", "Scroll UP")
                }
                if (scrollY == 0) {
                    Log.i("=============", "TOP SCROLL")
                }
                if (v.getChildAt(v.childCount - 1) != null) {
                    Log.e("============","viewModel.hasLoadedAllData:: ${viewModel.hasLoadedAllData}")
                    if (scrollY >= (v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight) && !viewModel.hasLoadedAllData) {
                        viewModel.isLoaded = true
                        viewModel.currentPage++
                        viewModel.callGetUserSurveyApi(viewModel.currentPage, IConstants.PAGE_LIMIT_20)
                    }
                }
            }
        }
    }
/*
    *//**
     * Notification count update
     *
     * @param updateNotificationCount
     *//*
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
    }*/

/*    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }*/
    private fun minutes(minutes: Double): String {
        //val hours = minutes / 60
        val remainingMinutes = minutes % 60
        Log.e("", "minutes: $remainingMinutes")
        val formatter: NumberFormat = DecimalFormat("#0")
        return formatter.format(remainingMinutes)
    }
    private fun hours(minutes: Double): String {
        val hours = minutes / 60
        //val remainingMinutes = minutes % 60
        Log.e("", "hours: $hours")
        val formatter: NumberFormat = DecimalFormat("#0")
        return formatter.format(hours)
    }
}