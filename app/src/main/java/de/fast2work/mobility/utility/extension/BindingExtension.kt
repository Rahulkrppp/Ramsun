package de.fast2work.mobility.utility.extension

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding

/**
 * this files contains extension mehods for binding
 *
 * @return
 */
fun ViewBinding.context(): Context = this.root.context

fun ViewBinding.getString(resId: Int): String {
    return context().getString(resId)
}

fun ViewBinding.getString(resId: Int, formatArgs: Any): String {
    return context().getString(resId, formatArgs)
}

fun ViewBinding.getColor(resId: Int): Int {
    return ContextCompat.getColor(context(), resId)
}