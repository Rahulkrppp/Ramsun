package com.app.knit.utility.customrecyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import de.fast2work.mobility.R
import de.fast2work.mobility.utility.customrecyclerview.Paginate

class RecyclerPaginate internal constructor(private val recyclerView: RecyclerView, private val callbacks: Callbacks, private val loadingTriggerThreshold: Int, addLoadingListItem: Boolean,
                                            loadingListItemCreator: LoadingListItemCreator?, loadingListItemSpanLookup: LoadingListItemSpanLookup?) : Paginate() {
    private var wrapperAdapter: WrapperAdapter? = null
    private var wrapperSpanSizeLookup: WrapperSpanSizeLookup? = null
    override fun setHasMoreDataToLoad(hasMoreDataToLoad: Boolean) {
        if (wrapperAdapter != null) {
            wrapperAdapter!!.displayLoadingRow(hasMoreDataToLoad)
        }
    }

    override fun unbind() {
        recyclerView.removeOnScrollListener(mOnScrollListener) // Remove scroll listener
        if (recyclerView.adapter is WrapperAdapter) {
            val wrapperAdapter = recyclerView.adapter as WrapperAdapter?
            val adapter = wrapperAdapter!!.wrappedAdapter
            adapter.unregisterAdapterDataObserver(mDataObserver) // Remove data observer
            recyclerView.adapter = adapter // Swap back original adapter
        }
        if (recyclerView.layoutManager is GridLayoutManager && wrapperSpanSizeLookup != null) {
            // Swap back original SpanSizeLookup
            val spanSizeLookup = wrapperSpanSizeLookup!!.wrappedSpanSizeLookup
            (recyclerView.layoutManager as GridLayoutManager?)!!.spanSizeLookup = spanSizeLookup
        }
    }

    fun checkEndOffset() {
        val visibleItemCount = recyclerView.childCount
        val totalItemCount = recyclerView.layoutManager!!.itemCount
        val firstVisibleItemPosition: Int
        firstVisibleItemPosition = if (recyclerView.layoutManager is LinearLayoutManager) {
            (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
        } else if (recyclerView.layoutManager is StaggeredGridLayoutManager) {
            // https://code.google.com/p/android/issues/detail?id=181461
            if (recyclerView.layoutManager!!.getChildCount() > 0) {
                (recyclerView.layoutManager as StaggeredGridLayoutManager?)!!.findFirstVisibleItemPositions(null)[0]
            } else {
                0
            }
        } else {
            throw IllegalStateException("LayoutManager needs to subclass LinearLayoutManager or StaggeredGridLayoutManager")
        }

        // Check if end of the list is reached (counting threshold) or if there is no items at all
        if (totalItemCount - visibleItemCount <= firstVisibleItemPosition + loadingTriggerThreshold || totalItemCount == 0) {
            // Call load more only if loading is not currently in progress and if there is more items to load
            if (!callbacks.isLoading() && !callbacks.hasLoadedAllItems()) {
                callbacks.onLoadMore()
            }
        }
    }

    private fun onAdapterDataChanged() {
        wrapperAdapter!!.displayLoadingRow(!callbacks.hasLoadedAllItems())
        checkEndOffset()
    }

    private val mOnScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            checkEndOffset() // Each time when list is scrolled check if end of the list is reached
        }
    }
    private val mDataObserver: RecyclerView.AdapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            wrapperAdapter!!.notifyDataSetChanged()
            onAdapterDataChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            wrapperAdapter!!.notifyItemRangeInserted(positionStart, itemCount)
            onAdapterDataChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            wrapperAdapter!!.notifyItemRangeChanged(positionStart, itemCount)
            onAdapterDataChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            wrapperAdapter!!.notifyItemRangeChanged(positionStart, itemCount, payload)
            onAdapterDataChanged()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            wrapperAdapter!!.notifyItemRangeRemoved(positionStart, itemCount)
            onAdapterDataChanged()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            wrapperAdapter!!.notifyItemMoved(fromPosition, toPosition)
            onAdapterDataChanged()
        }
    }

    class Builder(private val recyclerView: RecyclerView, private val callbacks: Callbacks) {
        private var loadingTriggerThreshold = 5
        private var addLoadingListItem = true
        private var loadingListItemCreator: LoadingListItemCreator? = null
        private var loadingListItemSpanLookup: LoadingListItemSpanLookup? = null

        /**
         * Set the offset from the end of the list at which the load more event needs to be triggered. Default offset
         * if 5.
         *
         * @param threshold number of items from the end of the list.
         */
        fun setLoadingTriggerThreshold(threshold: Int): Builder {
            loadingTriggerThreshold = threshold
            return this
        }

        /**
         * Setup loading row. If loading row is used original adapter set on RecyclerView will be wrapped with
         * internal adapter that will add loading row as the last item in the list. Paginate will observer the
         * changes upon original adapter and remove loading row if there is no more data to load. By default loading
         * row will be added.
         *
         * @param addLoadingListItem true if loading row needs to be added, false otherwise.
         * @see {@link Callbacks.hasLoadedAllItems
         */
        fun addLoadingListItem(addLoadingListItem: Boolean): Builder {
            this.addLoadingListItem = addLoadingListItem
            return this
        }

        /**
         * Set custom loading list item creator. If no creator is set default one will be used.
         *
         * @param creator Creator that will ne called for inflating and binding loading list item.
         */
        fun setLoadingListItemCreator(creator: LoadingListItemCreator?): Builder {
            loadingListItemCreator = creator
            return this
        }

        /**
         * Set custom SpanSizeLookup for loading list item. Use this when [GridLayoutManager] is used and
         * loading list item needs to have custom span. Full span of [GridLayoutManager] is used by default.
         *
         * @param loadingListItemSpanLookup LoadingListItemSpanLookup that will be called for loading list item span.
         */
        fun setLoadingListItemSpanSizeLookup(loadingListItemSpanLookup: LoadingListItemSpanLookup?): Builder {
            this.loadingListItemSpanLookup = loadingListItemSpanLookup
            return this
        }

        /**
         * Create pagination functionality upon RecyclerView.
         *
         * @return [Paginate] instance.
         */
        fun build(): Paginate {
            checkNotNull(recyclerView.adapter) { "Adapter needs to be set!" }
            checkNotNull(recyclerView.layoutManager) { "LayoutManager needs to be set on the RecyclerView" }
            if (loadingListItemCreator == null) {
                loadingListItemCreator = LoadingListItemCreator.DEFAULT
            }
            if (loadingListItemSpanLookup == null) {
                loadingListItemSpanLookup = DefaultLoadingListItemSpanLookup(recyclerView.layoutManager)
            }
            return RecyclerPaginate(recyclerView, callbacks, loadingTriggerThreshold, addLoadingListItem, loadingListItemCreator, loadingListItemSpanLookup)
        }

    }

    /**
     * Loading view span will be calculated by according to layout manager loading list item
     */
    private class DefaultLoadingListItemSpanLookup(layoutManager: RecyclerView.LayoutManager?) : LoadingListItemSpanLookup {
        private var loadingListItemSpan = 0

        init {
            if (layoutManager is GridLayoutManager) {
                // By default full span will be used for loading list item
                loadingListItemSpan = layoutManager.spanCount
            } else {
                loadingListItemSpan = 1
            }
        }

        override fun getSpanSize(): Int {
            return loadingListItemSpan
        }
    }

    /**
     * RecyclerView creator that will be called to create and bind loading list item
     */
    interface LoadingListItemCreator {
        /**
         *
         * @param parent   parent ViewGroup.
         * @param viewType type of the loading list item.
         * @return ViewHolder that will be used as loading list item.
         */
        fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

        /**
         * Bind the loading list item.
         *
         * @param holder   loading list item ViewHolder.
         * @param position loading list item position.
         */
        fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int)

        companion object {
            val DEFAULT: LoadingListItemCreator = object : LoadingListItemCreator {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.loading_row, parent, false)
                    return object : RecyclerView.ViewHolder(view) {}
                }

                override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
                    // No binding for default loading row
                }
            }
        }
    }

    /**
     * SpanSizeLookup that will be used to determine the span of loading list item.
     */
    interface LoadingListItemSpanLookup {
        /**
         * @return the span of loading list item.
         */
        fun getSpanSize(): Int
    }

    private inner class WrapperSpanSizeLookup(val wrappedSpanSizeLookup: GridLayoutManager.SpanSizeLookup, private val loadingListItemSpanLookup: LoadingListItemSpanLookup,
                                              private val wrapperAdapter: WrapperAdapter) : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return if (wrapperAdapter.isLoadingRow(position)) {
                loadingListItemSpanLookup.getSpanSize()
            } else {
                wrappedSpanSizeLookup.getSpanSize(position)
            }
        }

    }

    init {

        // Attach scrolling listener in order to perform end offset check on each scroll event
        recyclerView.addOnScrollListener(mOnScrollListener)
        if (addLoadingListItem) {
            // Wrap existing adapter with new adapter that will add loading row
            val adapter = recyclerView.adapter
            wrapperAdapter = WrapperAdapter(adapter!!, loadingListItemCreator!!)
            adapter.registerAdapterDataObserver(mDataObserver)
            recyclerView.adapter = wrapperAdapter

            // For GridLayoutManager use separate/customisable span lookup for loading row
            if (recyclerView.layoutManager is GridLayoutManager) {
                wrapperSpanSizeLookup = WrapperSpanSizeLookup((recyclerView.layoutManager as GridLayoutManager?)!!.spanSizeLookup, loadingListItemSpanLookup!!, wrapperAdapter!!)
                (recyclerView.layoutManager as GridLayoutManager?)!!.spanSizeLookup = wrapperSpanSizeLookup
            }
        }

        // Trigger initial check since adapter might not have any items initially so no scrolling events upon
        // RecyclerView (that triggers check) will occur
        checkEndOffset()
    }
}