package de.fast2work.mobility.ui.invoice.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.InvoiceDetailsApiResponse.InvoiceTransaction
import de.fast2work.mobility.databinding.ItemInvoiceDetailsMultipleLineBinding
import de.fast2work.mobility.utility.extension.capitalized
import de.fast2work.mobility.utility.extension.formatCurrency
import de.fast2work.mobility.utility.extension.getDrawableFromAttr
import de.fast2work.mobility.utility.extension.getString
import de.fast2work.mobility.utility.extension.toDDMMYYYY


class InvoiceDetailsAdapter(
    var context: Context,
    var list: ArrayList<InvoiceTransaction>,
    var currencySymbol: String, var currencyCode: String,var employeeRemarks:String,
    var clickListener: (view: View, model: InvoiceTransaction, position: Int) -> Unit = { _: View, _: InvoiceTransaction, _: Int -> }
) : RecyclerView.Adapter<InvoiceDetailsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemInvoiceDetailsMultipleLineBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("StringFormatMatches", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val item = list[holder.absoluteAdapterPosition]
        holder.itemBinding.apply {
            clMain.setOnClickListener {
                clickListener(clMain, item, position)
            }

            if (list.size>1){
                tvLineItem.text= context.getString(R.string.line_item_details, position + 1)
                if (position==0){
                    clChlid.isVisible = true
                    item.visibility = true
                    tvLineItem.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(context.getDrawableFromAttr(R.attr.imgUpArrowInvoice)), null)
                }
                consMain.setOnClickListener {
                    if (item.visibility) {
                        clChlid.isVisible = false
                        item.visibility = false
                        tvLineItem.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(context.getDrawableFromAttr(R.attr.imgDownArrowInvoice)), null)
                    } else {
                        clChlid.isVisible = true
                        item.visibility = true
                        tvLineItem.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(context.getDrawableFromAttr(R.attr.imgUpArrowInvoice)), null)
                    }
                }
            }else{
                clChlid.isVisible = true
                item.visibility = true
                tvLineItem.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                tvLineItem.text= context.getString(R.string.line_item_details_whithout_)
            }



            tvPaidByValue.text =
                item.paidBy?.capitalized()
                    ?.ifEmpty { getString(R.string.n_a) }
            if (item.expenseType.isNullOrEmpty()){
                tvExpenseForValue.text = getString(R.string.n_a)
            }else{
                tvExpenseForValue.text = item.expenseType
            }

            tvStartDateValueProduct.text =
                if (item.startDate?.toDDMMYYYY() == null) {
                    getString(R.string.n_a)
                } else {
                    item.startDate?.toDDMMYYYY()
                }

            tvEndDateValueProduct.text =
                if (item.endDate?.toDDMMYYYY() == null) {
                    getString(R.string.n_a)
                } else {
                    item.endDate?.toDDMMYYYY()
                }
            tvCategoryValue.text =
                if (!item.categoryName.isNullOrEmpty()) {
                    item.categoryName
                } else {
                    getString(R.string.n_a)
                }
            tvSubCategoryValue.text =
                if (!item.subCategoryName.isNullOrEmpty()) {
                    item.subCategoryName
                } else {
                    getString(R.string.n_a)
                }

            if (item.description?.isNullOrEmpty() == true) {
                tvProductDescriptionValue.text = getString(R.string.n_a)
            } else {
                tvProductDescriptionValue.text =
                    item.description
                        ?: getString(R.string.n_a)
            }
            tvSubCategoryValue.text =
                if (!item.subCategoryName.isNullOrEmpty()) {
                    item.subCategoryName
                } else {
                    getString(R.string.n_a)
                }

            if (currencySymbol.isNullOrEmpty()) {
                tvNetAmountValue.text = item.netAmount?.toFloat()?.formatCurrency(
                    "€" ?: ""
                )
                tvGrosAmountValue.text =
                    item.taxAmount?.toFloat()?.formatCurrency(
                        "€" ?: ""
                    )
                tvTaxAmountValue.text = item.grossAmount?.toFloat()?.formatCurrency(
                    "€" ?: ""
                )
            } else {
                tvNetAmountValue.text = item.netAmount?.toFloat()?.formatCurrency(
                    currencySymbol ?: ""
                )
                tvGrosAmountValue.text =
                    item.grossAmount?.toFloat()?.formatCurrency(
                        currencySymbol ?: ""
                    )
                tvTaxAmountValue.text = item.taxAmount?.toFloat()?.formatCurrency(
                    currencySymbol ?: ""
                )
            }
            tvCurrencyValue.text =
                if (currencyCode.isNullOrEmpty() && currencySymbol.isNullOrEmpty()) {
                    "EUR - (€)"
                } else {
                    currencyCode + " - (" + currencySymbol + ")"
                }
            if (item.contractCode.isNullOrEmpty()) {
                tvCodeValue.text = getString(R.string.n_a)

            } else {
                tvCodeValue.text = item.contractCode

            }
            if (item.productId.isNullOrEmpty()) {
                tvProductIdValue.text = getString(R.string.n_a)
            } else {
                tvProductIdValue.text = item.productId
            }
            tvUniqueIdValue.text = getString(R.string.n_a)
            tvSupplierNameValue.text = getString(R.string.n_a)
            /*tvSelfCommentsValue.text =
                if (!employeeRemarks.isNullOrEmpty()) {
                    employeeRemarks
                } else {
                    getString(R.string.n_a)
                }*/
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val itemBinding: ItemInvoiceDetailsMultipleLineBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}
