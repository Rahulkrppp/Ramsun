package de.fast2work.mobility.ui.sidemenu.notification.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R
import de.fast2work.mobility.data.response.NotificationItem
import de.fast2work.mobility.databinding.ItemNotificationBinding
import de.fast2work.mobility.utility.extension.clickWithDebounce
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.getShortDateFromTimeStamp
import de.fast2work.mobility.utility.extension.getShortDateFromTimeStampWithAgo

class NotificationAdapter (var context: Context, var list: ArrayList<NotificationItem>, var clickListener: (view: View, model: NotificationItem, position: Int) -> Unit = { _: View, _: NotificationItem, _: Int -> }) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[holder.absoluteAdapterPosition]
        holder.itemBinding.apply {
            tvMsgDisplay.text= HtmlCompat.fromHtml(item.notification.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
            ivDelete.clickWithDebounce {
                clickListener(it, item, holder.bindingAdapterPosition)
            }
            clMainNotification.clickWithDebounce {
                clickListener(clMainNotification, item, holder.bindingAdapterPosition)
            }
            if (item.isRead== "0"){
                clMainNotification.backgroundTintList = (ColorStateList.valueOf(context.getColorFromAttr(de.fast2work.mobility.R.attr.color_primary_alpha_5)))
            }else{
                clMainNotification.setBackgroundColor(context.getColorFromAttr(R.attr.backgroundColor))
            }
            tvTimeDisplay.text=item.notificationTime?.getShortDateFromTimeStamp(context)
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val itemBinding: ItemNotificationBinding) : RecyclerView.ViewHolder(itemBinding.root)
}