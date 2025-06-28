package de.fast2work.mobility.utility.customview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View


class StackedHorizontalProgressBar(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    private val paint = Paint()
    private var animatedProgress1 = 0f
    private var animatedProgress2 = 0f
    private var animatedProgress3 = 0f
    private var targetProgress1 = 0
    private var targetProgress2 = 0
    private var targetProgress3 = 0

    var colorOne : String = ""
    var colorTwo : String = ""
    var colorThree : String = ""


    // Method to set and animate progress values for each segment
    fun setProgress(progress1: Int, progress2: Int, progress3: Int,colorOne:String,colorTwo:String,colorThree:String) {
        this.targetProgress1 = progress1
        this.targetProgress2 = progress2
        this.targetProgress3 = progress3

        this.colorOne = colorOne
        this.colorTwo = colorTwo
        this.colorThree = colorThree

        // Start animations for each segment
        animateProgressSegment(1, progress1)
        animateProgressSegment(2, progress2)
        animateProgressSegment(3, progress3)
    }

    private fun animateProgressSegment(segment: Int, targetProgress: Int) {
        val animator = ValueAnimator.ofFloat(0f, targetProgress.toFloat())
        animator.setDuration(2000) // 1 second animation duration
        animator.addUpdateListener { animation: ValueAnimator ->
            val animatedValue = animation.animatedValue as Float
            when (segment) {
                1 -> animatedProgress1 = animatedValue
                2 -> animatedProgress2 = animatedValue
                3 -> animatedProgress3 = animatedValue
            }
            invalidate() // Redraw the view with updated progress
        }
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width
        val height = height

        // Draw the first segment with animated width
        //paint.color = Color.RED
        paint.color = Color.parseColor(colorOne)
        canvas.drawRect(0f, 0f, width * (animatedProgress1 / 100f), height.toFloat(), paint)

        // Draw the second segment with animated width
        paint.color = Color.parseColor(colorTwo)
        canvas.drawRect(
            width * (animatedProgress1 / 100f),
            0f,
            width * ((animatedProgress1 + animatedProgress2) / 100f),
            height.toFloat(),
            paint
        )

        // Draw the third segment with animated width
        paint.color = Color.parseColor(colorThree)
        canvas.drawRect(
            width * ((animatedProgress1 + animatedProgress2) / 100f),
            0f,
            width * ((animatedProgress1 + animatedProgress2 + animatedProgress3) / 100f),
            height.toFloat(),
            paint
        )
    }
}

//class StackedHorizontalProgressBar(context: Context?, attrs: AttributeSet?) :
//    View(context, attrs) {
//    private val paint = Paint()
//    private var progress1 = 0
//    private var progress2 = 0
//    private var progress3 = 0
//
//    fun setProgress(progress1: Int, progress2: Int, progress3: Int) {
//        this.progress1 = progress1
//        this.progress2 = progress2
//        this.progress3 = progress3
//        invalidate() // Redraw the view
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
//        val width = width
//        val height = height
//
//        // Draw the first segment
//        paint.color = Color.RED
//        canvas.drawRect(0f, 0f, width * (progress1 / 100f), height.toFloat(), paint)
//
//        // Draw the second segment
//        paint.color = Color.BLUE
//        canvas.drawRect(
//            width * (progress1 / 100f),
//            0f,
//            width * ((progress1 + progress2) / 100f),
//            height.toFloat(),
//            paint
//        )
//
//        // Draw the third segment
//        paint.color = Color.GREEN
//        canvas.drawRect(
//            width * ((progress1 + progress2) / 100f),
//            0f,
//            width * ((progress1 + progress2 + progress3) / 100f),
//            height.toFloat(),
//            paint
//        )
//    }
//}