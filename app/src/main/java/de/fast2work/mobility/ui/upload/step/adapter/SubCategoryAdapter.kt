package de.fast2work.mobility.ui.upload.step.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.SelectCategoryDataItem
import de.fast2work.mobility.data.response.SubCategoryItem
import de.fast2work.mobility.databinding.ItemSubcategoryBinding

//
//class SubCategoryAdapter (var context: Context, var parentPosition: Int,
//                          var list: ArrayList<SubCategoryItem>, var clickListener: (view: View, model: SelectCategoryDataItem, position: Int) -> Unit = { _: View, _: SelectCategoryDataItem, _: Int -> }) : RecyclerView.Adapter<SubCategoryAdapter.ViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        return ViewHolder(ItemSubcategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.setIsRecyclable(false)
//        val item = list[holder.absoluteAdapterPosition]
//
//        holder.itemBinding.apply {
//
//            list.forEach {
//                val rbn = RadioButton(context)
//                rbn.setId(View.generateViewId())
//                rbn.text = it.subCategoryName
//                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
//                rbn.setLayoutParams(params)
//                rgExpense.addView(rbn)
//            }
//
//        }
//    }
//
//    override fun getItemCount(): Int {
//        Log.e("=======","list ::${list.size}")
//        return list.size
//    }
//
//    class ViewHolder(val itemBinding: ItemSubcategoryBinding) : RecyclerView.ViewHolder(itemBinding.root)
//
//
//}

class SubCategoryAdapter(var context: Context, var parentPosition: Int,private val options: List<SubCategoryItem>) :
    RecyclerView.Adapter<SubCategoryAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val radioGroup: RadioGroup = itemView.findViewById(R.id.rg_expense)

        init {
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                selectedPosition = options.indexOfFirst { it.subCategoryId == checkedId }
                notifyDataSetChanged()
            }
        }

        fun bind(option: SubCategoryItem, position: Int) {
            radioGroup.clearCheck()
            val radioButton = RadioButton(itemView.context)
            radioButton.text = option.subCategoryName
            radioButton.id = option.subCategoryId?:0

            radioGroup.addView(radioButton)

            radioGroup.check(if (position == selectedPosition) option.subCategoryId?:0 else -1)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subcategory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(options[position], position)
    }

    override fun getItemCount(): Int = options.size
}