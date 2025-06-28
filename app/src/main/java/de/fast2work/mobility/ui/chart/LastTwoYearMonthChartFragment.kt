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
import de.fast2work.mobility.databinding.FragmentLastTwoYearMonthChartBinding
import de.fast2work.mobility.ui.chart.EmissionChartFragment.Companion
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
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
import de.fast2work.mobility.utility.chart.notimportant.charting.formatter.IndexAxisValueFormatter
import de.fast2work.mobility.utility.chart.notimportant.charting.highlight.Highlight
import de.fast2work.mobility.utility.chart.notimportant.charting.listener.OnChartValueSelectedListener
import de.fast2work.mobility.utility.extension.getColorFromAttr
import java.text.DecimalFormat
import java.util.Calendar


class LastTwoYearMonthChartFragment :
    BaseVMBindingFragment<FragmentLastTwoYearMonthChartBinding, Co2ReceiptViewModel>(
        Co2ReceiptViewModel::class.java
    ),
    OnChartValueSelectedListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return generateBinding(FragmentLastTwoYearMonthChartBinding.inflate(inflater), container)
    }
    companion object {
        const val PARAM_YEAR = "year"
        const val PARAM_TYPE = "travel_type"
        const val PARAM_VEHICLE_TYPE= "vehicle_type"
        fun newInstance(year: Int = 0, travelType: String, vehicleType:String = "") = LastTwoYearMonthChartFragment().apply {
            this.arguments = Bundle().apply {
                putInt(PARAM_YEAR, year)
                putString(PARAM_TYPE, travelType)
                putString(PARAM_VEHICLE_TYPE, vehicleType)

            }
        }
    }

    override fun attachObservers() {
        viewModel.lastTwoYearMonthList.clear()
        viewModel.co2ReceiptLiveData.observe(this) {
            it.data?.lastYearComparisonByMonth?.let { it1 ->
                viewModel.lastTwoYearMonthList.addAll(
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

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        /*val mv = MyMarkerView(this, R.layout.custom_marker_view)
        mv.setChartView(chart) // For bounds control*/

        //chart.setMarker(mv) // Set the marker to the chart
        binding!!.chart.xAxis.setDrawGridLines(false)
        binding!!.chart.setTouchEnabled(true)

        /**
         * XAxis
         */
        val xLabels: XAxis = binding!!.chart.xAxis
        xLabels.setDrawAxisLine(false)
        xLabels.granularity = 1f
        xLabels.isGranularityEnabled = true
        xLabels.textColor = requireActivity().getColorFromAttr(R.attr.colorTextView)
       // xLabels.textSize = 10f
        xLabels.labelRotationAngle=270f
        binding!!.chart.extraBottomOffset=13f
        xLabels.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular_400)
        xLabels.labelCount=viewModel.lastTwoYearMonthList.size
        xLabels.position = XAxis.XAxisPosition.BOTTOM
        binding!!.chart.xAxis.spaceMin = 0.5f
        xLabels.xOffset=10f
        xLabels.spaceMax = 0.5f
        xLabels.spaceMin = 0.5f

        val l: Legend = binding!!.chart.getLegend()
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(true)
        l.textColor=requireActivity().getColorFromAttr(R.attr.colorTextView)
        //l.typeface = tfLight
        l.typeface=ResourcesCompat.getFont(requireContext(), R.font.poppins_regular_400)
        l.yOffset = 0f
        l.xOffset = 10f
        l.yEntrySpace = 0f
        l.setTextSize(8f)

        /**
         * yAxis
         */

        binding!!.chart.isScaleYEnabled = false

        val leftAxis: YAxis = binding!!.chart.axisLeft
        leftAxis.setDrawAxisLine(false)
        leftAxis.setGranularity(1f)
        leftAxis.setCenterAxisLabels(true)
        leftAxis.setAxisMinimum(0f)
        //leftAxis.spaceTop = 10f
        leftAxis.spaceTop = 23f
        leftAxis.setValueFormatter(object: IAxisValueFormatter {
            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                //return "$value kg"
                //return "${DecimalFormat("#").format(value)} kg"
                if (value>=0.0) {
                    return "${DecimalFormat("#").format(value)} kg"
                }
                return ""
            }
        })
        //binding!!.chart.axisLeft.axisMinimum = 0f
        binding!!.chart.setFitBars(true)
        leftAxis.textColor = requireActivity().getColorFromAttr(R.attr.colorTextView)
        leftAxis.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular_400)
        //leftAxis.typeface = tfLight
        // leftAxis.valueFormatter = LargeValueFormatter()
        leftAxis.setAxisMinimum(0f)
        binding!!.chart.getAxisRight().setEnabled(false)

        //binding!!.chart.extraTopOffset=20f
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

        /* viewModel.lastTwoYearMonthList.add(LastTwoYearMonthRes(1,"Jan",20.9,80.0,2024,203))
         viewModel.lastTwoYearMonthList.add(LastTwoYearMonthRes(2,"Feb",30.9,60.0,2024,203))
         viewModel.lastTwoYearMonthList.add(LastTwoYearMonthRes(3,"Mar",40.9,50.0,2024,203)*/

    }

    private fun setData() {
        val groupSpace = 0.3f
        val barSpace = 0.05f // x4 DataSet

        val barWidth = 0.3f

        val values1 = ArrayList<BarEntry>()
        val values2 = ArrayList<BarEntry>()


       // val randomMultiplier: Float = 20 * 100000f

        /*for (i in 2022 until 2024) {
            values1.add(BarEntry(i.toFloat(), (Math.random() * randomMultiplier).toFloat()))
            values2.add(BarEntry(i.toFloat(), (Math.random() * randomMultiplier).toFloat()))

        }*/
        viewModel.lastTwoYearMonthList.forEach {
            if (it.lastYearCo2Value!=null){
                values1.add(BarEntry(it.month!!.toFloat(), it.lastYearCo2Value!!.toFloat()))
            }else{
                values1.add(BarEntry(it.month!!.toFloat(), 0f))
            }

           if (it.co2Value!=null){
               values2.add(BarEntry(it.month!!.toFloat(), it.co2Value!!.toFloat()))
           }else{
               values2.add(BarEntry(it.month!!.toFloat(),0f))
           }


            //it.lastYearCo2Value?.let { it1 -> it.month?.toFloat()?.let { it2 -> BarEntry(it2, it1.toFloat()) } }?.let { it2 -> values1.add(it2) }

           /* it.co2Value?.let { it1 ->
                it.month?.toFloat()
                    ?.let { it2 -> BarEntry(it2, it1.toFloat()) }
            }
                ?.let { it2 -> values2.add(it2) }*/
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

            //Log.e("", "setData===========: ${viewModel.lastTwoYearMonthList}")
            // create 4 DataSets
            if (viewModel.lastTwoYearMonthList.isNotEmpty()) {
                set1 = BarDataSet(values1, viewModel.lastTwoYearMonthList[0].lastYear.toString())
                set1.setColor(Color.parseColor("#6AD9AB"))
                set2 = BarDataSet(values2, viewModel.lastTwoYearMonthList[0].currentYear.toString())
                set2.setColor(Color.parseColor("#4EAF87"))
                val data = BarData(set1, set2)

                data.barWidth = barWidth
                data.setValueTextSize(0f)
                data.isHighlightEnabled=true



                binding!!.chart.setData(data)
                binding!!.chart.fitScreen()

                val nameArray: ArrayList<String> = arrayListOf()
                nameArray.add(getString(R.string.jan))
                nameArray.add(getString(R.string.feb))
                nameArray.add(getString(R.string.mar))
                nameArray.add(getString(R.string.apr))
                nameArray.add(getString(R.string.may))
                nameArray.add(getString(R.string.jun))
                nameArray.add(getString(R.string.jul))
                nameArray.add(getString(R.string.aug))
                nameArray.add(getString(R.string.sep))
                nameArray.add(getString(R.string.oct))
                nameArray.add(getString(R.string.nov))
                nameArray.add(getString(R.string.dec))

                /*viewModel.lastTwoYearMonthList.forEach {
                    nameArray.add(it.monthName.toString())
                }*/
                binding!!.chart.xAxis.valueFormatter = IndexAxisValueFormatter(nameArray)
                binding!!.chart.getXAxis().setAxisMinimum(0.toFloat())
                val groupWidth = data.getGroupWidth(groupSpace,barSpace)
                binding!!.chart.xAxis.axisMinimum = 0f
                binding!!.chart.xAxis.axisMaximum = groupWidth * viewModel.lastTwoYearMonthList.size
                binding!!.chart.groupBars(0.toFloat(), groupSpace, barSpace)
                binding!!.chart.xAxis.setCenterAxisLabels(true)
            }
        }
        binding!!.chart.invalidate()
        binding!!.chart.data?.notifyDataChanged()
        binding!!.chart.notifyDataSetChanged()
    }

    override fun setClickListener() {
    }


    override fun onValueSelected(e: Entry?, h: Highlight?) {

    }

    override fun onNothingSelected() {

    }


}