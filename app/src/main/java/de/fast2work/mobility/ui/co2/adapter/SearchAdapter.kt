package de.fast2work.mobility.ui.co2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.data.response.BudgetGroupInfoItem
import de.fast2work.mobility.data.response.SourceDestination
import de.fast2work.mobility.databinding.ItemSearchBinding
import de.fast2work.mobility.utility.extension.clickWithDebounce

class SearchAdapter (var context: Context, var list: ArrayList<SourceDestination>,
                     var clickListener: (view: View, model: SourceDestination, position: Int) -> Unit = { _: View, _: SourceDestination, _: Int -> }
    ) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val item=list[holder.absoluteAdapterPosition]
        holder.itemBinding.apply {
           tvAddress.text=item.display_place
           subTittle.text=item.compressed_address

            clMainSearch.clickWithDebounce {
                clickListener(clMainSearch,item,holder.absoluteAdapterPosition)
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    class ViewHolder(val itemBinding: ItemSearchBinding) : RecyclerView.ViewHolder(itemBinding.root)
}