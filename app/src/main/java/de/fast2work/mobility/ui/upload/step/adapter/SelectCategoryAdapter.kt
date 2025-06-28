package de.fast2work.mobility.ui.upload.step.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout.LayoutParams
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.CompoundButtonCompat
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.SelectCategoryDataItem
import de.fast2work.mobility.databinding.ItemSelectCategoryBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.loadImageIcons
import de.fast2work.mobility.utility.extension.textColor
import java.util.Collections.rotate


class SelectCategoryAdapter(
    var secondaryColor: String?,
    var categoryId: Int?=0,
    var subCategoryId: Int?=0,
    var context: Context,
    var list: ArrayList<SelectCategoryDataItem> = arrayListOf(),
    var selectedPostion:Int=0,
    var clickListener: (view: View, model: SelectCategoryDataItem, position: Int, subcategoryId:Int) -> Unit = { _: View, _: SelectCategoryDataItem, _: Int, _: Int -> },
) : RecyclerView.Adapter<SelectCategoryAdapter.ViewHolder>(), Filterable {
    private var filteredList: List<SelectCategoryDataItem> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSelectCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val item = filteredList[holder.absoluteAdapterPosition]
        holder.itemBinding.apply {
            tvCategory.text = item.categoryName


            if (item.categoryIcon != null) {
                ivCategory.loadImageIcons(context,item.categoryIcon)
                ivCategory.setColorFilter(context.getColorFromAttr(R.attr.colorTextView))
            }

            if(item.subCategory != null){
                item.subCategory.forEachIndexed { index, subCategoryItem ->
                    rgExpense.clearCheck()
                    val rbn = AppCompatRadioButton(context)
                    rbn.id = subCategoryItem.subCategoryId?:0
                    rbn.text = subCategoryItem.subCategoryName

                    val padding = context.resources.getDimensionPixelOffset(R.dimen._1sdp)
//                rbn.setPadding(padding, padding, padding, padding) // left, top, right, bottom

                    // Set margin programmatically using LayoutParams
                    val layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT)
                    layoutParams.setMargins(context.resources.getDimensionPixelOffset(R.dimen._45sdp), padding, 0,
                        padding) // left, top, right, bottom
                    rbn.layoutParams = layoutParams
                    addDivider(rgExpense, context)
                    rgExpense.addView(rbn)
                }
            }


           // if (selectedPostion==position){
                if (categoryId!=0) {
                    Log.e("=============","categoryId:: $categoryId")
                    if (categoryId == item.categoryId) {
                        clExpense.background = setStrokeDrawable(R.drawable.selection_box_radius_10)
                        item.subCategory.forEachIndexed { index, subCategoryItem ->
                            if (subCategoryId != 0) {
                                if (subCategoryId == subCategoryItem.subCategoryId) {
                                    val selectedRadioButton = subCategoryId?.let { rgExpense.findViewById<AppCompatRadioButton>(it) }
                                    if (selectedRadioButton != null) {
                                        selectedRadioButton.isChecked = true
                                        setRadioButtonSelectedColor(selectedRadioButton, context)
                                    }
                                }
                            }
                        }
                    //}
                }
            }

            rgExpense.setOnCheckedChangeListener { group, checkedId ->

                for (i in 0 until group.childCount) {
                    val radioButton = group.getChildAt(i) as? AppCompatRadioButton
                    // Check if the child is a RadioButton
                    if (radioButton != null && radioButton.id != checkedId) {
                        // Set text color to black for unchecked radio buttons
                        setRadioButtonChecked(radioButton, context)
                    }
                }



                val selectedRadioButton = group.findViewById<AppCompatRadioButton>(checkedId)
                if (selectedRadioButton != null) {
                    setRadioButtonSelectedColor(selectedRadioButton, context)
                }
                clExpense.background = setStrokeDrawable(R.drawable.selection_box_radius_10)
                categoryId = item.categoryId
                subCategoryId = checkedId
                clickListener(rgExpense, item, position, checkedId)
            }



            expandCollapseView(context, holder.itemBinding, item)
            clCategoryName.setOnClickListener {
                categoryId = 0
                subCategoryId = 0
                selectedPostion = position
                clExpense.setBackgroundResource(R.drawable.selected_bg_radius_10)

                filteredList.forEachIndexed { index, it ->
                    if (index == position) {
                        it.isExpanded = !it.isExpanded
                        ivArrow.rotation=180f
                    } else {
                        ivArrow.rotation=0f
                        it.isExpanded = false
                    }
                }
                notifyDataSetChanged()
                expandCollapseView(context, holder.itemBinding, item)
            }
        }
    }

//    override fun getItemCount(): Int {
//        return list.size
//    }

    class ViewHolder(val itemBinding: ItemSelectCategoryBinding) : RecyclerView.ViewHolder(itemBinding.root)

    private fun expandCollapseView(context: Context, binding: ItemSelectCategoryBinding, model: SelectCategoryDataItem) {
        binding.apply {
            binding.clCategory.isVisible = model.isExpanded
            if (model.isExpanded){
                binding.ivArrow.rotation=180f
            }
           // binding.ivArrow.rotation=180f

        }
    }

    private fun addDivider(parent: RadioGroup, context: Context) {
        val divider = View(context)
        divider.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 5) // Divider height
        divider.setBackgroundColor(context.getColorFromAttr(R.attr.colorLine)) // Set divider color
        parent.addView(divider)
    }

    private fun setRadioButtonChecked(rb: AppCompatRadioButton, context: Context) {
        rb.setTextColor(context.getColorFromAttr(com.google.android.material.R.attr.editTextColor))
        CompoundButtonCompat.setButtonTintList(rb,
            ColorStateList.valueOf(context.getColorFromAttr(com.google.android.material.R.attr.editTextColor)))
        rb.typeface = ResourcesCompat.getFont(context, R.font.poppins_regular_400)
//        if (rb.isChecked) {
//            rb.setTextColor(context.getColorFromAttr(com.google.android.material.R.attr.colorSecondary))
//            rb.typeface = ResourcesCompat.getFont(context, R.font.poppins_medium_500)
//            CompoundButtonCompat.setButtonTintList(rb, ColorStateList.valueOf(context.getColorFromAttr(com.google.android.material.R.attr.colorSecondary)))
//        }
    }

    private fun setRadioButtonSelectedColor(rb: AppCompatRadioButton, context: Context) {
        rb.typeface = ResourcesCompat.getFont(context, R.font.poppins_medium_500)
        if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
            CompoundButtonCompat.setButtonTintList(rb, ColorStateList.valueOf(ContextCompat.getColor(context,R.color.color_secondary_dark)))
            rb.setTextColor(context.getColor(R.color.color_secondary_dark))
        }else{
            if (secondaryColor?.isNotEmpty() == true) {
                rb.textColor(secondaryColor)
                CompoundButtonCompat.setButtonTintList(rb, ColorStateList.valueOf(Color.parseColor(secondaryColor)))
            }
        }
    }

    private fun setStrokeDrawable(drawable: Int): GradientDrawable? {
        val drawable = ContextCompat.getDrawable(context,drawable) as? GradientDrawable

        // Check if the drawable is a GradientDrawable and modify its stroke color
        drawable?.let {
            if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
                val strokeColor = Color.parseColor("#49C087")

                it.setStroke(context.resources.getDimensionPixelOffset(R.dimen._2sdp), strokeColor) // Set stroke width and color

            }else{
                val strokeColor = Color.parseColor(secondaryColor)
                it.setStroke(context.resources.getDimensionPixelOffset(R.dimen._2sdp), strokeColor) // Set stroke width and color

            }

        }
        return drawable
    }

   /* override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                *//*val filteredList = mutableListOf<SelectCategoryDataItem>()
                if (constraint.isNullOrBlank()) {
                    filteredList.addAll(list)
                } else {
                    val filterPattern = constraint.toString().toLowerCase().trim()
                    for (item in list) {
                        // Implement your filtering logic here
                        // For example, check if item's title or name matches the search query
                        if (item.categoryName!!.contains(filterPattern,ignoreCase = true) || item.subCategory[0].subCategoryName!!.contains(filterPattern)) {
                            filteredList.add(item)
                        }
                    }
                }*//*

                val queryString = constraint.toString()
                filteredList = if (queryString.isEmpty()) {
                    list
                } else {
                    list.filter {
                        it.categoryName.toString().contains(queryString, ignoreCase = true) || it.subCategory.takeIf { subCategories -> !subCategories.isNullOrEmpty() }?.any { subItems -> subItems.subCategoryName.toString().contains(queryString, ignoreCase = true)} == true
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }


            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null) {
                    if (results.values != null){
                        filteredList = results.values as ArrayList<SelectCategoryDataItem>
                        notifyDataSetChanged()
                    }else{
                        filteredList = list
                    }
                }

            }
        }
    }*/
   override fun getFilter(): Filter {
       return object : Filter() {
           override fun performFiltering(constraint: CharSequence?): FilterResults {

               val queryString = constraint.toString()
               filteredList = if (queryString.isEmpty()) {
                   list
               } else {
                   list.filter {
                       it.categoryName.toString().contains(queryString, ignoreCase = true) || it.subCategory.takeIf { subCategories -> !subCategories.isNullOrEmpty() }?.any { subItems -> subItems.subCategoryName.toString().contains(queryString, ignoreCase = true)} == true
                   }
               }

               val filterResults = FilterResults()
               filterResults.values = filteredList
               return filterResults
           }


           override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
               if (results != null) {
                   if (results.values != null){
                       filteredList = results.values as ArrayList<SelectCategoryDataItem>
                       notifyDataSetChanged()
                   }else{
                       filteredList = list
                   }
               }
           }
       }
   }
    override fun getItemCount(): Int {
        return filteredList.size
    }
}