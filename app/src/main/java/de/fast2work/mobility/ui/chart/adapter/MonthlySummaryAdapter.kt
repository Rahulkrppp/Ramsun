package de.fast2work.mobility.ui.chart.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.R

import de.fast2work.mobility.databinding.ItemMonthlySummaryBinding
import de.fast2work.mobility.ui.chart.Month


class MonthlySummaryAdapter(var context: Context, var list: ArrayList<Month>,
                            var clickListener: (view: View, model: Month, position: Int) -> Unit = { _: View, _: Month, _: Int -> },
) : RecyclerView.Adapter<MonthlySummaryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMonthlySummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val item = list[holder.absoluteAdapterPosition]
        holder.itemBinding.apply {
            if (position==0){
                llData.setBackgroundColor(Color.parseColor("#0D4CAC87"))
                tvMonth.typeface = ResourcesCompat.getFont(context, R.font.poppins_medium_500)
                tvJan.typeface = ResourcesCompat.getFont(context, R.font.poppins_medium_500)
                tvFeb.typeface = ResourcesCompat.getFont(context, R.font.poppins_medium_500)
                tvMar.typeface = ResourcesCompat.getFont(context, R.font.poppins_medium_500)
                tvApr.typeface = ResourcesCompat.getFont(context, R.font.poppins_medium_500)
                tvMay.typeface = ResourcesCompat.getFont(context, R.font.poppins_medium_500)
                tvJun.typeface = ResourcesCompat.getFont(context, R.font.poppins_medium_500)
                tvJul.typeface = ResourcesCompat.getFont(context, R.font.poppins_medium_500)
                tvAug.typeface = ResourcesCompat.getFont(context, R.font.poppins_medium_500)
                tvSep.typeface = ResourcesCompat.getFont(context, R.font.poppins_medium_500)
                tvOct.typeface = ResourcesCompat.getFont(context, R.font.poppins_medium_500)
                tvNov.typeface = ResourcesCompat.getFont(context, R.font.poppins_medium_500)
                tvDec.typeface = ResourcesCompat.getFont(context, R.font.poppins_medium_500)
            }
                tvMonth.text= item.month
                tvJan.text= item.jan
                tvFeb.text= item.feb
                tvMar.text= item.mar
                tvApr.text= item.apr
                tvMay.text= item.may
                tvJun.text= item.jun
                tvJul.text= item.jul
                tvAug.text= item.aug
                tvSep.text= item.sep
                tvOct.text= item.oct
                tvNov.text= item.nov
                tvDec.text= item.dec
            }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val itemBinding: ItemMonthlySummaryBinding) : RecyclerView.ViewHolder(itemBinding.root)
}