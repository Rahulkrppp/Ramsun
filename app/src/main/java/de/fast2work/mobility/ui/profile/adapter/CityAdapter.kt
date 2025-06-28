package de.fast2work.mobility.ui.profile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.data.response.CityResponse
import de.fast2work.mobility.databinding.ItemCountryCodeBinding
import de.fast2work.mobility.utility.extension.clickWithDebounce
import de.fast2work.mobility.utility.extension.toBlankString


class CityAdapter(var context: Context, var list: ArrayList<CityResponse.City>, var clickListener: (view: View, model: CityResponse.City, position: Int) ->
Unit = { _: View, _: CityResponse.City, _: Int -> }) : RecyclerView.Adapter<CityAdapter.ViewHolder>(), Filterable {
    private var filteredList: List<CityResponse.City> = list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCountryCodeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val item = filteredList[holder.absoluteAdapterPosition]
        holder.itemBinding.apply {
            ivCountry.visibility = View.GONE
            tvCountryName.text = item.cityName
            tvCountryCode.visibility = View.GONE
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
                    list.filter { it.cityName!!.contains(queryString, ignoreCase = true) }
                }

                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }


            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as ArrayList<CityResponse.City>
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    class ViewHolder(val itemBinding: ItemCountryCodeBinding) : RecyclerView.ViewHolder(itemBinding.root)
}