package de.fast2work.mobility.ui.upload.step.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.databinding.ItemUploadDocBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.utility.extension.clickWithDebounce
import de.fast2work.mobility.utility.extension.getFileSize
import de.fast2work.mobility.utility.preference.EasyPref
import java.io.File
import java.math.RoundingMode

class UploadImageAdapter(
    var context: Context,
    var list: ArrayList<File>,
    var clickListener: (view: View, model: File, position: Int) -> Unit = { _: View, _: File, _: Int -> }
) : RecyclerView.Adapter<UploadImageAdapter.ViewHolder>() {

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
            tvPdfName.text = item.name
            tvFileSize.text="${item.getFileSize()?.toBigDecimal()?.setScale(2, RoundingMode.UP)?.toDouble()} MB"

            if (item.name.contains(".pdf", true)) {
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
        }

    }


    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val itemBinding: ItemUploadDocBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}