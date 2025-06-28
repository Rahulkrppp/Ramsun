package de.fast2work.mobility.ui.chart


import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.utils.ColorTemplate
import de.fast2work.mobility.R
import de.fast2work.mobility.data.request.ChartReq
import de.fast2work.mobility.databinding.FragmentCo2EmissionChartBinding
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
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.datasets.IBarDataSet
import de.fast2work.mobility.utility.chart.notimportant.charting.listener.OnChartValueSelectedListener
import de.fast2work.mobility.utility.extension.getColorFromAttr
import java.text.DecimalFormat
import java.util.Calendar


class Co2EmissionChartFragment : BaseVMBindingFragment<FragmentCo2EmissionChartBinding, Co2ReceiptViewModel>(Co2ReceiptViewModel::class.java) ,
      OnChartValueSelectedListener {
    val nameArray: ArrayList<String> = arrayListOf()
    val nameArray1 = mutableListOf<String>()
    val colors = ArrayList<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return generateBinding(FragmentCo2EmissionChartBinding.inflate(inflater), container)
    }
    companion object {
        const val PARAM_YEAR = "year"
        const val PARAM_TYPE = "travel_type"
        const val PARAM_VEHICLE_TYPE= "vehicle_type"
        fun newInstance(year: Int = 0, travelType: String, vehicleType:String = "") = Co2EmissionChartFragment().apply {
            this.arguments = Bundle().apply {
                putInt(PARAM_YEAR, year)
                putString(PARAM_TYPE, travelType)
                putString(PARAM_VEHICLE_TYPE, vehicleType)

            }
        }
    }

    override fun attachObservers() {
        viewModel.co2ReceiptLiveData.observe(this) {
            viewModel.co2EmissionByMonthVehicleTypeList.clear()
            it.data?.emissionsByMonthVehicle?.let { it1 ->

                viewModel.co2EmissionByMonthVehicleTypeList.addAll(
                    it1
                )
            }
            nameArray1.clear()
            colors.clear()
            nameArray.clear()
            binding!!.chart.invalidate()
            binding!!.chart.clear()
            setChatData()
            /*Handler(Looper.getMainLooper()).postDelayed({

            }, 1000)*/
        }
    }

    private fun setChatData() {

        //viewModel.co2EmissionByMonthList.add(1,"")


        binding!!.chart.getDescription().setEnabled(false)

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        //binding!!.chart.setMaxVisibleValueCount(40)

        // scaling can now only be done on x- and y-axis separately

        // scaling can now only be done on x- and y-axis separately
        binding!!.chart.setPinchZoom(false)

        binding!!.chart.setDrawValueAboveBar(false)
        binding!!.chart.isHighlightFullBarEnabled = false
        binding!!.chart.setDrawGridBackground(false)
        binding!!.chart.xAxis.setDrawGridLines(false)
        binding!!.chart.backgroundTintList = (ColorStateList.valueOf(requireActivity().getColorFromAttr(de.fast2work.mobility.R.attr.colorCardView)))

        // change the position of the y-labels
        // change the position of the y-labels
        val leftAxis: YAxis = binding!!.chart.axisLeft
        // leftAxis.valueFormatter = MyAxisValueFormatter()
        leftAxis.setAxisMinimum(0f) // this replaces setStartAtZero(true)

        binding!!.chart.axisRight.isEnabled = false

        val xLabels: XAxis = binding!!.chart.getXAxis()
        xLabels.setDrawAxisLine(false)
        xLabels.granularity = 1f
        xLabels.setGranularityEnabled(true)
        xLabels.labelRotationAngle=270f
        xLabels.position = XAxis.XAxisPosition.BOTTOM
        xLabels.textColor = requireActivity().getColorFromAttr(R.attr.colorTextView)
        xLabels.typeface= ResourcesCompat.getFont(requireContext(),R.font.poppins_regular_400)
        binding!!.chart.extraBottomOffset=5f

        val yAxis: YAxis = binding!!.chart.axisLeft
        yAxis.setDrawAxisLine(false)
        binding!!.chart.axisLeft.axisMinimum = 0f
        yAxis.textColor = requireActivity().getColorFromAttr(R.attr.colorTextView)
        yAxis.typeface= ResourcesCompat.getFont(requireContext(),R.font.poppins_regular_400)
        yAxis.setValueFormatter(object: IAxisValueFormatter {
            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                return "${
                        DecimalFormat("#").format(value)} kg"
            }
        })




//        l.xOffset = 10f
//        l.horizontalAlignment=Legend.LegendHorizontalAlignment.LEFT

        binding!!.chart.isScaleYEnabled = false
        binding!!.chart.isScaleXEnabled = false
        binding!!.chart.isDragEnabled = true
        binding!!.chart.isDragXEnabled = true
        binding!!.chart.isDragYEnabled = false
        binding!!.chart.setFitBars(true)
        val mv = MyMarkerView(requireContext(), R.layout.custom_marker_view ,true)

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
    }

    override fun setClickListener() {

    }

    fun setData(){
        val values = java.util.ArrayList<BarEntry>()

        /*for (i in 0 until 39) {
            val mul: Float = (100 + 1).toFloat()
            val val1 = (Math.random() * mul).toFloat() + mul / 3
            val val2 = (Math.random() * mul).toFloat() + mul / 3
            val val3 = (Math.random() * mul).toFloat() + mul / 3
            values.add(
                BarEntry(
                    i.toFloat(), floatArrayOf(val1, val2, val3),resources.getDrawable(R.drawable.ic_search)
                )
            )
        }*/
       // val vehicleWiseEmissionsList :ArrayList<Float> = arrayListOf()
        viewModel.co2EmissionByMonthVehicleTypeList.forEachIndexed { index, co2EmissionByMonthVehicleTypeRes ->
            val valuesArray= mutableListOf<Float>()
            co2EmissionByMonthVehicleTypeRes.vehicleWiseEmissions?.forEach {
                //vehicleWiseEmissionsList.add(it.co2Value?.toFloat()?:0f)
                if (it.co2Value!=null) {
                    valuesArray.add(it.co2Value!!.toFloat())
                }else{
                    valuesArray.add(0f)
                }

            }
                values.add(BarEntry(index.toFloat(), valuesArray.toFloatArray(),valuesArray.toFloatArray()))
        }

        var set1: BarDataSet

        if (binding!!.chart.getData() != null &&
            binding!!.chart.getData().getDataSetCount() > 0
        ) {
            set1 = binding!!.chart.getData().getDataSetByIndex(/* index = */ 0) as BarDataSet
            set1.setValues(values)
            binding!!.chart.getData().notifyDataChanged()
            binding!!.chart.notifyDataSetChanged()
        } else {
            val dataSets = java.util.ArrayList<IBarDataSet>()
            dataSets.clear()

            val l: Legend = binding!!.chart.legend
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP)
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT)
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL)
            l.isWordWrapEnabled=true
            l.textSize=10f
            l.yOffset=5f
            l.xOffset=2f
            l.setMaxSizePercent(0.70f)
            l.setDrawInside(false)
            l.textColor= requireActivity().getColorFromAttr(R.attr.colorTextView)
            l.typeface=ResourcesCompat.getFont(requireContext(), R.font.poppins_regular_400)
            binding!!.chart.legend.isWordWrapEnabled=true


            set1 = BarDataSet(values, "")
            viewModel.co2EmissionByMonthVehicleTypeList.forEachIndexed { index, co2EmissionByMonthVehicleTypeRes ->
                nameArray.add(co2EmissionByMonthVehicleTypeRes.monthName.toString())
                if (index==0) {
                    co2EmissionByMonthVehicleTypeRes.vehicleWiseEmissions?.forEach {
                        colors.add(
                            de.fast2work.mobility.utility.chart.notimportant.charting.utils.ColorTemplate.rgb(
                                it.colorCode.toString()
                            )
                        )
                        it.vehicleTypeLabel?.let { it1 -> nameArray1.add(it1) }
                    }
                }

            }



            set1.setDrawIcons(false)
            set1.setColors(colors)
            if(nameArray1.size==1){
                set1.label = nameArray1.get(0)
            }else {
                set1.setStackLabels(nameArray1.toTypedArray())
            }
            dataSets.add(set1)
            val data = BarData(dataSets)

            //set1.stackLabels =nameArray1.toTypedArray()


            binding!!.chart.xAxis.valueFormatter = IndexAxisValueFormatter(nameArray)
            Log.e("", "nameArray: $nameArray")

            data.setValueTextSize(0f)
            data.barWidth=0.6f
            data.isHighlightEnabled=true
            binding!!.chart.setData(data)
            //binding!!.chart.setVisibleXRangeMaximum(4f)
            binding!!.chart.fitScreen()
            binding!!.chart.xAxis.labelCount=12
        }

        binding!!.chart.setFitBars(true)
        binding!!.chart.invalidate()
    }

     override fun onValueSelected(e: Entry?, h: Highlight?) {

     }

     override fun onNothingSelected() {

     }
    private fun getColors(): IntArray {

        // have as many colors as stack-values per entry
        val colors = IntArray(3)
        System.arraycopy(ColorTemplate.MATERIAL_COLORS, 0, colors, 0, 3)
        return colors
    }

 }