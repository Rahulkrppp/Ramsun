package com.shashank.platform.fancyflashbarlib

import android.app.Activity
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Spanned
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import com.shashank.platform.fancyflashbarlib.Flashbar.Gravity.BOTTOM
import com.shashank.platform.fancyflashbarlib.Flashbar.Gravity.TOP
import com.shashank.platform.fancyflashbarlib.anim.FlashAnim
import com.shashank.platform.fancyflashbarlib.anim.FlashAnimBarBuilder

private const val DEFAULT_SHADOW_STRENGTH = 4
private const val DEFAUT_ICON_SCALE = 1.0f

class Flashbar private constructor(private var builder: Builder) {

    private lateinit var flashbarContainerView: FlashbarContainerView
    private lateinit var flashbarView: FlashbarView

    /**
     * Shows a flashbar
     */
    fun show() {
        flashbarContainerView.show(builder.activity)
    }

    /**
     * Dismisses a flashbar
     */
    fun dismiss() {
        flashbarContainerView.dismiss()
    }

    /**
     * Returns true/false depending on whether the flashbar is showing or not
     * This represents the partial appearance of the flashbar
     */
    fun isShowing() = flashbarContainerView.isBarShowing()

    /**
     * Returns true/false depending on whether the flashbar has been completely shown or not
     * This represents the complete appearance of the flashbar
     */
    fun isShown() = flashbarContainerView.isBarShown()

    private fun construct() {
        flashbarContainerView = FlashbarContainerView(builder.activity)
        flashbarContainerView.adjustOrientation(builder.activity)
        flashbarContainerView.addParent(this)

        flashbarView = FlashbarView(builder.activity)
        flashbarView.init(builder.gravity, builder.castShadow, builder.shadowStrength)
        flashbarView.adjustWitPositionAndOrientation(builder.activity, builder.gravity)
        flashbarView.addParent(flashbarContainerView)

        flashbarContainerView.attach(flashbarView)

        initializeContainerDecor()
        initializeBarDecor()

        flashbarContainerView.construct()
    }

    private fun initializeContainerDecor() {
        with(flashbarContainerView) {
            setDuration(builder.duration)
            setBarShowListener(builder.onBarShowListener)
            setBarDismissListener(builder.onBarDismissListener)
            setBarDismissOnTapOutside(builder.barDismissOnTapOutside)
            setOnTapOutsideListener(builder.onTapOutsideListener)
            setOverlay(builder.overlay)
            setOverlayColor(builder.overlayColor)
            setOverlayBlockable(builder.overlayBlockable)
            setVibrationTargets(builder.vibrationTargets)

            setEnterAnim(builder.enterAnimBuilder!!)
            setExitAnim(builder.exitAnimBuilder!!)
            enableSwipeToDismiss(builder.enableSwipeToDismiss)
        }
    }

    private fun initializeBarDecor() {
        with(flashbarView) {
            setBarBackgroundColor(builder.backgroundColor)
            setBarBackgroundDrawable(builder.backgroundDrawable)
            setBarIconDrawable(builder.iconDrawable)
            setBarTapListener(builder.onBarTapListener)

            setTitle(builder.title)
            setTitleSpanned(builder.titleSpanned)
            setTitleTypeface(builder.titleTypeface)
            setTitleSizeInPx(builder.titleSizeInPx)
            setTitleSizeInSp(builder.titleSizeInSp)
            setTitleColor(builder.titleColor)
            setTitleAppearance(builder.titleAppearance)

            setMessage(builder.message)
            setMessageSpanned(builder.messageSpanned)
            setMessageTypeface(builder.messageTypeface)
            setMessageSizeInPx(builder.messageSizeInPx)
            setMessageSizeInSp(builder.messageSizeInSp)
            setMessageColor(builder.messageColor)
            setMessageAppearance(builder.messageAppearance)
        }
    }

    class Builder(internal var activity: Activity) {
        internal var gravity: Gravity = BOTTOM
        internal var backgroundColor: Int? = null
        internal var backgroundDrawable: Drawable? = null
        internal var iconDrawable: Drawable? = null
        internal var duration: Long = DURATION_INDEFINITE
        internal var onBarTapListener: OnTapListener? = null
        internal var onBarShowListener: OnBarShowListener? = null
        internal var onBarDismissListener: OnBarDismissListener? = null
        internal var barDismissOnTapOutside: Boolean = false
        internal var onTapOutsideListener: OnTapListener? = null
        internal var overlay: Boolean = false
        internal var overlayColor: Int = ContextCompat.getColor(activity, R.color.modal)
        internal var overlayBlockable: Boolean = false
        internal var castShadow: Boolean = true
        internal var shadowStrength: Int = DEFAULT_SHADOW_STRENGTH
        internal var enableSwipeToDismiss: Boolean = false
        internal var vibrationTargets: List<Vibration> = emptyList()

        internal var title: String? = null
        internal var titleSpanned: Spanned? = null
        internal var titleTypeface: Typeface? = null
        internal var titleSizeInPx: Float? = null
        internal var titleSizeInSp: Float? = null
        internal var titleColor: Int? = null
        internal var titleAppearance: Int? = null

        internal var message: String? = null
        internal var messageSpanned: Spanned? = null
        internal var messageTypeface: Typeface? = null
        internal var messageSizeInPx: Float? = null
        internal var messageSizeInSp: Float? = null
        internal var messageColor: Int? = null
        internal var messageAppearance: Int? = null

        internal var enterAnimBuilder: FlashAnimBarBuilder? = null
        internal var exitAnimBuilder: FlashAnimBarBuilder? = null

        fun backGroundColor(backgroundColor:Int) = apply { this.backgroundColor = backgroundColor }

        fun backGroundDrawable(backgroundDrawable:Drawable) = apply { this.backgroundDrawable = backgroundDrawable }

        fun iconDrawable(iconDrawable:Drawable) = apply { this.iconDrawable = iconDrawable }

        /**
         * Specifies the gravity from where the flashbar will be shown (top/bottom)
         * Default gravity is TOP
         */
        fun gravity(gravity: Gravity) = apply { this.gravity = gravity }

        /**
         * Sets listener to receive tap events on the surface of the bar
         */
        fun listenBarTaps(listener: OnTapListener) = apply {
            this.onBarTapListener = listener
        }

        /**
         * Specifies the duration for which the flashbar will be visible
         * By default, the duration is infinite
         */
        fun duration(milliseconds: Long) = apply {
            require(milliseconds > 0) { "Duration can not be negative or zero" }
            this.duration = milliseconds
        }

        /**
         * Sets listener to receive bar showing/shown events
         */
        fun barShowListener(listener: OnBarShowListener) = apply {
            this.onBarShowListener = listener
        }

        /**
         * Sets listener to receive bar dismissing/dismissed events
         */
        fun barDismissListener(listener: OnBarDismissListener) = apply {
            this.onBarDismissListener = listener
        }

        /**
         * Sets listener to receive tap events outside flashbar surface
         */
        fun listenOutsideTaps(listener: OnTapListener) = apply {
            this.onTapOutsideListener = listener
        }

        /**
         * Dismisses the bar on being tapped outside
         */
        fun dismissOnTapOutside() = apply {
            this.barDismissOnTapOutside = true
        }

        /**
         * Shows the modal overlay
         */
        fun showOverlay() = apply { this.overlay = true }

        /**
         * Specifies modal overlay color
         */
        fun overlayColor(@ColorInt color: Int) = apply { this.overlayColor = color }

        /**
         * Specifies modal overlay color resource
         * Modal overlay is automatically shown if color is set
         */
        fun overlayColorRes(@ColorRes colorId: Int) = apply {
            this.overlayColor = ContextCompat.getColor(activity, colorId)
        }

        /**
         * Specifies if modal overlay is blockable and should comsume touch events
         */
        fun overlayBlockable() = apply {
            this.overlayBlockable = true
        }

        /**
         * Specifies if the flashbar should cast shadow and the strength of it
         */
        /*@JvmOverloads
        fun castShadow(shadow: Boolean = true, strength: Int = DEFAULT_SHADOW_STRENGTH) = apply {
            require(strength > 0) { "Shadow strength can not be negative or zero" }

            this.castShadow = shadow
            this.shadowStrength = strength
        }*/

        /**
         * Specifies the enter animation of the flashbar
         */
        fun enterAnimation(builder: FlashAnimBarBuilder) = apply {
            this.enterAnimBuilder = builder
        }

        /**
         * Specifies the exit animation of the flashbar
         */
        fun exitAnimation(builder: FlashAnimBarBuilder) = apply {
            this.exitAnimBuilder = builder
        }

        /**
         * Enables swipe-to-dismiss for the flashbar
         */
        fun enableSwipeToDismiss() = apply {
            this.enableSwipeToDismiss = true
        }

        /**
         * Specifies whether the device should vibrate during flashbar enter/exit/both
         */
        fun vibrateOn(vararg vibrate: Vibration) = apply {
            require(vibrate.isNotEmpty()) { "Vibration targets can not be empty" }
            this.vibrationTargets = vibrate.toList()
        }

        /**
         * Specifies the title string
         */
        fun title(title: String) = apply { this.title = title }

        /**
         * Specifies the title string res
         */
        fun title(@StringRes titleId: Int) = apply { this.title = activity.getString(titleId) }

        /**
         * Specifies the title span
         */
        fun title(title: Spanned) = apply { this.titleSpanned = title }

        /**
         * Specifies the title typeface
         */
        fun titleTypeface(typeface: Typeface) = apply { this.titleTypeface = typeface }

        /**
         * Specifies the title size (in pixels)
         */
        fun titleSizeInPx(size: Float) = apply { this.titleSizeInPx = size }

        /**
         * Specifies the title size (in sp)
         */
        fun titleSizeInSp(size: Float) = apply { this.titleSizeInSp = size }

        /**
         * Specifies the title color
         */
        fun titleColor(@ColorInt color: Int) = apply { this.titleColor = color }

        /**
         * Specifies the title color resource
         */
        fun titleColorRes(@ColorRes colorId: Int) = apply {
            this.titleColor = ContextCompat.getColor(activity, colorId)
        }

        /**
         * Specifies the title appearance
         */
        fun titleAppearance(@StyleRes appearance: Int) = apply {
            this.titleAppearance = appearance
        }

        /**
         * Specifies the message string
         */
        fun message(message: String) = apply { this.message = message }

        /**
         * Specifies the message string resource
         */
        fun message(@StringRes messageId: Int) = apply {
            this.message = activity.getString(messageId)
        }

        /**
         * Specifies the message string span
         */
        fun message(message: Spanned) = apply { this.messageSpanned = message }

        /**
         * Specifies the message typeface
         */
        fun messageTypeface(typeface: Typeface) = apply { this.messageTypeface = typeface }

        /**
         * Specifies the message size (in pixels)
         */
        fun messageSizeInPx(size: Float) = apply { this.messageSizeInPx = size }

        /**
         * Specifies the message size (in sp)
         */
        fun messageSizeInSp(size: Float) = apply { this.messageSizeInSp = size }

        /**
         * Specifies the message color
         */
        fun messageColor(@ColorInt color: Int) = apply { this.messageColor = color }

        /**
         * Specifies the message color resource
         */
        fun messageColorRes(@ColorRes colorId: Int) = apply {
            this.messageColor = ContextCompat.getColor(activity, colorId)
        }

        /**
         * Specifies the message appearance
         */
        fun messageAppearance(@StyleRes appearance: Int) = apply {
            this.messageAppearance = appearance
        }

        fun build(): Flashbar {
            configureAnimation()
            val flashbar = Flashbar(this)
            flashbar.construct()
            return flashbar
        }

        /**
         * Shows the flashbar
         */
        fun show() = build().show()

        private fun configureAnimation() {
            enterAnimBuilder = if (enterAnimBuilder == null) {
                when (gravity) {
                    TOP -> FlashAnim.with(activity).animateBar().enter().fromTop()
                    BOTTOM -> FlashAnim.with(activity).animateBar().enter().fromBottom()
                }
            } else {
                when (gravity) {
                    TOP -> enterAnimBuilder!!.enter().fromTop()
                    BOTTOM -> enterAnimBuilder!!.enter().fromBottom()
                }
            }

            exitAnimBuilder = if (exitAnimBuilder == null) {
                when (gravity) {
                    TOP -> FlashAnim.with(activity).animateBar().exit().fromTop()
                    BOTTOM -> FlashAnim.with(activity).animateBar().exit().fromBottom()
                }
            } else {
                when (gravity) {
                    TOP -> exitAnimBuilder!!.exit().fromTop()
                    BOTTOM -> exitAnimBuilder!!.exit().fromBottom()
                }
            }
        }
    }

    companion object {
        const val DURATION_INDEFINITE = -1L
    }

    enum class Gravity { TOP, BOTTOM }

    enum class DismissEvent {
        TIMEOUT,
        MANUAL,
        TAP_OUTSIDE,
        SWIPE
    }

    enum class Vibration { SHOW, DISMISS }

    interface OnBarDismissListener {
        fun onDismissing(bar: Flashbar, isSwiped: Boolean)
        fun onDismissProgress(bar: Flashbar, progress: Float)
        fun onDismissed(bar: Flashbar, event: DismissEvent)
    }

    interface OnTapListener {
        fun onTap(flashbar: Flashbar)
    }

    interface OnBarShowListener {
        fun onShowing(bar: Flashbar)
        fun onShowProgress(bar: Flashbar, progress: Float)
        fun onShown(bar: Flashbar)
    }
}