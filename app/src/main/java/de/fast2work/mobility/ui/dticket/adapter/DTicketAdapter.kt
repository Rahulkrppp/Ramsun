package de.fast2work.mobility.ui.dticket.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.data.response.DTicketRes
import de.fast2work.mobility.databinding.ItemDTicketBinding
import de.fast2work.mobility.utility.util.IConstantsIcon
import de.fast2work.mobility.utility.util.IConstantsIcon.Companion.COMPLETE

class DTicketAdapter(
    var context: Context,
    var list: ArrayList<DTicketRes>,
      var clickButtonPurchaseTicket:String,
    var startDate:String,
    var clickListener: (view: View, model: DTicketRes, position: Int) ->
    Unit = { _: View, _: DTicketRes, _: Int -> }
) : RecyclerView.Adapter<DTicketAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDTicketBinding.inflate(
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
            tvPriceValue.text = "€${item.priceInCents/100}"
            tvDTicket.text = item.productName
            tvDecValue.text=item.productInfo.local_validity_description
            tvStartDateValue.text=startDate
            tvDay.text=" /${item.productInfo.ticket_type}"

            when (clickButtonPurchaseTicket) {
                IConstantsIcon.DISABLE -> {
                tvStartDate.isVisible=false
                tvStartDateValue.isVisible=false
                }

                IConstantsIcon.ACTIVATED -> {
                    tvStartDate.isVisible=true
                    tvStartDateValue.isVisible=true
                }

//                COMPLETE -> {
//                    tvStartDate.isVisible=true
//                    tvStartDateValue.isVisible=true
//                }


            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val itemBinding: ItemDTicketBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}