package de.fast2work.mobility.utility.extension

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation
import androidx.recyclerview.widget.RecyclerView

/**
 * for face animation
 */
fun View.fade(from: Float, to: Float, duration: Long, func: (Animation) -> Unit = {}) {
    val fadeIn = AlphaAnimation(from, to)
    fadeIn.duration = duration
    func(fadeIn)
    startAnimation(fadeIn)
}

/**
 * for scale animation
 */
fun View.scale(fromX: Float, toX: Float, fromY: Float, toY: Float, pivotX: Float, pivotY: Float, duration: Long, func: (Animation) -> Unit = {}) {
    val anim = ScaleAnimation(fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY)
    anim.duration = duration
    func(anim)
    startAnimation(anim)
}

//=========OBJECT ANIMATION============
fun View.getFadeAnimation(from: Float, to: Float, duration: Long, delay: Long, isReverse: Boolean = false): Animator {
    var fadeAnim = ObjectAnimator.ofFloat(this, ViewAnimationPropertyName.ALPHA, from, to)
    if (isReverse)
        fadeAnim = ObjectAnimator.ofFloat(this, ViewAnimationPropertyName.ALPHA, to, from)
    fadeAnim.startDelay = delay
    return fadeAnim
}

class ViewAnimationPropertyName {
    companion object {
        const val ROTATION = "rotation"
        const val ROTATION_X = "rotationX"
        const val ROTATION_Y = "rotationY"
        const val TRANSLATION_X = "translationX"
        const val TRANSLATION_Y = "translationY"
        const val SCALE_X = "scaleX"
        const val SCALE_Y = "scaleY"
        const val PIVOT_X = "pivotX"
        const val PIVOT_Y = "pivotY"
        const val ALPHA = "alpha"
        const val X = "x"
        const val Y = "y"
    }
}

fun View.getTranslateXAnimation(
        from: Float,
        to: Float,
        duration: Long,
        delay: Long,
        isReverse: Boolean = false
): Animator {
    var anim = ObjectAnimator.ofFloat(this, ViewAnimationPropertyName.TRANSLATION_X, from, to)
    if (isReverse)
        anim = ObjectAnimator.ofFloat(this, ViewAnimationPropertyName.TRANSLATION_X, to, from)
    anim.duration = duration
    anim.startDelay = delay
    return anim
}

fun View.getTranslateYAnimation(
        from: Float,
        to: Float,
        duration: Long,
        delay: Long = 0,
        isReverse: Boolean = false
): Animator {
    var anim = ObjectAnimator.ofFloat(this, ViewAnimationPropertyName.TRANSLATION_Y, from, to)
    if (isReverse)
        anim = ObjectAnimator.ofFloat(this, ViewAnimationPropertyName.TRANSLATION_Y, to, from)
    anim.duration = duration
    anim.startDelay = delay
    return anim
}
fun ViewGroup.runLayoutAnimation(resourceId: Int) {
    val context = this.context

    val controller = AnimationUtils.loadLayoutAnimation(context, resourceId)

    this.layoutAnimation = controller
    (this as? RecyclerView)?.adapter?.notifyDataSetChanged()
    this.scheduleLayoutAnimation()
}
