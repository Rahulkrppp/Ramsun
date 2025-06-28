package de.fast2work.mobility.ui.chart


import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.R
import de.fast2work.mobility.data.request.ChartReq
import de.fast2work.mobility.data.response.MonthWiseEmissionsRes
import de.fast2work.mobility.data.response.YearlyCo2TrendRes
import de.fast2work.mobility.databinding.FragmentMonthlySummaryBinding
import de.fast2work.mobility.ui.chart.EmissionChartFragment.Companion
import de.fast2work.mobility.ui.chart.adapter.MonthlySummaryAdapter
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.profile.ProfileViewModel
import de.fast2work.mobility.ui.savingandrecpit.co2receipt.Co2ReceiptViewModel
import de.fast2work.mobility.utility.customview.DividerItemDecorator
import java.util.Calendar
import java.util.Date


class MonthlySummaryFragment :
    BaseVMBindingFragment<FragmentMonthlySummaryBinding, Co2ReceiptViewModel>(
        Co2ReceiptViewModel::class.java
    ) {
    var monthlySummaryAdapter: MonthlySummaryAdapter? = null
    val monthlyValueList: ArrayList<Month> = arrayListOf()
    val yearlyCo2TrendList: ArrayList<YearlyCo2TrendRes> = arrayListOf()
    val trendList: ArrayList<MonthWiseEmissionsRes> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return generateBinding(FragmentMonthlySummaryBinding.inflate(inflater), container)
    }

    companion object {
        const val PARAM_YEAR = "year"
        const val PARAM_TYPE = "travel_type"
        const val PARAM_VEHICLE_TYPE= "vehicle_type"
        fun newInstance(year: Int = 0, travelType: String, vehicleType:String = "") = MonthlySummaryFragment().apply {
            this.arguments = Bundle().apply {
                putInt(PARAM_YEAR, year)
                putString(PARAM_TYPE, travelType)
                putString(PARAM_VEHICLE_TYPE, vehicleType)

            }
        }
    }
    override fun attachObservers() {
        viewModel.co2ReceiptLiveData.observe(this){
            Log.e("", "attachObservers:${it.data} ", )
            yearlyCo2TrendList.clear()
            it.data?.yearlyCo2Trend?.let { it1 -> yearlyCo2TrendList.addAll(it1) }
            monthlyValueList.add(Month(
                    getString(R.string.vehicle_type),
                    getString(R.string.jan),
                    getString(R.string.feb),
                    getString(R.string.mar),
                    getString(R.string.apr),
                    getString(R.string.may),
                    getString(R.string.jun),
                    getString(R.string.jul),
                    getString(R.string.aug),
                    getString(R.string.sep),
                    getString(R.string.oct),
                    getString(R.string.nov),
                    getString(R.string.dec)
                )
            )
            yearlyCo2TrendList.forEachIndexed { index1, yearlyCo2TrendRes ->
                val tempList = ArrayList<Double>()
                yearlyCo2TrendRes.monthWiseEmissions.forEachIndexed { index, monthWiseEmissionsRes ->
                   // monthWiseEmissionsRes.co2Value?.toDouble()?.let { tempList.add(it) }
                    if( monthWiseEmissionsRes.co2Value!=null)
                    {
                        tempList.add(monthWiseEmissionsRes.co2Value!!)
                    }else{
                        tempList.add(0.0)
                    }

                }
                monthlyValueList.add(
                    Month(
                        yearlyCo2TrendRes.vehicleTypeLabel.toString(),
                        tempList[0].toString(),
                        tempList[1].toString(),
                        tempList[2].toString(),
                        tempList[3].toString(),
                        tempList[4].toString(),
                        tempList[5].toString(),
                        tempList[6].toString(),
                        tempList[7].toString(),
                        tempList[8].toString(),
                        tempList[9].toString(),
                        tempList[10].toString(),
                        tempList[11].toString()
                    )
                )
            }

            monthlySummaryAdapter?.notifyDataSetChanged()
        }
    }

    override fun initComponents() {
        viewModel.selectedYear= arguments?.getInt(EmissionChartFragment.PARAM_YEAR)
        viewModel.selectedName= arguments?.getString(EmissionChartFragment.PARAM_TYPE,"").toString()
        viewModel.selectedNameModes= arguments?.getString(EmissionChartFragment.PARAM_VEHICLE_TYPE,"").toString()


        val chartParam = ChartReq().apply {
            this.year= viewModel.selectedYear
            this.travelType = viewModel.selectedName
            this.vehicleType = viewModel.selectedNameModes
        }
        viewModel.callChartCo2Api(chartParam)
        initRecyclerView()
    }

    override fun setClickListener() {

    }

    private fun initRecyclerView() {
        binding?.apply {
            monthlySummaryAdapter =
                MonthlySummaryAdapter(requireContext(), monthlyValueList) { view, model, position ->
                    when (view.id) {

                    }
                }
            recyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            val dividerItemDecoration: RecyclerView.ItemDecoration = DividerItemDecorator(
                ContextCompat.getDrawable(requireActivity(), R.drawable.divider)!!
            )
            recyclerView.addItemDecoration(dividerItemDecoration)
            recyclerView.adapter = monthlySummaryAdapter

        }
    }
}