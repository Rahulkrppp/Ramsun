package de.fast2work.mobility.ui.invoice.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.CategoryApiResponse
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.databinding.ItemFiltersBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.invoice.model.InvoiceStatusModel
import de.fast2work.mobility.utility.extension.clickWithDebounce
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.imageTint
import de.fast2work.mobility.utility.preference.EasyPref

class InvoiceCategoryAdapter(
    var context: Context, var list: ArrayList<CategoryApiResponse.Category>,
    var clickListener: (view: View, model: CategoryApiResponse.Category, position: Int) -> Unit = { _: View, _: CategoryApiResponse.Category, _: Int -> }
) : RecyclerView.Adapter<InvoiceCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFiltersBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val item = list[holder.absoluteAdapterPosition]
        holder.itemBinding.apply {
            tvTitle.text = item.categoryName
            if (item.isSelected) {
                ivCheckbox.imageTint(BaseApplication.tenantSharedPreference.getTenantPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)?.brandingInfo?.primaryColor)
                ivCheckbox.setImageResource(R.drawable.ic_checkbox_selected)
            } else {
                ivCheckbox.setImageResource(R.drawable.ic_checkbox_unselected)
                ivCheckbox.setColorFilter(context.getColorFromAttr(R.attr.colorTextView))
            }
            clItem.clickWithDebounce {
                clickListener(clItem, item, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val itemBinding: ItemFiltersBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}