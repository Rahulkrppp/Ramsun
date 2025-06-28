package de.fast2work.mobility.ui.chart

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import de.fast2work.mobility.R
import de.fast2work.mobility.data.request.ChartReq
import de.fast2work.mobility.data.response.EmissionsByVehicleTypeRes
import de.fast2work.mobility.data.response.UniqueVehiclesRes
import de.fast2work.mobility.databinding.ItemEmissionsChartBinding
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.savingandrecpit.co2receipt.Co2ReceiptViewModel
import de.fast2work.mobility.ui.savingandrecpit.co2receipt.FilterBottomSheetFragment
import de.fast2work.mobility.utility.chart.notimportant.MyMarkerView
import de.fast2work.mobility.utility.chart.notimportant.charting.components.AxisBase
import de.fast2work.mobility.utility.chart.notimportant.charting.components.XAxis
import de.fast2work.mobility.utility.chart.notimportant.charting.components.YAxis
import de.fast2work.mobility.utility.chart.notimportant.charting.data.Entry
import de.fast2work.mobility.utility.chart.notimportant.charting.data.LineData
import de.fast2work.mobility.utility.chart.notimportant.charting.data.LineDataSet
import de.fast2work.mobility.utility.chart.notimportant.charting.formatter.IAxisValueFormatter
import de.fast2work.mobility.utility.chart.notimportant.charting.formatter.IFillFormatter
import de.fast2work.mobility.utility.chart.notimportant.charting.formatter.IndexAxisValueFormatter
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.datasets.ILineDataSet
import de.fast2work.mobility.utility.chart.notimportant.charting.utils.Utils
import de.fast2work.mobility.utility.extension.getColorFromAttr
import java.text.DecimalFormat
import java.util.Calendar


class EmissionChartFragment :
    BaseVMBindingFragment<ItemEmissionsChartBinding, Co2ReceiptViewModel>(Co2ReceiptViewModel::class.java) {

    var emissionsByVehicleTypeResList: ArrayList<EmissionsByVehicleTypeRes> = arrayListOf()
    var index=-1
    companion object {
        const val PARAM_YEAR = "year"
        const val PARAM_TYPE = "travel_type"
        const val PARAM_VEHICLE_TYPE= "vehicle_type"
        fun newInstance(year: Int = 0, travelType: String, vehicleType:String = "") = EmissionChartFragment().apply {
            this.arguments = Bundle().apply {
                putInt(PARAM_YEAR, year)
                putString(PARAM_TYPE, travelType)
                putString(PARAM_VEHICLE_TYPE, vehicleType)

            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return generateBinding(ItemEmissionsChartBinding.inflate(inflater), container)
    }


    override fun attachObservers() {
        viewModel.co2ReceiptLiveData.observe(this){
            binding!!.chart1.isVisible=true
            Log.e("", "attachObservers:${it.data} ")
            emissionsByVehicleTypeResList.clear()
            it.data?.emissionsByVehicleType?.let { it1 -> emissionsByVehicleTypeResList.addAll(it1) }

           setChartData()
        }
    }

    private fun setChartData() {

        // // Chart Style // //


        // background color



        // background color
        binding!!.chart1.setBackgroundColor(Color.TRANSPARENT)

        // disable description text

        // disable description text
        binding!!.chart1.description.isEnabled = false

        // enable touch gestures

        // enable touch gestures
        binding!!.chart1.setTouchEnabled(true)

        // set listeners

        // set listeners
        binding!!.chart1.setDrawGridBackground(false)

        // enable scaling and dragging

        // enable scaling and dragging
        binding!!.chart1.setDragEnabled(true)
        binding!!.chart1.setScaleEnabled(true)
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);

        // force pinch zoom along both axis
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);

        // force pinch zoom along both axis
        binding!!.chart1.setPinchZoom(true)
        binding!!.chart1.legend.isEnabled=false
        /**
         * xAxis
         */
        val xAxis: XAxis = binding!!.chart1.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        //xAxis.labelRotationAngle = 340f
        xAxis.isGranularityEnabled=true
        xAxis.setDrawGridLinesBehindData(false)
        xAxis.textColor = requireActivity().getColorFromAttr(R.attr.colorTextView)
        xAxis.typeface= ResourcesCompat.getFont(requireContext(),R.font.poppins_regular_400)

        xAxis.labelCount=emissionsByVehicleTypeResList.size
        //xAxis.spaceMin = 0.2f
        //xAxis.xOffset=1f
        if (emissionsByVehicleTypeResList.size-1<1){
            xAxis.axisMinimum = -0.3f
        }else{
            //xAxis.axisMinimum = 0.2f
        }

        //xAxis.mAxisMaximum = emissionsByVehicleTypeRes.size.toFloat()
        xAxis.setDrawAxisLine(false)
        //binding!!.chart1.xAxis.spaceMin = 0.2f
        xAxis.labelRotationAngle=270f
        xAxis.spaceMax = 0.2f


        val yAxis: YAxis = binding!!.chart1.axisLeft
        yAxis.setDrawAxisLine(false)
        yAxis.textColor = requireActivity().getColorFromAttr(R.attr.colorChartYaixs)
        yAxis.textSize=10f
        yAxis.typeface= ResourcesCompat.getFont(requireContext(),R.font.poppins_regular_400)
        yAxis.setValueFormatter(object:IAxisValueFormatter{
            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                return "${DecimalFormat("#").format(value)} kg"
            }
        })
       // yAxis.gridLineWidth = 0.3f
        yAxis.axisMinimum = 0.5f
        yAxis.axisMaximum = 5.0f

         binding!!.chart1.axisRight.isEnabled = false
        // horizontal grid lines
        // yAxis.enableGridDashedLine(10f, 10f, 0f)
        //binding!!.chart1.getAxisLeft().setDrawGridLines(false)
        binding!!.chart1.getXAxis().setDrawGridLines(false)

        binding!!.chart1.isScaleYEnabled = true
        binding!!.chart1.isScaleXEnabled = true
        binding!!.chart1.isDragEnabled = true
        binding!!.chart1.isDragXEnabled = true
        binding!!.chart1.isDragYEnabled = true
        binding!!.chart1.setScaleEnabled(false)
       // binding!!.chart1.setVisibleXRangeMaximum(3f)
       // binding!!.chart1.setFitBars(true)
        binding!!.chart1.setDrawMarkers(true)
        // axis range
        //yAxis.setAxisMaximum(100f)
        yAxis.setAxisMinimum(0f)
        val mv = MyMarkerView(requireContext(), R.layout.custom_marker_view,false)

        // Set the marker to the chart

        // Set the marker to the chart
        mv.setChartView(binding!!.chart1)
        mv.setChartView(binding!!.chart1)

        binding!!.chart1.setMarker(mv)

        setData()

       /* val maxValue = emissionsByVehicleTypeResList.maxWithOrNull(Comparator { a, b ->
            (a.co2Value ?: 0.0).compareTo(b.co2Value ?: 0.0)
        })

        if (maxValue != null) {
            yAxis.mAxisMaximum= maxValue.co2Value!!.toFloat()
        }*/


        // create marker to display box when values are selected


    }

    override fun initComponents() {
        viewModel.selectedYear= arguments?.getInt(PARAM_YEAR)
        viewModel.selectedName= arguments?.getString(PARAM_TYPE,"").toString()
        viewModel.selectedNameModes= arguments?.getString(PARAM_VEHICLE_TYPE,"").toString()

        binding!!.chart1.setNoDataText("")
        val chartParam = ChartReq().apply {
            this.year= viewModel.selectedYear
            this.travelType = viewModel.selectedName
            this.vehicleType = viewModel.selectedNameModes
        }
        viewModel.callChartCo2Api(chartParam)
/*        emissionsByVehicleTypeRes.add(EmissionsByVehicleTypeRes("car","small","car",55.7))
        emissionsByVehicleTypeRes.add(EmissionsByVehicleTypeRes("bus","large","bus",69.7))
        emissionsByVehicleTypeRes.add(EmissionsByVehicleTypeRes("train","long","train",77.7))
        emissionsByVehicleTypeRes.add(EmissionsByVehicleTypeRes("train","long","train",20.7))
        emissionsByVehicleTypeRes.add(EmissionsByVehicleTypeRes("cycle","long","train",10.7))
        emissionsByVehicleTypeRes.add(EmissionsByVehicleTypeRes("train","long","train",5.0))
        emissionsByVehicleTypeRes.add(EmissionsByVehicleTypeRes("train","long","train",90.0))
        emissionsByVehicleTypeRes.add(EmissionsByVehicleTypeRes("flight","long","train",58.0))
        emissionsByVehicleTypeRes.add(EmissionsByVehicleTypeRes("toy car","long","train",85.0))
        emissionsByVehicleTypeRes.add(EmissionsByVehicleTypeRes("train","long","train",50.0))
        emissionsByVehicleTypeRes.add(EmissionsByVehicleTypeRes("train","long","train",56.0))
        emissionsByVehicleTypeRes.add(EmissionsByVehicleTypeRes("train","long","train",59.0))*/



    }

    override fun setClickListener() {

    }
    private fun setData() {
        val values = ArrayList<Entry>()

        for (i in 0 until emissionsByVehicleTypeResList.size) {
            emissionsByVehicleTypeResList.get(i).co2Value?.toFloat()
                ?.let { Entry(Math.ceil(i.toDouble()).toFloat(), it) }
                ?.let { values.add(it) }
        }
        val set1: LineDataSet
        if (binding!!.chart1.getData() != null &&
            binding!!.chart1.getData().getDataSetCount() > 0
        ) {
            set1 = binding!!.chart1.getData().getDataSetByIndex(0) as LineDataSet
            set1.setValues(values)
            set1.notifyDataSetChanged()
            binding!!.chart1.getData().notifyDataChanged()
            binding!!.chart1.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, "")
            set1.setDrawIcons(false)
            val nameArray:ArrayList<String> = arrayListOf()
            emissionsByVehicleTypeResList.forEach {
                nameArray.add(it.vehicleTypeLabel.toString())
            }
            binding!!.chart1.xAxis.valueFormatter = IndexAxisValueFormatter(nameArray)
            // draw dashed line
            set1.enableDashedLine(20f, 0f, 0f)

            // black lines and points
            set1.setColor(Color.parseColor("#4CAC87"))
            set1.setCircleColor(Color.parseColor("#4CAC87"))

            // line thickness and point size
            set1.setLineWidth(3f)
            set1.circleRadius = 7f
            set1.circleHoleRadius=3f

            // draw points as solid circles
            set1.setDrawCircleHole(true)

            // customize legend entry
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f

            // text size of values
            set1.valueTextSize = 0f
            set1.highLightColor=requireActivity().getColor(R.color.transparent)

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f)

            // set the filled area
            set1.setDrawFilled(true)
            set1.fillFormatter =
                IFillFormatter { dataSet, dataProvider -> binding!!.chart1.getAxisLeft().getAxisMinimum() }

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                val drawable = ContextCompat.getDrawable(requireActivity(), R.drawable.fade_red)
                set1.fillDrawable = drawable
            } else {
                set1.setFillColor(Color.BLACK)
            }
            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)
            data.setDrawValues(false)
            data.isHighlightEnabled=true
            binding!!.chart1.fitScreen()
            if (nameArray.size>3){
                binding!!.chart1.setVisibleXRangeMinimum(nameArray.size.toFloat())
            }else{
                binding!!.chart1.setVisibleXRangeMinimum(3f)
            }
            //binding!!.chart1.xAxis.axisMaximum = data.xMax + 0.5f
            //binding!!.chart1.setVisibleXRangeMaximum(3f)



            val maxValue: Float = data.getYMax()
            //binding!!.chart1.getAxisLeft().mAxisMaximum = maxValue.toInt()+20f

            val extraSpace = (maxValue / (binding!!.chart1.getAxisLeft().labelCount + 3))
            binding!!.chart1.getAxisLeft().mAxisMaximum = maxValue + (if (extraSpace < 1) 1 else extraSpace).toFloat()

            // set data
            //binding!!.chart1.axisLeft.mAxisMaximum= 203f
            binding!!.chart1.fitScreen()
            binding!!.chart1.setAutoScaleMinMaxEnabled(true)
            binding!!.chart1.setData(data)
        }
    }

}