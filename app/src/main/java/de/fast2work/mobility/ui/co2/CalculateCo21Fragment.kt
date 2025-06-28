package de.fast2work.mobility.ui.co2

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.ItemTransportDetails
import de.fast2work.mobility.data.response.ModeOfTransport
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.data.response.User
import de.fast2work.mobility.databinding.FragmentCalculateCo21Binding
import de.fast2work.mobility.db.ModelStops
import de.fast2work.mobility.ui.co2.SearchListFragment.Companion.PARAM_RESULT
import de.fast2work.mobility.ui.co2.SearchListFragment.Companion.PARAM_SEND_MODEL
import de.fast2work.mobility.ui.co2.SearchListFragment.Companion.PARAM_SET_INNER_SOURCE
import de.fast2work.mobility.ui.co2.SearchListFragment.Companion.PARAM_SET_SOURCE
import de.fast2work.mobility.ui.co2.adapter.Co2EmissionAdapter
import de.fast2work.mobility.ui.co2.bottom.CO2EmissionsSurveyInstructionsBottomSheetFragment
import de.fast2work.mobility.ui.co2.bottom.Co2ModeOfTransportBottomSheetFragment
import de.fast2work.mobility.ui.co2.bottom.Co2ModeOfTransportViewModel
import de.fast2work.mobility.ui.co2.bottom.TransportDetailBottomSheetFragment
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.dialog.DialogUtil
import de.fast2work.mobility.utility.extension.backgroundColorTint
import de.fast2work.mobility.utility.extension.buttonTextColorText
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.getTrimText
import de.fast2work.mobility.utility.extension.imageTickTintCheckBox
import de.fast2work.mobility.utility.extension.parcelable
import de.fast2work.mobility.utility.extension.setVisible
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.preference.EasyPref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class CalculateCo21Fragment :
    BaseVMBindingFragment<FragmentCalculateCo21Binding, CalculateCo21ViewModel>(
        CalculateCo21ViewModel::class.java
    ) {
    private var sourceDestination = false
    private var co2EmissionAdapter: Co2EmissionAdapter? = null
    private var mainDataList: ArrayList<ModelStops> = arrayListOf()

    private var modeOfTransportList: ArrayList<ModelStops> = arrayListOf()

    private var modelModeOfTransport: ModeOfTransport? = null
    private val co2ModeOfTransportViewModel: Co2ModeOfTransportViewModel by viewModels()
    private var surveyName: String = ""

    companion object {
        const val PARAM_SURVEY_ID = "SurveyId"
        const val PARAM_SURVEY_NAME = "SurveyName"
        fun newInstance(surveyId: Int = 0, surveyName: String) = CalculateCo21Fragment().apply {
            this.arguments = Bundle().apply {
                this.putInt(PARAM_SURVEY_ID, surveyId)
                this.putString(PARAM_SURVEY_NAME, surveyName)

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return generateBinding(FragmentCalculateCo21Binding.inflate(inflater), container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(PARAM_RESULT) { _: String, bundle: Bundle ->
            sourceDestination = bundle.getBoolean(PARAM_SET_SOURCE)
            viewModel.sourceDestination = bundle.parcelable(PARAM_SEND_MODEL)
            if (bundle.getBoolean(PARAM_SET_INNER_SOURCE)) {
                modeOfTransportList?.let {

                    if (viewModel.selectedLocationIndex == 0 && viewModel.sourceDestination?.compressed_address.toString() != viewModel.toAddressLocal &&
                        !viewModel.isSelectedProfileAddress
                    ) {
                        //viewModel.isSelectedProfileAddress = false
                        binding?.ivCheckboxProfile?.setImageResource(R.drawable.checkbox_unselected)
                        context?.getColorFromAttr(R.attr.colorTextView)
                            ?.let { binding?.ivCheckboxProfile?.setColorFilter(it) }
                        context?.getColorFromAttr(R.attr.colorCheckBoxProfileAddress)
                            ?.let { binding!!.tvProfileAddress.setTextColor(it) }
                        binding!!.tvProfileAddress.typeface =
                            ResourcesCompat.getFont(requireContext(), R.font.poppins_light_300)
                    }



                    it[viewModel.selectedLocationIndex].transportStopAddress =
                        viewModel.sourceDestination?.compressed_address.toString()
                    it[viewModel.selectedLocationIndex].transportStopAddressLatitude =
                        viewModel.sourceDestination?.lat.toString()
                    it[viewModel.selectedLocationIndex].transportStopAddressLongitude =
                        viewModel.sourceDestination?.long.toString()



                    if (it.size > viewModel.selectedLocationIndex + 1) {
                        it[viewModel.selectedLocationIndex + 1].transportStartAddress =
                            viewModel.sourceDestination?.compressed_address.toString()
                        it[viewModel.selectedLocationIndex + 1].transportStartAddressLatitude =
                            viewModel.sourceDestination?.lat.toString()
                        it[viewModel.selectedLocationIndex + 1].transportStartAddressLongitude =
                            viewModel.sourceDestination?.long.toString()
                    }
                }
                co2EmissionAdapter?.notifyItemChanged(viewModel.selectedLocationIndex)
                if (modeOfTransportList!!.size > viewModel.selectedLocationIndex + 1) {
                    co2EmissionAdapter?.notifyItemChanged(viewModel.selectedLocationIndex + 1)
                }
                //deleteOldData()

                val iterator = mainDataList.iterator()
                while (iterator.hasNext()) {
                    val i = iterator.next()
                    if (i.transportDay == viewModel.selectedDay) {
                        iterator.remove()
                    }
                }
                mainDataList.addAll(modeOfTransportList)
            } else {
                if (sourceDestination) {
                    binding!!.telSearchSource.setText(viewModel.sourceDestination?.compressed_address)
                    viewModel.fromAddressLocal =
                        viewModel.sourceDestination?.compressed_address.toBlankString()
                    viewModel.fromLatitudeLocal = viewModel.sourceDestination?.lat.toBlankString()
                    viewModel.fromLongitudeLocal = viewModel.sourceDestination?.long.toBlankString()
                    searchIconSetSource()
                    if (modeOfTransportList!!.isNotEmpty()) {
                        modeOfTransportList!![0].transportStartAddress =
                            viewModel.sourceDestination?.compressed_address.toBlankString()
                        modeOfTransportList!![0].transportStartAddressLatitude =
                            viewModel.sourceDestination?.lat.toBlankString()
                        modeOfTransportList!![0].transportStartAddressLongitude =
                            viewModel.sourceDestination?.long.toBlankString()


                    }
                } else {
                    binding!!.telSearchDestination.setText(viewModel.sourceDestination?.compressed_address)
                    viewModel.toAddressLocal =
                        viewModel.sourceDestination?.compressed_address.toBlankString()
                    viewModel.toLatitudeLocal = viewModel.sourceDestination?.lat.toBlankString()
                    viewModel.toLongitudeLocal = viewModel.sourceDestination?.long.toBlankString()
                    searchIconSetDestination()


                    if (modeOfTransportList!!.isNotEmpty()) {
                        modeOfTransportList[0].transportStopAddress =
                            binding!!.telSearchDestination.text.toString()
                        modeOfTransportList[0].transportStopAddressLatitude =
                            viewModel.sourceDestination?.lat.toBlankString()
                        modeOfTransportList[0].transportStopAddressLongitude =
                            viewModel.sourceDestination?.long.toBlankString()
                    }
                    co2EmissionAdapter?.notifyDataSetChanged()
                    binding!!.tlSearchDestination.isEnabled = false
                    binding!!.tlSearchSource.isEnabled = false
                }
                val iterator = mainDataList.iterator()
                while (iterator.hasNext()) {
                    val i = iterator.next()
                    if (i.transportDay == viewModel.selectedDay) {
                        iterator.remove()
                    }
                }
                mainDataList.addAll(modeOfTransportList)
            }
        }
    }

    override fun attachObservers() {
        viewModel.co2SurveyLiveData.observe(this) {
            if (it.isSuccess) {
                showSuccessMessage(it.responseMessage)
                popFragment()
            } else {
                showErrorMessage(it.responseMessage)
                popFragment()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        ui()
        hideTabs()
    }

    override fun initComponents() {
        viewModel.surveyIdLocal = arguments?.getInt(PARAM_SURVEY_ID) ?: 0
        surveyName = arguments?.getString(PARAM_SURVEY_NAME).toString()
        binding!!.tvTitle.text = surveyName
        if (isFirstTimeLoad) {
            setToolbar()
            searchIconSetSource()
            searchIconSetDestination()
            openCo2BottomSheet()
            loadDefaultData()
            initrvData()
            setThemeForView(binding!!.btnSubmit)
            selectedDay(binding!!.tvMo, getString(R.string.mo))

        }
    }


    private fun loadDefaultData() {
        mainDataList.clear()
        mainDataList.add(
            ModelStops(
                getString(R.string.mo),
                "",
                "",
                binding!!.telSearchDestination.text.toString(),
                "",
                viewModel.fromLatitudeLocal,
                viewModel.fromLongitudeLocal,
                viewModel.toLatitudeLocal,
                viewModel.toLongitudeLocal,
                null, null,
                false,
                ""
            )
        )
    }

    private fun deleteOldData() {
        val iterator = mainDataList.iterator()
        while (iterator.hasNext()) {
            val i = iterator.next()
            if (i.transportDay == viewModel.selectedDay) {
                iterator.remove()
            }
        }
        mainDataList.addAll(modeOfTransportList)
        modeOfTransportList.clear()
        binding!!.telMeansOfTransport.setText("")
        binding!!.telTransportDetails.setText("")
        binding!!.tlTransport.isVisible = true

    }

    private fun isValid(): Boolean {
        binding!!.tvSourceError.isVisible = false
        binding!!.tvDestinationError.isVisible = false

        if (binding?.telSearchSource.getTrimText().isEmpty()) {
            binding!!.tvSourceError.isVisible = true
        }
        if (binding?.telSearchDestination.getTrimText().isEmpty()) {
            binding!!.tvDestinationError.isVisible = true
        }
//        viewModel.allDayDateSaveList.forEachIndexed { index, dayModel ->
//
//        }
        if (binding?.telSearchDestination.getTrimText()
                .isEmpty() && binding?.telSearchSource.getTrimText().isEmpty()
        ) {
            binding!!.tvDestinationError.isVisible = true
            binding!!.tvSourceError.isVisible = true
            return false
        } else if (binding?.telSearchSource.getTrimText().isEmpty()) {
            return false
        } else if (binding?.telSearchDestination.getTrimText().isEmpty()) {
            return false
        }



        binding!!.tlSearchDestination.isEnabled = false
        binding!!.tlSearchSource.isEnabled = false

        showChangeText()

        return true
    }

    private fun checkDetailsFilledOrNot(): Boolean {
        modeOfTransportList.forEach {
            if (it.transportMode != "") {
                if (it.transportMeans != null && it.transportMeans?.label != "") {
                    return false
                }
            } else {
                return true
            }
        }
        return true
    }


    override fun setClickListener() {
        binding?.apply {
            ivEye.clickWithDebounce {
                openCo2BottomSheet()
            }
            telTransportDetails.clickWithDebounce {
                modelModeOfTransport?.let {
                    if (it.items.size > 0) {
                        binding?.apply {
                            val dialog = TransportDetailBottomSheetFragment.newInstance(it.items)
                            dialog.sendClickListener = {
                                telTransportDetails.setText(it.label)
                                modeOfTransportList[viewModel.selectedLocationIndex].transportDetails =
                                    it
                                //deleteOldData()
                                dialog.dismiss()
                            }
                            dialog.show(childFragmentManager, "")
                        }
                    }
                }

            }
            telMeansOfTransport.clickWithDebounce {
                telTransportDetails.setText("")

                val dialog = Co2ModeOfTransportBottomSheetFragment.newInstance(
                    co2ModeOfTransportViewModel,
                    modelModeOfTransport
                )
                dialog.sendClickListener = {
                    modelModeOfTransport = it
                    telMeansOfTransport.setText(it.label.toString())
                    modeOfTransportList.forEachIndexed { d,index->


                        modeOfTransportList[d].transportMeans = it
                        modeOfTransportList[d].isShareVisible = it.isSharable
                        modeOfTransportList[d].maximumNoOfPeople = it.noOfPeople
                        modeOfTransportList[d].isShared = false

                        //it.isShared=false
                    }
                    co2EmissionAdapter?.notifyDataSetChanged()

                    if (it.items.size > 0) {
                        telTransportDetails.alpha = 1f
                        tlTransport.isVisible = true
                    } else {
                        telTransportDetails.setText("-")
                        tlTransport.isVisible = false
                    }
                    //deleteOldData()
                    dialog.dismiss()
                }
                dialog.show(childFragmentManager, "")

            }

            ivCheckboxProfile.clickWithDebounce {
                binding!!.tvSourceError.isVisible = false
                binding!!.tvDestinationError.isVisible = false
                if (viewModel.isSelectedProfileAddress && viewModel.isProfileDialogShow) {
                    DialogUtil.showDialog(
                        parentFragmentManager, "",
                        getString(R.string.clearing_the_prefilled_addresses_will_clear_all_entered_journey_data_do_you_want_to_proceed),
                        getString(R.string.yes_), getString(R.string.no), object : DialogUtil.IL {
                            override fun onSuccess() {
                                viewModel.isSelectedProfileAddress =
                                    !viewModel.isSelectedProfileAddress
                                checkBoxProfileAddress()
                                clearAllData()
                                clearMainData()
                                binding!!.tlSearchDestination.isEnabled = true
                                binding!!.tlSearchSource.isEnabled = true
                                co2EmissionAdapter?.notifyDataSetChanged()
                            }

                            override fun onCancel(isNeutral: Boolean) {

                            }
                        }, isCancelShow = false, isTitleShow = View.GONE
                    )
                } else {
                    viewModel.isSelectedProfileAddress = !viewModel.isSelectedProfileAddress
                    checkBoxProfileAddress()
                }


            }

            tvProfileAddress.clickWithDebounce {
                binding!!.tvSourceError.isVisible = false
                binding!!.tvDestinationError.isVisible = false
                if (viewModel.isSelectedProfileAddress && viewModel.isProfileDialogShow) {
                    DialogUtil.showDialog(
                        parentFragmentManager, "",
                        getString(R.string.clearing_the_prefilled_addresses_will_clear_all_entered_journey_data_do_you_want_to_proceed),
                        getString(R.string.yes_), getString(R.string.no), object : DialogUtil.IL {
                            @SuppressLint("NotifyDataSetChanged")
                            override fun onSuccess() {
                                viewModel.isSelectedProfileAddress =
                                    !viewModel.isSelectedProfileAddress
                                checkBoxProfileAddress()
                                clearAllData()
                                clearMainData()
                                co2EmissionAdapter?.notifyDataSetChanged()
                            }

                            override fun onCancel(isNeutral: Boolean) {

                            }
                        }, isCancelShow = false, isTitleShow = View.GONE
                    )
                } else {
                    viewModel.isSelectedProfileAddress = !viewModel.isSelectedProfileAddress
                    checkBoxProfileAddress()
                }
                /*viewModel.isSelectedProfileAddress = !viewModel.isSelectedProfileAddress
                checkBoxProfileAddress()*/
            }

            ivClearDestination.clickWithDebounce {
                telSearchDestination.setText("")
                searchIconSetDestination()
            }

            ivPreviousDay.clickWithDebounce {
                viewModel.isPreviousDaySelected = true
                checkBoxPreviousDayCalculation()
                fetchPreviousDayData()
            }
            tvPreviousDay.clickWithDebounce {
                viewModel.isPreviousDaySelected = true
                checkBoxPreviousDayCalculation()
                fetchPreviousDayData()
            }
            telSearchSource.clickWithDebounce {
                pushFragment(SearchListFragment.newInstance(true))
            }
            telSearchDestination.clickWithDebounce {
                pushFragment(SearchListFragment.newInstance(false))
            }
            tvInOffice.clickWithDebounce {
                if (isValid()) {
                    if (modeOfTransportList.size > 0) {
                        modeOfTransportList.get(0).workingMode = "office"
                        modeOfTransportList.get(0).transportMode = "single"
                        clTypesTransport.setVisible(true)
                        btnReset.isVisible=true
                        tlMeansOfTransport.setVisible(true)
                        tlTransport.setVisible(true)
                        rvData.setVisible(true)
                        img.setVisible(false)
                        clAddJourney.setVisible(true)
                        handleSelectedImages(
                            false,
                            R.drawable.ic_checkbox_unselected_circle,
                            tvInHome,
                            ivCheckboxInHome
                        )
                        handleSelectedImages(
                            true,
                            R.drawable.ic_selected_mode,
                            tvInOffice,
                            ivCheckboxInOffice
                        )

                        handleSelectedImages(
                            false,
                            R.drawable.ic_checkbox_unselected_circle,
                            tvMultiMode,
                            ivMultiModel
                        )
                        handleSelectedImages(
                            true,
                            R.drawable.ic_selected_mode,
                            tvSingleMode,
                            ivSingleMode
                        )
                        Log.e("", "setClickListener:${mainDataList} ")
                        co2EmissionAdapter?.notifyDataSetChanged()
                    }
                }
            }
            ivCheckboxInOffice.clickWithDebounce {
                if (isValid()) {
                    if (modeOfTransportList.size > 0) {
                        modeOfTransportList.get(0).workingMode = "office"
                        modeOfTransportList.get(0).transportMode = "single"
                        btnReset.isVisible=true
                        clTypesTransport.setVisible(true)
                        tlMeansOfTransport.setVisible(true)
                        tlTransport.setVisible(true)
                        rvData.setVisible(true)
                        img.setVisible(false)
                        clAddJourney.setVisible(true)
                        img.setVisible(false)
                        handleSelectedImages(
                            false,
                            R.drawable.ic_checkbox_unselected_circle,
                            tvInHome,
                            ivCheckboxInHome
                        )
                        handleSelectedImages(
                            true,
                            R.drawable.ic_selected_mode,
                            tvInOffice,
                            ivCheckboxInOffice
                        )

                        handleSelectedImages(
                            false,
                            R.drawable.ic_checkbox_unselected_circle,
                            tvMultiMode,
                            ivMultiModel
                        )
                        handleSelectedImages(
                            true,
                            R.drawable.ic_selected_mode,
                            tvSingleMode,
                            ivSingleMode
                        )
                        Log.e("", "setClickListener:${mainDataList} ")
                        co2EmissionAdapter?.notifyDataSetChanged()
                    }
                }
            }
            tvInHome.clickWithDebounce {
                if (isValid()) {
                    if (checkDetailsFilledOrNot()) {
                        btnReset.isVisible=true
                        homeUI()
                    } else {
                        showDialog(
                            "office_to_home",
                            "",
                            getString(R.string.switching_to_working_from_home_will_temporarily_save_all_entered_journey_data_and_disable_further_entries_do_you_want_to_proceed)
                        )
                    }
                }
            }
            ivCheckboxInHome.clickWithDebounce {
                if (isValid()) {
                    if (checkDetailsFilledOrNot()) {
                        if (modeOfTransportList.size > 0) {
                            modeOfTransportList.get(0).workingMode = "home"
                            clTypesTransport.setVisible(false)
                            btnReset.isVisible=true
                            img.setVisible(true)
                            tlMeansOfTransport.setVisible(false)
                            tlTransport.setVisible(false)
                            rvData.setVisible(false)
                            clAddJourney.setVisible(false)
                            handleSelectedImages(
                                true,
                                R.drawable.ic_selected_mode,
                                tvInHome,
                                ivCheckboxInHome
                            )
                            handleSelectedImages(
                                false,
                                R.drawable.ic_checkbox_unselected_circle,
                                tvInOffice,
                                ivCheckboxInOffice
                            )
                        }
                    }else {
                        showDialog(
                            "office_to_home",
                            "",
                            getString(R.string.switching_to_working_from_home_will_temporarily_save_all_entered_journey_data_and_disable_further_entries_do_you_want_to_proceed)
                        )
                    }
                }

            }
            tvMultiMode.clickWithDebounce {
                if (isValid()) {
                    if (checkDetailsFilledOrNot()) {
                        multiModeUI()
                    } else {
                        showDialog(
                            "single_to_multi",
                            "",
                            getString(R.string.switching_to_multi_mode_will_erase_all_data_entered_in_single_mode_do_you_want_to_proceed)
                        )
                    }
                }
            }
            ivMultiModel.clickWithDebounce {
                if (isValid()) {
                    if (checkDetailsFilledOrNot()) {
                        multiModeUI()
                    } else {
                        showDialog(
                            "single_to_multi",
                            "",
                            getString(R.string.switching_to_multi_mode_will_erase_all_data_entered_in_single_mode_do_you_want_to_proceed)
                        )
                    }
                }
            }
            tvSingleMode.clickWithDebounce {
                if (isValid()) {
                    if (checkDetailsFilledOrNot()) {
                        singleModeUI()
                    } else {
                        showDialog(
                            "multi_to_single",
                            "",
                            getString(R.string.switching_to_single_mode_will_erase_all_data_entered_in_multi_mode_do_you_want_to_proceed)
                        )
                    }
                }
            }
            ivSingleMode.clickWithDebounce {
                if (isValid()) {
                    if (checkDetailsFilledOrNot()) {
                        singleModeUI()
                    } else {
                        showDialog(
                            "multi_to_single",
                            "",
                            getString(R.string.switching_to_single_mode_will_erase_all_data_entered_in_multi_mode_do_you_want_to_proceed)
                        )
                    }
                }
            }

            tvMo.clickWithDebounce {
                viewModel.selectedLocationIndex = 0
                deleteOldData()
                clPreviousDay.setVisible(false)
                selectedDay(binding!!.tvMo, getString(R.string.mo))
            }
            tvTu.clickWithDebounce {
                viewModel.selectedLocationIndex = 0
                viewModel.isPreviousDaySelected = false
                setNormalButtonUI()
                deleteOldData()
                clPreviousDay.setVisible(true)
                selectedDay(binding!!.tvTu, getString(R.string.tu))

            }
            tvWe.clickWithDebounce {
                viewModel.selectedLocationIndex = 0
                viewModel.isPreviousDaySelected = false
                setNormalButtonUI()
                deleteOldData()
                clPreviousDay.setVisible(true)
                selectedDay(binding!!.tvWe, getString(R.string.we))
            }
            tvTh.clickWithDebounce {
                viewModel.selectedLocationIndex = 0
                viewModel.isPreviousDaySelected = false
                setNormalButtonUI()
                deleteOldData()
                clPreviousDay.setVisible(true)
                selectedDay(binding!!.tvTh, getString(R.string.th))

            }
            tvFr.clickWithDebounce {
                viewModel.selectedLocationIndex = 0
                viewModel.isPreviousDaySelected = false
                setNormalButtonUI()
                deleteOldData()
                clPreviousDay.setVisible(true)
                selectedDay(binding!!.tvFr, getString(R.string.fr))

            }
            tvSa.clickWithDebounce {
                viewModel.selectedLocationIndex = 0
                viewModel.isPreviousDaySelected = false
                setNormalButtonUI()
                deleteOldData()
                clPreviousDay.setVisible(true)
                selectedDay(binding!!.tvSa, getString(R.string.sa))
            }
            tvSu.clickWithDebounce {
                viewModel.selectedLocationIndex = 0
                viewModel.isPreviousDaySelected = false
                setNormalButtonUI()
                deleteOldData()
                clPreviousDay.setVisible(true)
                selectedDay(binding!!.tvSu, getString(R.string.su))
            }
            clAddJourney.clickWithDebounce {
                if (checkDataValidation()) {
                    Log.e("", "setClickListener: ${mainDataList} ", )
                    if (modeOfTransportList.size < 4) {
                        Log.e("", "mainDataList.add phele: ${mainDataList} ", )
                        mainDataList.add(
                            ModelStops(
                                viewModel.selectedDay,
                                modeOfTransportList[0].workingMode,
                                modeOfTransportList[0].transportMode,
                                "",
                                modeOfTransportList[modeOfTransportList.size - 1].transportStopAddress,
                                "", "",
                                modeOfTransportList[modeOfTransportList.size - 1].transportStopAddressLatitude,
                                modeOfTransportList[modeOfTransportList.size - 1].transportStopAddressLongitude,
                                if(modeOfTransportList[0].transportMode=="single") modeOfTransportList[0].transportMeans else null, if(modeOfTransportList[0].transportMode=="single") modeOfTransportList[0].transportDetails else null,
                                false,
                                "",
                                maximumNoOfPeople = modeOfTransportList[modeOfTransportList.size - 1].maximumNoOfPeople,
                                isShareVisible = modeOfTransportList[0].isShareVisible
                            )
                        )
                        modeOfTransportList.add(
                            ModelStops(
                                viewModel.selectedDay,
                                modeOfTransportList[0].workingMode,
                                modeOfTransportList[0].transportMode,
                                "",
                                modeOfTransportList[modeOfTransportList.size - 1].transportStopAddress,
                                "", "",
                                modeOfTransportList[modeOfTransportList.size - 1].transportStopAddressLatitude,
                                modeOfTransportList[modeOfTransportList.size - 1].transportStopAddressLongitude,
                                if(modeOfTransportList[0].transportMode=="single") modeOfTransportList[0].transportMeans else null, if(modeOfTransportList[0].transportMode=="single") modeOfTransportList[0].transportDetails else null,
                                false,
                                "",
                                maximumNoOfPeople = modeOfTransportList[modeOfTransportList.size - 1].maximumNoOfPeople,
                                isShareVisible = modeOfTransportList[0].isShareVisible
                            )
                        )
                        if (modeOfTransportList.size >= 2) {
                            modeOfTransportList[modeOfTransportList.size - 2].transportStopAddress =
                                ""
                            modeOfTransportList[modeOfTransportList.size - 2].transportStopAddressLatitude =
                                ""
                            modeOfTransportList[modeOfTransportList.size - 2].transportStopAddressLongitude =
                                ""
                        } else {
                            modeOfTransportList[modeOfTransportList.size - 1].transportStopAddress =
                                ""
                            modeOfTransportList[modeOfTransportList.size - 1].transportStopAddressLatitude =
                                ""
                            modeOfTransportList[modeOfTransportList.size - 1].transportStopAddressLongitude =
                                ""
                        }
                        Log.e("", "setClickListener baad mei: ${mainDataList} ", )
                        //co2EmissionAdapter?.notifyDataSetChanged()
                        mainScollView.post(Runnable { mainScollView.fullScroll(ScrollView.FOCUS_DOWN) })
                        if (modeOfTransportList.size == 4) {
                            clAddJourney.setVisible(false)
                        }

                    }
                    co2EmissionAdapter?.notifyDataSetChanged()
                }
            }
            btnSubmit.clickWithDebounce {
                if (isValid()) {
                    if (checkCurrentDayValidation()) {
                        Log.e("", "setClickListener: ${mainDataList}", )
                        if (checkAllDayData()) {
                            DialogUtil.showDialog(
                                parentFragmentManager,
                                "",
                                getString(R.string.are_you_sure_you_want_to_submit_the_survey),
                                getString(R.string.yes_),
                                getString(R.string.no),
                                object : DialogUtil.IL {
                                    override fun onSuccess() {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            deleteOldData()
                                            selectedDay(
                                                returnCurrentDayTextView(viewModel.selectedDay),
                                                viewModel.selectedDay
                                            )
                                            delay(1000)
                                            print("DATA-----${modeOfTransportList}")
                                            mainDataList.let { it1 ->
                                                viewModel.onSubmitClickListeners(
                                                    it1
                                                )
                                            }
                                        }


                                    }

                                    override fun onCancel(isNeutral: Boolean) {

                                    }
                                },
                                isCancelShow = false,
                                isTitleShow = View.GONE
                            )

                        }
                    }
//                    else {
//                        showErrorMessage("Please ensure you provide data for at least one workday before submitting the survey.")
//                    }
                }
            }
            tvChange.clickWithDebounce {
                showDialog(
                    "changeAddress", "",
                    getString(R.string.if_you_change_the_address_your_all_journey_data_will_be_cleared_do_you_want_to_procced)
                )
            }
            btnReset.clickWithDebounce {

                    DialogUtil.showDialog(
                        parentFragmentManager, "",
                        getString(R.string.are_you_sure_you_want_to_reset_the_data_for_the_selected_day_all_data_entered_will_be_permanently_cleared_do_you_want_to_proceed),
                        getString(R.string.yes_), getString(R.string.no), object : DialogUtil.IL {
                            override fun onSuccess() {
                                clearAllData()
                                setNormalButtonUI()
                                selectedDay(returnCurrentDayTextView(viewModel.selectedDay), viewModel.selectedDay)
                            }

                            override fun onCancel(isNeutral: Boolean) {

                            }
                        }, isCancelShow = false, isTitleShow = View.GONE
                    )
            }
        }
    }

    private fun openCo2BottomSheet() {
        val dialog = CO2EmissionsSurveyInstructionsBottomSheetFragment.newInstance()
        dialog.show(childFragmentManager, "")
    }


    private fun multiModeUI() {
        binding!!.apply {
            if (modeOfTransportList.size > 0) {
                modeOfTransportList.get(0).workingMode = "office"
                modeOfTransportList.get(0).transportMode = "multi"
                tlMeansOfTransport.setVisible(false)
                tlTransport.setVisible(false)
                rvData.setVisible(true)
                img.setVisible(false)
                clAddJourney.setVisible(true)
                img.setVisible(false)
                handleSelectedImages(
                    true,
                    R.drawable.ic_selected_mode,
                    tvMultiMode,
                    ivMultiModel
                )
                handleSelectedImages(
                    false,
                    R.drawable.ic_checkbox_unselected_circle,
                    tvSingleMode,
                    ivSingleMode
                )
                co2EmissionAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun singleModeUI() {
        binding!!.apply {
            if (modeOfTransportList.size > 0) {
                modeOfTransportList.get(0).workingMode = "office"
                modeOfTransportList.get(0).transportMode = "single"
                tlMeansOfTransport.setVisible(true)
                tlTransport.setVisible(true)
                rvData.setVisible(true)
                img.setVisible(false)
                clAddJourney.setVisible(true)
                img.setVisible(false)
                handleSelectedImages(
                    false,
                    R.drawable.ic_checkbox_unselected_circle,
                    tvMultiMode,
                    ivMultiModel
                )
                handleSelectedImages(true, R.drawable.ic_selected_mode, tvSingleMode, ivSingleMode)
                co2EmissionAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun clearMainData() {
        val iterator = mainDataList.iterator()
        while (iterator.hasNext()) {
            iterator.next()
            iterator.remove()
        }
        binding!!.telMeansOfTransport.setText("")
        binding!!.telTransportDetails.setText("")
        binding!!.tlTransport.isVisible = true

        selectedDay(returnCurrentDayTextView(viewModel.selectedDay), viewModel.selectedDay)
    }

    private fun returnCurrentDayTextView(dayName: String): AppCompatTextView {
        when (dayName) {
            getString(R.string.mo) -> return binding!!.tvMo
            getString(R.string.tu) -> return binding!!.tvTu
            getString(R.string.we) -> return binding!!.tvWe
            getString(R.string.th) -> return binding!!.tvTh
            getString(R.string.fr) -> return binding!!.tvFr
            getString(R.string.sa) -> return binding!!.tvSa
            getString(R.string.su) -> return binding!!.tvSu
        }
        return return binding!!.tvMo
    }

    private fun clearAllData() {
        val iterator = mainDataList.iterator()
        while (iterator.hasNext()) {
            val i = iterator.next()
            if (i.transportDay == viewModel.selectedDay) {
                iterator.remove()
            }
        }
        binding!!.telMeansOfTransport.setText("")
        binding!!.telTransportDetails.setText("")
        binding!!.tlTransport.isVisible = true

        clearSelectedDayData(modeOfTransportList.size)
    }

    private fun clearSelectedDayData(size: Int) {

        if (modeOfTransportList.size > 1) {
            modeOfTransportList.removeAt(modeOfTransportList.size - 1)
            clearSelectedDayData(modeOfTransportList.size)
        } else {
            modeOfTransportList[0].transportMeans = null
            modeOfTransportList[0].transportDetails = null
            modeOfTransportList[0].transportStartAddress = viewModel.fromAddressLocal
            modeOfTransportList[0].transportStopAddress = viewModel.toAddressLocal
            modeOfTransportList[0].transportStartAddressLatitude = viewModel.fromLatitudeLocal
            modeOfTransportList[0].transportStartAddressLongitude = viewModel.fromLongitudeLocal
            modeOfTransportList[0].transportStopAddressLatitude = viewModel.toLatitudeLocal
            modeOfTransportList[0].transportStopAddressLongitude = viewModel.toLongitudeLocal
            modeOfTransportList[0].isShareVisible = false
            modeOfTransportList[0].isShared = false
            modeOfTransportList[0].numberOfPeople = ""
            modeOfTransportList[0].maximumNoOfPeople = 5
        }
        co2EmissionAdapter?.notifyDataSetChanged()
    }


    /* This method is used to set the toolbar in this screen. */
    private fun setToolbar() {
        binding!!.customToolbar.let {
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
    private fun checkBoxProfileAddress() {
        val userData =
            BaseApplication.sharedPreference.getPrefModel(EasyPref.USER_DATA, User::class.java)
        if (userData?.fromAddress?.isEmpty() == true && userData.toAddress.isEmpty()) {
            showErrorMessage(getString(R.string.no_address_in_a_profile_update_your_profile_or_uncheck_the_checkbox))
        } else {
            if (viewModel.isSelectedProfileAddress) {
                binding?.ivCheckboxProfile?.setImageResource(R.drawable.ic_checkbox_selected)
                binding?.ivCheckboxProfile?.imageTickTintCheckBox(
                    BaseApplication.tenantSharedPreference.getTenantPrefModel(
                        EasyPref.TENANT_DATA, TenantInfoModel::class.java
                    )?.brandingInfo?.primaryColor
                )
                binding!!.tvProfileAddress.buttonTextColorText(tenantInfoData?.brandingInfo?.primaryColor)
                binding!!.tvProfileAddress.typeface =
                    ResourcesCompat.getFont(requireContext(), R.font.poppins_medium_500)
                binding!!.telSearchSource.setText(userData?.fromAddress)
                binding!!.telSearchDestination.setText(userData?.toAddress)
                viewModel.fromAddressLocal = userData?.fromAddress.toString()
                viewModel.toAddressLocal = userData?.toAddress.toString()
                viewModel.fromLatitudeLocal = userData?.fromLatitude.toBlankString()
                viewModel.fromLongitudeLocal = userData?.fromLongitude.toBlankString()

                viewModel.toLatitudeLocal = userData?.toLatitude.toBlankString()
                viewModel.toLongitudeLocal = userData?.toLongitude.toBlankString()

                if (modeOfTransportList.isNotEmpty()) {
                    modeOfTransportList[0].transportStartAddress =
                        userData?.fromAddress.toBlankString()
                    modeOfTransportList[0].transportStartAddressLatitude =
                        userData?.fromLatitude.toBlankString()
                    modeOfTransportList[0].transportStartAddressLongitude =
                        userData?.fromLongitude.toBlankString()


                    modeOfTransportList[0].transportStopAddress =
                        userData?.toAddress.toBlankString()
                    modeOfTransportList[0].transportStopAddressLatitude =
                        userData?.toLatitude.toBlankString()
                    modeOfTransportList[0].transportStopAddressLongitude =
                        userData?.toLongitude.toBlankString()


                    co2EmissionAdapter?.notifyDataSetChanged()
                }

            } else {
                binding?.ivCheckboxProfile?.setImageResource(R.drawable.checkbox_unselected)
                context?.getColorFromAttr(R.attr.colorTextView)
                    ?.let { binding?.ivCheckboxProfile?.setColorFilter(it) }
                context?.getColorFromAttr(R.attr.colorCheckBoxProfileAddress)
                    ?.let { binding!!.tvProfileAddress.setTextColor(it) }
                binding!!.tvProfileAddress.typeface =
                    ResourcesCompat.getFont(requireContext(), R.font.poppins_light_300)
                binding!!.telSearchSource.setText("")
                binding!!.telSearchDestination.setText("")
                viewModel.fromLatitudeLocal = ""
                viewModel.fromLongitudeLocal = ""

                viewModel.toLatitudeLocal = ""
                viewModel.toLongitudeLocal = ""

                if (modeOfTransportList.isNotEmpty()) {
                    modeOfTransportList[0].transportStartAddress = ""
                    modeOfTransportList[0].transportStartAddressLatitude = ""
                    modeOfTransportList[0].transportStartAddressLongitude = ""


                    modeOfTransportList[0].transportStopAddress = ""
                    modeOfTransportList[0].transportStopAddressLatitude = ""
                    modeOfTransportList[0].transportStopAddressLongitude = ""


                    co2EmissionAdapter?.notifyDataSetChanged()
                }


            }
        }
        searchIconSetSource()
        searchIconSetDestination()
    }

    private fun showChangeText() {
        if (!binding!!.telSearchSource.text.isNullOrEmpty() && !binding!!.telSearchDestination.text.isNullOrEmpty()) {
            binding!!.tvChange.visibility = View.VISIBLE
            viewModel.isProfileDialogShow = true
        } else {
            binding!!.tvChange.visibility = View.INVISIBLE
        }
    }

    /**
     * This method contains code click for previous day
     *
     */
    private fun checkBoxPreviousDayCalculation() {

        if (!viewModel.isPreviousDaySelected) {
            setNormalButtonUI()
        } else {
            setFillButtonUI()
        }
    }

    private fun setFillButtonUI() {
        binding?.ivPreviousDay?.backgroundTintList =
            (ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorSearchHint)))

        context?.getColorFromAttr(R.attr.colorCheckBoxProfileAddress)
            ?.let { binding!!.tvPreviousDay.setTextColor(it) }
        binding!!.tvPreviousDay.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.poppins_light_300)

    }

    private fun setNormalButtonUI() {
        binding!!.ivPreviousDay.backgroundColorTint(tenantInfoData?.brandingInfo?.primaryColor)
        binding!!.tvPreviousDay.buttonTextColorText(tenantInfoData?.brandingInfo?.primaryColor)
        binding!!.tvPreviousDay.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.poppins_medium_500)

    }

    private fun ui() {
        binding!!.apply {
            if (BaseApplication.themeValue != Configuration.UI_MODE_NIGHT_YES) {
                clWorkingFrom.backgroundTintList =
                    ColorStateList.valueOf(requireContext().getColor(R.color.color_primary_5))
                clTransport.backgroundTintList =
                    ColorStateList.valueOf(requireContext().getColor(R.color.color_primary_5))

            } else {
                clWorkingFrom.backgroundTintList =
                    ColorStateList.valueOf(requireContext().getColor(R.color.color_primary_40))
                clTransport.backgroundTintList =
                    ColorStateList.valueOf(requireContext().getColor(R.color.color_primary_40))
            }
        }
    }

    private fun searchIconSetSource() {
        if (binding!!.telSearchSource.text?.isEmpty() == true) {
            binding!!.telSearchSource.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_search,
                0,
                0,
                0
            )
        } else {
            binding!!.telSearchSource.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
        }
    }

    private fun searchIconSetDestination() {
        if (binding!!.telSearchDestination.text?.isEmpty() == true) {
            binding!!.telSearchDestination.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_search,
                0,
                0,
                0
            )
        } else {
            binding!!.telSearchDestination.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                0,
                0
            )
        }
    }

    private fun initrvData() {
        if (co2EmissionAdapter == null) {
            modeOfTransportList.clear()
            modeOfTransportList.addAll(mainDataList.filter { day -> day.transportDay == viewModel.selectedDay } as ArrayList)
            co2EmissionAdapter = modeOfTransportList.let {
                Co2EmissionAdapter(false, requireContext(), it) { view, model, position ->
                    when (view.id) {
                        R.id.iv_delete -> {
                            DialogUtil.showDialog(
                                parentFragmentManager, getString(R.string.delete_journey),
                                getString(R.string.are_you_sure_want_to_remove_this_journey),
                                getString(
                                    R.string.yes_
                                ), getString(R.string.no),
                                object : DialogUtil.IL {
                                    override fun onSuccess() {
                                        modeOfTransportList.get(position - 1).transportStopAddress =
                                            modeOfTransportList.get(position).transportStopAddress
                                        modeOfTransportList.get(position - 1).transportStopAddressLatitude =
                                            modeOfTransportList.get(position).transportStopAddressLatitude
                                        modeOfTransportList.get(position - 1).transportStopAddressLongitude =
                                            modeOfTransportList.get(position).transportStopAddressLongitude

                                        modeOfTransportList.removeAt(position)
                                        co2EmissionAdapter?.notifyItemRemoved(position)
                                        if (modeOfTransportList.size < 4) {
                                            binding!!.clAddJourney.setVisible(true)
                                        }
                                        //co2EmissionAdapter?.notifyDataSetChanged()
                                    }

                                    override fun onCancel(isNeutral: Boolean) {
                                    }
                                },
                                isCancelShow = false
                            )
                        }

                        R.id.tel_transport_details -> {
                            modelModeOfTransport?.let {
                                if (it.items.size > 0) {
                                    binding?.apply {
                                        val dialog =
                                            TransportDetailBottomSheetFragment.newInstance(it.items)
                                        dialog.sendClickListener = {
                                            //telTransportDetails.setText(it.label)
                                            model.transportDetails = it
                                            model.numberOfPeople = ""
                                           // co2EmissionAdapter?.notifyDataSetChanged()
                                            co2EmissionAdapter?.notifyItemChanged(position)
                                            dialog.dismiss()
                                        }
                                        dialog.show(childFragmentManager, "")
                                    }
                                }
                            }
                        }

                        R.id.meansTransport -> {
                            val dialog = Co2ModeOfTransportBottomSheetFragment.newInstance(
                                co2ModeOfTransportViewModel,
                                modelModeOfTransport
                            )
                            dialog.sendClickListener = {
                                modelModeOfTransport = it
                                model.transportMeans = it
                                modeOfTransportList[position].isShareVisible = it.isSharable
                                modeOfTransportList[position].maximumNoOfPeople = it.noOfPeople

//                                model.isShareVisible = it.isSharable
//                                model.maximumNoOfPeople = it.noOfPeople
                                model.isShared=false
                                Log.e("", "initrvData=====:${it.items.size} ")

                                if (it.items.size > 0) {
                                    //telTransportDetails.alpha = 1f
                                    model.transportDetails = ItemTransportDetails("", "", false)
                                } else {
                                    //model.transportDetails?.label = "-"
                                    model.transportDetails = ItemTransportDetails("-", "-", false)
                                    //model.transportDetails= null
                                }
                                co2EmissionAdapter?.notifyItemChanged(position)
                                dialog.dismiss()
                            }
                            dialog.show(childFragmentManager, "")
                        }

                        R.id.tv_yes -> {
                            if (!model.isShared) {
                                showDialogForSharedProfile(
                                    "yes",
                                    "",
                                    getString(R.string.switching_to_a_shared_ride_mode_will_require_additional_details_e_g_number_of_passengers_do_you_want_to_continue),
                                    position,
                                    model
                                )
                            }
                        }

                        R.id.iv_yes -> {
                            if (!model.isShared) {
                                showDialogForSharedProfile(
                                    "yes",
                                    "",
                                    getString(R.string.switching_to_a_shared_ride_mode_will_require_additional_details_e_g_number_of_passengers_do_you_want_to_continue),
                                    position,
                                    model
                                )
                            }
                        }

                        R.id.tv_no -> {
                            if (model.isShared) {
                                showDialogForSharedProfile(
                                    "no",
                                    "",
                                    getString(R.string.switching_to_a_non_shared_mode_will_remove_shared_ride_details_do_you_want_to_continue),
                                    position,
                                    model
                                )
                            }
                        }

                        R.id.iv_no -> {
                            if (model.isShared) {
                                showDialogForSharedProfile(
                                    "no",
                                    "",
                                    getString(R.string.switching_to_a_non_shared_mode_will_remove_shared_ride_details_do_you_want_to_continue),
                                    position,
                                    model
                                )
                            }
                        }

                        R.id.tel_stop_address -> {
                            viewModel.selectedLocationIndex = position
                            pushFragment(SearchListFragment.newInstance(true, false))
                        }

//                        R.id.tel_no_of_transport -> {
//                            (view as TextInputEditText).addTextChangedListener(object : TextWatcher {
//                                    override fun beforeTextChanged(
//                                        s: CharSequence?,
//                                        start: Int,
//                                        count: Int,
//                                        after: Int
//                                    ) {
//
//                                    }
//
//                                    override fun onTextChanged(
//                                        s: CharSequence?,
//                                        start: Int,
//                                        before: Int,
//                                        count: Int
//                                    ) {
//                                        if (s.toString().isNotEmpty()) {
//                                            if (s.toString().toInt() <= model.maximumNoOfPeople) {
//                                                Log.e("", "onTextChanged: if ${position}")
////                                                (mainDataList.filter { day -> day.transportDay == viewModel.selectedDay } as ArrayList<ModelStops>).mapIndexed { index, modelStops ->
////                                                    if(index==position){
////                                                        modelStops.numberOfPeople = s.toString()
////                                                    }
////                                                }
//
////                                                modeOfTransportList.get(position).numberOfPeople =
////                                                    s.toString()
//                                                model.numberOfPeople = s.toString()
//                                                //co2EmissionAdapter?.notifyItemChanged(position)
//                                            }
//                                            else {
////                                                modeOfTransportList.get(position).numberOfPeople = ""
//                                                model.numberOfPeople=model.maximumNoOfPeople.toString()
//
////                                                Log.e("", "onTextChanged: else")
//                                            }
//                                        }
//                                    }
//
//                                    override fun afterTextChanged(s: Editable?) {
//
//                                    }
//                                }
//                            )
//                            co2EmissionAdapter?.notifyItemChanged(position)
//                        }
                    }
                }
            }
            binding!!.rvData.layoutManager = LinearLayoutManager(activity)
            binding!!.rvData.adapter = co2EmissionAdapter

            //co2EmissionAdapter?.notifyDataSetChanged()

        }
    }

    /* This method is used to set the selected Images of screen. */
    private fun handleSelectedImages(
        isSelected: Boolean,
        image: Int,
        tv: TextView,
        img: AppCompatImageView
    ) {

        if (isSelected) {
            binding?.apply {
                img.setImageResource(image)
                img.imageTickTintCheckBox(
                    BaseApplication.tenantSharedPreference.getTenantPrefModel(
                        EasyPref.TENANT_DATA,
                        TenantInfoModel::class.java
                    )?.brandingInfo?.primaryColor
                )
                tv.buttonTextColorText(tenantInfoData?.brandingInfo?.primaryColor)
                tv.typeface =
                    ResourcesCompat.getFont(requireContext(), R.font.poppins_semi_bold_600)
            }
        } else {
            binding?.apply {
                img.setImageResource(image)
                context?.getColorFromAttr(R.attr.colorTextView)
                    ?.let { img.setColorFilter(it) }
                tv.setTextColor(requireContext().getColorFromAttr(R.attr.colorTextView))
                tv.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_medium_500)
            }
        }
    }

    /* This method is used to update the data based on selected day.*/
    private fun selectedDay(textview: AppCompatTextView, day: String) {
        binding?.apply {
            tvMo.isSelected = false
            tvTu.isSelected = false
            tvWe.isSelected = false
            tvTh.isSelected = false
            tvFr.isSelected = false
            tvSa.isSelected = false
            tvSu.isSelected = false

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

            tvMo.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_unselect_r10)
            tvMo.backgroundTintList =
                ColorStateList.valueOf(requireActivity().getColorFromAttr(R.attr.colorBgUnselectDay))
            tvTu.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_unselect_r10)
            tvTu.backgroundTintList =
                ColorStateList.valueOf(requireActivity().getColorFromAttr(R.attr.colorBgUnselectDay))
            tvWe.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_unselect_r10)
            tvWe.backgroundTintList =
                ColorStateList.valueOf(requireActivity().getColorFromAttr(R.attr.colorBgUnselectDay))
            tvTh.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_unselect_r10)
            tvTh.backgroundTintList =
                ColorStateList.valueOf(requireActivity().getColorFromAttr(R.attr.colorBgUnselectDay))
            tvFr.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_unselect_r10)
            tvFr.backgroundTintList =
                ColorStateList.valueOf(requireActivity().getColorFromAttr(R.attr.colorBgUnselectDay))
            tvSa.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_unselect_r10)
            tvSa.backgroundTintList =
                ColorStateList.valueOf(requireActivity().getColorFromAttr(R.attr.colorBgUnselectDay))
            tvSu.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_unselect_r10)
            tvSu.backgroundTintList =
                ColorStateList.valueOf(requireActivity().getColorFromAttr(R.attr.colorBgUnselectDay))
        }
        isDayFilledData()
        textview.isSelected = true
        textview.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_radius_10)
        textview.backgroundColorTint(
            BaseApplication.tenantSharedPreference.getTenantPrefModel(
                EasyPref.TENANT_DATA,
                TenantInfoModel::class.java
            )?.brandingInfo?.primaryColor
        )
        textview.setTextColor((ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorSelected))))


        viewModel.selectedDay = day

        filterDataDayWise(day)


    }

    private fun filterDataDayWise(dayName: String) {

            modeOfTransportList.clear()
            modeOfTransportList.addAll(mainDataList.filter { day -> day.transportDay == dayName } as ArrayList<ModelStops>)
            if (modeOfTransportList.size == 0) {
                mainDataList.add(
                    ModelStops(
                        dayName,
                        "",
                        "",
                        viewModel.fromAddressLocal,
                        viewModel.toAddressLocal,
                        viewModel.fromLatitudeLocal,
                        viewModel.fromLongitudeLocal,
                        viewModel.toLatitudeLocal, viewModel.toLongitudeLocal,
                        null, null,
                        false,
                        "",
                        isPrefillData = viewModel.isPreviousDaySelected
                    )
                )
                modeOfTransportList.add(
                    ModelStops(
                        dayName,
                        "",
                        "",
                        viewModel.fromAddressLocal,
                        viewModel.toAddressLocal,
                        viewModel.fromLatitudeLocal,
                        viewModel.fromLongitudeLocal,
                        viewModel.toLatitudeLocal, viewModel.toLongitudeLocal,
                        null, null,
                        false,
                        "",
                        isPrefillData = viewModel.isPreviousDaySelected
                    )
                )
            }
            setupUI()
    }

    private fun setupUI() {
       binding!!.btnReset.isVisible=false
        if (modeOfTransportList.size > 0) {
            binding!!.apply {
                if (modeOfTransportList.get(0).workingMode.lowercase() == "office") {
                    rvData.setVisible(true)
                    clAddJourney.setVisible(true)
                    binding!!.btnReset.isVisible=true
                    img.setVisible(false)
                    if (modeOfTransportList.get(0).transportMode.lowercase() == "single") {
                        clTypesTransport.setVisible(true)
                        tlMeansOfTransport.setVisible(true)
                        tlTransport.setVisible(true)
                        if (modeOfTransportList.get(0).transportMeans != null) {
                            telMeansOfTransport.setText(modeOfTransportList.get(0).transportMeans?.label.toString())
                        } else {
                            telMeansOfTransport.setText("")
                        }
                        if (modeOfTransportList.get(0).transportDetails != null) {
                            binding!!.tlTransport.isVisible = true
                            telTransportDetails.setText(modeOfTransportList.get(0).transportDetails?.label.toString())
                        } else {
                            if(modeOfTransportList.get(0).transportMeans!=null && modeOfTransportList.get(0).transportMeans!!.items.isEmpty()) {
                                binding!!.tlTransport.isVisible = false
                                telTransportDetails.setText("")
                            }
                        }
                        handleSelectedImages(
                            false,
                            R.drawable.ic_checkbox_unselected_circle,
                            tvMultiMode,
                            ivMultiModel
                        )
                        handleSelectedImages(
                            true,
                            R.drawable.ic_selected_mode,
                            tvSingleMode,
                            ivSingleMode
                        )
                    } else {
                        tlMeansOfTransport.setVisible(false)
                        tlTransport.setVisible(false)
                        handleSelectedImages(
                            false,
                            R.drawable.ic_checkbox_unselected_circle,
                            tvSingleMode,
                            ivSingleMode
                        )
                        handleSelectedImages(
                            true,
                            R.drawable.ic_selected_mode,
                            tvMultiMode,
                            ivMultiModel
                        )
                    }
                    handleSelectedImages(
                        false,
                        R.drawable.ic_checkbox_unselected_circle,
                        tvInHome,
                        ivCheckboxInHome
                    )
                    handleSelectedImages(
                        true,
                        R.drawable.ic_selected_mode,
                        tvInOffice,
                        ivCheckboxInOffice
                    )

                } else if (modeOfTransportList.get(0).workingMode.lowercase() == "home") {
                    clTypesTransport.setVisible(false)
                    tlMeansOfTransport.setVisible(false)
                    tlTransport.setVisible(false)
                    binding!!.btnReset.isVisible=true
                    rvData.setVisible(false)
                    clAddJourney.setVisible(false)
                    img.setVisible(true)
                    handleSelectedImages(
                        true,
                        R.drawable.ic_selected_mode,
                        tvInHome,
                        ivCheckboxInHome
                    )
                    handleSelectedImages(
                        false,
                        R.drawable.ic_checkbox_unselected_circle,
                        tvInOffice,
                        ivCheckboxInOffice
                    )
                } else {
                    clTypesTransport.setVisible(false)
                    tlMeansOfTransport.setVisible(false)
                    tlTransport.setVisible(false)
                    rvData.setVisible(false)
                    clAddJourney.setVisible(false)
                    img.setVisible(false)
                    handleSelectedImages(
                        false,
                        R.drawable.ic_checkbox_unselected_circle,
                        tvInHome,
                        ivCheckboxInHome
                    )
                    handleSelectedImages(
                        false,
                        R.drawable.ic_checkbox_unselected_circle,
                        tvInOffice,
                        ivCheckboxInOffice
                    )
                }
            }

        } else {
            binding!!.apply {
                rvData.setVisible(false)
                clAddJourney.setVisible(false)
                clTypesTransport.setVisible(false)
                tlMeansOfTransport.setVisible(false)
                tlTransport.setVisible(false)
                handleSelectedImages(
                    false,
                    R.drawable.ic_checkbox_unselected_circle,
                    tvInHome,
                    ivCheckboxInHome
                )
                handleSelectedImages(
                    false,
                    R.drawable.ic_checkbox_unselected_circle,
                    tvInOffice,
                    ivCheckboxInOffice
                )
            }
        }
        co2EmissionAdapter?.notifyDataSetChanged()
    }


    private fun showDialog(type: String, title: String, message: String) {
        DialogUtil.showDialog(
            parentFragmentManager, title, message,
            getString(R.string.yes_), getString(R.string.no), object : DialogUtil.IL {
                override fun onSuccess() {
                    if (type == "office_to_home") {
                        homeUI()
                    } else if (type == "single_to_multi") {
                        clearAllData()
                        multiModeUI()
                    } else if (type == "multi_to_single") {
                        clearAllData()
                        singleModeUI()
                    } else if (type == "changeAddress") {
                        clearAllData()
                        clearMainData()
                        binding!!.tlSearchDestination.isEnabled = true
                        binding!!.tlSearchSource.isEnabled = true

                    }
                }

                override fun onCancel(isNeutral: Boolean) {

                }
            }, isCancelShow = false, isTitleShow = View.GONE
        )
    }

    private fun showDialogForSharedProfile(
        type: String,
        title: String,
        message: String,
        position: Int,
        model: ModelStops
    ) {
        DialogUtil.showDialog(parentFragmentManager, title, message,
            getString(R.string.yes_), getString(R.string.no), object : DialogUtil.IL {
                override fun onSuccess() {
                    if (type == "yes") {
                        model.isShared = true
                        co2EmissionAdapter?.notifyItemChanged(position)
                    } else if (type == "no") {
                        model.isShared = false
                        model.numberOfPeople = ""
                        co2EmissionAdapter?.notifyItemChanged(position)
                    }
                }

                override fun onCancel(isNeutral: Boolean) {

                }
            }, isCancelShow = false, isTitleShow = View.GONE
        )
    }

    private fun homeUI() {
        if (modeOfTransportList.size > 0) {
            modeOfTransportList.get(0).workingMode = "home"
            binding!!.apply {
                clTypesTransport.setVisible(false)
                img.setVisible(true)
                tlMeansOfTransport.setVisible(false)
                tlTransport.setVisible(false)
                rvData.setVisible(false)
                clAddJourney.setVisible(false)
                handleSelectedImages(
                    true,
                    R.drawable.ic_selected_mode,
                    tvInHome,
                    ivCheckboxInHome
                )
                handleSelectedImages(
                    false,
                    R.drawable.ic_checkbox_unselected_circle,
                    tvInOffice,
                    ivCheckboxInOffice
                )
            }
        }
    }

    private fun fetchPreviousDayData() {
        var previousDayName = ""
        val iterator = mainDataList.iterator()
        while (iterator.hasNext()) {
            val i = iterator.next()
            if (i.transportDay == viewModel.selectedDay) {
                iterator.remove()
            }
        }
        when (viewModel.selectedDay) {
            getString(R.string.tu) -> previousDayName = getString(R.string.mo)
            getString(R.string.we) -> previousDayName = getString(R.string.tu)
            getString(R.string.th) -> previousDayName = getString(R.string.we)
            getString(R.string.fr) -> previousDayName = getString(R.string.th)
            getString(R.string.sa) -> previousDayName = getString(R.string.fr)
            getString(R.string.su) -> previousDayName = getString(R.string.sa)

        }
        modeOfTransportList.clear()
        val list = mainDataList.filter { d -> d.transportDay == previousDayName } as ArrayList

        list.forEach { data ->
            val obj = ModelStops()
            obj.transportDay = viewModel.selectedDay
            obj.transportMode = data.transportMode
            obj.transportMeans = data.transportMeans
            obj.isShareVisible = data.isShareVisible
            obj.numberOfPeople = data.numberOfPeople
            obj.maximumNoOfPeople = data.maximumNoOfPeople
            obj.transportStartAddress = data.transportStartAddress
            obj.transportStopAddress = data.transportStopAddress
            obj.transportStartAddressLatitude = data.transportStartAddressLatitude
            obj.transportStartAddressLongitude = data.transportStartAddressLongitude
            obj.transportStopAddressLatitude = data.transportStopAddressLatitude
            obj.transportStopAddressLongitude = data.transportStopAddressLongitude
            obj.isPrefillData = true
            obj.isShared = data.isShared
            obj.transportDetails = data.transportDetails
            obj.workingMode = data.workingMode
            //obj.maximumNoOfPeople = data.maximumNoOfPeople
            modeOfTransportList.add(obj)
        }

        //Log.d("DDDD===","$modeOfTransportList")
        co2EmissionAdapter?.notifyDataSetChanged()
        setupUI()
    }


    private fun checkAllDayData(): Boolean {

        val dayData = ArrayList<String>()
        val mondayData = mainDataList.filter { d -> d.transportDay == getString(R.string.mo) }
        val tuesdayData = mainDataList.filter { d -> d.transportDay == getString(R.string.tu) }
        val wednesdayData = mainDataList.filter { d -> d.transportDay == getString(R.string.we) }
        val thursdayData = mainDataList.filter { d -> d.transportDay == getString(R.string.th) }
        val fridayData = mainDataList.filter { d -> d.transportDay == getString(R.string.fr) }
        val saturdayData = mainDataList.filter { d -> d.transportDay == getString(R.string.sa) }
        val sundayData = mainDataList.filter { d -> d.transportDay == getString(R.string.su) }


        var checkAnyDataFilled = false
        if (mondayData.size > 0) {
            if (!selectedDayValidation(getString(R.string.mo))) {
                checkAnyDataFilled = true
            }
        }
        if (tuesdayData.size > 0) {
            if (!selectedDayValidation(getString(R.string.tu))) {
                checkAnyDataFilled = true
            }
        }

        if (wednesdayData.size > 0) {
            if (!selectedDayValidation(getString(R.string.we))) {
                checkAnyDataFilled = true
            }
        }
        if (thursdayData.size > 0) {
            if (!selectedDayValidation(getString(R.string.th))) {
                checkAnyDataFilled = true

            }
        }
        if (fridayData.size > 0) {
            if (!selectedDayValidation(getString(R.string.fr))) {
                checkAnyDataFilled = true
            }
        }
        if (saturdayData.size > 0) {
            if (!selectedDayValidation(getString(R.string.sa))) {
                checkAnyDataFilled = true
            }
        }
        if (sundayData.size > 0) {
            if (!selectedDayValidation(getString(R.string.su))) {
                checkAnyDataFilled = true
            }
        }

        /*if(checkAnyDataFilled){
            showErrorMessage(getString(R.string.please_ensure_you_provide_data_for_at_least_one_workday_before_submitting_the_survey))
            return false
        }*/


        if (mondayData.size > 0) {
            if (mondayData[0].workingMode == "" && tuesdayData.size == 0 && wednesdayData.size == 0 && thursdayData.size == 0 && fridayData.size == 0
                && saturdayData
                    .size == 0 &&
                sundayData.size == 0
            ) {
                showErrorMessage(getString(R.string.please_ensure_you_provide_data_for_at_least_one_workday_before_submitting_the_survey))
                return false
            }
        }



        if (mondayData.size > 0) {
            if (selectedDayValidation(getString(R.string.mo))) {
            } else {
                dayData.add("Monday")
            }
        }
        if (tuesdayData.size > 0) {
            if (selectedDayValidation(getString(R.string.tu))) {
            } else {
                dayData.add("Tuesday")
            }
        }
        if (wednesdayData.size > 0) {
            if (selectedDayValidation(getString(R.string.we))) {
            } else {
                dayData.add("Wednesday")
            }
        }
        if (thursdayData.size > 0) {
            if (selectedDayValidation(getString(R.string.th))) {
            } else {
                dayData.add("Thursday")
            }
        }
        if (fridayData.size > 0) {
            if (selectedDayValidation(getString(R.string.fr))) {
            } else {
                dayData.add("Friday")
            }
        }
        if (saturdayData.size > 0) {
            if (selectedDayValidation(getString(R.string.sa))) {
            } else {
                dayData.add("Saturday")
            }
        }
        if (sundayData.size > 0) {
            if (selectedDayValidation(getString(R.string.su))) {
            } else {
                dayData.add("Sunday")
            }
        }

        if (dayData.size > 0) {
            var msg = "Please provide the required information for the selected day(s):\n"
            msg += TextUtils.join("\n", dayData);
            showErrorMessage(msg)
            return false
        }
        return true
    }

    private fun checkCurrentDayValidation(): Boolean {
        modeOfTransportList.forEach {
            if (it.workingMode.isEmpty()) {
                return true
            }
            if (it.workingMode.lowercase() == "home") {
                return true
            }
            if (it.transportMode.isEmpty()) {
                showErrorMessage(getString(R.string.select_the_number_of_travel_modes_used_to_reach_the_office))
                return false
            }
            if (it.transportMeans == null || it.transportMeans!!.label == "") {
                showErrorMessage(getString(R.string.please_select_the_mode_of_transport))
                return false
            }
            if (it.transportMode == "multi") {
                if (it.transportDetails == null || it.transportDetails!!.label == "") {
                    showErrorMessage(getString(R.string.please_select_the_transport_detail))
                    return false
                }
            }
            if (it.transportStartAddress.isEmpty()) {
                showErrorMessage(getString(R.string.please_enter_start_address))
                return false
            }
            if (it.transportStopAddress.isEmpty()) {
                showErrorMessage(getString(R.string.please_enter_stop_address))
                return false
            }
            if (it.isShared) {
                if (it.numberOfPeople.isEmpty()) {
                    showErrorMessage(getString(R.string.please_enter_number_of_people))
                    return false
                }
            }
        }
        return true
    }

    private fun selectedDayValidation(dayName: String): Boolean {
        val list = mainDataList.filter { d -> d.transportDay == dayName }
        list.forEach {
            if (it.workingMode.isEmpty()) {
                return true
            }
            if (it.workingMode.lowercase() == "home") {
                return true
            }
            if (it.transportMode.isEmpty()) {
                return false
            }
            if (it.transportMeans == null || it.transportMeans!!.label == "") {
                return false
            }
            if (it.transportMode == "multi") {
                if (it.transportDetails == null || it.transportDetails!!.label == "") {
                    return false
                }

            } else {
                if (it.transportDetails == null) {
                    if(it.transportMeans?.label!=null && it.transportMeans?.items!!.isNotEmpty()){
                        return false
                    }

                }
            }
            if (it.transportStartAddress.isEmpty()) {
                return false
            }
            if (it.transportStopAddress.isEmpty()) {
                return false
            }
            if (it.isShared) {
                if (it.numberOfPeople.isEmpty()) {
                    return false
                }
            }
        }
        return true
    }


    private fun checkDataValidation(): Boolean {
        if (modeOfTransportList.size > 0) {
            if (modeOfTransportList[0].transportMode.lowercase() == "single") {
                if (modeOfTransportList[0].transportMeans == null) {
                    showErrorMessage(getString(R.string.please_select_the_mode_of_transport))
                    return false
                }
                if (binding!!.telTransportDetails.text.isNullOrEmpty()) {
                    showErrorMessage(getString(R.string.please_select_the_transport_detail))
                    return false
                }

                for (i in modeOfTransportList) {
                    if (i.transportStartAddress.isEmpty()) {
                        showErrorMessage(getString(R.string.please_enter_source_address))
                        return false
                    }
                    if (i.transportStopAddress.isEmpty()) {
                        showErrorMessage(getString(R.string.please_enter_destination_address))
                        return false
                    }
                    if (i.isShareVisible && i.isShared) {
                        if (i.numberOfPeople.isEmpty()) {
                            showErrorMessage(getString(R.string.please_enter_number_of_people))
                            return false
                        }
                    }
                }

            } else {
                for (i in modeOfTransportList) {
                    if (i.transportMeans == null) {
                        showErrorMessage(getString(R.string.please_select_a_transport_mode))
                        return false
                    }
                    if (i.transportDetails == null) {
                        showErrorMessage(getString(R.string.please_select_transport_details))
                        return false
                    }
                    if (i.transportStartAddress.isEmpty()) {
                        showErrorMessage(getString(R.string.please_enter_source_address))
                        return false
                    }
                    if (i.transportStopAddress.isEmpty()) {
                        showErrorMessage(getString(R.string.please_enter_destination_address))
                        return false
                    }
                    if (i.isShareVisible && i.isShared) {
                        if (i.numberOfPeople.isEmpty()) {
                            showErrorMessage(getString(R.string.please_enter_number_of_people))
                            return false
                        }
                    }
                }
            }

        }
        return true
    }

    private fun isDayFilledData() {
        viewModel.dayList.clear()
        mainDataList.forEach {
            if (it.workingMode != "") viewModel.dayList.add(it.transportDay)
        }

        if (viewModel.dayList.contains(getString(R.string.mo))) {
            binding!!.apply {
                tvMo.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_filled)
                tvMo.backgroundTintList = ColorStateList.valueOf(
                    requireActivity().getColorFromAttr(
                        com.google.android.material.R.attr.colorSecondary
                    )
                )
            }
        }
        if (viewModel.dayList.contains(getString(R.string.tu))) {
            binding!!.apply {
                tvTu.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_filled)
                tvTu.backgroundTintList = ColorStateList.valueOf(
                    requireActivity().getColorFromAttr(
                        com.google.android.material.R.attr.colorSecondary
                    )
                )
            }
        }
        if (viewModel.dayList.contains(getString(R.string.we))) {
            binding!!.apply {
                tvWe.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_filled)
                tvWe.backgroundTintList = ColorStateList.valueOf(
                    requireActivity().getColorFromAttr(
                        com.google.android.material.R.attr.colorSecondary
                    )
                )
            }
        }
        if (viewModel.dayList.contains(getString(R.string.th))) {
            binding!!.apply {
                tvTh.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_filled)
                tvTh.backgroundTintList = ColorStateList.valueOf(
                    requireActivity().getColorFromAttr(
                        com.google.android.material.R.attr.colorSecondary
                    )
                )
            }
        }
        if (viewModel.dayList.contains(getString(R.string.fr))) {
            binding!!.apply {
                tvFr.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_filled)
                tvFr.backgroundTintList = ColorStateList.valueOf(
                    requireActivity().getColorFromAttr(
                        com.google.android.material.R.attr.colorSecondary
                    )
                )
            }
        }
        if (viewModel.dayList.contains(getString(R.string.sa))) {
            binding!!.apply {
                tvSa.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_filled)
                tvSa.backgroundTintList = ColorStateList.valueOf(
                    requireActivity().getColorFromAttr(
                        com.google.android.material.R.attr.colorSecondary
                    )
                )
            }
        }
        if (viewModel.dayList.contains(getString(R.string.su))) {
            binding!!.apply {
                tvSu.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_filled)
                tvSu.backgroundTintList = ColorStateList.valueOf(
                    requireActivity().getColorFromAttr(
                        com.google.android.material.R.attr.colorSecondary
                    )
                )
            }
        }
    }


}