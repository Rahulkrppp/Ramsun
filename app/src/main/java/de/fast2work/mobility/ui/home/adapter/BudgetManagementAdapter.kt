package de.fast2work.mobility.ui.home.adapter

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Outline
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.BudgetGroupInfoItem
import de.fast2work.mobility.databinding.ItemBudgetManagementBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.utility.extension.capitalized
import de.fast2work.mobility.utility.extension.formatCurrency
import de.fast2work.mobility.utility.extension.formatCurrencyNew
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.loadImageIcons
import kotlin.math.roundToInt


class BudgetManagementAdapter(
    var context: Context, var list: ArrayList<BudgetGroupInfoItem>,
    var clickListener: (view: View, model: BudgetGroupInfoItem, position: Int) -> Unit = { _: View, _: BudgetGroupInfoItem, _: Int -> },
) : RecyclerView.Adapter<BudgetManagementAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemBudgetManagementBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val item = list[holder.absoluteAdapterPosition]
        holder.itemBinding.apply {
            tvCategory.text = item.categoryName?.capitalized()
            tvCategoryPrice.text = item.unusedBudget.formatCurrencyNew(item.currencySymbol?:"")
            //progressBar.progress = item.getBudgetProgressValue().roundToInt()
            //progressBar.setIndicatorColor(Color.parseColor(item.colorCode))


            item.amountInformation?.let {
                val totalValue = it.availableAmount + it.usedAmount + it.pendingAmount
                val availableProgress = ((100 * it.availableAmount)/totalValue)
                val usedProgress = ((100 * it.usedAmount)/totalValue)
                val pendingProgress = ((100 * it.pendingAmount)/totalValue)

                if (totalValue>0) {
                    progressBarNew.setProgress(
                        availableProgress.roundToInt(),
                        usedProgress.roundToInt(),
                        pendingProgress.roundToInt(),
                        it.colors?.availableColor.toString(),
                        it.colors?.usedColor.toString(),
                        it.colors?.pendingColor.toString()
                    )
                }else{
                    if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
                        progressBarNew.setProgress(100,0,0,"#4D274072","#4D274072","#4D274072")
                    }else {
                        progressBarNew.setProgress(100,0,0,"#D9D9D9","#D9D9D9","#D9D9D9")
                    }

                }
                progressBarNew.setClipToOutline(true) // Clips the view to the outline
                progressBarNew.setOutlineProvider(object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        val cornerRadius = 20
                        outline.setRoundRect(0, 0, view.width, view.height, cornerRadius.toFloat())
                    }
                })
            }


//            val trackColor = ColorUtils.setAlphaComponent(Color.parseColor(item.colorCode), (255 * 0.2).toInt())
//            progressBar.trackColor = trackColor
            clMail.setOnClickListener {
                clickListener(clMail, item, position)
            }

//            ivIcon.setImageResource(R.drawable.ic_no_image)
            if (item.categoryIcon != null) {
                ivIcon.loadImageIcons(context,item.categoryIcon)
                ivIcon.setColorFilter(context.getColorFromAttr(R.attr.colorTextView))
            } /*else {
//                ivIcon.loadCircleImage("")
            }*/
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val itemBinding: ItemBudgetManagementBinding) : RecyclerView.ViewHolder(itemBinding.root)
}