package de.fast2work.mobility.ui.chart

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import de.fast2work.mobility.R
import de.fast2work.mobility.data.request.ChartReq
import de.fast2work.mobility.data.response.KmsVehicleTypeRes
import de.fast2work.mobility.databinding.FragmentKilometerChartBinding
import de.fast2work.mobility.ui.chart.EmissionChartFragment.Companion
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.profile.ProfileViewModel
import de.fast2work.mobility.ui.savingandrecpit.co2receipt.Co2ReceiptViewModel
import de.fast2work.mobility.utility.chart.notimportant.charting.animation.Easing
import de.fast2work.mobility.utility.chart.notimportant.charting.components.AxisBase
import de.fast2work.mobility.utility.chart.notimportant.charting.data.PieData
import de.fast2work.mobility.utility.chart.notimportant.charting.data.PieDataSet
import de.fast2work.mobility.utility.chart.notimportant.charting.data.PieEntry
import de.fast2work.mobility.utility.chart.notimportant.charting.formatter.IAxisValueFormatter
import de.fast2work.mobility.utility.chart.notimportant.charting.formatter.PercentFormatter
import de.fast2work.mobility.utility.chart.notimportant.charting.utils.ColorTemplate
import de.fast2work.mobility.utility.chart.notimportant.charting.utils.MPPointF
import java.util.Calendar


class KilometerChartFragment : BaseVMBindingFragment<FragmentKilometerChartBinding, Co2ReceiptViewModel>(Co2ReceiptViewModel::class.java),
    IAxisValueFormatter{


    val kmsVehicleTypeList:ArrayList<KmsVehicleTypeRes> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return generateBinding(FragmentKilometerChartBinding.inflate(inflater), container)
    }
    companion object {
        const val PARAM_YEAR = "year"
        const val PARAM_TYPE = "travel_type"
        const val PARAM_VEHICLE_TYPE= "vehicle_type"
        fun newInstance(year: Int = 0, travelType: String, vehicleType:String = "") = KilometerChartFragment().apply {
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
            kmsVehicleTypeList.clear()
            it.data?.kmsByVehicleType?.let { it1 -> kmsVehicleTypeList.addAll(it1) }
                setChatData()
        }
    }

    private fun setChatData() {

        if (kmsVehicleTypeList.size-1<1){
            kmsVehicleTypeList.forEach {
                if (it.kmValue==0.0){
                    binding!!.ivNoData.isVisible=true
                    binding!!.chart.isVisible=false
                }else{
                    binding!!.ivNoData.isVisible=false
                    binding!!.chart.isVisible=true
                }
            }

        }else{
            binding!!.ivNoData.isVisible=false
            binding!!.chart.isVisible=true
        }
        binding!!.chart.setDragDecelerationFrictionCoef(0.95f)


        binding!!.chart.setDrawHoleEnabled(true)
        binding!!.chart.setHoleColor(Color.WHITE)

        binding!!.chart.setTransparentCircleColor(Color.WHITE)
        binding!!.chart.setTransparentCircleAlpha(0)

        binding!!.chart.setHoleRadius(0f)
        binding!!.chart.setTransparentCircleRadius(61f)

        binding!!.chart.setDrawCenterText(true)

        //binding!!.chart.setRotationAngle(0f)
        // enable rotation of the chart by touch
        // enable rotation of the chart by touch
        binding!!.chart.setRotationEnabled(false)
        binding!!.chart.setHighlightPerTapEnabled(false)
        binding!!.chart.description.isEnabled=false


        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener



       // binding!!.chart.animateY(1400, Easing.EaseInOutQuad)
        // chart.spin(2000, 0, 360);

        // chart.spin(2000, 0, 360);
        /*val l: Legend = binding!!.chart.getLegend()
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 0f
        l.yEntrySpace = 10f
        l.yOffset = 0f*/
        binding!!.chart.legend.isEnabled=false

        // entry label styling

        // entry label styling
        binding!!.chart.setEntryLabelColor(Color.WHITE)
        //chart.setEntryLabelTypeface(tfRegular)
        binding!!.chart.setEntryLabelTextSize(12f)
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
       /* kmsVehicleTypeList.add(KmsVehicleTypeRes("car","small","car",89,"#FFC540"))
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

    }

    override fun setClickListener() {

    }
    private fun setData() {
        val entries = ArrayList<PieEntry>()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (i in 0 until kmsVehicleTypeList.size) {
            kmsVehicleTypeList.get(i).kmValue?.let {
                PieEntry(
                    it.toFloat(),
                    "",
                    resources.getDrawable(R.drawable.ic_search)
                )
            }?.let {
                entries.add(
                    it
                )
            }
        }
        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawIcons(false)
        dataSet.setSliceSpace(0f)
        dataSet.setIconsOffset(MPPointF(0f, 40f))
        dataSet.selectionShift = kmsVehicleTypeList.size.toFloat()

        val MATERIAL_COLORS = intArrayOf()

        // add a lot of colors
        val colors = ArrayList<Int>()
       kmsVehicleTypeList.forEach {
            it.colorCode?.let {
                it1 ->
                colors.add( ColorTemplate.rgb(it1))

            }
        }
        //for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        //for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        //for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        /*for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)*/
        //colors.add(ColorTemplate.getHoloBlue())
        dataSet.colors = colors
        //dataSet.setSelectionShift(0f);
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(0f)
        data.setValueTextColor(Color.WHITE)
//        var tfLight = Typeface.createFromAsset(requireActivity().assets, "OpenSans-Light.ttf")
        //data.setValueTypeface(tfLight)
        binding!!.chart.setData(data)

        // undo all highlights
        binding!!.chart.highlightValues(null)
        binding!!.chart.invalidate()
    }
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return "hello"
    }


}