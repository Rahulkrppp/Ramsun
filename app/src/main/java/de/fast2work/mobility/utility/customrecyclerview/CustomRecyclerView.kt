package com.app.knit.utility.customrecyclerview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import de.fast2work.mobility.utility.customrecyclerview.Paginate.Companion.with
import com.app.knit.utility.customrecyclerview.RecyclerPaginate.LoadingListItemCreator
import com.app.knit.utility.customrecyclerview.RecyclerPaginate.LoadingListItemSpanLookup
import de.fast2work.mobility.R
import de.fast2work.mobility.utility.customrecyclerview.Paginate

class CustomRecyclerView : RecyclerView {
    private var mContext: Context? = null
    private var loadMoreType = 0
    private var needPagination = true
    private var listOrientation = 0
    private var listType = 0
    private var layoutOrientation = 0
    private var addLoadingRow = true
    private val customLoadingListItem = false
    var grid_span = 3
    private var recyclerPaginate: RecyclerPaginate? = null
    var isPaginationInitialise : Boolean =false
    /**
     * Constructor with 1 parameter context and attrs
     *
     * @param context
     */
    constructor(context: Context?) : super(context!!) {}

    /**
     * Constructor with 2 parameters context and attrs
     *
     * @param context
     * @param attrs
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.mContext = context
        initCustomText(context, attrs)
    }

    /**
     * Initializes all the attributes and respective methods are called based on the attributes
     *
     * @param context
     * @param attrs
     */
    private fun initCustomText(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.CustomRecyclerView)
        needPagination = ta.getBoolean(R.styleable.CustomRecyclerView_pagination, false)
        addLoadingRow = ta.getBoolean(R.styleable.CustomRecyclerView_loadmore_visibility, true)
        loadMoreType = ta.getInt(R.styleable.CustomRecyclerView_loadmore_type, 0)
        listOrientation = ta.getInt(R.styleable.CustomRecyclerView_list_orientation, 0)
        listType = ta.getInt(R.styleable.CustomRecyclerView_list_type, 0)
        grid_span = ta.getInt(R.styleable.CustomRecyclerView_gird_span, 3)
        /**
         * A custom view uses isInEditMode() to determine whether or not it is being rendered inside the editor
         * and if so then loads test data instead of real data.
         */
        var layoutManager: LayoutManager? = null
        if (!isInEditMode) {
            layoutOrientation = when (listOrientation) {
                0 -> OrientationHelper.VERTICAL
                1 -> OrientationHelper.HORIZONTAL
                else -> OrientationHelper.VERTICAL
            }
            when (listType) {
                0 -> layoutManager = LinearLayoutManager(context, layoutOrientation, false)
                1 -> layoutManager = GridLayoutManager(context, grid_span, layoutOrientation, false)
                2 -> layoutManager = StaggeredGridLayoutManager(grid_span, layoutOrientation)
            }
            when (loadMoreType) {
                0 -> setLoadMoreType(layoutManager, false)
                1 -> setLoadMoreType(layoutManager, true)
                else -> {
                }
            }
            setLayoutManager(layoutManager)
        }
    }

    private fun setLoadMoreType(layoutManager: LayoutManager?, isLoadMoreFromBottom: Boolean) {
        if (layoutManager is LinearLayoutManager) {
            layoutManager.reverseLayout = isLoadMoreFromBottom
        } else {
            (layoutManager as StaggeredGridLayoutManager?)!!.reverseLayout = isLoadMoreFromBottom
        }
    }

    fun setListPagination(callbacks: Paginate.Callbacks?) {
        if(callbacks == null){
            recyclerPaginate = null
            return
        }
        if (needPagination) {
            Log.e("CustomRecyclerView","setListPagination")
            isPaginationInitialise = true
            recyclerPaginate = with(this, callbacks)
                .setLoadingTriggerThreshold(0)
                .addLoadingListItem(addLoadingRow)
                .setLoadingListItemCreator(CustomLoadingListItemCreator())
                .setLoadingListItemSpanSizeLookup(object : LoadingListItemSpanLookup {
                    override fun getSpanSize(): Int {
                        return grid_span
                    }

                })
                .build() as RecyclerPaginate
        }
    }

    private inner class CustomLoadingListItemCreator : LoadingListItemCreator {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            if (listOrientation == 0) {
                return VH(inflater.inflate(R.layout.loading_row, parent, false))
            } else {
                return VH(inflater.inflate(R.layout.loading_horizontal_row, parent, false))
            }
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val vh = holder as VH?
            if (layoutManager is StaggeredGridLayoutManager) {
                val params = vh!!.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
                params.isFullSpan = true
            }
        }
    }

    internal class VH(itemView: View?) : ViewHolder(itemView!!)
}