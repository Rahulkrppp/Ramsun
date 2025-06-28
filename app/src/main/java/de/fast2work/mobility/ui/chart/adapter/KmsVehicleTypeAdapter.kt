package de.fast2work.mobility.ui.chart.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.data.response.KmsVehicleTypeRes
import de.fast2work.mobility.databinding.ItemKmsVehicleTypeBinding

class KmsVehicleTypeAdapter(
    var context: Context, var list: ArrayList<KmsVehicleTypeRes>,
    var clickListener: (view: View, model: KmsVehicleTypeRes, position: Int) -> Unit = { _: View, _: KmsVehicleTypeRes, _: Int -> },
) : RecyclerView.Adapter<KmsVehicleTypeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemKmsVehicleTypeBinding.inflate(
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
            tvVehicleName.text = "${item.vehicleTypeLabel}"
            tvKms.text = (item.kmValue.let { "${item.kmValue} Km" }?:run { "" }).toString()
            ivBgColor.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(item.colorCode.toString()))
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val itemBinding: ItemKmsVehicleTypeBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}