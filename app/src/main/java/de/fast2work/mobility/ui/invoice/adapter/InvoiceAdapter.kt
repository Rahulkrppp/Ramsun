package de.fast2work.mobility.ui.invoice.adapter

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.InvoiceApiResponse
import de.fast2work.mobility.databinding.ItemInvoiceBinding
import de.fast2work.mobility.utility.extension.SCREEN_WIDTH
import de.fast2work.mobility.utility.extension.capitalized
import de.fast2work.mobility.utility.extension.clickWithDebounce
import de.fast2work.mobility.utility.extension.formatCurrency
import de.fast2work.mobility.utility.extension.getString
import de.fast2work.mobility.utility.extension.toDDMMYYYY
import java.util.Collections
import kotlin.collections.Collection as Collection1

class InvoiceAdapter(
    var context: Context,
    var list: ArrayList<InvoiceApiResponse>,
    var isVisibilityInvoiceType: Boolean = false,
    var clickListener: (view: View, model: InvoiceApiResponse, position: Int) -> Unit = { _: View, _: InvoiceApiResponse, _: Int -> }
) : RecyclerView.Adapter<InvoiceAdapter.ViewHolder>() {
    var popupWindow:PopupWindow?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemInvoiceBinding.inflate(
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
            clMain.setOnClickListener {
                clickListener(clMain, item, position)
            }
            tvInvoiceName.text = if(item.supplierName.isNullOrEmpty()){
                getString(R.string.n_a)
            }else{
                item.supplierName.toString()
            }

            if (item.approvalStatus.toString().lowercase() == "rejected" || item.approvalStatus.toString().lowercase() == "approved") {
                if (item.approvalStatus.toString().lowercase() == "rejected") {
                    tvInvoiceStatus.text = getString(R.string.rejected).capitalized()
                } else if (item.approvalStatus.toString().lowercase() == "approved") {
                    tvInvoiceStatus.text = getString(R.string.approved).capitalized()
                }


                when (item.approvalStatus?.lowercase()) {
                    "pending" -> {
                        tvInvoiceStatus.setTextColor(ContextCompat.getColor(context, R.color.color_pending))
                        ivStatusBullet.setColorFilter(ContextCompat.getColor(context, R.color.color_pending))
                    }

                    "inreview" -> {
                        tvInvoiceStatus.setTextColor(ContextCompat.getColor(context, R.color.color_pending))
                        ivStatusBullet.setColorFilter(ContextCompat.getColor(context, R.color.color_pending))
                    }

                    "rejected" -> {
                        tvInvoiceStatus.setTextColor(ContextCompat.getColor(context, R.color.color_rejected))
                        ivStatusBullet.setColorFilter(ContextCompat.getColor(context, R.color.color_rejected))
                    }

                    "approved" -> {
                        tvInvoiceStatus.setTextColor(ContextCompat.getColor(context, R.color.color_approved))
                        ivStatusBullet.setColorFilter(ContextCompat.getColor(context, R.color.color_approved))
                    }
                }
            }else{
                if (item.captureCo2){
                    tvInvoiceStatus.text = getString(R.string.co2_data_missing)
                    tvInvoiceStatus.setTextColor(ContextCompat.getColor(context, R.color.color_80A500))
                    ivStatusBullet.setColorFilter(ContextCompat.getColor(context, R.color.color_80A500))
                }else {
                    if (item.approvalStatus.toString()
                            .lowercase() == "pending" || item.approvalStatus.toString()
                            .lowercase() == "inreview"
                    ) {
                        tvInvoiceStatus.text = getString(R.string.pending).capitalized()
                    }

                    when (item.approvalStatus?.lowercase()) {
                        "pending" -> {
                            tvInvoiceStatus.setTextColor(ContextCompat.getColor(context, R.color.color_pending))
                            ivStatusBullet.setColorFilter(ContextCompat.getColor(context, R.color.color_pending))
                        }

                        "inreview" -> {
                            tvInvoiceStatus.setTextColor(ContextCompat.getColor(context, R.color.color_pending))
                            ivStatusBullet.setColorFilter(ContextCompat.getColor(context, R.color.color_pending))
                        }

                       /* "rejected" -> {
                            tvInvoiceStatus.setTextColor(ContextCompat.getColor(context, R.color.color_rejected))
                            ivStatusBullet.setColorFilter(ContextCompat.getColor(context, R.color.color_rejected))
                        }

                        "approved" -> {
                            tvInvoiceStatus.setTextColor(ContextCompat.getColor(context, R.color.color_approved))
                            ivStatusBullet.setColorFilter(ContextCompat.getColor(context, R.color.color_approved))
                        }*/
                    }
                }
            }


            tvInvoiceDate.text = if (item.invoiceDate.isNullOrEmpty()) {
                getString(R.string.n_a)
            } else {
                item.invoiceDate.toDDMMYYYY()
            }
            if (isVisibilityInvoiceType) {
                tvInvoiceType.isVisible = false
                ivTypeBullet.isVisible=false
            } else {
                ivTypeBullet.isVisible=true
                tvInvoiceType.isVisible = true
                tvInvoiceType.text = if(item.categoryName.isNullOrEmpty()){
                    getString(R.string.n_a)
                }else{
                    if(item.categoryName.size==1){
                        item.categoryName[0]
                    }else{
                        tvInvoiceType.clickWithDebounce {

//                            item.categoryName?.forEach {
//
//                            }

                            toShowInfoPopup(tvInvoiceType,TextUtils.join(", ",item.categoryName))
                        }
                        "${item.categoryName[0]}+${item.categoryName.size-1}"
                    }

                }
            }

            tvInvoiceAmount.text =  item.grossAmount?.toFloat()?.formatCurrency(item.currencySymbol?:"")

        }
    }

    private fun toShowInfoPopup(view: View,msg:String){
        val layoutInflater : LayoutInflater = context.getSystemService(
            LAYOUT_INFLATER_SERVICE
        ) as LayoutInflater
        val popupView: View = layoutInflater.inflate(R.layout.layout_info, null)
        val tvMessage : AppCompatTextView = popupView.findViewById(R.id.tvMessage) as AppCompatTextView
        tvMessage.setText(msg)
        popupWindow = PopupWindow(popupView, (SCREEN_WIDTH * 0.55).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow?.showAsDropDown(view, 50, 50, Gravity.TOP)
    }
    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val itemBinding: ItemInvoiceBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}