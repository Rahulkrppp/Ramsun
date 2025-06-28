package com.shashank.platform.fancyflashbarlib

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import android.text.Spanned
import android.text.TextUtils
import android.util.TypedValue
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM
import android.widget.RelativeLayout.ALIGN_PARENT_TOP
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.shashank.platform.fancyflashbarlib.Flashbar.Gravity
import com.shashank.platform.fancyflashbarlib.Flashbar.Gravity.BOTTOM
import com.shashank.platform.fancyflashbarlib.Flashbar.Gravity.TOP
import com.shashank.platform.fancyflashbarlib.SwipeDismissTouchListener.DismissCallbacks
import com.shashank.platform.fancyflashbarlib.util.getStatusBarHeightInPx
//import kotlinx.android.synthetic.main.flash_bar_view.view.*

/**
 * The actual Flashbar withView representation that can consist of the title, message, button, icon, etc.
 * Its size is adaptive and depends solely on the amount of content present in it. It always matches
 * the width of the screen.
 *
 * It can either be present at the top or at the bottom of the screen. It will always consume touch
 * events and respond as necessary.
 */
internal class FlashbarView(context: Context) : LinearLayout(context) {

    private val TOP_COMPENSATION_MARGIN = resources.getDimension(R.dimen.fb_top_compensation_margin).toInt()
    private val BOTTOM_COMPENSATION_MARGIN = resources.getDimension(R.dimen.fb_bottom_compensation_margin).toInt()

    private lateinit var parentFlashbarContainer: FlashbarContainerView
    private lateinit var gravity: Gravity

    private var isMarginCompensationApplied: Boolean = false

    private lateinit var fbContent : LinearLayout
    lateinit var fbRoot : LinearLayout
    lateinit var fbTitle : AppCompatTextView
    lateinit var fbMessage : AppCompatTextView
    lateinit var fbIcon : AppCompatImageView

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (!isMarginCompensationApplied) {
            isMarginCompensationApplied = true

            val params = layoutParams as ViewGroup.MarginLayoutParams
            when (gravity) {
                TOP -> params.topMargin = -TOP_COMPENSATION_MARGIN
                BOTTOM -> params.bottomMargin = -BOTTOM_COMPENSATION_MARGIN
            }
            requestLayout()
        }
    }

    internal fun init(
            gravity: Gravity,
            castShadow: Boolean,
            shadowStrength: Int) {
        this.gravity = gravity
        this.orientation = VERTICAL

        // If the bar appears with the bottom, then the shadow needs to added to the top of it,
        // Thus, before the inflation of the bar
        if (castShadow && gravity == BOTTOM) {
            //castShadow(ShadowView.ShadowType.TOP, shadowStrength)
        }

        inflate(context, R.layout.flash_bar_view, this)

        fbContent = findViewById<LinearLayout>(R.id.fbContent)
        fbRoot  = findViewById<LinearLayout>(R.id.fbRoot)
        fbTitle = findViewById<AppCompatTextView>(R.id.fbTitle)
        fbMessage = findViewById<AppCompatTextView>(R.id.fbMessage)
        fbIcon = findViewById<AppCompatImageView>(R.id.fbIcon)

        // If the bar appears with the top, then the shadow needs to added to the bottom of it,
        // Thus, after the inflation of the bar
        if (castShadow && gravity == TOP) {
            //castShadow(ShadowView.ShadowType.BOTTOM, shadowStrength)
        }
    }

    internal fun adjustWitPositionAndOrientation(activity: Activity,
                                                 gravity: Gravity) {
        val flashbarViewLp = RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        val statusBarHeight = activity.getStatusBarHeightInPx()

        val flashbarViewContentLp = fbContent.layoutParams as LinearLayout.LayoutParams

        when (gravity) {
            TOP -> {
                flashbarViewContentLp.topMargin = statusBarHeight.plus(TOP_COMPENSATION_MARGIN / 2)
                flashbarViewLp.addRule(ALIGN_PARENT_TOP)
            }
            BOTTOM -> {
                flashbarViewContentLp.bottomMargin = BOTTOM_COMPENSATION_MARGIN
                flashbarViewLp.addRule(ALIGN_PARENT_BOTTOM)
            }
        }
        fbContent.layoutParams = flashbarViewContentLp
        layoutParams = flashbarViewLp
    }

    internal fun addParent(flashbarContainerView: FlashbarContainerView) {
        this.parentFlashbarContainer = flashbarContainerView
    }

    internal fun setBarBackgroundDrawable(drawable: Drawable?) {
        if (drawable == null) return

        this.fbContent.background = drawable
    }

    internal fun setBarIconDrawable(drawable: Drawable?) {
        if (drawable == null) return

        this.fbIcon.setImageDrawable(drawable)
    }

    internal fun setBarBackgroundColor(@ColorInt color: Int?) {
        if (color == null) return
        this.fbRoot.setBackgroundColor(color)
    }

    internal fun setBarTapListener(listener: Flashbar.OnTapListener?) {
        if (listener == null) return

        this.fbRoot.setOnClickListener {
            listener.onTap(parentFlashbarContainer.parentFlashbar)
        }
    }

    internal fun setTitle(title: String?) {
        if (TextUtils.isEmpty(title)) return

        this.fbTitle.text = title
        this.fbTitle.visibility = VISIBLE
    }

    internal fun setTitleSpanned(title: Spanned?) {
        if (title == null) return

        this.fbTitle.text = title
        this.fbTitle.visibility = VISIBLE
    }

    internal fun setTitleTypeface(typeface: Typeface?) {
        if (typeface == null) return
        fbTitle.typeface = typeface
    }

    internal fun setTitleSizeInPx(size: Float?) {
        if (size == null) return
        fbTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
    }

    internal fun setTitleSizeInSp(size: Float?) {
        if (size == null) return
        fbTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
    }

    internal fun setTitleColor(color: Int?) {
        if (color == null) return
        fbTitle.setTextColor(color)
    }

    internal fun setTitleAppearance(titleAppearance: Int?) {
        if (titleAppearance == null) return

        if (SDK_INT >= M) {
            this.fbTitle.setTextAppearance(titleAppearance)
        } else {
            this.fbTitle.setTextAppearance(fbTitle.context, titleAppearance)
        }
    }

    internal fun setMessage(message: String?) {
        if (TextUtils.isEmpty(message)) return

        this.fbMessage.text = message
        this.fbMessage.visibility = VISIBLE
    }

    internal fun setMessageSpanned(message: Spanned?) {
        if (message == null) return

        this.fbMessage.text = message
        this.fbMessage.visibility = VISIBLE
    }

    internal fun setMessageTypeface(typeface: Typeface?) {
        if (typeface == null) return
        this.fbMessage.typeface = typeface
    }

    internal fun setMessageSizeInPx(size: Float?) {
        if (size == null) return
        this.fbMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
    }

    internal fun setMessageSizeInSp(size: Float?) {
        if (size == null) return
        this.fbMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
    }

    internal fun setMessageColor(color: Int?) {
        if (color == null) return
        this.fbMessage.setTextColor(color)
    }

    internal fun setMessageAppearance(messageAppearance: Int?) {
        if (messageAppearance == null) return

       /* if (SDK_INT >= M) {
            this.fbMessage.setTextAppearance(messageAppearance)
        } else {
            this.fbMessage.setTextAppearance(fbMessage.context, messageAppearance)
        }*/
    }

    internal fun enableSwipeToDismiss(enable: Boolean, callbacks: DismissCallbacks) {
        if (enable) {
            fbRoot.setOnTouchListener(SwipeDismissTouchListener(this, callbacks))
        }
    }
}