package de.fast2work.mobility.ui.profile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.data.response.CountryList
import de.fast2work.mobility.databinding.ItemCountryCodeBinding
import de.fast2work.mobility.utility.customview.countrypicker.CountryPicker
import de.fast2work.mobility.utility.extension.clickWithDebounce
import de.fast2work.mobility.utility.extension.toBlankString


class CountryCodeAdapter(var context: Context, var list: ArrayList<CountryList>, var clickListener: (view: View, model: CountryList, position: Int) -> Unit = { _: View, _: CountryList, _: Int -> }) : RecyclerView.Adapter<CountryCodeAdapter.ViewHolder>(), Filterable {
    private var filteredList: List<CountryList> = list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCountryCodeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val item = filteredList[holder.absoluteAdapterPosition]
        holder.itemBinding.apply {
            ivCountry.setImageBitmap(CountryPicker.loadImageFromAssets(context, item.countryCode.toBlankString()))
            tvCountryName.text = item.name
            tvCountryCode.text = item.iso
            clMainCode.clickWithDebounce {
                clickListener(clMainCode, item, position)
            }

        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val queryString = constraint.toBlankString()
                filteredList = if (queryString.isEmpty()) {
                    list
                } else {
                    list.filter { it.name!!.contains(queryString, ignoreCase = true) }
                }

                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }


            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as ArrayList<CountryList>
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    class ViewHolder(val itemBinding: ItemCountryCodeBinding) : RecyclerView.ViewHolder(itemBinding.root)
}