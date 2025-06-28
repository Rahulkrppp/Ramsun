package de.fast2work.mobility.utility.util

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat.ThemeCompat

class ResourceProvider(
    val context: Context
) {
    fun getDrawable(id: Int): Drawable? {
        return context.resources.getDrawable(id,context.theme)
    }

    fun getString(id: Int): String {
        return context.getString(id)
    }
}