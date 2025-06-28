package de.fast2work.mobility.ui.co2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.ItemTransportDetails
import de.fast2work.mobility.data.response.SourceDestination
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.databinding.ItemCo2ModeTransporstBinding
import de.fast2work.mobility.databinding.ItemSearchBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.utility.extension.buttonTextColorNotification
import de.fast2work.mobility.utility.extension.clickWithDebounce
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.imageTickTint
import de.fast2work.mobility.utility.preference.EasyPref

class TransportDetailsAdapter(var context: Context, var list: ArrayList<ItemTransportDetails>,
                              var clickListener: (view: View, model: ItemTransportDetails, position: Int) -> Unit = { _: View, _: ItemTransportDetails, _: Int -> }
) : RecyclerView.Adapter<TransportDetailsAdapter.ViewHolder>() {
    var tenantInfoData = BaseApplication.tenantSharedPreference.getTenantPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCo2ModeTransporstBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val item=list[holder.absoluteAdapterPosition]
        holder.itemBinding.apply {
            tvModeTransport.text=item.label
            if (item.isSelected){
                ivTick.isVisible=true
                tvModeTransport.typeface= ResourcesCompat.getFont(context, R.font.poppins_bold_700)
                // tvCountryName.setTextColor(getColor(R.color.color_primary))
                tvModeTransport.buttonTextColorNotification(tenantInfoData?.brandingInfo?.primaryColor)
                ivTick.imageTickTint(tenantInfoData?.brandingInfo?.primaryColor)
                // tvCountryName.setTextColor(context.getColorFromAttr(R.attr.colorEditTextBorder))
            }else{
                ivTick.isVisible=false
                tvModeTransport.typeface= ResourcesCompat.getFont(context, R.font.poppins_regular_400)
                tvModeTransport.setTextColor(context.getColorFromAttr(R.attr.colorTextView))
            }
            clMainModeOfTransport.clickWithDebounce {
                list.forEachIndexed { index, itemTransportDetails ->
                    itemTransportDetails.isSelected=false
                }
                item.isSelected = !item.isSelected
                clickListener(clMainModeOfTransport,item,holder.absoluteAdapterPosition)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    class ViewHolder(val itemBinding: ItemCo2ModeTransporstBinding) : RecyclerView.ViewHolder(itemBinding.root)
}