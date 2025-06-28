package de.fast2work.mobility.ui.savingandrecpit.co2receipt

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat.recreate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import de.fast2work.mobility.R
import de.fast2work.mobility.data.request.ChartReq
import de.fast2work.mobility.data.request.StaticPageReq
import de.fast2work.mobility.data.response.KmsVehicleTypeRes
import de.fast2work.mobility.data.response.MonthWiseEmissionsRes
import de.fast2work.mobility.data.response.UniqueVehiclesRes
import de.fast2work.mobility.data.response.User
import de.fast2work.mobility.data.response.YearlyCo2TrendRes
import de.fast2work.mobility.databinding.FragmentCo2ReceiptBinding
import de.fast2work.mobility.ui.chart.Co2EmissionChartFragment
import de.fast2work.mobility.ui.chart.EmissionChartFragment
import de.fast2work.mobility.ui.chart.KilometerChartFragment
import de.fast2work.mobility.ui.chart.LastTwoYearMonthChartFragment
import de.fast2work.mobility.ui.chart.LastTwoYearVehicleTypeFragment
import de.fast2work.mobility.ui.chart.MonthlySummaryFragment
import de.fast2work.mobility.ui.chart.adapter.KmsVehicleTypeAdapter
import de.fast2work.mobility.ui.chart.adapter.YearlyCo2TrendAdapter
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.dashboard.INDEX_CENTER_CO2
import de.fast2work.mobility.ui.dashboard.INDEX_CONTACT_CO2
import de.fast2work.mobility.ui.profile.bottom.PreferredLanguageBottomSheetFragment
import de.fast2work.mobility.ui.sidemenu.staticpage.StaticPageActivity
import de.fast2work.mobility.utility.extension.MM_FORMAT
import de.fast2work.mobility.utility.extension.SERVER_FORMAT
import de.fast2work.mobility.utility.extension.YYYY_FORMAT
import de.fast2work.mobility.utility.extension.displayDate
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.preference.EasyPref
import de.fast2work.mobility.utility.util.LocalConfig
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.log


class Co2ReceiptFragment : BaseVMBindingFragment<FragmentCo2ReceiptBinding, Co2ReceiptViewModel>(
    Co2ReceiptViewModel::class.java
) {

    var kmsVehicleTypeAdapter: KmsVehicleTypeAdapter? = null
    var yearlyCo2TrendAdapter: YearlyCo2TrendAdapter? = null
    val kmsVehicleTypeList: ArrayList<KmsVehicleTypeRes> = arrayListOf()
    val yearlyCo2TrendList: ArrayList<YearlyCo2TrendRes> = arrayListOf()


    val trendList: ArrayList<MonthWiseEmissionsRes> = arrayListOf()
    companion object {
        const val SELECT_TYPE = ""

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return generateBinding(FragmentCo2ReceiptBinding.inflate(inflater), container)
    }

    override fun attachObservers() {
        viewModel.co2ReceiptLiveData.observe(this) {
            Log.e("", "attachObservers:${it.data} ")
            if (it.data?.yearlyCo2Trend?.isNotEmpty() == true) {
                yearlyCo2TrendList.clear()
                binding!!.llYearlyTrend.isVisible = true
                binding!!.clYearlyTrendNoData.isVisible = false
                it.data?.yearlyCo2Trend?.let { it1 -> yearlyCo2TrendList.addAll(it1) }
                yearlyCo2TrendAdapter?.notifyDataSetChanged()
            } else {
                binding!!.llYearlyTrend.isVisible = false
                binding!!.clYearlyTrendNoData.isVisible = true
            }
            viewModel.uniqueVehiclesList.clear()
            it.data?.uniqueVehicles?.let { it1 -> viewModel.uniqueVehiclesList.addAll(it1) }
            if (it.data?.emissionsByVehicleType?.isNotEmpty() == true) {
                binding!!.llEmission.isVisible = true
                binding!!.clEmissionNoData.isVisible = false
            } else {
                binding!!.llEmission.isVisible = false
                binding!!.clEmissionNoData.isVisible = true
            }
            if (it.data?.kmsByVehicleType?.isNotEmpty() == true) {
                binding!!.llKilometer.isVisible = true
                binding!!.clKilometerNoData.isVisible = false
                kmsVehicleTypeList.clear()
                it.data?.kmsByVehicleType?.let { it1 -> kmsVehicleTypeList.addAll(it1) }
                kmsVehicleTypeAdapter?.notifyDataSetChanged()
            } else {
                binding!!.llKilometer.isVisible = false
                binding!!.clKilometerNoData.isVisible = true
            }

            if (it.data?.emissionsByMonthVehicle?.isNotEmpty()==true){
                binding!!.llCo2Emission.isVisible = true
                binding!!.clCo2EmissionNoData.isVisible = false
            }else{
                binding!!.llCo2Emission.isVisible = false
                binding!!.clCo2EmissionNoData.isVisible = true
            }
            if (it.data?.lastYearComparisonByMonth?.isNotEmpty()==true){
                binding!!.llLast2YMonth.isVisible = true
                binding!!.clLast2YMonthNoData.isVisible = false
            }else{
                binding!!.llLast2YMonth.isVisible = false
                binding!!.clLast2YMonthNoData.isVisible = true
            }

            if (it.data?.lastYearComparisonByVehicle?.isNotEmpty()==true){
                binding!!.llLast2YVehicle.isVisible = true
                binding!!.clLast2YVehicleNoData.isVisible = false
            }else{
                binding!!.llLast2YVehicle.isVisible = false
                binding!!.clLast2YVehicleNoData.isVisible = true
            }
            if (it.data?.yearlyCo2Trend?.isNotEmpty()==true){
                binding!!.llMonthlySummary.isVisible = true
                binding!!.clMonthlySummaryNoData.isVisible = false
            }else{
                binding!!.llMonthlySummary.isVisible = false
                binding!!.clMonthlySummaryNoData.isVisible = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.selectedYear==0) {
            viewModel.selectedYear = Calendar.getInstance().get(Calendar.YEAR)
        }
        binding!!.tvCalendar.text = viewModel.selectedYear.toString()

        if (viewModel.selectedName.isEmpty()){
            viewModel.selectedName="all"
            binding!!.tvAllTypes.text= getString(R.string.all_types)

        }else{
            //binding!!.tvAllTypes.text= viewModel.selectedName
        }
        if (viewModel.selectedNameModes.isEmpty()){
            viewModel.selectedNameModes="all"
            binding!!.tvAllModes.text= getString(R.string.all_modes)
        }else{
            //binding!!.tvAllModes.text= viewModel.selectedNameModes
        }

      reLoad()
    }

    private fun reLoad() {
        val userData = BaseApplication.sharedPreference.getPrefModel(EasyPref.USER_DATA, User::class.java)

        if (userData?.lockCo2ReceiptDashboard==true) {
            binding!!.clLockChart.isVisible = true
            binding!!.llChart.isVisible = false
            binding!!.swipeRefresh.isEnabled = false
            if (tenantInfoData?.tenantInfo?.enabledServices.equals(LocalConfig.co2_management, true)) {
                binding!!.tvDes.text=getString(R.string.commuter_mobility_also_has_an_impact_on_our_carbon_footprint_support_us_with_your_participation_let_s_make_our_emissions_visible_together)
                binding!!.tvDes1.text=getString(R.string.currently_released_your_co2_survey)
                binding!!.tvDes1.isVisible=true
            } else {
                binding!!.tvDes.text=
                    getString(R.string.that_you_are_interested_in_your_co2_receipts_currently_the_receipts_dashboard_about_your_emissions_is_not_released_you_can_contact_your_admin_for_more_details)
                binding!!.tvDes1.isVisible=false
            }
        }else{
            binding!!.clLockChart.isVisible = false
            binding!!.llChart.isVisible = true
            binding!!.swipeRefresh.isEnabled = false

            val chartParam = ChartReq().apply {
                this.year= viewModel.selectedYear
                this.travelType = viewModel.selectedName
                this.vehicleType = viewModel.selectedNameModes
            }
            viewModel.callChartCo2Api(chartParam, true)
            Log.e("", "initComponents=======: rahul", )

           // viewModel.callChartCo2Api(chartParam, true)

            loadFragEmissions(viewModel.selectedYear?.let { EmissionChartFragment.newInstance(it,viewModel.selectedName,viewModel.selectedNameModes) }, 0)
            Handler(Looper.getMainLooper()).postDelayed({
                loadFragKilometer(viewModel.selectedYear?.let { KilometerChartFragment.newInstance(it,viewModel.selectedName,viewModel.selectedNameModes) }, 0)
            }, 500)
            Handler(Looper.getMainLooper()).postDelayed({
                loadFragCo2Emiss(viewModel.selectedYear?.let { Co2EmissionChartFragment.newInstance(it,viewModel.selectedName,viewModel.selectedNameModes) }, 0)

            }, 500)

            Handler(Looper.getMainLooper()).postDelayed({
                loadFragLastTwoYearMonth(viewModel.selectedYear?.let {
                    LastTwoYearMonthChartFragment.newInstance(
                        it,viewModel.selectedName,viewModel.selectedNameModes)
                }, 0)
            }, 500)
            Handler(Looper.getMainLooper()).postDelayed({
                loadFragLastTwoYearVehicleType(viewModel.selectedYear?.let {
                    LastTwoYearVehicleTypeFragment.newInstance(
                        it,viewModel.selectedName,viewModel.selectedNameModes)
                }, 0)
            }, 500)
            Handler(Looper.getMainLooper()).postDelayed({
                loadFragMonthlySummary(viewModel.selectedYear?.let {
                    MonthlySummaryFragment.newInstance(
                        it,viewModel.selectedName,viewModel.selectedNameModes)
                }, 0)
            }, 500)
            initRecyclerViewKms()
            initRecyclerViewYearlyTrend()

        }
    }


    override fun initComponents() {

        setBoldAndColorSpannableCo2EmissionByMonthByVehicleType(binding!!.tvCo2Emi,getString(R.string.co2_emissions))
        setBoldAndColorSpannableLast2YearCo2EmissionByMonth(binding!!.tvLastTwoYearMonth,getString(R.string.last_2_years))
        setBoldAndColorSpannableLast2YearCo2EmissionByVehicleType(binding!!.tvLastTwoYearVehicle,getString(R.string.last_2_years))
        setBoldAndColorSpannableCo2EmissionByVehicleType(binding!!.tvEss,getString(R.string.co2_emissions))

        binding?.btnContactAdmin?.backgroundTintList = ColorStateList.valueOf(Color.parseColor(tenantInfoData?.brandingInfo?.primaryColor.toBlankString()))

        /*  kmsVehicleTypeList.add(KmsVehicleTypeRes("car","small","car",89,"#FFC540"))
          kmsVehicleTypeList.add(KmsVehicleTypeRes("car","small","bus",60,"#3DD79A"))
          kmsVehicleTypeList.add(KmsVehicleTypeRes("car","small","train",30,"#53B4FE"))
          kmsVehicleTypeList.add(KmsVehicleTypeRes("car","small","flight",30,"#5084D3"))
          kmsVehicleTypeList.add(KmsVehicleTypeRes("car","small","cycle",30,"#EE9250"))
          kmsVehicleTypeList.add(KmsVehicleTypeRes("car","small","cycle",30,"#FFC540"))
          kmsVehicleTypeList.add(KmsVehicleTypeRes("car","small","cycle",3,"#49C087"))
          kmsVehicleTypeList.add(KmsVehicleTypeRes("car","small","cycle",30,"#FFC540"))
          kmsVehicleTypeList.add(KmsVehicleTypeRes("car","small","cycle",10,"#FFC540"))
          kmsVehicleTypeList.add(KmsVehicleTypeRes("car","small","cycle",30,"#49C087"))
          kmsVehicleTypeList.add(KmsVehicleTypeRes("car","small","cycle",5,"#49C087"))*/


        /*       trendList.add(MonthWiseEmissionsRes(1,"jan",25.6))
               trendList.add(MonthWiseEmissionsRes(2,"feb",0.0))
               trendList.add(MonthWiseEmissionsRes(3,"mar",60.0))
               trendList.add(MonthWiseEmissionsRes(4,"apr",70.6))
               trendList.add(MonthWiseEmissionsRes(5,"may",50.6))
               trendList.add(MonthWiseEmissionsRes(6,"jun",40.6))
               trendList.add(MonthWiseEmissionsRes(7,"jun",40.6))
               trendList.add(MonthWiseEmissionsRes(8,"jun",0.0))
               trendList.add(MonthWiseEmissionsRes(9,"jun",0.0))
               trendList.add(MonthWiseEmissionsRes(10,"jun",1.0))
               trendList.add(MonthWiseEmissionsRes(11,"jun",40.6))
               trendList.add(MonthWiseEmissionsRes(12,"jun",40.6))


               yearlyCo2TrendList.add(YearlyCo2TrendRes("car","small","car",89.0,"#FFC540", monthWiseEmissions =trendList ))
               yearlyCo2TrendList.add(YearlyCo2TrendRes("car","small","car",89.0,"#3DD79A",trendList))
               yearlyCo2TrendAdapter?.notifyDataSetChanged()*/

    }

    override fun setClickListener() {
        binding?.apply {
            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                reLoad()
            }
            btnContactAdmin.clickWithDebounce {
                switchTab(INDEX_CONTACT_CO2)
            }

            tvCalendar.setOnClickListener {
                val pd = de.fast2work.mobility.utility.customview.MonthYearPickerDialog(0, binding!!.tvCalendar.text.toString().toInt(),
                    showTitle = false,
                    showMonthPicker = false)
                pd.setListener { _, p1, p2, _ ->
                    binding!!.tvCalendar.text = p1.toString()
                    viewModel.selectedYear=p1
                    reLoad()
                }
                pd.show(parentFragmentManager, "MonthYearPickerDialog")

            }

            tvAllTypes.clickWithDebounce {
                openAllTypesBottomSheet(true, getString(R.string.select_types), arrayListOf())
            }
            tvAllModes.clickWithDebounce {
                openAllTypesBottomSheet(false,
                    getString(R.string.select_mode_of_transport), viewModel.uniqueVehiclesList)
            }
        }
    }

    private fun openAllTypesBottomSheet(allTypes: Boolean, tittle: String,uniqueVehiclesList:ArrayList<UniqueVehiclesRes>) {
        val dialog = FilterBottomSheetFragment.newInstance(isAllType = allTypes,title= tittle,  if (tittle==getString(R.string.select_types)){
            viewModel.selectedName
        }else{
            viewModel.selectedNameModes
        },uniqueVehiclesList)
        dialog.sendClickListener = {
            Log.e("=================","it: ${it}")
            dialog.dismiss()
            if (tittle==SELECT_TYPE) {
                if (!it.key.equals(viewModel.selectedName,true)){
                    viewModel.selectedNameModes = "all"
                    binding!!.tvAllModes.text = getString(R.string.all_modes)
                    viewModel.uniqueVehiclesList.clear()
                }
                viewModel.selectedName = it.key
                binding!!.tvAllTypes.text = it.name
            }else{
                viewModel.selectedNameModes = it.key
                binding!!.tvAllModes.text = it.name
            }
            Handler(Looper.getMainLooper()).postDelayed({
                reLoad()
            }, 500)

        }
        dialog.show(childFragmentManager, "")
    }

    private fun loadFragEmissions(fragment_name: Fragment?, flag: Int) {
        binding?.childFragmentContainer?.removeAllViews()
        val fm: FragmentManager = requireActivity().supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        if (flag == 0) {
            ft.add(de.fast2work.mobility.R.id.child_fragment_container, fragment_name!!)
            //fm.popBackStack(Root_Frag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            //ft.addToBackStack(Root_Frag)
        }
        ft.commit()
    }

    private fun loadFragKilometer(fragment_name: Fragment?, flag: Int) {
        binding?.childFragmentContainerKilometer?.removeAllViews()
        val fm: FragmentManager = requireActivity().supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        if (flag == 0) {
            ft.add(de.fast2work.mobility.R.id.child_fragment_container_kilometer, fragment_name!!)
            //fm.popBackStack(Root_Frag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            //ft.addToBackStack(Root_Frag)
        }
        ft.commit()
    }

    private fun loadFragCo2Emiss(fragment_name: Fragment?, flag: Int) {
        binding?.childFragmentContainerCo2Emi?.removeAllViews()
        val fm: FragmentManager = requireActivity().supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        if (flag == 0) {
            ft.add(de.fast2work.mobility.R.id.child_fragment_container_co2_emi, fragment_name!!)
            //fm.popBackStack(Root_Frag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            //ft.addToBackStack(Root_Frag)
        }
        ft.commit()
    }

    private fun loadFragLastTwoYearMonth(fragment_name: Fragment?, flag: Int) {
        binding?.childFragmentContainerLastTwoYearMonth?.removeAllViews()
        val fm: FragmentManager = requireActivity().supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        if (flag == 0) {
            ft.add(
                de.fast2work.mobility.R.id.child_fragment_container_last_two_year_month,
                fragment_name!!
            )
            //fm.popBackStack(Root_Frag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            //ft.addToBackStack(Root_Frag)
        }
        ft.commit()
    }

    private fun loadFragLastTwoYearVehicleType(fragment_name: Fragment?, flag: Int) {
        binding?.childFragmentContainerLastTwoYearVehicle?.removeAllViews()
        val fm: FragmentManager = requireActivity().supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        if (flag == 0) {
            ft.add(
                de.fast2work.mobility.R.id.child_fragment_container_last_two_year_vehicle,
                fragment_name!!
            )
            //fm.popBackStack(Root_Frag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            //ft.addToBackStack(Root_Frag)
        }
        ft.commit()
    }

    private fun loadFragMonthlySummary(fragment_name: Fragment?, flag: Int) {
        binding?.childFragmentContainerMonthlySummary?.removeAllViews()
        val fm: FragmentManager = requireActivity().supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        if (flag == 0) {
            ft.add(
                de.fast2work.mobility.R.id.child_fragment_container_monthly_summary,
                fragment_name!!
            )
            //fm.popBackStack(Root_Frag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            //ft.addToBackStack(Root_Frag)
        }
        ft.commit()
    }

    private fun initRecyclerViewKms() {
        binding?.apply {
            kmsVehicleTypeAdapter = KmsVehicleTypeAdapter(
                requireContext(),
                kmsVehicleTypeList
            ) { view, model, position ->
                when (view.id) {

                }
            }
            recycleViewKiloMeter.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL, false
            )

            recycleViewKiloMeter.adapter = kmsVehicleTypeAdapter

        }
    }

    private fun initRecyclerViewYearlyTrend() {
        binding?.apply {
            yearlyCo2TrendAdapter = YearlyCo2TrendAdapter(
                requireContext(),
                yearlyCo2TrendList
            ) { view, model, position ->
                when (view.id) {

                }
            }
            rvYearlyCo2Trend.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvYearlyCo2Trend.adapter = yearlyCo2TrendAdapter

        }
    }
    /**
     * This method contains code for all the setBoldAndColorSpannableCo2EmissionByMonthByVehicleType
     *
     */
    private fun setBoldAndColorSpannableCo2EmissionByMonthByVehicleType(textView: TextView, vararg portions: String) {
        val label = textView.text.toString()
        val spannableString1 = SpannableString(label)
        for (portion in portions) {
            val startIndex = label.indexOf(portion)
            val endIndex = startIndex + portion.length
            try {
                spannableString1.setSpan(object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        when (portion) {


                        }
                    }

                    override fun updateDrawState(ds: TextPaint) {// override updateDrawState
                        ds.isUnderlineText = false // set to false to remove underline
                    }

                }, startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

                spannableString1.setSpan(
                    ForegroundColorSpan(requireActivity().getColorFromAttr(R.attr.colorTextView)), startIndex, endIndex,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE)
//                spannableString1.setSpan(CustomTypefaceSpan("", ResourcesCompat.getFont(this, R.font.roboto_medium_500)!!), startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                textView.movementMethod = LinkMovementMethod.getInstance()
                textView.highlightColor = Color.TRANSPARENT
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        textView.text = spannableString1
    }



    /**
     * This method contains code for all the setBoldAndColorSpannableCo2EmissionByMonthByVehicleType
     *
     */
    private fun setBoldAndColorSpannableLast2YearCo2EmissionByMonth(textView: TextView, vararg portions: String) {
        val label = textView.text.toString()
        val spannableString1 = SpannableString(label)
        for (portion in portions) {
            val startIndex = label.indexOf(portion)
            val endIndex = startIndex + portion.length
            try {
                spannableString1.setSpan(object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        when (portion) {


                        }
                    }

                    override fun updateDrawState(ds: TextPaint) {// override updateDrawState
                        ds.isUnderlineText = false // set to false to remove underline
                    }

                }, startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

                spannableString1.setSpan(
                    ForegroundColorSpan(requireActivity().getColorFromAttr(R.attr.colorTextView)), startIndex, endIndex,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE)
//                spannableString1.setSpan(CustomTypefaceSpan("", ResourcesCompat.getFont(this, R.font.roboto_medium_500)!!), startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                textView.movementMethod = LinkMovementMethod.getInstance()
                textView.highlightColor = Color.TRANSPARENT
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        textView.text = spannableString1
    }


    /**
     * This method contains code for all the setBoldAndColorSpannableCo2EmissionByMonthByVehicleType
     *
     */
    private fun setBoldAndColorSpannableLast2YearCo2EmissionByVehicleType(textView: TextView, vararg portions: String) {
        val label = textView.text.toString()
        val spannableString1 = SpannableString(label)
        for (portion in portions) {
            val startIndex = label.indexOf(portion)
            val endIndex = startIndex + portion.length
            try {
                spannableString1.setSpan(object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        when (portion) {


                        }
                    }

                    override fun updateDrawState(ds: TextPaint) {// override updateDrawState
                        ds.isUnderlineText = false // set to false to remove underline
                    }

                }, startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

                spannableString1.setSpan(
                    ForegroundColorSpan(requireActivity().getColorFromAttr(R.attr.colorTextView)), startIndex, endIndex,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE)
//                spannableString1.setSpan(CustomTypefaceSpan("", ResourcesCompat.getFont(this, R.font.roboto_medium_500)!!), startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                textView.movementMethod = LinkMovementMethod.getInstance()
                textView.highlightColor = Color.TRANSPARENT
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        textView.text = spannableString1
    }

    /**
     * This method contains code for all the setBoldAndColorSpannableCo2EmissionByVehicleType
     *
     */
    private fun setBoldAndColorSpannableCo2EmissionByVehicleType(textView: TextView, vararg portions: String) {
        val label = textView.text.toString()
        val spannableString1 = SpannableString(label)
        for (portion in portions) {
            val startIndex = label.indexOf(portion)
            val endIndex = startIndex + portion.length
            try {
                spannableString1.setSpan(object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        when (portion) {


                        }
                    }

                    override fun updateDrawState(ds: TextPaint) {// override updateDrawState
                        ds.isUnderlineText = false // set to false to remove underline
                    }

                }, startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

                spannableString1.setSpan(
                    ForegroundColorSpan(requireActivity().getColorFromAttr(R.attr.colorTextView)), startIndex, endIndex,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE)
//                spannableString1.setSpan(CustomTypefaceSpan("", ResourcesCompat.getFont(this, R.font.roboto_medium_500)!!), startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                textView.movementMethod = LinkMovementMethod.getInstance()
                textView.highlightColor = Color.TRANSPARENT
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        textView.text = spannableString1
    }

}