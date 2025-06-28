package de.fast2work.mobility.ui.co2

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.ItemTransportDetails
import de.fast2work.mobility.data.response.ModeOfTransport
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.databinding.FragmentCalculateCo21Binding
import de.fast2work.mobility.db.ModelStops
import de.fast2work.mobility.ui.co2.adapter.Co2EmissionAdapter
import de.fast2work.mobility.ui.co2.bottom.Co2ModeOfTransportViewModel
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.extension.backgroundColorTint
import de.fast2work.mobility.utility.extension.buttonTextColorText
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.imageTickTintCheckBox
import de.fast2work.mobility.utility.extension.setVisible
import de.fast2work.mobility.utility.preference.EasyPref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


class ViewSurveyFragment :
    BaseVMBindingFragment<FragmentCalculateCo21Binding, CalculateCo21ViewModel>(
        CalculateCo21ViewModel::class.java
    ) {
    private var co2EmissionAdapter: Co2EmissionAdapter? = null
    private var mainDataList: ArrayList<ModelStops> = arrayListOf()
    private var modeOfTransportList: ArrayList<ModelStops> = arrayListOf()

    private var modelModeOfTransport: ModeOfTransport? = null
    private val co2ModeOfTransportViewModel: Co2ModeOfTransportViewModel by viewModels()
    private var surveyName: String = ""

    companion object {
        const val PARAM_SURVEY_ID = "SurveyId"
        const val PARAM_SURVEY_NAME = "SurvyName"
        fun newInstance(surveyId: Int = 0, surveyName: String) = ViewSurveyFragment().apply {
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

    }

    override fun attachObservers() {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getStoredUserSurveyListLiveData.collect {
                //binding!!.clMain.visibility = View.VISIBLE
                if (it.isSuccess) {
                    //Log.e("DATA===",it.data.toString())
                    it.data?.let { dayData ->
                        viewModel.fromAddressLocal = dayData.fromAddress
                        viewModel.fromLatitudeLocal = dayData.fromLatitude
                        viewModel.fromLongitudeLocal = dayData.fromLongitude
                        viewModel.toAddressLocal = dayData.toAddress
                        viewModel.toLatitudeLocal = dayData.toLatitude
                        viewModel.toLongitudeLocal = dayData.toLongitude

                        binding!!.telSearchSource.setText(viewModel.fromAddressLocal)
                        binding!!.telSearchDestination.setText(viewModel.toAddressLocal)

                        if (dayData != null)
                            dayData.dayData.forEach { day ->
                                if (day.journeyData.isNotEmpty()) viewModel.dayList.add(
                                    viewModel.getDayName(
                                        day.day
                                    )
                                )
                                day.journeyData.forEach { journey ->
                                    val objJourney = ModelStops(
                                        transportDay = viewModel.getDayName(day.day),
                                        workingMode = day.workFrom,
                                        transportMode = if (day.transportType != null) day.transportType.toString() else "",
                                        transportStartAddress = if (journey.sourceAddress != null) journey.sourceAddress.toString() else "",
                                        transportStopAddress = if (journey.destinationAddress != null) journey.destinationAddress.toString() else "",

                                        transportStartAddressLatitude = if (journey.sourcelatitude != null) journey.sourcelatitude.toString() else "",

                                        transportStartAddressLongitude = if (journey.sourcelongitude != null) journey.sourcelongitude.toString() else "",

                                        transportStopAddressLatitude = if (journey.destinationlatitude != null) journey.destinationlatitude.toString() else "",

                                        transportStopAddressLongitude = if (journey.destinationlongitude != null) journey.destinationlongitude.toString() else "",

                                        transportMeans = ModeOfTransport(
                                            arrayListOf(),
                                            key = journey.transportMode,
                                            label = getValueFromKey(journey.transportMode,0).toString()
                                        ),
                                        transportDetails = ItemTransportDetails(
                                            key = journey.transportChildMode,
                                            label = if (journey.transportChildMode.isNotEmpty() && !journey.transportChildMode.equals("-")) getValueFromKey(journey.transportChildMode,1).toString() else "-"
                                        ),
                                        isShared = journey.isShared,
                                        numberOfPeople = journey.noOfPeople.toString(),
                                        isShareVisible = journey.isShared,
                                        maximumNoOfPeople = journey.noOfPeople,
                                        isPrefillData = false
                                    )
                                    mainDataList.add(objJourney)
                                    Log.e("", "attachObservers: ==== ${mainDataList}")
                                }
                            }

                        selectedDay(binding!!.tvMo, "Mo")
                    }
                }
            }
        }
    }

    private fun getValueFromKey(key: String, item:Int): String {
        val transportDataJsonArray = """
[
  {
    "key": "bike",
    "label": "Bike",
    "items": [],
    "icon": "https://static.biketour.guide/static/steigum/images/mbg_icons/bike.svg?v=a4999ef",
    "sharable": false,
    "noOfPeople": 0
  },
  {
    "key": "car",
    "label": "Car",
    "items": [
      {
        "key": "small",
        "label": "Small",
        "icon": "https://static.biketour.guide/static/steigum/images/mbg_icons/small_car.svg?v=a4999ef"
      },
      {
        "key": "medium",
        "label": "Medium",
        "icon": "https://static.biketour.guide/static/steigum/images/mbg_icons/medium_car.svg?v=a4999ef"
      },
      {
        "key": "large",
        "label": "Large",
        "icon": "https://static.biketour.guide/static/steigum/images/mbg_icons/large_car.svg?v=a4999ef"
      }
    ],
    "sharable": true,
    "noOfPeople": 5
  },
  {
    "key": "electric_car",
    "label": "Electric Car",
    "items": [],
    "icon": "https://static.biketour.guide/static/steigum/images/mbg_icons/electric_car.svg?v=a4999ef",
    "sharable": true,
    "noOfPeople": 5
  },
  {
    "key": "hybrid_car",
    "label": "Hybrid Car",
    "items": [],
    "icon": "https://static.biketour.guide/static/steigum/images/mbg_icons/hybrid_car.svg?v=a4999ef",
    "sharable": true,
    "noOfPeople": 5
  },
  {
    "key": "train",
    "label": "Train",
    "items": [
      {
        "key": "ic_ice",
        "label": "ic & ice",
        "icon": "https://static.biketour.guide/static/steigum/images/mbg_icons/train_ic_ice.svg?v=a4999ef"
      },
      {
        "key": "rb_re",
        "label": "rb & re",
        "icon": "https://static.biketour.guide/static/steigum/images/mbg_icons/train_rb_re.svg?v=a4999ef"
      },
      {
        "key": "sb",
        "label": "sb",
        "icon": "https://static.biketour.guide/static/steigum/images/mbg_icons/sb_train.svg?v=a4999ef"
      }
    ],
    "sharable": false,
    "noOfPeople": 0
  },
  {
    "key": "subway",
    "label": "Subway",
    "items": [],
    "icon": "https://static.biketour.guide/static/steigum/images/mbg_icons/subway.svg?v=a4999ef",
    "sharable": false,
    "noOfPeople": 0
  },
  {
    "key": "walking",
    "label": "Walking",
    "items": [],
    "icon": "https://static.biketour.guide/static/steigum/images/mbg_icons/walking.svg?v=a4999ef",
    "sharable": false,
    "noOfPeople": 0
  },
  {
    "key": "bus",
    "label": "Bus",
    "items": [
      {
        "key": "nahverkehr",
        "label": "nahverkehr",
        "icon": "https://static.biketour.guide/static/steigum/images/mbg_icons/bus_local_traffic.svg?v=a4999ef"
      },
      {
        "key": "fernverkehr",
        "label": "fernverkehr",
        "icon": "https://static.biketour.guide/static/steigum/images/mbg_icons/bus_long_distance_traffic.svg?v=a4999ef"
      }
    ],
    "sharable": false,
    "noOfPeople": 0
  },
  {
    "key": "scooter",
    "label": "Scooter",
    "items": [
      {
        "key": "electric",
        "label": "Eletric",
        "icon": "https://static.biketour.guide/static/steigum/images/mbg_icons/scooter_electric.svg?v=a4999ef"
      },
      {
        "key": "fuel",
        "label": "Fuel",
        "icon": "https://static.biketour.guide/static/steigum/images/mbg_icons/scooter.svg?v=a4999ef"
      }
    ],
    "sharable": true,
    "noOfPeople": 2
  },
  {
    "key": "plane",
    "label": "Plane",
    "items": [],
    "icon": "https://static.biketour.guide/static/steigum/images/mbg_icons/plane.svg?v=a4999ef",
    "sharable": false,
    "noOfPeople": 0
  },
  {
    "key": "ferry",
    "label": "Ferry",
    "items": [],
    "icon": "https://static.biketour.guide/static/steigum/images/mbg_icons/ferry.svg?v=a4999ef",
    "sharable": false,
    "noOfPeople": 0
  }
]
""".trimIndent()
        val jsonArray = JSONArray(transportDataJsonArray)


       var data:String = ""
        if(item==0) {
         data = findByKey(jsonArray, key)
        }else{
            data = findByInnerKey(jsonArray, key)
        }

        return  data
    }

    private fun findByKey(jsonArray: JSONArray, searchKey: String): String {
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            if (obj.getString("key") == searchKey) {
                return obj.getString("label")
            }
        }
        return searchKey // not found
    }

    private fun findByInnerKey(jsonArray: JSONArray, searchKey: String): String {
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)

                for (j in 0 until obj.getJSONArray("items").length()) {
                    val objInner = obj.getJSONArray("items").getJSONObject(j)
                    if (objInner.getString("key") == searchKey) {
                        return objInner.getString("label")
                    }
            }
        }
        return searchKey // not found
    }

    override fun onStart() {
        super.onStart()
        ui()
        hideTabs()
    }

    override fun initComponents() {
        viewModel.surveyIdLocal = arguments?.getInt(PARAM_SURVEY_ID) ?: 0
        surveyName = arguments?.getString(PARAM_SURVEY_NAME).toString()
        if (isFirstTimeLoad) {
            setToolbar()
            searchIconSetSource()
            searchIconSetDestination()
            setupUIForViewSurvey()
            viewModel.callGetStoredUserSurveyListApi(viewModel.surveyIdLocal)
            initrvData()
            selectedDay(binding!!.tvMo, "Mo")
        }
    }

    private fun setupUIForViewSurvey() {
        binding!!.apply {
            tvTitle.setText(surveyName)
            tvDes.setVisible(false)
            ivEye.setVisible(false)
            btnSubmit.setVisible(false)
            ivCheckboxProfile.setVisible(false)
            tvProfileAddress.setVisible(false)
            cvSource.isEnabled = false
            cvDestination.isEnabled = false
            clAddJourney.setVisible(false)
            clTypesTransport.setVisible(true)

            tlSearchDestination.isEnabled = false
            tlSearchSource.isEnabled = false
            telMeansOfTransport.isEnabled = false
            telTransportDetails.isEnabled = false
            telTransportDetails.alpha = 1f
        }
    }

    private fun isDayFilledData() {
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
        if (viewModel.dayList.contains(getString(R.string.tu)) || viewModel.dayList.contains("Tu")) {
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
        if (viewModel.dayList.contains(getString(R.string.we)) || viewModel.dayList.contains("We")) {
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
        if (viewModel.dayList.contains(getString(R.string.th)) || viewModel.dayList.contains("Th")) {
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
        if (viewModel.dayList.contains(getString(R.string.fr)) || viewModel.dayList.contains("Fr")) {
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
        if (viewModel.dayList.contains(getString(R.string.sa)) || viewModel.dayList.contains("Sa")) {
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
        if (viewModel.dayList.contains(getString(R.string.su)) || viewModel.dayList.contains("Su")) {
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


    override fun setClickListener() {
        binding?.apply {

            tvMo.clickWithDebounce {
                //deleteOldData()
                selectedDay(binding!!.tvMo, getString(R.string.mo))
            }
            tvTu.clickWithDebounce {
                ///deleteOldData()
                selectedDay(binding!!.tvTu, getString(R.string.tu))

            }
            tvWe.clickWithDebounce {
                //deleteOldData()
                selectedDay(binding!!.tvWe, getString(R.string.we))
            }
            tvTh.clickWithDebounce {
                //deleteOldData()
                selectedDay(binding!!.tvTh, getString(R.string.th))

            }
            tvFr.clickWithDebounce {
                //deleteOldData()
                selectedDay(binding!!.tvFr, getString(R.string.fr))

            }
            tvSa.clickWithDebounce {
                //deleteOldData()
                selectedDay(binding!!.tvSa, getString(R.string.sa))
            }
            tvSu.clickWithDebounce {
                //deleteOldData()
                selectedDay(binding!!.tvSu, getString(R.string.su))
            }

        }
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
            modeOfTransportList.addAll(mainDataList.filter { day -> day.transportDay == viewModel.selectedDay } as ArrayList)
            co2EmissionAdapter = modeOfTransportList.let {
                Co2EmissionAdapter(true, requireContext(), it) { view, model, position ->

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


            tvMo.setTextColor(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorUnSelected)))
            tvTu.setTextColor(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorUnSelected)))
            tvWe.setTextColor(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorUnSelected)))
            tvTh.setTextColor(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorUnSelected)))
            tvFr.setTextColor(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorUnSelected)))
            tvSa.setTextColor(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorUnSelected)))
            tvSu.setTextColor(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorUnSelected)))

//            tvMo.background=ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_select_unselect)
            tvMo.backgroundTintList =
                ColorStateList.valueOf(requireActivity().getColorFromAttr(R.attr.colorBgUnselectDay))
//            tvTu.background=ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_unselect_r10)
            tvTu.backgroundTintList =
                ColorStateList.valueOf(requireActivity().getColorFromAttr(R.attr.colorBgUnselectDay))
//            tvWe.background=ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_unselect_r10)
            tvWe.backgroundTintList =
                ColorStateList.valueOf(requireActivity().getColorFromAttr(R.attr.colorBgUnselectDay))
//            tvTh.background=ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_unselect_r10)
            tvTh.backgroundTintList =
                ColorStateList.valueOf(requireActivity().getColorFromAttr(R.attr.colorBgUnselectDay))
//            tvFr.background=ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_unselect_r10)
            tvFr.backgroundTintList =
                ColorStateList.valueOf(requireActivity().getColorFromAttr(R.attr.colorBgUnselectDay))
//            tvSa.background=ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_unselect_r10)
            tvSa.backgroundTintList =
                ColorStateList.valueOf(requireActivity().getColorFromAttr(R.attr.colorBgUnselectDay))
//            tvSu.background=ContextCompat.getDrawable(requireContext(), R.drawable.bg_day_unselect_r10)
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

    private fun ui() {
        binding!!.apply {
            if (BaseApplication.themeValue != Configuration.UI_MODE_NIGHT_YES) {
                clWorkingFrom.backgroundTintList =
                    ColorStateList.valueOf(requireContext().getColor(R.color.color_primary_5))
                clTransport.backgroundTintList =
                    ColorStateList.valueOf(requireContext().getColor(R.color.color_primary_5))

            } else {
                clTransport.backgroundTintList =
                    ColorStateList.valueOf(requireContext().getColor(R.color.color_primary_40))

                clWorkingFrom.backgroundTintList =
                    ColorStateList.valueOf(requireContext().getColor(R.color.color_primary_40))
            }
        }
    }

    private fun filterDataDayWise(dayName: String) {
        modeOfTransportList.clear()
        modeOfTransportList.addAll(mainDataList.filter { day -> day.transportDay == dayName } as ArrayList<ModelStops>)
        setupUI()

    }

    private fun setupUI() {
        if (modeOfTransportList.size > 0) {
            binding!!.apply {
                if (modeOfTransportList.get(0).workingMode.lowercase() == "office") {
                    rvData.setVisible(true)
                    clAddJourney.setVisible(false)
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
                            if (modeOfTransportList.get(0).transportDetails?.label == "-") {
                                tlTransport.setVisible(false)
                            }
                            telTransportDetails.setText(modeOfTransportList.get(0).transportDetails?.label.toString())
                        } else {
                            telTransportDetails.setText("")
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
                        clTypesTransport.setVisible(true)
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
                    homeUI()
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

}