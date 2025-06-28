package de.fast2work.mobility.ui.invoice.invoicedetails.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.InvoiceRefDocListRes
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.databinding.ItemUploadDocBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.utility.extension.clickWithDebounce
import de.fast2work.mobility.utility.extension.findUrlName
import de.fast2work.mobility.utility.extension.getFileSizeFromUrl
import de.fast2work.mobility.utility.preference.EasyPref


class ShowDocImageAdapter (var context: Context,
var list: ArrayList<InvoiceRefDocListRes>,
var clickListener: (view: View, model: InvoiceRefDocListRes, position: Int) -> Unit = { _: View, _: InvoiceRefDocListRes, _: Int -> }
) : RecyclerView.Adapter<ShowDocImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemUploadDocBinding.inflate(
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


            tvPdfName.text = findUrlName(item.url.toString())

            ivClose.setImageResource(R.drawable.ic_download)

            tvFileSize.text=item.url?.getFileSizeFromUrl()

            if (item.url?.contains(".pdf", true) == true) {
                Glide.with(context).load(R.drawable.ic_pdf_display).placeholder(
                    ContextCompat.getDrawable(context, R.drawable.ic_pdf_display)
                ).into(ivUrlLogo)
            } else {
                Glide.with(context).load(R.drawable.ic_img_display).placeholder(
                    ContextCompat.getDrawable(context, R.drawable.ic_img_display)
                ).into(ivUrlLogo)
            }
            ivClose.clickWithDebounce {
                clickListener(ivClose,item,holder.absoluteAdapterPosition)
            }
            cardViewDoc.clickWithDebounce {
                clickListener(cardViewDoc,item,holder.absoluteAdapterPosition)
            }
        }

    }


    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val itemBinding: ItemUploadDocBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}