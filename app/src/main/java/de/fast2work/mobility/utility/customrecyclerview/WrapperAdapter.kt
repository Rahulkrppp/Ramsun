package com.app.knit.utility.customrecyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

internal class WrapperAdapter(val wrappedAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, private val loadingListItemCreator: RecyclerPaginate.LoadingListItemCreator) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var isDisplayLoadingRow = true
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_VIEW_TYPE_LOADING) {
            loadingListItemCreator.onCreateViewHolder(parent, viewType)
        } else {
            wrappedAdapter.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isLoadingRow(position)) {
            loadingListItemCreator.onBindViewHolder(holder, position)
        } else {
            wrappedAdapter.onBindViewHolder(holder, position)
        }
    }

    override fun getItemCount(): Int {
        return if (isDisplayLoadingRow) wrappedAdapter.itemCount + 1 else wrappedAdapter.itemCount
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoadingRow(position)) ITEM_VIEW_TYPE_LOADING else wrappedAdapter.getItemViewType(position)
    }

    override fun getItemId(position: Int): Long {
        return if (isLoadingRow(position)) RecyclerView.NO_ID else wrappedAdapter.getItemId(position)
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(hasStableIds)
        wrappedAdapter.setHasStableIds(hasStableIds)
    }

    fun displayLoadingRow(displayLoadingRow: Boolean) {
        if (isDisplayLoadingRow != displayLoadingRow) {
            isDisplayLoadingRow = displayLoadingRow
            notifyDataSetChanged()
        }
    }

    fun isLoadingRow(position: Int): Boolean {
        return isDisplayLoadingRow && position == getLoadingRowPosition()
    }

    private fun getLoadingRowPosition(): Int = if (isDisplayLoadingRow) itemCount - 1 else -1

    companion object {
        private const val ITEM_VIEW_TYPE_LOADING = Int.MAX_VALUE - 50 // Magic
    }

}