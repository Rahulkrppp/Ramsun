package de.fast2work.mobility.ui.chart.adapter

import android.R
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.data.response.YearlyCo2TrendRes
import de.fast2work.mobility.databinding.ItemYearlyCo2TrendBinding
import de.fast2work.mobility.utility.chart.notimportant.MyMarkerView
import de.fast2work.mobility.utility.chart.notimportant.charting.components.XAxis
import de.fast2work.mobility.utility.chart.notimportant.charting.components.YAxis
import de.fast2work.mobility.utility.chart.notimportant.charting.data.BarData
import de.fast2work.mobility.utility.chart.notimportant.charting.data.BarDataSet
import de.fast2work.mobility.utility.chart.notimportant.charting.data.BarEntry
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.datasets.IBarDataSet
import de.fast2work.mobility.utility.chart.notimportant.charting.utils.Fill
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.imageTint
import de.fast2work.mobility.utility.extension.loadImage
import de.fast2work.mobility.utility.extension.loadUrl
import de.fast2work.mobility.utility.util.Util.dpToPx
import okhttp3.internal.notify


class YearlyCo2TrendAdapter(
    var context: Context, var list: ArrayList<YearlyCo2TrendRes>,
    var clickListener: (view: View, model: YearlyCo2TrendRes, position: Int) -> Unit = { _: View, _: YearlyCo2TrendRes, _: Int -> },
) : RecyclerView.Adapter<YearlyCo2TrendAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemYearlyCo2TrendBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.itemBinding.rootView.getLayoutParams().width = (getScreenWidth(context)*0.85.toFloat()).toInt()
        val item = list[holder.absoluteAdapterPosition]
        holder.itemBinding.apply {
            val maxValue = item.monthWiseEmissions.maxWithOrNull(Comparator { a, b ->
                (a.co2Value ?: 0.0).compareTo(b.co2Value ?: 0.0)
            })

            var dummyBarValue: Double = 1.515

           /* if (maxValue?.co2Value != null && maxValue.co2Value != 0.0) {
                dummyBarValue = maxValue.co2Value!! / 2
                chart.axisLeft.resetAxisMaximum()
            } else {
                chart.axisLeft.axisMaximum = 100.0f
            }*/


          /*  val maxValue = item.monthWiseEmissions.maxWithOrNull(Comparator { a, b ->
                (a.co2Value ?: 0.0).compareTo(b.co2Value ?: 0.0)
            })

            var dummyBarValue: Double = 50.0*/

            if (maxValue?.co2Value != null && maxValue.co2Value != 0.0) {
                dummyBarValue = maxValue.co2Value!! / 66
                chart.axisLeft.resetAxisMaximum()
                chart.axisLeft.axisMaximum = maxValue?.co2Value!!.toFloat()
            } else {
                chart.axisLeft.resetAxisMaximum()
                chart.axisLeft.axisMaximum = 100.0f
            }

            tvVehicleName.text = "${item.vehicleTypeLabel}"
            tvKms.text =
                (item.totalCo2Value.let { "${item.totalCo2Value} Kg" } ?: run { "" }).toString()

            ivVehicle.imageTint(item.colorCode)
            if (item.iconUrl.toString().endsWith(".svg")) {
                ivVehicle.loadUrl(item.iconUrl.toString())
            } else {
                ivVehicle.loadImage(context, item.iconUrl.toString())
            }
            //


            chart.setDrawBarShadow(false)
            chart.setDrawValueAboveBar(true)
            chart.setDrawGridBackground(false)
            chart.isScaleYEnabled = false
            chart.isScaleXEnabled = false
            chart.isDragEnabled = false
            chart.isDragXEnabled = false
            chart.isDragYEnabled = false
            chart.getDescription().setEnabled(false)
            chart.setTouchEnabled(true)
            chart.axisLeft.axisMinimum = 0f
            chart.setFitBars(true)
            val mv = MyMarkerView(context, de.fast2work.mobility.R.layout.custom_marker_view,false)

            // Set the marker to the chart

            // Set the marker to the chart
            mv.setChartView(chart)
            mv.setChartView(chart)

            chart.setMarker(mv)

            // if more than 60 entries are displayed in the chart, no values will be
            // drawn

            // if more than 60 entries are displayed in the chart, no values will be
            // drawn
            //chart.setMaxVisibleValueCount(60)

            // scaling can now only be done on x- and y-axis separately

            // scaling can now only be done on x- and y-axis separately
            chart.setPinchZoom(false)

            chart.setDrawGridBackground(false)
            chart.legend.isEnabled=false
            // chart.setDrawYLabels(false);

            // chart.setDrawYLabels(false);

            val xAxis: XAxis = chart.getXAxis()
            xAxis.isEnabled =false


            val leftAxis: YAxis = chart.axisLeft
            leftAxis.isEnabled =false

            val rightAxis: YAxis = chart.axisRight
            rightAxis.isEnabled =false
            val start = 1f
            val values = mutableListOf<BarEntry>()

            val chartColors: MutableList<Int> = mutableListOf()
//            var startColor1:Int?=null
            item.monthWiseEmissions.forEachIndexed { index, data ->
                if (data.co2Value!=null && data.co2Value!=0.0){
                    values.add(BarEntry(index.toFloat(),data.co2Value!!.toFloat(),true))
                /*    data.co2Value?.toFloat()?.let { BarEntry(index.toFloat(), it) }
                        ?.let { values.add(it) }*/
                    chartColors.add(Color.parseColor(item.colorCode.toString()))
                }else{
                    values.add(BarEntry(index.toFloat(),dummyBarValue.toFloat(),false))
                    chartColors.add(context.getColorFromAttr(de.fast2work.mobility.R.attr.colorSearchHint))
                }
            }


            /*val gradientFills: MutableList<Fill> = java.util.ArrayList<Fill>()
            gradientFills.add(startColor1?.let { Fill(it, startColor1) })*/

//            {
//                var i = start.toInt()
//                while (i < start + count) {
//                    val `val`: Float = (Math.random() * (range + 1)).toFloat()
//                    if (Math.random() * 100 < 25) {
//                        values.add(BarEntry(i, `val`, context.getDrawable(R.drawable.star)))
//                    } else {
//                        values.add(BarEntry(i.toFloat(), `val`))
//                    }
//                    i++
//                }
//            }

            val set1: BarDataSet

            if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0
            ) {
                set1 = chart.getData().getDataSetByIndex(0) as BarDataSet
                set1.setValues(values)
                set1.colors = chartColors
                chart.getData().notifyDataChanged()
                chart.notifyDataSetChanged()
            } else {
                set1 = BarDataSet(values, "")
                set1.setDrawIcons(false)
                set1.colors = chartColors

                val dataSets = java.util.ArrayList<IBarDataSet>()
                dataSets.add(set1)
                val data = BarData(dataSets)
                data.setValueTextSize(0f)
                data.isHighlightEnabled=true
                data.barWidth = 0.6f
                chart.setData(data)
            }


            /* barChart.setBarWidth(50)
            barChart.setRadius(0f)
            item.monthWiseEmissions.forEachIndexed { index, monthWiseEmissionsRes ->

                if(monthWiseEmissionsRes.co2Value!=null){
                    barChart.setColor(item.colorCode,context)
                    barChart.addData(monthWiseEmissionsRes.co2Value!!.toFloat(), "")

                }else{
                    barChart.setColor(item.colorCode,context)
                    barChart.addData(0.0f, "")

                }*/
//                monthWiseEmissionsRes.co2Value?.let {
//                    if (it <= 0.0){
//                        val colorCode="#FFFFFF"
//                        barChart.setColor(colorCode,context)
//                        barChart.addData(1.0f, "")
//                    }else {
//                        barChart.addData(it.toFloat(), "")
//                        barChart.setColor(item.colorCode,context)
//                    }
//                }?:run {
//                    barChart.addData(1.0f, "")
//                    barChart.setColor(item.colorCode,context)
//                }



        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val itemBinding: ItemYearlyCo2TrendBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}

fun getScreenWidth(context: Context): Int {
    val wm = context.getSystemService(WINDOW_SERVICE) as WindowManager
    val dm = DisplayMetrics()
    wm.defaultDisplay.getMetrics(dm)
    return (dm.widthPixels - dpToPx(5))
}