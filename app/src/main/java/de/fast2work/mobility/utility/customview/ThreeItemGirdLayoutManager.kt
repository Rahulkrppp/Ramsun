package de.fast2work.mobility.utility.customview

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager

class ThreeItemGirdLayoutManager (context: Context) : GridLayoutManager(context,12) {
    override fun getPaddingLeft(): Int {
        val totalWidth = width
        val itemWidth = totalWidth / 3 // Divide by the number of items you want to display
        val extraSpace = totalWidth - (itemWidth * 3)
        return extraSpace / 2
    }

    override fun getPaddingRight(): Int {
        return getPaddingLeft()
    }
}