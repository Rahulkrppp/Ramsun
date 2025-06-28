package de.fast2work.mobility.ui.savingandrecpit.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.R
import de.fast2work.mobility.data.model.FilterModel
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.databinding.ItemPreferredLanBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.utility.extension.buttonTextColorNotification
import de.fast2work.mobility.utility.extension.clickWithDebounce
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.imageTickTint
import de.fast2work.mobility.utility.preference.EasyPref

class FilterAdapter(
    var context: Context,
    var list: ArrayList<FilterModel>,
    var clickListener: (view: View, model: FilterModel, position: Int) -> Unit = { _: View, _: FilterModel, _: Int -> }
) : RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPreferredLanBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    var tenantInfoData = BaseApplication.tenantSharedPreference.getTenantPrefModel(
        EasyPref.TENANT_DATA,
        TenantInfoModel::class.java
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val item = list[holder.absoluteAdapterPosition]
        holder.itemBinding.apply {
            ivCountry.visibility = View.GONE
            tvCountryName.text = item.name
            if (item.isSelected) {
                ivTick.isVisible = true
                tvCountryName.typeface = ResourcesCompat.getFont(context, R.font.poppins_bold_700)
                // tvCountryName.setTextColor(getColor(R.color.color_primary))
                tvCountryName.buttonTextColorNotification(tenantInfoData?.brandingInfo?.primaryColor)
                ivTick.imageTickTint(tenantInfoData?.brandingInfo?.primaryColor)
                // tvCountryName.setTextColor(context.getColorFromAttr(R.attr.colorEditTextBorder))
            } else {
                ivTick.isVisible = false
                tvCountryName.typeface =
                    ResourcesCompat.getFont(context, R.font.poppins_regular_400)
                tvCountryName.setTextColor(context.getColorFromAttr(R.attr.colorTextView))
            }
            clMainCode.clickWithDebounce {
                list.forEachIndexed { index, preferredLanguageModel ->
                    preferredLanguageModel.isSelected = false
                }
                item.isSelected != item.isSelected
                clickListener(clMainCode, item, position)
                notifyDataSetChanged()
            }

        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val itemBinding: ItemPreferredLanBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}