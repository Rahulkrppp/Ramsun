package de.fast2work.mobility.ui.co2

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import de.fast2work.mobility.R
import de.fast2work.mobility.data.eventbus.UpdateNotificationCount
import de.fast2work.mobility.data.model.DayModel
import de.fast2work.mobility.data.response.Co2SurveyGetDataResp
import de.fast2work.mobility.data.response.ItemTransportDetails
import de.fast2work.mobility.data.response.ModeOfTransport
import de.fast2work.mobility.data.response.PushNotification
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.data.response.User
import de.fast2work.mobility.databinding.FragmentCalculateCo2Binding
import de.fast2work.mobility.ui.co2.SearchListFragment.Companion.PARAM_RESULT
import de.fast2work.mobility.ui.co2.SearchListFragment.Companion.PARAM_SEND_MODEL
import de.fast2work.mobility.ui.co2.SearchListFragment.Companion.PARAM_SET_SOURCE
import de.fast2work.mobility.ui.co2.bottom.CO2EmissionsSurveyInstructionsBottomSheetFragment
import de.fast2work.mobility.ui.co2.bottom.Co2ModeOfTransportBottomSheetFragment
import de.fast2work.mobility.ui.co2.bottom.Co2ModeOfTransportViewModel
import de.fast2work.mobility.ui.co2.bottom.TransportDetailBottomSheetFragment
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.dashboard.DashboardActivity
import de.fast2work.mobility.ui.home.HomeFragment
import de.fast2work.mobility.ui.sidemenu.notification.NotificationFragment
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.extension.backgroundColorTint
import de.fast2work.mobility.utility.extension.buttonTextColorNotification
import de.fast2work.mobility.utility.extension.buttonTextColorText
import de.fast2work.mobility.utility.extension.capitalized
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.getTrimText
import de.fast2work.mobility.utility.extension.imageTickTintCheckBox
import de.fast2work.mobility.utility.extension.parcelable
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.preference.EasyPref
import de.fast2work.mobility.utility.util.IConstants
import de.fast2work.mobility.utility.util.LocalConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.log

/**
 * Fragment used for CalculateCo2 Upload
 * */
class CalculateCo2Fragment : BaseVMBindingFragment<FragmentCalculateCo2Binding, CalculateCo2ViewModel>(
    CalculateCo2ViewModel::class.java) {
    private var sourceDestination=false
    private val co2ModeOfTransportViewModel: Co2ModeOfTransportViewModel by viewModels()
    private var modelModeOfTransport: ModeOfTransport?=null
   private var modelTransportDetails: ItemTransportDetails?=null
    private var daySelected=""

    companion object {
        const val PARAM_IS_VIEW_SURVEY="viewSurvey"
        const val PARAM_SURVEY_ID="SurveyId"
        fun newInstance(isViewSurvey:Boolean=false,surveyId:Int=0) = CalculateCo2Fragment().apply {
            this.arguments = Bundle().apply {
                this.putBoolean(PARAM_IS_VIEW_SURVEY,isViewSurvey)
                this.putInt(PARAM_SURVEY_ID,surveyId)

            }
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return generateBinding(FragmentCalculateCo2Binding.inflate(inflater), container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(PARAM_RESULT) { _: String, bundle: Bundle ->
            sourceDestination=bundle.getBoolean(PARAM_SET_SOURCE)
            viewModel.sourceDestination = bundle.parcelable(PARAM_SEND_MODEL)
            if (sourceDestination){
                binding!!.telSearchSource.setText(viewModel.sourceDestination?.compressed_address)
                viewModel.fromAddressLocal=viewModel.sourceDestination?.compressed_address.toBlankString()
                viewModel.fromLatitudeLocal=viewModel.sourceDestination?.lat.toBlankString()
                viewModel.fromLongitudeLocal=viewModel.sourceDestination?.long.toBlankString()
                searchIconSetSource()
            }else{
                binding!!.telSearchDestination.setText(viewModel.sourceDestination?.compressed_address)
                viewModel.toAddressLocal=viewModel.sourceDestination?.compressed_address.toBlankString()
                viewModel.toLatitudeLocal=viewModel.sourceDestination?.lat.toBlankString()
                viewModel.toLongitudeLocal=viewModel.sourceDestination?.long.toBlankString()
                searchIconSetDestination()
            }
        }
    }

    override fun attachObservers() {
        /*BaseApplication.notificationCount.observe(this){
            if (it > 0) {
                binding!!.toolbar.tvNotificationCount.visibility = View.VISIBLE
                binding!!.toolbar.ivNotification.visibility = View.VISIBLE
                binding!!.toolbar.tvNotificationCount.text = it.toBlankString()
            } else {
                binding!!.toolbar.tvNotificationCount.visibility = View.GONE
                binding!!.toolbar.ivNotification.visibility = View.VISIBLE
            }
        }*/
        viewModel.co2SurveyLiveData.observe(this){
            if (it.isSuccess){
                showSuccessMessage(it.responseMessage)
                popFragment()
            }else{
                showErrorMessage(it.responseMessage)
                popFragment()
                /*viewModel.dayDataSendList[0].day=6
                viewModel.dayDataSendList[1].day=0
                viewModel.dayDataSendList[2].day=1
                viewModel.dayDataSendList[3].day=2
                viewModel.dayDataSendList[4].day=3
                viewModel.dayDataSendList[5].day=4
                viewModel.dayDataSendList[6].day=5*/
            }
        }
        viewModel.errorLiveData.observe(this){
            showErrorMessage(it.toBlankString())
        }
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getStoredUserSurveyListLiveData.collect {
                binding!!.clMain.visibility = View.VISIBLE
                if (it.isSuccess){
                    viewModel.co2SurveyGetDataResp=it.data
                    setDataEditViewSurveyList(it.data)
                }
            }
        }
    }

    /**
     * This method contains setData ViewSurvey
     *
     */
    private fun setDataEditViewSurveyList(data: Co2SurveyGetDataResp?) {
        selectedDay(binding!!.tvMo,getString(R.string.mo))
        binding?.apply {
            telSearchSource.setText(data?.fromAddress)
            telSearchDestination.setText(data?.toAddress)
            if (binding!!.telSearchSource.text?.isEmpty() == true){
                binding!!.telSearchSource.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0)
            } else {
                binding!!.telSearchSource.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            }
            if (binding!!.telSearchDestination.text?.isEmpty() == true){
                binding!!.telSearchDestination.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0)
            } else {
                binding!!.telSearchDestination.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            }
        }
    }

    override fun initComponents() {
        viewModel.isViewSurvey= arguments?.getBoolean(PARAM_IS_VIEW_SURVEY) == true
        viewModel.surveyIdLocal=arguments?.getInt(PARAM_SURVEY_ID)?:0
      if (isFirstTimeLoad){
          setToolbar()
          setThemeForView(binding!!.btnSubmit)
          if (viewModel.isViewSurvey){
              binding!!.clMain.visibility = View.GONE
              binding!!.tvTitle.text= getString(R.string.view_co2_survey)
              setDisableISViewSurvey()
              viewModel.callGetStoredUserSurveyListApi(viewModel.surveyIdLocal)
              searchIconSetSource()
              searchIconSetDestination()
              binding!!.tlSearchSource.alpha=0.5f
              binding!!.telSearchDestination.alpha=0.5f
              binding!!.ivEye.isVisible=false
          }else{
              openCo2BottomSheet()
              binding!!.ivEye.isVisible=true
              binding!!.clMain.visibility = View.VISIBLE
              setDisableColor()
              selectedDay(binding!!.tvMo,getString(R.string.mo))
              viewModel.allDayDateSaveList.add(DayModel(getString(R.string.mo)))
              viewModel.allDayDateSaveList.add(DayModel(getString(R.string.tu)))
              viewModel.allDayDateSaveList.add(DayModel(getString(R.string.we)))
              viewModel.allDayDateSaveList.add(DayModel(getString(R.string.th)))
              viewModel.allDayDateSaveList.add(DayModel(getString(R.string.fr)))
              viewModel.allDayDateSaveList.add(DayModel(getString(R.string.sa)))
              viewModel.allDayDateSaveList.add(DayModel(getString(R.string.su)))
              //Log.e("=============","viewModel.allDayDateSaveList 1::: ${viewModel.allDayDateSaveList}")

              binding!!.telSearchSource.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_search, 0,0, 0)
              binding!!.telSearchDestination.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_search, 0,0, 0)
          }

      }
    }

    private fun openCo2BottomSheet() {
        val dialog = CO2EmissionsSurveyInstructionsBottomSheetFragment.newInstance()
        dialog.show(childFragmentManager, "")
    }

    override fun setClickListener() {
        binding?.apply {
            ivEye.clickWithDebounce {
                openCo2BottomSheet()
            }
            ivCheckboxProfile.setOnClickListener {
                viewModel.isSelectedProfileAddress = !viewModel.isSelectedProfileAddress
                checkBoxProfileAddress()
            }
            tvProfileAddress.setOnClickListener {
                viewModel.isSelectedProfileAddress = !viewModel.isSelectedProfileAddress
                checkBoxProfileAddress()
            }
            ivCheckboxInOffice.setOnClickListener {
                setCheckUncheckOffice()

            }
            tvInOffice.setOnClickListener {
                /*//viewModel.isSelectedInOffice = !viewModel.isSelectedInOffice
                selectInOfficeCheckBox()*/
                setCheckUncheckOffice()
            }
            ivCheckboxInHome.setOnClickListener {
                setCheckUncheckHome()
            }
            tvInHome.setOnClickListener {
                setCheckUncheckHome()
            }
            tvCheckboxTakeOver.setOnClickListener {
                setDataPrefillButton()
            }

            tvMo.setOnClickListener {
                selectedDay(tvMo,getString(R.string.mo))
                if (!viewModel.isViewSurvey){
                    tvCheckboxTakeOver.isVisible=false
                    tvNa.isVisible=true
                }
            }
            tvTu.setOnClickListener {
                if (!viewModel.isViewSurvey){
                    setShowHideCheckBox()
                }
                selectedDay(tvTu,getString(R.string.tu))

            }
            tvWe.setOnClickListener {
                if (!viewModel.isViewSurvey){
                    setShowHideCheckBox()
                }
                selectedDay(tvWe,getString(R.string.we))
            }
            tvTh.setOnClickListener {
                if (!viewModel.isViewSurvey){
                    setShowHideCheckBox()
                }
                selectedDay(tvTh,getString(R.string.th))

            }
            tvFr.setOnClickListener {
                if (!viewModel.isViewSurvey){
                    setShowHideCheckBox()
                }
                selectedDay(tvFr,getString(R.string.fr))

            }
            tvSa.setOnClickListener {
                if (!viewModel.isViewSurvey){
                    setShowHideCheckBox()
                }
                selectedDay(tvSa,getString(R.string.sa))
            }
            tvSu.setOnClickListener {
                if (!viewModel.isViewSurvey){
                    setShowHideCheckBox()
                }
                selectedDay(tvSu,getString(R.string.su))
            }
            ivClearSource.clickWithDebounce {
                telSearchSource.setText("")
                searchIconSetSource()
            }
            ivClearDestination.clickWithDebounce {
                telSearchDestination.setText("")
                searchIconSetDestination()
            }
            telSearchSource.setOnClickListener {
                pushFragment(SearchListFragment.newInstance(true))
            }
            telSearchDestination.setOnClickListener {
                pushFragment(SearchListFragment.newInstance(false))
            }
            tvModeTransport.clickWithDebounce {
                openModeOfTransportBottomSheet()
            }

            tvTransportDetails.clickWithDebounce {
               openTransportDetailsBottomSheet()

            }
            btnSubmit.clickWithDebounce {
               /* Log.e("", "setClickListener:${isValid()} ", )*/
                if (isValid()) {
                    if (viewModel.isSelectedInOfficeMo || viewModel.isSelectedInOfficeTu || viewModel.isSelectedInOfficeWe || viewModel.isSelectedInOfficeTh || viewModel.isSelectedInOfficeFr ||viewModel.isSelectedInOfficeSa || viewModel.isSelectedInOfficeSu
                        || viewModel.isSelectedInHomeMo|| viewModel.isSelectedInHomeTu || viewModel.isSelectedInHomeWe ||  viewModel.isSelectedInHomeTh ||  viewModel.isSelectedInHomeFr ||  viewModel.isSelectedInHomeSa ||  viewModel.isSelectedInHomeSu){
                        viewModel.fromAddressLocal = telSearchSource.text.toBlankString()
                        viewModel.toAddressLocal = telSearchDestination.text.toBlankString()
                        viewModel.callCo2SurveyApi()
                    }else{
                        showErrorMessage(getString(R.string.please_select_the_mode_of_transport_for_mo))
                    }

                }

            }
        }
    }
    /**
     * This method contains code setCheckUncheckHome
     *
     */
    private fun setCheckUncheckHome() {
        when (daySelected) {
            getString(R.string.mo) -> {
                viewModel.isSelectedInHomeMo = !viewModel.isSelectedInHomeMo
                viewModel.isSelectedInOfficeMo=false
            }
            getString(R.string.tu) -> {
                viewModel.isSelectedInHomeTu = !viewModel.isSelectedInHomeTu
                viewModel.isSelectedInOfficeTu=false
            }
            getString(R.string.we) -> {
                viewModel.isSelectedInHomeWe = !viewModel.isSelectedInHomeWe
                viewModel.isSelectedInOfficeWe=false
            }
            getString(R.string.th) -> {
                viewModel.isSelectedInHomeTh = !viewModel.isSelectedInHomeTh
                viewModel.isSelectedInOfficeTh=false
            }
            getString(R.string.fr) -> {
                viewModel.isSelectedInHomeFr = !viewModel.isSelectedInHomeFr
                viewModel.isSelectedInOfficeFr=false
            }
            getString(R.string.sa) -> {
                viewModel.isSelectedInHomeSa = !viewModel.isSelectedInHomeSa
                viewModel.isSelectedInOfficeSa=false
            }
            getString(R.string.su) -> {
                viewModel.isSelectedInHomeSu = !viewModel.isSelectedInHomeSu
                viewModel.isSelectedInOfficeSu=false
            }
            else -> {

            }
        }
        selectInHomeCheckBox()
    }
    /**
     * This method contains code selectInHomeCheckBox
     *
     */
    private fun selectInHomeCheckBox() {
        Log.e("=============", "Scrollin ${viewModel.isSelectedInOfficeMo}")
        Log.e("=============", "Scrollhome ${viewModel.isSelectedInHomeMo}")
        when (daySelected) {
            getString(R.string.mo) -> {
                if (viewModel.isSelectedInHomeMo){
                    setEnableColorWfh()
                    binding!!.tvModeTransport.text=getText(R.string.mode_of_transport)
                    binding!!.tvTransportDetails.text=getText(R.string.transport_details)
                    viewModel.allDayDateSaveList[0].modeOfTransport=null
                }else{
                    setDisableColorWfh()
                }
            }
            getString(R.string.tu) -> {
                if (viewModel.isSelectedInHomeTu){
                    setEnableColorWfh()
                    binding!!.tvModeTransport.text=getText(R.string.mode_of_transport)
                    binding!!.tvTransportDetails.text=getText(R.string.transport_details)
                    viewModel.allDayDateSaveList[1].modeOfTransport=null
                }else{
                    setDisableColorWfh()
                }
            }
            getString(R.string.we) -> {
                if (viewModel.isSelectedInHomeWe){
                    setEnableColorWfh()
                    binding!!.tvModeTransport.text=getText(R.string.mode_of_transport)
                    binding!!.tvTransportDetails.text=getText(R.string.transport_details)
                    viewModel.allDayDateSaveList[2].modeOfTransport=null
                }else{
                    setDisableColorWfh()
                }
            }
            getString(R.string.th) -> {
                if (viewModel.isSelectedInHomeTh){
                    setEnableColorWfh()
                    binding!!.tvModeTransport.text=getText(R.string.mode_of_transport)
                    binding!!.tvTransportDetails.text=getText(R.string.transport_details)
                    viewModel.allDayDateSaveList[3].modeOfTransport=null
                }else{
                    setDisableColorWfh()
                }
            }
            getString(R.string.fr) -> {
                if (viewModel.isSelectedInHomeFr){
                    setEnableColorWfh()
                    binding!!.tvModeTransport.text=getText(R.string.mode_of_transport)
                    binding!!.tvTransportDetails.text=getText(R.string.transport_details)
                    viewModel.allDayDateSaveList[4].modeOfTransport=null
                }else{
                    setDisableColorWfh()
                }
            }
            getString(R.string.sa) -> {
                if (viewModel.isSelectedInHomeSa){
                    setEnableColorWfh()
                    binding!!.tvModeTransport.text=getText(R.string.mode_of_transport)
                    binding!!.tvTransportDetails.text=getText(R.string.transport_details)
                    viewModel.allDayDateSaveList[5].modeOfTransport=null
                }else{
                    setDisableColorWfh()
                }
            }
            getString(R.string.su) -> {
                if (viewModel.isSelectedInHomeSu){
                    setEnableColorWfh()
                    binding!!.tvModeTransport.text=getText(R.string.mode_of_transport)
                    binding!!.tvTransportDetails.text=getText(R.string.transport_details)
                    viewModel.allDayDateSaveList[6].modeOfTransport=null
                }else{
                    setDisableColorWfh()
                }
            }
            else -> {

            }
        }
    }
    /**
     * This method contains code setEnableColorWfh
     *
     */
    private fun setEnableColorWfh() {
        binding?.apply {
            ivCheckboxInHome.setImageResource(R.drawable.ic_checkbox_selected)
            ivCheckboxInHome.imageTickTintCheckBox(BaseApplication.tenantSharedPreference.getTenantPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)?.brandingInfo?.primaryColor)
            tvInHome.buttonTextColorText(tenantInfoData?.brandingInfo?.primaryColor)
            tvInHome.typeface= ResourcesCompat.getFont(requireContext(), R.font.poppins_semi_bold_600)
            setDisableColor()
        }
    }
    /**
     * This method contains code setDisableColorWfh
     *
     */
    private fun setDisableColorWfh() {
        binding?.apply {
            ivCheckboxInHome.setImageResource(R.drawable.ic_checkbox_unselected)
            context?.getColorFromAttr(R.attr.colorTextView)
                ?.let { ivCheckboxInHome.setColorFilter(it) }
            tvInHome.setTextColor(requireContext().getColorFromAttr(R.attr.colorTextView))
            tvInHome.typeface= ResourcesCompat.getFont(requireContext(), R.font.poppins_medium_500)
        }
    }
    /**
     * This method contains code setCheckUncheckOffice
     *
     */
    private fun setCheckUncheckOffice() {
        when (daySelected) {
            getString(R.string.mo) -> {
                viewModel.isSelectedInOfficeMo = !viewModel.isSelectedInOfficeMo
                viewModel.isSelectedInHomeMo=false
            }
            getString(R.string.tu) -> {
                viewModel.isSelectedInOfficeTu = !viewModel.isSelectedInOfficeTu
                viewModel.isSelectedInHomeTu=false
            }
            getString(R.string.we) -> {
                viewModel.isSelectedInOfficeWe = !viewModel.isSelectedInOfficeWe
                viewModel.isSelectedInHomeWe=false
            }
            getString(R.string.th) -> {
                viewModel.isSelectedInOfficeTh = !viewModel.isSelectedInOfficeTh
                viewModel.isSelectedInHomeTh=false
            }
            getString(R.string.fr) -> {
                viewModel.isSelectedInOfficeFr = !viewModel.isSelectedInOfficeFr
                viewModel.isSelectedInHomeFr=false
            }
            getString(R.string.sa) -> {
                viewModel.isSelectedInOfficeSa = !viewModel.isSelectedInOfficeSa
                viewModel.isSelectedInHomeSa=false
            }
            getString(R.string.su) -> {
                viewModel.isSelectedInOfficeSu = !viewModel.isSelectedInOfficeSu
                viewModel.isSelectedInHomeSu=false
            }
            else -> {

            }
        }
        binding?.ivCheckboxInHome?.setImageResource(R.drawable.ic_checkbox_unselected)
        context?.getColorFromAttr(R.attr.colorTextView)
            ?.let { binding?.ivCheckboxInOffice?.setColorFilter(it) }
        selectInOfficeCheckBox()

    }

    /**
     * This method contains code setCheckUncheckOffice
     *
     */
    private fun setDataPrefillButton() {
        binding?.apply {
            Log.e("", "setDataPrefillButton:${viewModel.allDayDateSaveList} ", )
            if (daySelected==getString(R.string.mo)){
                binding!!.ivArrowTransport.isVisible=true
                binding!!.tvTransportDetails.isEnabled=true

                viewModel.allDayDateSaveList[0].modeOfTransport = modelModeOfTransport
                tvModeTransport.text=viewModel.allDayDateSaveList[0].modeOfTransport?.label
                viewModel.allDayDateSaveList[0].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                    if (itemTransportDetails.isSelected){
                        tvTransportDetails.text=itemTransportDetails.label
                        setDisableColorShowArrow()
                    }
                }
                if (viewModel.allDayDateSaveList[0].modeOfTransport?.items?.size==0){
                    binding!!.tvTransportDetails.text="-"
                    binding!!.ivArrowTransport.isVisible=false
                    binding!!.tvTransportDetails.isEnabled=false
                    binding!!.tvTransportDetails.setTextColor(requireContext().getColorFromAttr(R.attr.colorTextView))
                }
            }
            if (daySelected==getString(R.string.tu)){
                binding!!.ivArrowTransport.isVisible=true
                binding!!.tvTransportDetails.isEnabled=true

               val d=viewModel.allDayDateSaveList.find { obj -> obj.day== getString(R.string.mo)}
                viewModel.allDayDateSaveList[1].modeOfTransport = d?.modeOfTransport
                viewModel.allDayDateSaveList[1].transportDetails = d?.transportDetails
                tvModeTransport.text=d?.modeOfTransport?.label
                d?.modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                    if (itemTransportDetails.isSelected){
                        tvTransportDetails.text=itemTransportDetails.label
                        setDisableColorShowArrow()
                    }
                }
                if (d?.modeOfTransport?.items?.size==0){
                    binding!!.tvTransportDetails.text="-"
                    binding!!.ivArrowTransport.isVisible=false
                    binding!!.tvTransportDetails.isEnabled=false
                    binding!!.tvTransportDetails.setTextColor(requireContext().getColorFromAttr(R.attr.colorTextView))
                }

            }
            if (daySelected==getString(R.string.we)){
                binding!!.ivArrowTransport.isVisible=true
                binding!!.tvTransportDetails.isEnabled=true

                val d=viewModel.allDayDateSaveList.find { obj -> obj.day== getString(R.string.tu)}
                viewModel.allDayDateSaveList[2].modeOfTransport = d?.modeOfTransport
                viewModel.allDayDateSaveList[2].transportDetails = d?.transportDetails
                    tvModeTransport.text=d?.modeOfTransport?.label
                d?.modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                        if (itemTransportDetails.isSelected){
                            tvTransportDetails.text=itemTransportDetails.label
                            setDisableColorShowArrow()
                        }
                    }

                    if (d?.modeOfTransport?.items?.size==0){
                        binding!!.tvTransportDetails.text="-"
                        binding!!.ivArrowTransport.isVisible=false
                        binding!!.tvTransportDetails.isEnabled=false
                        binding!!.tvTransportDetails.setTextColor(requireContext().getColorFromAttr(R.attr.colorTextView))
                    }

            }
            if (daySelected==getString(R.string.th)){
                binding!!.ivArrowTransport.isVisible=true
                binding!!.tvTransportDetails.isEnabled=true

                val d=viewModel.allDayDateSaveList.find { obj -> obj.day== getString(R.string.we)}
                viewModel.allDayDateSaveList[3].modeOfTransport = d?.modeOfTransport
                viewModel.allDayDateSaveList[3].transportDetails = d?.transportDetails
                tvModeTransport.text=d?.modeOfTransport?.label
                d?.modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                    if (itemTransportDetails.isSelected){
                        tvTransportDetails.text=itemTransportDetails.label
                        setDisableColorShowArrow()
                    }
                }
                if (d?.modeOfTransport?.items?.size==0){
                    binding!!.tvTransportDetails.text="-"
                    binding!!.ivArrowTransport.isVisible=false
                    binding!!.tvTransportDetails.isEnabled=false
                    binding!!.tvTransportDetails.setTextColor(requireContext().getColorFromAttr(R.attr.colorTextView))
                }
            }
            if (daySelected==getString(R.string.fr)){
                binding!!.ivArrowTransport.isVisible=true
                binding!!.tvTransportDetails.isEnabled=true

                val d=viewModel.allDayDateSaveList.find { obj -> obj.day== getString(R.string.th)}
                viewModel.allDayDateSaveList[4].modeOfTransport = d?.modeOfTransport
                viewModel.allDayDateSaveList[4].transportDetails = d?.transportDetails
                tvModeTransport.text=d?.modeOfTransport?.label
                d?.modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                    if (itemTransportDetails.isSelected){
                        tvTransportDetails.text=itemTransportDetails.label
                        setDisableColorShowArrow()
                    }
                }
                if (d?.modeOfTransport?.items?.size==0){
                    binding!!.tvTransportDetails.text="-"
                    binding!!.ivArrowTransport.isVisible=false
                    binding!!.tvTransportDetails.isEnabled=false
                    binding!!.tvTransportDetails.setTextColor(requireContext().getColorFromAttr(R.attr.colorTextView))
                }
            }
            if (daySelected==getString(R.string.sa)){
                binding!!.ivArrowTransport.isVisible=true
                binding!!.tvTransportDetails.isEnabled=true

                val d=viewModel.allDayDateSaveList.find { obj -> obj.day== getString(R.string.fr)}
                viewModel.allDayDateSaveList[5].modeOfTransport = d?.modeOfTransport
                viewModel.allDayDateSaveList[5].transportDetails = d?.transportDetails

                tvModeTransport.text=d?.modeOfTransport?.label
                d?.modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                    if (itemTransportDetails.isSelected){
                        tvTransportDetails.text=itemTransportDetails.label
                        setDisableColorShowArrow()

                    }
                }
                if (d?.modeOfTransport?.items?.size==0){
                    binding!!.tvTransportDetails.text="-"
                    binding!!.ivArrowTransport.isVisible=false
                    binding!!.tvTransportDetails.isEnabled=false
                    binding!!.tvTransportDetails.setTextColor(requireContext().getColorFromAttr(R.attr.colorTextView))
                }
            }
            if (daySelected==getString(R.string.su)){
                binding!!.ivArrowTransport.isVisible=true
                binding!!.tvTransportDetails.isEnabled=true

                val d=viewModel.allDayDateSaveList.find { obj -> obj.day== getString(R.string.sa)}
                viewModel.allDayDateSaveList[6].modeOfTransport = d?.modeOfTransport
                viewModel.allDayDateSaveList[6].transportDetails = d?.transportDetails

                tvModeTransport.text=d?.modeOfTransport?.label
                d?.modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                    if (itemTransportDetails.isSelected){
                        tvTransportDetails.text=itemTransportDetails.label
                        setDisableColorShowArrow()
                    }
                }
                if (d?.modeOfTransport?.items?.size==0){
                    binding!!.tvTransportDetails.text="-"
                    binding!!.ivArrowTransport.isVisible=false
                    binding!!.tvTransportDetails.isEnabled=false
                    binding!!.tvTransportDetails.setTextColor(requireContext().getColorFromAttr(R.attr.colorTextView))
                }
            }
            tvCheckboxTakeOver.isEnabled=false
            tvCheckboxTakeOver.backgroundTintList=(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorSearchHint)))
        }
    }
    /**
     * This method contains code openBottomSheetWithSelected and date set
     *
     */
    private fun openBottomSheetWithSelected(itemTransportDetailsList: ArrayList<ItemTransportDetails>) {

        binding?.apply {
            val dialog = TransportDetailBottomSheetFragment.newInstance(itemTransportDetailsList)

            dialog.sendClickListener = {
                when (daySelected) {
                    getString(R.string.mo) -> {
                        modelTransportDetails = it
                        viewModel.allDayDateSaveList[0].transportDetails = it
                        tvTransportDetails.text = modelTransportDetails?.label
                        viewModel.allDayDateSaveList[0].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                            if (it.label == itemTransportDetails.label) {
                                viewModel.allDayDateSaveList[0].modeOfTransport?.items?.set(index, it)
                            }
                        }
                        modelModeOfTransport =  viewModel.allDayDateSaveList[0].modeOfTransport
                    }

                    getString(R.string.tu) -> {
                        modelTransportDetails = it
                        viewModel.allDayDateSaveList[1].transportDetails = modelTransportDetails
                        tvTransportDetails.text = modelTransportDetails?.label
                        viewModel.allDayDateSaveList[1].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                            if (it.label == itemTransportDetails.label) {
                                viewModel.allDayDateSaveList[1].modeOfTransport?.items?.set(index, it)
                            }
                        }
                        modelModeOfTransport =  viewModel.allDayDateSaveList[1].modeOfTransport
                    }

                    getString(R.string.we) -> {
                        modelTransportDetails = it
                        viewModel.allDayDateSaveList[2].transportDetails = modelTransportDetails
                        tvTransportDetails.text = modelTransportDetails?.label
                        viewModel.allDayDateSaveList[2].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                            if (it.label == itemTransportDetails.label) {
                                viewModel.allDayDateSaveList[2].modeOfTransport?.items?.set(index, it)
                            }
                        }
                        modelModeOfTransport =  viewModel.allDayDateSaveList[2].modeOfTransport
                    }

                    getString(R.string.th) -> {
                        modelTransportDetails = it
                        viewModel.allDayDateSaveList[3].transportDetails = modelTransportDetails
                        tvTransportDetails.text = modelTransportDetails?.label
                        viewModel.allDayDateSaveList[3].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                            if (it.label == itemTransportDetails.label) {
                                viewModel.allDayDateSaveList[3].modeOfTransport?.items?.set(index, it)
                            }
                        }
                        modelModeOfTransport =  viewModel.allDayDateSaveList[3].modeOfTransport
                    }

                    getString(R.string.fr) -> {
                        modelTransportDetails = it
                        viewModel.allDayDateSaveList[4].transportDetails = modelTransportDetails
                        tvTransportDetails.text = modelTransportDetails?.label
                        viewModel.allDayDateSaveList[4].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                            if (it.label == itemTransportDetails.label) {
                                viewModel.allDayDateSaveList[4].modeOfTransport?.items?.set(index, it)
                            }
                        }
                        modelModeOfTransport =  viewModel.allDayDateSaveList[4].modeOfTransport
                    }

                    getString(R.string.sa) -> {
                        modelTransportDetails = it
                        viewModel.allDayDateSaveList[5].transportDetails = modelTransportDetails
                        tvTransportDetails.text = modelTransportDetails?.label
                        viewModel.allDayDateSaveList[5].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                            if (it.label == itemTransportDetails.label) {
                                viewModel.allDayDateSaveList[5].modeOfTransport?.items?.set(index, it)
                            }
                        }
                        modelModeOfTransport =  viewModel.allDayDateSaveList[5].modeOfTransport
                    }

                    getString(R.string.su) -> {
                        modelTransportDetails = it
                        viewModel.allDayDateSaveList[6].transportDetails = modelTransportDetails
                        tvTransportDetails.text = modelTransportDetails?.label
                        viewModel.allDayDateSaveList[6].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                            if (it.label == itemTransportDetails.label) {
                                viewModel.allDayDateSaveList[6].modeOfTransport?.items?.set(index, it)
                            }
                        }
                        modelModeOfTransport =  viewModel.allDayDateSaveList[6].modeOfTransport
                    }

                    else -> {

                    }
                }

                dialog.dismiss()
            }
            dialog.show(childFragmentManager, "")

        }
    }
    /**
     * This method contains code openBottomSheetWithSelected and data set
     *
     */
    private fun openTransportDetailsBottomSheet() {
        binding?.apply {
            when (daySelected) {
                getString(R.string.mo) -> {
                    viewModel.allDayDateSaveList[0].modeOfTransport?.items?.let { openBottomSheetWithSelected(it) }
                }
                getString(R.string.tu) -> {
                    viewModel.allDayDateSaveList[1].modeOfTransport?.items?.let { openBottomSheetWithSelected(it) }
                }
                getString(R.string.we) -> {
                    viewModel.allDayDateSaveList[2].modeOfTransport?.items?.let { openBottomSheetWithSelected(it) }
                }
                getString(R.string.th) -> {
                    viewModel.allDayDateSaveList[3].modeOfTransport?.items?.let { openBottomSheetWithSelected(it) }
                }
                getString(R.string.fr) -> {
                    viewModel.allDayDateSaveList[4].modeOfTransport?.items?.let { openBottomSheetWithSelected(it) }
                }
                getString(R.string.sa) -> {
                    viewModel.allDayDateSaveList[5].modeOfTransport?.items?.let { openBottomSheetWithSelected(it) }
                }
                getString(R.string.su) -> {
                    viewModel.allDayDateSaveList[6].modeOfTransport?.items?.let { openBottomSheetWithSelected(it) }
                }
                else -> {

                }
            }
        }
    }
    /**
     * This method contains code openModeOfTransportBottomSheet and data set
     *
     */
    private fun openModeOfTransportBottomSheet() {
        binding?.apply {
            when (daySelected) {
                viewModel.allDayDateSaveList[0].day -> {
                    modelModeOfTransport= viewModel.allDayDateSaveList[0].modeOfTransport
                }
                viewModel.allDayDateSaveList[1].day -> {
                    modelModeOfTransport= viewModel.allDayDateSaveList[1].modeOfTransport
                }
                viewModel.allDayDateSaveList[2].day -> {
                    modelModeOfTransport= viewModel.allDayDateSaveList[2].modeOfTransport

                }
                viewModel.allDayDateSaveList[3].day -> {
                    modelModeOfTransport= viewModel.allDayDateSaveList[3].modeOfTransport
                }
                viewModel.allDayDateSaveList[4].day -> {
                    modelModeOfTransport= viewModel.allDayDateSaveList[4].modeOfTransport
                }
                viewModel.allDayDateSaveList[5].day -> {
                    modelModeOfTransport= viewModel.allDayDateSaveList[5].modeOfTransport
                }
                viewModel.allDayDateSaveList[6].day -> {
                    modelModeOfTransport= viewModel.allDayDateSaveList[6].modeOfTransport                    }
                else -> {
                }
            }
            val dialog = Co2ModeOfTransportBottomSheetFragment.newInstance(co2ModeOfTransportViewModel,modelModeOfTransport)
            dialog.sendClickListener = {
                tvTransportDetails.setTextColor(requireContext().getColorFromAttr(R.attr.colorTextView))
                if (it.items.size!=0){
                    tvTransportDetails.isEnabled=true
                    tvTransportDetails.text=getString(R.string.transport_details)
                    ivArrowTransport.isVisible=true
                }else{
                    tvTransportDetails.text="-"
                    tvTransportDetails.isEnabled=false
                    ivArrowTransport.isVisible=false
                }

                when (daySelected) {
                    viewModel.allDayDateSaveList[0].day -> {
                        modelModeOfTransport = it
                        viewModel.allDayDateSaveList[0].modeOfTransport = modelModeOfTransport
                        tvModeTransport.text=modelModeOfTransport?.label
                        viewModel.allDayDateSaveList[0].transportDetails?.label=it.label
                    }
                    viewModel.allDayDateSaveList[1].day -> {
                        modelModeOfTransport = it
                        viewModel.allDayDateSaveList[1].modeOfTransport = modelModeOfTransport
                        tvModeTransport.text=modelModeOfTransport?.label
                        viewModel.allDayDateSaveList[1].transportDetails?.label=it.label
                    }
                    viewModel.allDayDateSaveList[2].day -> {
                        modelModeOfTransport = it
                        viewModel.allDayDateSaveList[2].modeOfTransport = modelModeOfTransport
                        tvModeTransport.text=modelModeOfTransport?.label
                        viewModel.allDayDateSaveList[2].transportDetails?.label=it.label
                    }
                    viewModel.allDayDateSaveList[3].day -> {
                        modelModeOfTransport = it
                        viewModel.allDayDateSaveList[3].modeOfTransport = modelModeOfTransport
                        tvModeTransport.text=modelModeOfTransport?.label
                    }
                    viewModel.allDayDateSaveList[4].day -> {
                        modelModeOfTransport = it
                        viewModel.allDayDateSaveList[4].modeOfTransport = modelModeOfTransport
                        tvModeTransport.text=modelModeOfTransport?.label
                    }
                    viewModel.allDayDateSaveList[5].day -> {
                        modelModeOfTransport = it
                        viewModel.allDayDateSaveList[5].modeOfTransport = modelModeOfTransport
                        tvModeTransport.text=modelModeOfTransport?.label
                    }
                    viewModel.allDayDateSaveList[6].day -> {
                        modelModeOfTransport = it
                        viewModel.allDayDateSaveList[6].modeOfTransport = modelModeOfTransport
                        tvModeTransport.text=modelModeOfTransport?.label
                    }
                    else -> {

                    }
                }
                dialog.dismiss()

            }
            dialog.show(childFragmentManager, "")
        }
    }

    private fun setToolbar() {
        binding!!.toolbar.let {
            overrideToolbar(it, ToolbarConfig().apply {
                showBackButton = true
                showWhiteBg = true

                //showLogoIcon = true
                showViewLine = true
            })

            it.ivBack.clickWithDebounce {
                popFragment()
            }
        }
    }
    /**
     * This method contains code click ok profile check box set ProfileAddress
     *
     */
    private fun checkBoxProfileAddress(){
        val userData = BaseApplication.sharedPreference.getPrefModel(EasyPref.USER_DATA, User::class.java)
        if (userData?.fromAddress?.isEmpty() == true && userData.toAddress.isEmpty()){
            showErrorMessage(getString(R.string.no_address_in_a_profile_update_your_profile_or_uncheck_the_checkbox))
        }else{
            if (viewModel.isSelectedProfileAddress) {
                binding?.ivCheckboxProfile?.setImageResource(R.drawable.ic_checkbox_selected)
                binding?.ivCheckboxProfile?.imageTickTintCheckBox(BaseApplication.tenantSharedPreference.getTenantPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)?.brandingInfo?.primaryColor)
                binding!!.tvProfileAddress.buttonTextColorText(tenantInfoData?.brandingInfo?.primaryColor)
                binding!!.tvProfileAddress.typeface= ResourcesCompat.getFont(requireContext(), R.font.poppins_medium_500)
                binding!!.telSearchSource.setText(userData?.fromAddress)
                binding!!.telSearchDestination.setText(userData?.toAddress)
                viewModel.fromLatitudeLocal= userData?.fromLatitude.toBlankString()
                viewModel.fromLongitudeLocal= userData?.fromLongitude.toBlankString()

                viewModel.toLatitudeLocal= userData?.toLatitude.toBlankString()
                viewModel.toLongitudeLocal= userData?.toLongitude.toBlankString()
            } else {
                binding?.ivCheckboxProfile?.setImageResource(R.drawable.ic_checkbox_unselected)
                context?.getColorFromAttr(R.attr.colorTextView)
                    ?.let { binding?.ivCheckboxProfile?.setColorFilter(it) }
                context?.getColorFromAttr(R.attr.colorCheckBoxProfileAddress)
                    ?.let { binding!!.tvProfileAddress.setTextColor(it) }
                binding!!.tvProfileAddress.typeface= ResourcesCompat.getFont(requireContext(), R.font.poppins_light_300)
                binding!!.telSearchSource.setText("")
                binding!!.telSearchDestination.setText("")
                viewModel.fromLatitudeLocal= ""
                viewModel.fromLongitudeLocal= ""

                viewModel.toLatitudeLocal=""
                viewModel.toLongitudeLocal=""
            }
        }

        searchIconSetSource()
        searchIconSetDestination()
    }
    /**
     * This method contains code setDisableColor
     *
     */
    private fun setDisableColor(){
        binding?.apply {
            tvModeTransport.setTextColor(requireContext().getColorFromAttr(R.attr.colorSearchHint))
            tvTransportDetails.setTextColor(requireContext().getColorFromAttr(R.attr.colorSearchHint))
            tvTake.setTextColor(requireContext().getColorFromAttr(R.attr.colorSearchHint))
            tvNa.setTextColor(requireContext().getColorFromAttr(R.attr.colorSearchHint))

            tvModeTransport.typeface=ResourcesCompat.getFont(requireContext(),R.font.poppins_regular_400)
            tvTransportDetails.typeface=ResourcesCompat.getFont(requireContext(),R.font.poppins_regular_400)
            tvTake.typeface=ResourcesCompat.getFont(requireContext(),R.font.poppins_regular_400)
            tvNa.typeface=ResourcesCompat.getFont(requireContext(),R.font.poppins_regular_400)

            ivArrowMode.setColorFilter(requireContext().getColorFromAttr(R.attr.colorSearchHint))
            ivArrowTransport.setColorFilter(requireContext().getColorFromAttr(R.attr.colorSearchHint))

            tvModeTransport.isEnabled=false
            tvTransportDetails.isEnabled=false
            tvTake.isEnabled=false
            tvCheckboxTakeOver.isEnabled=false

            binding?.ivCheckboxInOffice?.setImageResource(R.drawable.ic_checkbox_unselected)
            context?.getColorFromAttr(R.attr.colorTextView)
                ?.let { binding?.ivCheckboxInOffice?.setColorFilter(it) }

           // tvInOffice.buttonTextColorNotification(tenantInfoData?.brandingInfo?.primaryColor)
            tvCheckboxTakeOver.backgroundTintList=(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorSearchHint)))
            tvInOffice.setTextColor(requireContext().getColorFromAttr(R.attr.colorTextView))
            tvInOffice.typeface= ResourcesCompat.getFont(requireContext(), R.font.poppins_medium_500)
        }
    }
    /**
     * This method contains code selectedDay
     *
     */
    private fun selectedDay(textview:AppCompatTextView,day:String){
        binding?.apply {
            tvMo.isSelected=false
            tvTu.isSelected=false
            tvWe.isSelected=false
            tvTh.isSelected=false
            tvFr.isSelected=false
            tvSa.isSelected=false
            tvSu.isSelected=false

            tvMo.background = null
            tvWe.background = null
            tvTu.background = null
            tvTh.background = null
            tvFr.background = null
            tvSa.background = null
            tvSu.background = null

            tvMo.setTextColor(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorUnSelected)))
            tvTu.setTextColor(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorUnSelected)))
            tvWe.setTextColor(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorUnSelected)))
            tvTh.setTextColor(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorUnSelected)))
            tvFr.setTextColor(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorUnSelected)))
            tvSa.setTextColor(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorUnSelected)))
            tvSu.setTextColor(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorUnSelected)))
        }

        textview.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_radius_10)
        textview.backgroundColorTint(BaseApplication.tenantSharedPreference.getTenantPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)?.brandingInfo?.primaryColor)
        textview.setTextColor((ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorSelected))))

        daySelected=day

       if (viewModel.isViewSurvey){
           setShowHideCheckBoxIsViewSurvey()
           setDayInViewTime(day)
       }else{
          setDayCreateSurvey(day)
       }
    }
    /**
     * This method contains code setDayInViewTime
     *
     */
    private fun setDayInViewTime(day: String) {
       binding?.apply {

//               when (day) {
//                   getString(R.string.mo) -> {
//                       viewModel.co2SurveyGetDataResp?.dayData?.forEachIndexed { index, dayDataReq ->
//                           if (index == viewModel.dayOne && dayDataReq.transportMode.isEmpty()) {
//                               tvModeTransport.text = getString(R.string.mode_of_transport)
//                           } else if (index == viewModel.dayOne ) {
//                               val transportMode= dayDataReq.transportMode.replace("_", " ")
//                               tvModeTransport.text = transportMode.capitalized()
//                           }
//                           val transportDetails=dayDataReq.transportType.replace("_", " ")
//                           if (index == viewModel.dayOne) {
//                               if (dayDataReq.isWfh.equals("yes", true)) {
//                                   inWorkFromHomeCheckBoxSelected()
//                                   transportDetailsTextDisplay(transportDetails)
//                               } else {
//                                   inWorkFromHomeCheckBoxUNSelected()
//                                   transportDetailsTextDisplay(transportDetails)
//                               }
//                               if (dayDataReq.inOffice.equals("yes", true)) {
//                                   inOfficeCheckBoxSelected()
//                                   showHideTransportDetailsArrow(transportDetails)
//                               } else {
//                                   inOfficeCheckBoxUNSelected()
//                                   transportDetailsTextDisplay(transportDetails)
//                               }
//                           }
//                       }
//
//                   }
//
//                   getString(R.string.tu) -> {
//                       viewModel.co2SurveyGetDataResp?.dayData?.forEachIndexed { index, dayDataReq ->
//                           if (index == viewModel.dayTwo && dayDataReq.transportMode.isEmpty()) {
//                               tvModeTransport.text = getString(R.string.mode_of_transport)
//                           } else if (index == viewModel.dayTwo ) {
//                               val transportMode= dayDataReq.transportMode.replace("_", " ")
//                               tvModeTransport.text = transportMode.capitalized()
//                           }
//                           val transportDetails=dayDataReq.transportType.replace("_", " ")
//                           if (index == viewModel.dayTwo) {
//                               if (dayDataReq.isWfh.equals("yes", true)) {
//                                   inWorkFromHomeCheckBoxSelected()
//                                   transportDetailsTextDisplay(transportDetails)
//                               } else {
//                                   inWorkFromHomeCheckBoxUNSelected()
//                                   transportDetailsTextDisplay(transportDetails)
//                               }
//                               if (dayDataReq.inOffice.equals("yes", true)) {
//                                   inOfficeCheckBoxSelected()
//                                   showHideTransportDetailsArrow(transportDetails)
//                               } else {
//                                   inOfficeCheckBoxUNSelected()
//                                   transportDetailsTextDisplay(transportDetails)
//                               }
//                           }
//                       }
//                   }
//
//                   getString(R.string.we) -> {
//                       viewModel.co2SurveyGetDataResp?.dayData?.forEachIndexed { index, dayDataReq ->
//                           if (index == viewModel.dayThree && dayDataReq.transportMode.isEmpty()) {
//                               tvModeTransport.text = getString(R.string.mode_of_transport)
//                           } else if (index == viewModel.dayThree) {
//                               val transportMode= dayDataReq.transportMode.replace("_", " ")
//                               tvModeTransport.text = transportMode.capitalized()
//                           }
//                           val transportDetails=dayDataReq.transportType.replace("_", " ")
//                           if (index == viewModel.dayThree) {
//                               if (dayDataReq.isWfh.equals("yes", true)) {
//                                   inWorkFromHomeCheckBoxSelected()
//                                   transportDetailsTextDisplay(transportDetails)
//                               } else {
//                                   inWorkFromHomeCheckBoxUNSelected()
//                                   transportDetailsTextDisplay(transportDetails)
//                               }
//                               if (dayDataReq.inOffice.equals("yes", true)) {
//                                   inOfficeCheckBoxSelected()
//                                   showHideTransportDetailsArrow(transportDetails)
//                               } else {
//                                   inOfficeCheckBoxUNSelected()
//                                   transportDetailsTextDisplay(transportDetails)
//                               }
//                           }
//                       }
//                   }
//
//                   getString(R.string.th) -> {
//                       viewModel.co2SurveyGetDataResp?.dayData?.forEachIndexed { index, dayDataReq ->
//                           if (index == viewModel.dayFour && dayDataReq.transportMode.isEmpty()) {
//                               tvModeTransport.text = getString(R.string.mode_of_transport)
//                           } else if (index == viewModel.dayFour) {
//                              val transportMode= dayDataReq.transportMode.replace("_", " ")
//                               tvModeTransport.text = transportMode.capitalized()
//                           }
//                           val transportDetails=dayDataReq.transportType.replace("_", " ")
//                           if (index == viewModel.dayFour) {
//                               if (dayDataReq.isWfh.equals("yes", true)) {
//                                   inWorkFromHomeCheckBoxSelected()
//                                   transportDetailsTextDisplay(transportDetails)
//                               } else {
//                                   inWorkFromHomeCheckBoxUNSelected()
//                                   transportDetailsTextDisplay(transportDetails)
//                               }
//                               if (dayDataReq.inOffice.equals("yes", true)) {
//                                   inOfficeCheckBoxSelected()
//                                   showHideTransportDetailsArrow(transportDetails)
//                               } else {
//                                   inOfficeCheckBoxUNSelected()
//                                   transportDetailsTextDisplay(transportDetails)
//                               }
//                           }
//                       }
//                   }
//
//                   getString(R.string.fr) -> {
//                       viewModel.co2SurveyGetDataResp?.dayData?.forEachIndexed { index, dayDataReq ->
//                           if (index == viewModel.dayFive && dayDataReq.transportMode.isEmpty()) {
//                               tvModeTransport.text = getString(R.string.mode_of_transport)
//                           } else if (index == viewModel.dayFive) {
//                               val transportMode= dayDataReq.transportMode.replace("_", " ")
//                               tvModeTransport.text = transportMode.capitalized()
//                           }
//                           val transportDetails=dayDataReq.transportType.replace("_", " ")
//                           if (index == viewModel.dayFive) {
//                               if (dayDataReq.isWfh.equals("yes", true)) {
//                                   inWorkFromHomeCheckBoxSelected()
//                                   transportDetailsTextDisplay(transportDetails)
//                               } else {
//                                   inWorkFromHomeCheckBoxUNSelected()
//                                   transportDetailsTextDisplay(transportDetails)
//                               }
//                               if (dayDataReq.inOffice.equals("yes", true)) {
//                                   inOfficeCheckBoxSelected()
//                                   showHideTransportDetailsArrow(transportDetails)
//                               } else {
//                                   inOfficeCheckBoxUNSelected()
//                                   transportDetailsTextDisplay(transportDetails)
//                               }
//                           }
//                       }
//                   }
//
//                   getString(R.string.sa) -> {
//                       viewModel.co2SurveyGetDataResp?.dayData?.forEachIndexed { index, dayDataReq ->
//                           if (index == viewModel.daySix && dayDataReq.transportMode.isEmpty()) {
//                               tvModeTransport.text = getString(R.string.mode_of_transport)
//                           } else if (index == viewModel.daySix) {
//                               val transportMode= dayDataReq.transportMode.replace("_", " ")
//                               tvModeTransport.text = transportMode.capitalized()
//                           }
//                           val transportDetails=dayDataReq.transportType.replace("_", " ")
//                           if (index == viewModel.daySix) {
//                               if (dayDataReq.isWfh.equals("yes", true)) {
//                                   inWorkFromHomeCheckBoxSelected()
//                                   transportDetailsTextDisplay(transportDetails)
//                               } else {
//                                   inWorkFromHomeCheckBoxUNSelected()
//                                   transportDetailsTextDisplay(transportDetails)
//                               }
//                               if (dayDataReq.inOffice.equals("yes", true)) {
//                                   inOfficeCheckBoxSelected()
//                                   showHideTransportDetailsArrow(transportDetails)
//                               } else {
//                                   inOfficeCheckBoxUNSelected()
//                                   transportDetailsTextDisplay(transportDetails)
//                               }
//                           }
//                       }
//                   }
//
//                   getString(R.string.su) -> {
//                       viewModel.co2SurveyGetDataResp?.dayData?.forEachIndexed { index, dayDataReq ->
//                           if (index == viewModel.dayZero && dayDataReq.transportMode.isEmpty()) {
//                               tvModeTransport.text = getString(R.string.mode_of_transport)
//                           } else if (index == viewModel.dayZero) {
//                               val transportMode= dayDataReq.transportMode.replace("_", " ")
//                               tvModeTransport.text = transportMode.capitalized()
//                           }
//                           val transportDetails=dayDataReq.transportType.replace("_", " ")
//                           if (index == viewModel.dayZero) {
//                               if (dayDataReq.isWfh.equals("yes", true)) {
//                                   inWorkFromHomeCheckBoxSelected()
//                                   transportDetailsTextDisplay(transportDetails)
//                               } else {
//                                   inWorkFromHomeCheckBoxUNSelected()
//                                   transportDetailsTextDisplay(transportDetails)
//                               }
//                               if (dayDataReq.inOffice.equals("yes", true)) {
//                                   inOfficeCheckBoxSelected()
//                                   showHideTransportDetailsArrow(transportDetails)
//                               } else {
//                                   inOfficeCheckBoxUNSelected()
//                                   transportDetailsTextDisplay(transportDetails)
//                               }
//                           }
//                       }
//                   }
//
//                   else -> {
//
//                   }
//           }
       }
    }

    private fun showHideTransportDetailsArrow(transportType:String) {
        binding?.apply {
            if (transportType.isEmpty()) {
                tvTransportDetails.text ="-"
                // getString(R.string.transport_details)
                ivArrowTransport.isVisible=false
            } else {
                //val transportDetails=transportType.replace("_", " ")
                ivArrowTransport.isVisible=true
                tvTransportDetails.text = transportType.capitalized()
            }
        }
    }

    private fun transportDetailsTextDisplay(transportType:String) {
        binding!!.ivArrowTransport.isVisible=true
        if (transportType.isEmpty()) {
            binding!!.tvTransportDetails.text = getString(R.string.transport_details)
        } else {
            //val transportDetails=transportType.replace("_", " ")
            binding!!.tvTransportDetails.text = transportType.capitalized()
        }
    }
    /**
     * This method contains code setDayCreateSurvey
     *
     */
    private fun setDayCreateSurvey(day: String) {
        Log.e("", "list==================: ${viewModel.allDayDateSaveList}", )
        if (viewModel.allDayDateSaveList.size >0) {
            when (day) {
                getString(R.string.mo) -> {
                    Log.e("", "setDayCreateSurvey0: ${viewModel.allDayDateSaveList[0]}", )
                    if (viewModel.allDayDateSaveList[0].modeOfTransport?.label?.isEmpty()==true || viewModel.allDayDateSaveList[0].modeOfTransport?.label==null){
                        binding!!.tvModeTransport.text=getString(R.string.mode_of_transport)
                    }else{
                        binding!!.tvModeTransport.text = viewModel.allDayDateSaveList[0].modeOfTransport?.label
                        setDisableColorShowArrow()
                    }

                    selectInOfficeCheckBox()
                    selectInHomeCheckBox()
                    if (viewModel.allDayDateSaveList[0].modeOfTransport?.items!=null){
                        if (viewModel.allDayDateSaveList[0].modeOfTransport?.items?.size==0){
                            binding!!.tvTransportDetails.text="-"
                            setDisableColorHideArrow()
                        }else if (viewModel.allDayDateSaveList[0].transportDetails?.label?.isEmpty()==true || viewModel.allDayDateSaveList[0].transportDetails?.label==null){
                            if (viewModel.allDayDateSaveList[0].modeOfTransport?.items==null){
                                binding!!.tvTransportDetails.text=getString(R.string.transport_details)
                                setDisableColorHideArrow()
                            }else{
                                viewModel.allDayDateSaveList[0].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                                    if (itemTransportDetails.isSelected){
                                        binding!!.tvTransportDetails.text=itemTransportDetails.label
                                        return
                                    }
                                }
                                binding!!.tvTransportDetails.text=getString(R.string.transport_details)
                            }
                        }else{

                            //

                            viewModel.allDayDateSaveList[0].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                                if (itemTransportDetails.isSelected){
                                    binding!!.tvTransportDetails.text=itemTransportDetails.label
                                    return
                                }
                            }
                            if(viewModel.allDayDateSaveList[0].transportDetails?.label==null)
                            binding!!.tvTransportDetails.text = "-"
                            else binding!!.tvTransportDetails.text=getString(R.string.transport_details)
                        }
                    }else{
                        binding!!.tvTransportDetails.text=getString(R.string.transport_details)
                    }

                }

                getString(R.string.tu) -> {
                    Log.e("", "setDayCreateSurvey1: ${viewModel.allDayDateSaveList[1]}", )
                    if (viewModel.allDayDateSaveList[1].modeOfTransport?.label?.isEmpty()==true || viewModel.allDayDateSaveList[1].modeOfTransport?.label==null){
                        binding!!.tvModeTransport.text=getString(R.string.mode_of_transport)
                    }else{
                        binding!!.tvModeTransport.text = viewModel.allDayDateSaveList[1].modeOfTransport?.label
                        setDisableColorShowArrow()
                    }

                    selectInOfficeCheckBox()
                    selectInHomeCheckBox()
                    if (viewModel.allDayDateSaveList[1].modeOfTransport?.items!=null){
                        if (viewModel.allDayDateSaveList[1].modeOfTransport?.items?.size==0){
                            binding!!.tvTransportDetails.text="-"
                            setDisableColorHideArrow()
                        }
                        else if (viewModel.allDayDateSaveList[1].transportDetails?.label?.isEmpty()==true || viewModel.allDayDateSaveList[1].transportDetails?.label==null){
                            if (viewModel.allDayDateSaveList[1].modeOfTransport?.items==null){
                                binding!!.tvTransportDetails.text=getString(R.string.transport_details)
                                setDisableColorHideArrow()
                            }else{
                                viewModel.allDayDateSaveList[1].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                                    if (itemTransportDetails.isSelected){
                                        binding!!.tvTransportDetails.text=itemTransportDetails.label
                                        return
                                    }
                                }
                                binding!!.tvTransportDetails.text=getString(R.string.transport_details)
                            }
                        }else{
                            //binding!!.tvTransportDetails.text = viewModel.allDayDateSaveList[1].transportDetails?.label ?: "-"
                            viewModel.allDayDateSaveList[1].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                                if (itemTransportDetails.isSelected){
                                    binding!!.tvTransportDetails.text=itemTransportDetails.label
                                    return
                                }
                            }
                            if(viewModel.allDayDateSaveList[1].transportDetails?.label==null)
                                binding!!.tvTransportDetails.text = "-"
                            else binding!!.tvTransportDetails.text=getString(R.string.transport_details)

                        }
                    }else{
                        binding!!.tvTransportDetails.text=getString(R.string.transport_details)
                    }

                }

                getString(R.string.we) -> {
                    Log.e("", "setDayCreateSurvey2: ${viewModel.allDayDateSaveList[2]}", )
                    if (viewModel.allDayDateSaveList[2].modeOfTransport?.label?.isEmpty()==true || viewModel.allDayDateSaveList[2].modeOfTransport?.label==null){
                        binding!!.tvModeTransport.text=getString(R.string.mode_of_transport)
                    }else{
                        binding!!.tvModeTransport.text = viewModel.allDayDateSaveList[2].modeOfTransport?.label
                        setDisableColorShowArrow()
                    }

                    selectInOfficeCheckBox()
                    selectInHomeCheckBox()
                    if (viewModel.allDayDateSaveList[2].modeOfTransport?.items!=null){
                    if (viewModel.allDayDateSaveList[2].modeOfTransport?.items?.size==0){
                        binding!!.tvTransportDetails.text="-"
                        setDisableColorHideArrow()
                    }else if (viewModel.allDayDateSaveList[2].transportDetails?.label?.isEmpty()==true || viewModel.allDayDateSaveList[2].transportDetails?.label==null){
                        if (viewModel.allDayDateSaveList[2].modeOfTransport?.items==null){
                            binding!!.tvTransportDetails.text=getString(R.string.transport_details)
                            setDisableColorHideArrow()
                        }else{
                            viewModel.allDayDateSaveList[2].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                                if (itemTransportDetails.isSelected){
                                    binding!!.tvTransportDetails.text=itemTransportDetails.label
                                    return
                                }
                                binding!!.tvTransportDetails.text=getString(R.string.transport_details)

                            }
                        }
                    }else{
                        viewModel.allDayDateSaveList[2].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                            if (itemTransportDetails.isSelected){
                                binding!!.tvTransportDetails.text=itemTransportDetails.label
                                return
                            }
                        }
                        if(viewModel.allDayDateSaveList[2].transportDetails?.label==null)
                            binding!!.tvTransportDetails.text = "-"
                        else binding!!.tvTransportDetails.text=getString(R.string.transport_details)
                    }
                    }else{
                        binding!!.tvTransportDetails.text=getString(R.string.transport_details)
                    }
                }

                getString(R.string.th) -> {
                    //binding!!.tvTransportDetails.text = viewModel.allDayDateSaveList[3].transportDetails?.label ?: "-"
                    if (viewModel.allDayDateSaveList[3].modeOfTransport?.label?.isEmpty()==true || viewModel.allDayDateSaveList[3].modeOfTransport?.label==null){
                        binding!!.tvModeTransport.text=getString(R.string.mode_of_transport)
                    }else{
                        binding!!.tvModeTransport.text = viewModel.allDayDateSaveList[3].modeOfTransport?.label
                        setDisableColorShowArrow()
                    }

                    selectInOfficeCheckBox()
                    selectInHomeCheckBox()
                    if (viewModel.allDayDateSaveList[3].modeOfTransport?.items!=null) {
                        if (viewModel.allDayDateSaveList[3].modeOfTransport?.items?.size == 0) {
                            binding!!.tvTransportDetails.text = "-"
                            setDisableColorHideArrow()
                        } else if (viewModel.allDayDateSaveList[3].transportDetails?.label?.isEmpty() == true || viewModel.allDayDateSaveList[3].transportDetails?.label == null) {
                            if (viewModel.allDayDateSaveList[3].modeOfTransport?.items == null) {
                                binding!!.tvTransportDetails.text = getString(R.string.transport_details)
                                setDisableColorHideArrow()
                            } else {
                                viewModel.allDayDateSaveList[3].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                                    if (itemTransportDetails.isSelected) {
                                        binding!!.tvTransportDetails.text = itemTransportDetails.label
                                        return
                                    }
                                    binding!!.tvTransportDetails.text = getString(R.string.transport_details)

                                }
                            }
                        } else {
                            viewModel.allDayDateSaveList[3].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                                if (itemTransportDetails.isSelected){
                                    binding!!.tvTransportDetails.text=itemTransportDetails.label
                                    return
                                }
                            }
                            if(viewModel.allDayDateSaveList[3].transportDetails?.label==null)
                                binding!!.tvTransportDetails.text = "-"
                            else binding!!.tvTransportDetails.text=getString(R.string.transport_details)
                        }
                    }else{
                        binding!!.tvTransportDetails.text = getString(R.string.transport_details)
                    }
                }

                getString(R.string.fr) -> {
                    //binding!!.tvTransportDetails.text = viewModel.allDayDateSaveList[4].transportDetails?.label ?: "-"
                    if (viewModel.allDayDateSaveList[4].modeOfTransport?.label?.isEmpty()==true || viewModel.allDayDateSaveList[4].modeOfTransport?.label==null){
                        binding!!.tvModeTransport.text=getString(R.string.mode_of_transport)
                    }else{
                        binding!!.tvModeTransport.text = viewModel.allDayDateSaveList[4].modeOfTransport?.label
                        setDisableColorShowArrow()
                    }

                    selectInOfficeCheckBox()
                    selectInHomeCheckBox()
                    if (viewModel.allDayDateSaveList[4].modeOfTransport?.items!=null) {
                        if (viewModel.allDayDateSaveList[4].modeOfTransport?.items?.size == 0) {
                            binding!!.tvTransportDetails.text = "-"
                            setDisableColorHideArrow()
                        } else if (viewModel.allDayDateSaveList[4].transportDetails?.label?.isEmpty() == true || viewModel.allDayDateSaveList[4].transportDetails?.label == null) {
                            if (viewModel.allDayDateSaveList[4].modeOfTransport?.items == null) {
                                binding!!.tvTransportDetails.text = getString(R.string.transport_details)
                                setDisableColorHideArrow()
                            } else {
                                viewModel.allDayDateSaveList[4].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                                    if (itemTransportDetails.isSelected) {
                                        binding!!.tvTransportDetails.text = itemTransportDetails.label
                                        return
                                    }
                                    binding!!.tvTransportDetails.text = getString(R.string.transport_details)

                                }
                            }
                        } else {
                            viewModel.allDayDateSaveList[4].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                                if (itemTransportDetails.isSelected){
                                    binding!!.tvTransportDetails.text=itemTransportDetails.label
                                    return
                                }
                            }
                            if(viewModel.allDayDateSaveList[4].transportDetails?.label==null)
                                binding!!.tvTransportDetails.text = "-"
                            else binding!!.tvTransportDetails.text=getString(R.string.transport_details)
                        }
                    }else{
                        binding!!.tvTransportDetails.text = getString(R.string.transport_details)
                    }
                    //selectInOfficeCheckBox()
                }

                getString(R.string.sa) -> {
                    //binding!!.tvTransportDetails.text = viewModel.allDayDateSaveList[5].transportDetails?.label ?: "-"
                    if (viewModel.allDayDateSaveList[5].modeOfTransport?.label?.isEmpty()==true || viewModel.allDayDateSaveList[5]
                            .modeOfTransport?.label==null){
                        binding!!.tvModeTransport.text=getString(R.string.mode_of_transport)
                    }else{
                        binding!!.tvModeTransport.text = viewModel.allDayDateSaveList[5].modeOfTransport?.label
                        setDisableColorShowArrow()
                    }

                    selectInOfficeCheckBox()
                    selectInHomeCheckBox()
                    if (viewModel.allDayDateSaveList[5].modeOfTransport?.items!=null) {
                        if (viewModel.allDayDateSaveList[5].modeOfTransport?.items?.size == 0) {
                            binding!!.tvTransportDetails.text = "-"
                            setDisableColorHideArrow()
                        } else if (viewModel.allDayDateSaveList[5].transportDetails?.label?.isEmpty() == true || viewModel.allDayDateSaveList[5].transportDetails?.label == null) {
                            if (viewModel.allDayDateSaveList[5].modeOfTransport?.items == null) {
                                binding!!.tvTransportDetails.text = getString(R.string.transport_details)
                                setDisableColorHideArrow()
                            } else {
                                viewModel.allDayDateSaveList[5].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                                    if (itemTransportDetails.isSelected) {
                                        binding!!.tvTransportDetails.text = itemTransportDetails.label
                                        return
                                    }
                                    binding!!.tvTransportDetails.text = getString(R.string.transport_details)

                                }
                            }
                        } else {
                            viewModel.allDayDateSaveList[5].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                                if (itemTransportDetails.isSelected){
                                    binding!!.tvTransportDetails.text=itemTransportDetails.label
                                    return
                                }
                            }
                            if(viewModel.allDayDateSaveList[5].transportDetails?.label==null)
                                binding!!.tvTransportDetails.text = "-"
                            else binding!!.tvTransportDetails.text=getString(R.string.transport_details)
                        }
                    }else{
                        binding!!.tvTransportDetails.text = getString(R.string.transport_details)
                    }
                    //selectInOfficeCheckBox()
                }

                getString(R.string.su) -> {
                    if (viewModel.allDayDateSaveList[6].modeOfTransport?.label?.isEmpty()==true || viewModel.allDayDateSaveList[6]
                            .modeOfTransport?.label==null){
                        binding!!.tvModeTransport.text=getString(R.string.mode_of_transport)
                    }else{
                        binding!!.tvModeTransport.text = viewModel.allDayDateSaveList[6].modeOfTransport?.label
                        setDisableColorShowArrow()
                    }

                    selectInOfficeCheckBox()
                    selectInHomeCheckBox()
                    if (viewModel.allDayDateSaveList[6].modeOfTransport?.items!=null) {
                        if (viewModel.allDayDateSaveList[6].modeOfTransport?.items?.size == 0) {
                            binding!!.tvTransportDetails.text = "-"
                            setDisableColorHideArrow()
                        } else if (viewModel.allDayDateSaveList[6].transportDetails?.label?.isEmpty() == true || viewModel.allDayDateSaveList[6].transportDetails?.label == null) {
                            if (viewModel.allDayDateSaveList[6].modeOfTransport?.items == null) {
                                binding!!.tvTransportDetails.text = getString(R.string.transport_details)
                                setDisableColorHideArrow()
                            } else {
                                viewModel.allDayDateSaveList[6].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                                    if (itemTransportDetails.isSelected) {
                                        binding!!.tvTransportDetails.text = itemTransportDetails.label
                                        return
                                    }
                                    binding!!.tvTransportDetails.text = getString(R.string.transport_details)

                                }
                            }
                        } else {
                            viewModel.allDayDateSaveList[6].modeOfTransport?.items?.forEachIndexed { index, itemTransportDetails ->
                                if (itemTransportDetails.isSelected){
                                    binding!!.tvTransportDetails.text=itemTransportDetails.label
                                    return
                                }
                            }
                            if(viewModel.allDayDateSaveList[6].transportDetails?.label==null)
                                binding!!.tvTransportDetails.text = "-"
                            else binding!!.tvTransportDetails.text=getString(R.string.transport_details)
                        }
                    }else{
                        binding!!.tvTransportDetails.text = getString(R.string.transport_details)
                    }
                    //selectInOfficeCheckBox()
                }

                else -> {

                }
            }
        }

    }

    private fun setDisableColorShowArrow() {
        binding!!.ivArrowTransport.isVisible=true
        binding!!.tvTransportDetails.isEnabled=true
        binding!!.tvTransportDetails.setTextColor(requireContext().getColorFromAttr(R.attr.colorTextView))
    }

    private fun setDisableColorHideArrow() {
        binding!!.ivArrowTransport.isVisible=false
        binding!!.tvTransportDetails.isEnabled=false
        binding!!.tvTransportDetails.setTextColor(requireContext().getColorFromAttr(R.attr.colorSearchHint))
    }

    private fun setShowHideCheckBox(){
        binding!!.tvCheckboxTakeOver.isVisible=true
        binding!!.tvCheckboxTakeOver.isEnabled=true
        binding!!.tvCheckboxTakeOver.backgroundColorTint(tenantInfoData?.brandingInfo?.primaryColor)
        binding!!.tvNa.isVisible=false
    }
    private fun setShowHideCheckBoxIsViewSurvey(){
        /*binding!!.tvCheckboxTakeOver.isVisible=true
        binding!!.tvCheckboxTakeOver.isEnabled=false
        binding!!.tvCheckboxTakeOver.backgroundColorTint(tenantInfoData?.brandingInfo?.primaryColor)*/
        binding!!.tvNa.isVisible=false
        binding!!.view2.isVisible=false
        binding!!.tvTake.isVisible=false
        binding!!. tvNa.isVisible=false
        binding!!.tvCheckboxTakeOver.isVisible=false
    }
    /**
     * This method contains code selectInOfficeCheckBox
     *
     */
    private fun selectInOfficeCheckBox(){
        Log.e("=============", "Scroll ${viewModel.isSelectedInOfficeMo}")
        when (daySelected) {
            getString(R.string.mo) -> {
                if (viewModel.isSelectedInOfficeMo){
                    setEnableColor()
                }else{
                    setDisableColor()
                }
            }

            getString(R.string.tu) -> {
                binding!!.tvNa.isVisible=false
                if (viewModel.allDayDateSaveList[0].modeOfTransport?.label?.isEmpty()==true || viewModel.allDayDateSaveList[0].modeOfTransport?.label==null){
                    binding!!.tvCheckboxTakeOver.isVisible=true
                    binding!!.tvCheckboxTakeOver.isEnabled=false
                    binding!!.tvCheckboxTakeOver.backgroundTintList=(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorSearchHint)))
                }else{
                    binding!!.tvCheckboxTakeOver.isVisible=true
                    binding!!.tvCheckboxTakeOver.isEnabled=true
                    binding!!.tvCheckboxTakeOver.backgroundColorTint(tenantInfoData?.brandingInfo?.primaryColor)
                }
                if (viewModel.isSelectedInOfficeTu){
                    setEnableColor()
                }else{
                    setDisableColor()
                }
            }
            getString(R.string.we) -> {
                if (viewModel.allDayDateSaveList[1].modeOfTransport?.label?.isEmpty()==true || viewModel.allDayDateSaveList[1].modeOfTransport?.label==null){
                    binding!!.tvCheckboxTakeOver.isVisible=true
                    binding!!.tvCheckboxTakeOver.isEnabled=false
                    binding!!.tvCheckboxTakeOver.backgroundTintList=(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorSearchHint)))
                }else{
                    binding!!.tvCheckboxTakeOver.isVisible=true
                    binding!!.tvCheckboxTakeOver.isEnabled=true
                    binding!!.tvCheckboxTakeOver.backgroundColorTint(tenantInfoData?.brandingInfo?.primaryColor)
                }
                if (viewModel.isSelectedInOfficeWe){
                    setEnableColor()
                }else{
                    setDisableColor()
                }
            }
            getString(R.string.th) -> {
                if (viewModel.allDayDateSaveList[2].modeOfTransport?.label?.isEmpty()==true || viewModel.allDayDateSaveList[2].modeOfTransport?.label==null){
                    binding!!.tvCheckboxTakeOver.isVisible=true
                    binding!!.tvCheckboxTakeOver.isEnabled=false
                    binding!!.tvCheckboxTakeOver.backgroundTintList=(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorSearchHint)))
                }else{
                    binding!!.tvCheckboxTakeOver.isVisible=true
                    binding!!.tvCheckboxTakeOver.isEnabled=true
                    binding!!.tvCheckboxTakeOver.backgroundColorTint(tenantInfoData?.brandingInfo?.primaryColor)
                }
                if (viewModel.isSelectedInOfficeTh){
                    setEnableColor()
                }else{
                    setDisableColor()
                }
            }
            getString(R.string.fr) -> {
                if (viewModel.allDayDateSaveList[3].modeOfTransport?.label?.isEmpty()==true || viewModel.allDayDateSaveList[3].modeOfTransport?.label==null){
                    binding!!.tvCheckboxTakeOver.isVisible=true
                    binding!!.tvCheckboxTakeOver.isEnabled=false
                    binding!!.tvCheckboxTakeOver.backgroundTintList=(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorSearchHint)))
                }else{
                    binding!!.tvCheckboxTakeOver.isVisible=true
                    binding!!.tvCheckboxTakeOver.isEnabled=true
                    binding!!.tvCheckboxTakeOver.backgroundColorTint(tenantInfoData?.brandingInfo?.primaryColor)
                }
                if (viewModel.isSelectedInOfficeFr){
                    setEnableColor()
                }else{
                    setDisableColor()
                }
            }
            getString(R.string.sa) -> {
                if (viewModel.allDayDateSaveList[4].modeOfTransport?.label?.isEmpty()==true || viewModel.allDayDateSaveList[4].modeOfTransport?.label==null){
                    binding!!.tvCheckboxTakeOver.isVisible=true
                    binding!!.tvCheckboxTakeOver.isEnabled=false
                    binding!!.tvCheckboxTakeOver.backgroundTintList=(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorSearchHint)))
                }else{
                    binding!!.tvCheckboxTakeOver.isVisible=true
                    binding!!.tvCheckboxTakeOver.isEnabled=true
                    binding!!.tvCheckboxTakeOver.backgroundColorTint(tenantInfoData?.brandingInfo?.primaryColor)
                }
                if (viewModel.isSelectedInOfficeSa){
                    setEnableColor()
                }else{
                    setDisableColor()
                }
            }
            getString(R.string.su) -> {
                if (viewModel.allDayDateSaveList[5].modeOfTransport?.label?.isEmpty()==true || viewModel.allDayDateSaveList[5].modeOfTransport?.label==null){
                    binding!!.tvCheckboxTakeOver.isVisible=true
                    binding!!.tvCheckboxTakeOver.isEnabled=false
                    binding!!.tvCheckboxTakeOver.backgroundTintList=(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorSearchHint)))
                }else{
                    binding!!.tvCheckboxTakeOver.isVisible=true
                    binding!!.tvCheckboxTakeOver.isEnabled=true
                    binding!!.tvCheckboxTakeOver.backgroundColorTint(tenantInfoData?.brandingInfo?.primaryColor)
                }
                if (viewModel.isSelectedInOfficeSu){
                    setEnableColor()
                }else{
                    setDisableColor()
                }
            }
            else -> {

            }
        }

    }

    /**
     * This method contains code setEnableColor
     *
     */
    private fun setEnableColor(){
        binding?.apply {
            tvModeTransport.setTextColor(requireContext().getColorFromAttr(R.attr.colorTextView))
            //tvTransportDetails.setTextColor(requireContext().getColorFromAttr(R.attr.colorTextView))
            tvTake.setTextColor(requireContext().getColorFromAttr(R.attr.colorTextView))
            tvNa.setTextColor(requireContext().getColorFromAttr(R.attr.colorTextView))

            tvModeTransport.typeface=ResourcesCompat.getFont(requireContext(),R.font.poppins_medium_500)
            tvTransportDetails.typeface=ResourcesCompat.getFont(requireContext(),R.font.poppins_medium_500)
            tvTake.typeface=ResourcesCompat.getFont(requireContext(),R.font.poppins_medium_500)
            tvNa.typeface=ResourcesCompat.getFont(requireContext(),R.font.poppins_medium_500)

            ivArrowMode.setColorFilter(requireContext().getColorFromAttr(R.attr.colorTextView))
            ivArrowTransport.setColorFilter(requireContext().getColorFromAttr(R.attr.colorTextView))

            tvModeTransport.isEnabled=true
          //  tvTransportDetails.isEnabled=true
            tvTake.isEnabled=true
            //tvCheckboxTakeOver.isEnabled=true

            //tvCheckboxTakeOver.backgroundColorTint(tenantInfoData?.brandingInfo?.primaryColor)
            binding?.ivCheckboxInOffice?.setImageResource(R.drawable.ic_checkbox_selected)
            binding?.ivCheckboxInOffice?.imageTickTintCheckBox(BaseApplication.tenantSharedPreference.getTenantPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)?.brandingInfo?.primaryColor)
            tvInOffice.buttonTextColorText(tenantInfoData?.brandingInfo?.primaryColor)
            tvInOffice.typeface= ResourcesCompat.getFont(requireContext(), R.font.poppins_semi_bold_600)
            setDisableColorWfh()
        }
    }
    private fun searchIconSetSource(){
        if (binding!!.telSearchSource.text?.isEmpty() == true){
                binding!!.telSearchSource.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0)
            } else {
            binding!!.telSearchSource.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
        }
    }
    private fun searchIconSetDestination(){
        if (binding!!.telSearchDestination.text?.isEmpty() == true){
            binding!!.telSearchDestination.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0)
        } else {
            binding!!.telSearchDestination.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
        }
    }
    /**
     * This method contains code isValid
     *
     */
    private fun isValid(): Boolean{
        val valid = booleanArrayOf(true,true,true,true,true,true,true)
        val validTrn=booleanArrayOf(true,true,true,true,true,true,true)
        binding!!.tvSourceError.isVisible=false
        binding!!.tvDestinationError.isVisible=false

        if (binding?.telSearchSource.getTrimText().isEmpty()){
            binding!!.tvSourceError.isVisible=true
        }
        if (binding?.telSearchDestination.getTrimText().isEmpty()){
            binding!!.tvDestinationError.isVisible=true
        }
//        viewModel.allDayDateSaveList.forEachIndexed { index, dayModel ->
//
//        }
        if (binding?.telSearchDestination.getTrimText().isEmpty()&&binding?.telSearchSource.getTrimText().isEmpty()){
            binding!!.tvDestinationError.isVisible=true
            binding!!.tvSourceError.isVisible=true
            return false
        }else if(binding?.telSearchSource.getTrimText().isEmpty()){
            return false
        }else if(binding?.telSearchDestination.getTrimText().isEmpty()){
            return false
        }

        if (viewModel.isSelectedInOfficeMo){

            valid[0] = false
            validTrn[0] = false

            if (viewModel.allDayDateSaveList[0].modeOfTransport?.items==null){
                showErrorMessage(getString(R.string.please_select_the_mode_of_transport_for_mo))
                valid[0] = false
            }else{
                if (viewModel.allDayDateSaveList[0].modeOfTransport?.items?.size!=0){
                    viewModel.allDayDateSaveList[0].modeOfTransport?.items?.forEachIndexed { i, itemTransportDetails ->
                        if (itemTransportDetails.isSelected) {
                            validTrn[0]= true
                        }
                    }
                    if (!validTrn[0]){
                        showErrorMessage(getString(R.string.please_select_the_transport_details_for_mo))
                        valid[0] =  false
                    }else{
                        valid[0] = true
                    }
                }else{
                    valid[0] = true
                }
            }
        }
        if (viewModel.isSelectedInOfficeTu) {
            valid[1] = false
            validTrn[1] = false
            if (viewModel.allDayDateSaveList[1].modeOfTransport?.items == null) {
                showErrorMessage(getString(R.string.please_select_the_mode_of_transport_for_tu))
                valid[1] = false
            }else {
                if (viewModel.allDayDateSaveList[1].modeOfTransport?.items?.size!=0){
                    viewModel.allDayDateSaveList[1].modeOfTransport?.items?.forEachIndexed { i, itemTransportDetails ->
                        if (itemTransportDetails.isSelected) {
                            validTrn[1] = true
                        }
                    }
                    if (!validTrn[1]) {
                        showErrorMessage(getString(R.string.please_select_the_transport_details_for_tu))
                        valid[1] = false
                    }else{
                        valid[1] = true
                    }
                }else{
                    valid[1] = true
                }
            }
        }

        if (viewModel.isSelectedInOfficeWe){
            valid[2] = false
            validTrn[2] = false
            if (viewModel.allDayDateSaveList[2].modeOfTransport?.items == null) {
                showErrorMessage(getString(R.string.please_select_the_mode_of_transport_for_we))
                valid[2] = false
            }else {
                if (viewModel.allDayDateSaveList[2].modeOfTransport?.items?.size!=0){
                    viewModel.allDayDateSaveList[2].modeOfTransport?.items?.forEachIndexed { i, itemTransportDetails ->
                        if (itemTransportDetails.isSelected) {
                            validTrn[2] = true
                        }
                    }
                    if (!validTrn[2]) {
                        showErrorMessage(getString(R.string.please_select_the_transport_details_for_we))
                        valid[2] = false
                    }else{
                        valid[2] = true
                    }

                }else{
                    valid[2] = true
                }
            }
                /*if (viewModel.allDayDateSaveList[2].modeOfTransport?.items!=null &&iewModel.allDayDateSaveList[1].modeOfTransport?.items?.size!!>0){
                    dayModel.modeOfTransport?.items?.forEachIndexed { i, itemTransportDetails ->
                        if (itemTransportDetails.isSelected) {
                            validTrn = true
                            valid = true
                        }
                    }
                    if (!validTrn) {
                        showErrorMessage(getString(R.string.please_select_the_transport_details_for_we))
                        valid = false
                    }
                }
                if (dayModel.modeOfTransport?.items==null){
                    showErrorMessage(getString(R.string.please_select_the_mode_of_transport_for_we))
                    valid = false
                }*/

        }
        if (viewModel.isSelectedInOfficeTh){
            valid[3] = false
            validTrn[3] = false
            if (viewModel.allDayDateSaveList[3].modeOfTransport?.items == null) {
                showErrorMessage(getString(R.string.please_select_the_mode_of_transport_for_th))
                valid[3] = false
            }else {
                if (viewModel.allDayDateSaveList[3].modeOfTransport?.items?.size!=0){
                    viewModel.allDayDateSaveList[3].modeOfTransport?.items?.forEachIndexed { i, itemTransportDetails ->
                        if (itemTransportDetails.isSelected) {
                            validTrn[3] = true
                        }
                    }
                    if (!validTrn[3]) {
                        showErrorMessage(getString(R.string.please_select_the_transport_details_for_th))
                        valid[3] = false
                    }else{
                        valid[3] = true
                    }

                }else{
                    valid[3] = true
                }
            }
           /* if (index==3){
                if (dayModel.modeOfTransport?.items?.size!=0){
                    dayModel.modeOfTransport?.items?.forEachIndexed { i, itemTransportDetails ->
                        if (itemTransportDetails.isSelected) {
                            validTrn= true
                        }
                    }
                    valid = if (validTrn){
                        true
                    }else {
                        showErrorMessage(getString(R.string.please_select_the_transport_details_for_th))
                        false
                    }
                }
                if (dayModel.modeOfTransport?.items==null){
                    showErrorMessage(getString(R.string.please_select_the_mode_of_transport_for_th))
                    valid = false
                }
            }*/
        }
        if (viewModel.isSelectedInOfficeFr){
            valid[4] = false
            validTrn[4] = false
            if (viewModel.allDayDateSaveList[4].modeOfTransport?.items == null) {
                showErrorMessage(getString(R.string.please_select_the_mode_of_transport_for_fr))
                valid[4] = false
            }else {
                if (viewModel.allDayDateSaveList[4].modeOfTransport?.items?.size!=0){
                    viewModel.allDayDateSaveList[4].modeOfTransport?.items?.forEachIndexed { i, itemTransportDetails ->
                        if (itemTransportDetails.isSelected) {
                            validTrn[4] = true
                        }
                    }
                    if (!validTrn[4]) {
                        showErrorMessage(getString(R.string.please_select_the_transport_details_for_fr))
                        valid[4] = false
                    }else{
                        valid[4] = true
                    }

                }else{
                    valid[4] = true
                }
            }
            /*if (index==4){
                if (dayModel.modeOfTransport?.items?.size!=0){
                    dayModel.modeOfTransport?.items?.forEachIndexed { i, itemTransportDetails ->
                        if (itemTransportDetails.isSelected) {
                            validTrn= true
                        }
                    }
                    valid = if (validTrn){
                        true
                    }else {
                        showErrorMessage(getString(R.string.please_select_the_transport_details_for_fr))
                        false
                    }
                }
                if (dayModel.modeOfTransport?.items==null){
                    showErrorMessage(getString(R.string.please_select_the_mode_of_transport_for_fr))
                    valid = false
                }
            }*/
        }
        if (viewModel.isSelectedInOfficeSa){
            valid[5] = false
            validTrn[5] = false
            if (viewModel.allDayDateSaveList[5].modeOfTransport?.items == null) {
                showErrorMessage(getString(R.string.please_select_the_mode_of_transport_for_sa))
                valid[5] = false
            }else {
                if (viewModel.allDayDateSaveList[5].modeOfTransport?.items?.size!=0){
                    viewModel.allDayDateSaveList[5].modeOfTransport?.items?.forEachIndexed { i, itemTransportDetails ->
                        if (itemTransportDetails.isSelected) {
                            validTrn[5] = true
                        }
                    }
                    if (!validTrn[5]) {
                        showErrorMessage(getString(R.string.please_select_the_transport_details_for_sa))
                        valid[5] = false
                    }else{
                        valid[5] = true
                    }

                }else{
                    valid[5] = true
                }
            }
            /*if (index==5){
                if (dayModel.modeOfTransport?.items?.size!=0){
                    dayModel.modeOfTransport?.items?.forEachIndexed { i, itemTransportDetails ->
                        if (itemTransportDetails.isSelected) {
                            validTrn= true
                        }
                    }
                    valid = if (validTrn){
                        true
                    }else {
                        showErrorMessage(getString(R.string.please_select_the_transport_details_for_sa))
                        false
                    }
                }
                if (dayModel.modeOfTransport?.items==null){
                    showErrorMessage(getString(R.string.please_select_the_mode_of_transport_for_sa))
                    valid = false
                }
            }*/
        }
        if (viewModel.isSelectedInOfficeSu){
            valid[6] = false
            validTrn[6] = false
            if (viewModel.allDayDateSaveList[6].modeOfTransport?.items == null) {
                showErrorMessage(getString(R.string.please_select_the_mode_of_transport_for_su))
                valid[6] = false
            }else {
                if (viewModel.allDayDateSaveList[6].modeOfTransport?.items?.size!=0){
                    viewModel.allDayDateSaveList[6].modeOfTransport?.items?.forEachIndexed { i, itemTransportDetails ->
                        if (itemTransportDetails.isSelected) {
                            validTrn[6] = true
                        }
                    }
                    if (!validTrn[6]) {
                        showErrorMessage(getString(R.string.please_select_the_transport_details_for_su))
                        valid[6] = false
                    }else{
                        valid[6] = true
                    }

                }else{
                    valid[6] = true
                }
            }
            /*if (index==6){
                if (dayModel.modeOfTransport?.items?.size!=0){
                    dayModel.modeOfTransport?.items?.forEachIndexed { i, itemTransportDetails ->
                        if (itemTransportDetails.isSelected) {
                            validTrn= true
                        }
                    }
                    valid = if (validTrn){
                        true
                    }else {
                        showErrorMessage(getString(R.string.please_select_the_transport_details_for_su))
                        false
                    }
                }
                if (dayModel.modeOfTransport?.items==null){
                    showErrorMessage(getString(R.string.please_select_the_mode_of_transport_for_su))
                    valid = false
                }
            }*/
        }



        return valid[0]&& valid[1]&& valid[2]&& valid[3]&& valid[4]&& valid[5]&& valid[6]
    }

    /**
     * This method contains code setDisableISViewSurvey
     *
     */
    private fun setDisableISViewSurvey(){
        binding?.apply {
            tvModeTransport.setTextColor(requireContext().getColorFromAttr(R.attr.colorSearchHint))
            tvTransportDetails.setTextColor(requireContext().getColorFromAttr(R.attr.colorSearchHint))
            tvTake.setTextColor(requireContext().getColorFromAttr(R.attr.colorSearchHint))
            tvNa.setTextColor(requireContext().getColorFromAttr(R.attr.colorSearchHint))

            tvModeTransport.typeface=ResourcesCompat.getFont(requireContext(),R.font.poppins_regular_400)
            tvTransportDetails.typeface=ResourcesCompat.getFont(requireContext(),R.font.poppins_regular_400)
            tvTake.typeface=ResourcesCompat.getFont(requireContext(),R.font.poppins_regular_400)
            tvNa.typeface=ResourcesCompat.getFont(requireContext(),R.font.poppins_regular_400)

            ivArrowMode.setColorFilter(requireContext().getColorFromAttr(R.attr.colorSearchHint))
            ivArrowTransport.setColorFilter(requireContext().getColorFromAttr(R.attr.colorSearchHint))


            binding?.ivCheckboxInOffice?.setImageResource(R.drawable.ic_checkbox_unselected)
            context?.getColorFromAttr(R.attr.colorTextView)
                ?.let { binding?.ivCheckboxInOffice?.setColorFilter(it) }

            //tvInOffice.buttonTextColorNotification(tenantInfoData?.brandingInfo?.primaryColor)
            tvCheckboxTakeOver.backgroundTintList=(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorSearchHint)))
            tvInOffice.setTextColor(requireContext().getColorFromAttr(R.attr.colorTextView))
            tvInOffice.typeface= ResourcesCompat.getFont(requireContext(), R.font.poppins_medium_500)

            telSearchDestination.isEnabled=false
            telSearchSource.isEnabled=false
            tvDisableView.isVisible=true
            ivClearSource.isEnabled=false
            ivClearDestination.isEnabled=false
            tvInOffice.isEnabled=false
            ivCheckboxInOffice.isEnabled=false
            tvInHome.isEnabled=false
            ivCheckboxInHome.isEnabled=false
            tvModeTransport.isEnabled=false
            tvTransportDetails.isEnabled=false
            tvTake.isEnabled=false
            tvCheckboxTakeOver.isEnabled=false

            tvProfileAddress.isVisible=false
            ivCheckboxProfile.isVisible=false
            tvDes.isVisible=false
            btnSubmit.isVisible=false
            ivClearSource.isVisible=false
            ivClearDestination.isVisible=false
            view2.isVisible=false
            tvTake.isVisible=false
            tvNa.isVisible=false
            tvCheckboxTakeOver.isVisible=false

        }
    }
    private  fun inOfficeCheckBoxSelected(){
        binding?.apply {
            ivCheckboxInOffice.setImageResource(R.drawable.ic_checkbox_selected)
            ivCheckboxInOffice.imageTickTintCheckBox(BaseApplication.tenantSharedPreference.getTenantPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)?.brandingInfo?.primaryColor)
            tvInOffice.buttonTextColorText(tenantInfoData?.brandingInfo?.primaryColor)
            tvInOffice.typeface= ResourcesCompat.getFont(requireContext(), R.font.poppins_semi_bold_600)
        }
    }
    private fun inWorkFromHomeCheckBoxSelected(){
        binding?.apply {
            ivCheckboxInHome.setImageResource(R.drawable.ic_checkbox_selected)
            ivCheckboxInHome.imageTickTintCheckBox(BaseApplication.tenantSharedPreference.getTenantPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)?.brandingInfo?.primaryColor)
            tvInHome.buttonTextColorText(tenantInfoData?.brandingInfo?.primaryColor)
            tvInHome.typeface= ResourcesCompat.getFont(requireContext(), R.font.poppins_semi_bold_600)
        }
    }
    private fun inWorkFromHomeCheckBoxUNSelected(){
        binding?.apply {
            ivCheckboxInHome.setImageResource(R.drawable.ic_checkbox_unselected)
            context?.getColorFromAttr(R.attr.colorTextView)
                ?.let { ivCheckboxInHome.setColorFilter(it) }
            tvInHome.setTextColor(requireContext().getColorFromAttr(R.attr.colorTextView))
            tvInHome.typeface= ResourcesCompat.getFont(requireContext(), R.font.poppins_medium_500)
        }
    }
  private  fun inOfficeCheckBoxUNSelected(){
        binding?.apply {
            ivCheckboxInOffice.setImageResource(R.drawable.ic_checkbox_unselected)
            context?.getColorFromAttr(R.attr.colorTextView)
                ?.let { ivCheckboxInOffice.setColorFilter(it) }
            tvInOffice.setTextColor(requireContext().getColorFromAttr(R.attr.colorTextView))
            tvInOffice.typeface= ResourcesCompat.getFont(requireContext(), R.font.poppins_medium_500)
        }
    }
    override fun onStart() {
        super.onStart()
        //hideTabs()
        if (tenantInfoData?.tenantInfo?.enabledServices.equals(LocalConfig.co2_management,true)) {
            (baseActivity as DashboardActivity).binding.bottomBarCo2.clBottomBar.visibility = View.GONE
        }else{
            (baseActivity as DashboardActivity).binding.bottomBar.clBottomBar.visibility = View.GONE
        }
        //EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        //EventBus.getDefault().unregister(this)
    }

    /**
     * Notification count update
     *
     * @param updateNotificationCount
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun notificationCountUpdate(updateNotificationCount: UpdateNotificationCount) {
        /*if (updateNotificationCount.pushNotificationCount!! > 0) {
            binding!!.toolbar.tvNotificationCount.visibility = View.VISIBLE
            binding!!.toolbar.ivNotification.visibility = View.VISIBLE
            binding!!.toolbar.tvNotificationCount.visibility = updateNotificationCount.pushNotificationCount?:0
        } else {
            binding!!.toolbar.tvNotificationCount.visibility = View.GONE
            binding!!.toolbar.ivNotification.visibility = View.VISIBLE
        }*/
    }
}