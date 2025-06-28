package de.fast2work.mobility.utility.util

import android.content.Context
import android.util.AttributeSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout

class CustomAppBarLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppBarLayout(context, attrs, defStyleAttr) {

    override fun getBehavior(): CoordinatorLayout.Behavior<AppBarLayout> {
        return Behavior().also {
            it.setDragCallback(object : Behavior.DragCallback() {
                override fun canDrag(appBarLayout: AppBarLayout) = true
            })
        }
    }
}