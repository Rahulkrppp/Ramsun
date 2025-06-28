package de.fast2work.mobility.ui.chart

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import de.fast2work.mobility.R
import de.fast2work.mobility.data.request.ChartReq
import de.fast2work.mobility.databinding.FragmentLasttwoYearVehicleTypeBinding
import de.fast2work.mobility.ui.chart.EmissionChartFragment.Companion
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.profile.ProfileViewModel
import de.fast2work.mobility.ui.savingandrecpit.co2receipt.Co2ReceiptViewModel
import de.fast2work.mobility.utility.chart.notimportant.MyMarkerView
import de.fast2work.mobility.utility.chart.notimportant.charting.components.AxisBase
import de.fast2work.mobility.utility.chart.notimportant.charting.components.Legend
import de.fast2work.mobility.utility.chart.notimportant.charting.components.XAxis
import de.fast2work.mobility.utility.chart.notimportant.charting.components.YAxis
import de.fast2work.mobility.utility.chart.notimportant.charting.data.BarData
import de.fast2work.mobility.utility.chart.notimportant.charting.data.BarDataSet
import de.fast2work.mobility.utility.chart.notimportant.charting.data.BarEntry
import de.fast2work.mobility.utility.chart.notimportant.charting.data.Entry
import de.fast2work.mobility.utility.chart.notimportant.charting.formatter.IAxisValueFormatter
import de.fast2work.mobility.utility.chart.notimportant.charting.formatter.IFillFormatter
import de.fast2work.mobility.utility.chart.notimportant.charting.formatter.IndexAxisValueFormatter
import de.fast2work.mobility.utility.chart.notimportant.charting.highlight.Highlight
import de.fast2work.mobility.utility.chart.notimportant.charting.listener.OnChartValueSelectedListener
import de.fast2work.mobility.utility.extension.getColorFromAttr
import java.text.DecimalFormat
import java.util.Calendar
import kotlin.math.roundToInt


class LastTwoYearVehicleTypeFragment : BaseVMBindingFragment<FragmentLasttwoYearVehicleTypeBinding, Co2ReceiptViewModel>(
    Co2ReceiptViewModel::class.java) ,
    OnChartValueSelectedListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return generateBinding(FragmentLasttwoYearVehicleTypeBinding.inflate(inflater), container)
    }
    companion object {
        const val PARAM_YEAR = "year"
        const val PARAM_TYPE = "travel_type"
        const val PARAM_VEHICLE_TYPE= "vehicle_type"
        fun newInstance(year: Int = 0, travelType: String, vehicleType:String = "") = LastTwoYearVehicleTypeFragment().apply {
            this.arguments = Bundle().apply {
                putInt(PARAM_YEAR, year)
                putString(PARAM_TYPE, travelType)
                putString(PARAM_VEHICLE_TYPE, vehicleType)

            }
        }
    }
    override fun attachObservers() {
        viewModel.co2ReceiptLiveData.observe(this) {
            viewModel.lastTwoYearVehicleTypeList.clear()
            it.data?.lastYearComparisonByVehicle?.let { it1 ->
                viewModel.lastTwoYearVehicleTypeList.addAll(
                    it1
                )
            }
            Handler(Looper.getMainLooper()).postDelayed({
                setChatData()
            }, 1000)
        }
    }

    private fun setChatData() {
        binding!!.chart.setOnChartValueSelectedListener(this)
        binding!!.chart.getDescription().setEnabled(false)

//        chart.setDrawBorders(true);

        // scaling can now only be done on x- and y-axis separately

//        chart.setDrawBorders(true);

        // scaling can now only be done on x- and y-axis separately
        binding!!.chart.setPinchZoom(false)

        binding!!.chart.setDrawBarShadow(false)

        binding!!.chart.setDrawGridBackground(false)


        binding!!.chart.xAxis.setDrawGridLines(false)
        val xLabels: XAxis = binding!!.chart.getXAxis()
        xLabels.setDrawAxisLine(false)
        xLabels.granularity = 1f
        //xLabels.labelRotationAngle=300f

        xLabels.labelRotationAngle=270f
        xLabels.position = XAxis.XAxisPosition.BOTTOM
        xLabels.textColor = requireActivity().getColorFromAttr(R.attr.colorTextView)
        xLabels.typeface= ResourcesCompat.getFont(requireContext(),R.font.poppins_regular_400)
        xLabels.labelCount=viewModel.lastTwoYearVehicleTypeList.size
        binding!!.chart.xAxis.spaceMin = 0.5f

        //binding!!.chart.extraBottomOffset=10f



        val yAxis: YAxis = binding!!.chart.axisLeft
        yAxis.setDrawAxisLine(false)
        yAxis.setAxisMinimum(0f)
        yAxis.spaceTop = 23f
        binding!!.chart.setFitBars(true)
        yAxis.setValueFormatter(object: IAxisValueFormatter {
            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                //return "$value kg"
                if (value>=0.0) {
                    return "${DecimalFormat("#").format(value)} kg"
                }
                return ""
            }
        })
        yAxis.textColor = requireActivity().getColorFromAttr(R.attr.colorTextView)
        yAxis.typeface= ResourcesCompat.getFont(requireContext(),R.font.poppins_regular_400)
        binding!!.chart.isScaleYEnabled = false


        /*
                seekBarX.setProgress(10)
                seekBarY.setProgress(100)*/

        val l: Legend =  binding!!.chart.getLegend()
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.textColor= requireActivity().getColorFromAttr(R.attr.colorTextView)
        l.setDrawInside(true)
        l.typeface=ResourcesCompat.getFont(requireContext(), R.font.poppins_regular_400)
        //l.typeface = tfLight
        l.yOffset = 0f
        l.xOffset = 10f
        l.yEntrySpace = 0f
        l.setTextSize(8f)

        val leftAxis: YAxis = binding!!.chart.axisLeft
        leftAxis.setGranularity(1f)
        leftAxis.setCenterAxisLabels(true)
        //leftAxis.typeface = tfLight
        // leftAxis.valueFormatter = LargeValueFormatter()
       // leftAxis.spaceTop = 35f
        leftAxis.setAxisMinimum(0f) // this replaces setStartAtZero(true)
        binding!!.chart.getAxisRight().setEnabled(false)
        binding!!.chart.isScaleYEnabled = false
        binding!!.chart.isScaleXEnabled = false
        binding!!.chart.isDragEnabled = true
        binding!!.chart.isDragXEnabled = true
        binding!!.chart.isDragYEnabled = false
        //binding!!.chart.setVisibleXRangeMaximum(3f)
        val mv = MyMarkerView(requireContext(), R.layout.custom_marker_view,false)

        // Set the marker to the chart

        // Set the marker to the chart
        mv.setChartView(binding!!.chart)
        mv.setChartView(binding!!.chart)
        binding!!.chart.setMarker(mv)
        //binding!!.chart.setFitBars(true)
        setData()

    }

    override fun initComponents() {
        viewModel.selectedYear= arguments?.getInt(EmissionChartFragment.PARAM_YEAR)
        viewModel.selectedName= arguments?.getString(EmissionChartFragment.PARAM_TYPE,"").toString()
        viewModel.selectedNameModes= arguments?.getString(EmissionChartFragment.PARAM_VEHICLE_TYPE,"").toString()

        binding!!.chart.setNoDataText("")
        val chartParam = ChartReq().apply {
            this.year= viewModel.selectedYear
            this.travelType = viewModel.selectedName
            this.vehicleType = viewModel.selectedNameModes
        }
        viewModel.callChartCo2Api(chartParam)

    }

    private fun setData() {
        val groupSpace = 0.3f
        val barSpace = 0.05f // x4 DataSet

        val barWidth = 0.3f

        val values1 = ArrayList<BarEntry>()
        val values2 = ArrayList<BarEntry>()


        val randomMultiplier: Float = 20 * 100000f
/*
        for (i in 0 until 2) {
            values1.add(BarEntry(i.toFloat(), (Math.random() * randomMultiplier).toFloat()))
            values2.add(BarEntry(i.toFloat(), (Math.random() * randomMultiplier).toFloat()))

        }*/
        viewModel.lastTwoYearVehicleTypeList.forEachIndexed { index, lastTwoYearVehicleTypeRes ->
            if (lastTwoYearVehicleTypeRes.lastYearCo2Value != null) {
                values1.add(BarEntry(index.toFloat(), lastTwoYearVehicleTypeRes.lastYearCo2Value!!.toFloat()))
            } else {
                values1.add(BarEntry(index.toFloat(), 0f))
            }

            if (lastTwoYearVehicleTypeRes.co2Value != null) {
                values2.add(BarEntry(index.toFloat(), lastTwoYearVehicleTypeRes.co2Value!!.toFloat()))
            } else {
                values2.add(BarEntry(index.toFloat(), 0f))
            }
        }

        val set1: BarDataSet
        val set2: BarDataSet
        /* val set3: BarDataSet
         val set4: BarDataSet*/

        if (binding!!.chart.getData() != null && binding!!.chart.getData().getDataSetCount() > 0) {
            set1 = binding!!.chart.getData().getDataSetByIndex(0) as BarDataSet
            set2 = binding!!.chart.getData().getDataSetByIndex(1) as BarDataSet
            //set3 = binding!!.chart.getData().getDataSetByIndex(2) as BarDataSet
            //set4 = binding!!.chart.getData().getDataSetByIndex(3) as BarDataSet
            set1.setValues(values1)
            set2.setValues(values2)
            //set3.setValues(values3)
            //set4.setValues(values4)
            binding!!.chart.getData().notifyDataChanged()
            binding!!.chart.notifyDataSetChanged()
        } else {
            // create 4 DataSets
                if (viewModel.lastTwoYearVehicleTypeList.isNotEmpty()) {
                    //set3 = BarDataSet(values3, "Company C")
                    //set3.setColor(Color.rgb(242, 247, 158))
                    //set4 = BarDataSet(values4, "Company D")
                    //set4.setColor(Color.rgb(255, 102, 0))
                    set1 = BarDataSet(
                        values1,
                        viewModel.lastTwoYearVehicleTypeList[0].lastYear.toString()
                    )
                    set1.setColor(Color.parseColor("#6AD9AB"))
                    set2 = BarDataSet(
                        values2,
                        viewModel.lastTwoYearVehicleTypeList[0].currentYear.toString()
                    )
                    set2.setColor(Color.parseColor("#4EAF87"))

                    val data = BarData(set1, set2)


                    data.barWidth = barWidth
                    data.setValueTextSize(0f)
                    data.isHighlightEnabled=true
                    binding!!.chart.setData(data)
                    binding!!.chart.fitScreen()
                    //binding!!.chart.extraBottomOffset=10f


                    val nameArray: ArrayList<String> = arrayListOf()
                    viewModel.lastTwoYearVehicleTypeList.forEachIndexed { index, lastTwoYearVehicleTypeRes ->
                        nameArray.add(lastTwoYearVehicleTypeRes.vehicleTypeLabel.toString())
                    }
                    binding!!.chart.xAxis.valueFormatter = IndexAxisValueFormatter(nameArray)
                    binding!!.chart.getXAxis().setAxisMinimum(0.toFloat())
                    val groupWidth = data.getGroupWidth(groupSpace, barSpace)

                    binding!!.chart.xAxis.axisMinimum = 0f
                    binding!!.chart.xAxis.axisMaximum = groupWidth * viewModel.lastTwoYearVehicleTypeList.size
                    binding!!.chart.groupBars(0.toFloat(), groupSpace, barSpace)
                    binding!!.chart.xAxis.setCenterAxisLabels(true)
                    binding!!.chart.invalidate()

                    binding!!.chart.data?.notifyDataChanged()
                    binding!!.chart.notifyDataSetChanged()
                }


            /*data.barWidth = barWidth
            data.setValueTextSize(0f)
            //data.setValueFormatter(LargeValueFormatter())
            // data.setValueTypeface(tfLight)
            binding!!.chart.setData(data)
            val nameArray:ArrayList<String> = arrayListOf()
            nameArray.add("car")
            nameArray.add("bus")

            binding!!.chart.xAxis.valueFormatter = IndexAxisValueFormatter(nameArray)

            binding!!.chart.getXAxis().setAxisMinimum(0.toFloat())

            // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters

            // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
            binding!!.chart.getXAxis().setAxisMaximum(
                0 + binding!!.chart.getBarData().getGroupWidth(groupSpace, barSpace) * 2
            )
            binding!!.chart.groupBars(0.toFloat(), groupSpace, barSpace)

            binding!!.chart.xAxis.setCenterAxisLabels(true)*/
        }





        // specify the width each bar should have

        // specify the width each bar should have
        //binding!!.chart.getBarData().setBarWidth(barWidth)

        // restrict the x-axis range

        // restrict the x-axis range


    }

    override fun setClickListener() {
    }


    override fun onValueSelected(e: Entry?, h: Highlight?) {

    }

    override fun onNothingSelected() {

    }

   /* override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                var count = 5

                var factor = axis!!.axisMaximum / count

                var index = ((value / factor)).roundToInt()

                if (index >= 0 && index < count) {

                    return "rahul"
                }

                return ""
        //return "nic"
    }*/
}