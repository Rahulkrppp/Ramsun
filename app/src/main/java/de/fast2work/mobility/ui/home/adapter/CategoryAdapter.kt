package de.fast2work.mobility.ui.home.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.BudgetGroup
import de.fast2work.mobility.data.response.BudgetItem
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.databinding.ItemFullTimeBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.utility.extension.backgroundColorTint
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.preference.EasyPref

class CategoryAdapter (var context: Context, var list: ArrayList<BudgetItem>,
                       var clickListener: (view: View, model: BudgetItem, position: Int) -> Unit = { _: View, _: BudgetItem, _: Int -> }) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemFullTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val item=list[holder.absoluteAdapterPosition]
        holder.itemBinding.apply {

            tvItem.text=item.budgetGroupName

            if (item.isSelect){
                tvItem.background = ContextCompat.getDrawable(context, R.drawable.bg_radius_10)
                tvItem.backgroundColorTint(BaseApplication.tenantSharedPreference.getTenantPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)?.brandingInfo?.primaryColor)
                tvItem.setTextColor((ColorStateList.valueOf(context.getColorFromAttr(R.attr.colorSelected))))
            }else{
                tvItem.background = null
                tvItem.setTextColor(ColorStateList.valueOf(context.getColorFromAttr(R.attr.colorUnSelected)))
            }


            tvItem.setOnClickListener {
               clickListener(tvItem,item,position)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    class ViewHolder(val itemBinding: ItemFullTimeBinding) : RecyclerView.ViewHolder(itemBinding.root)
}