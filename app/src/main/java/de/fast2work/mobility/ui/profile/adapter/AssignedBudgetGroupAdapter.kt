package de.fast2work.mobility.ui.profile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.data.response.AssignedBudgetGroups
import de.fast2work.mobility.databinding.ItemAssignedBudgetBinding


class AssignedBudgetGroupAdapter (var context: Context, var list: ArrayList<AssignedBudgetGroups>) : RecyclerView.Adapter<AssignedBudgetGroupAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAssignedBudgetBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val item=list[holder.absoluteAdapterPosition]
        holder.itemBinding.apply {
            tvAssigned.text=/*"● "+*/item.budgetGroupName

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    class ViewHolder(val itemBinding: ItemAssignedBudgetBinding) : RecyclerView.ViewHolder(itemBinding.root)
}