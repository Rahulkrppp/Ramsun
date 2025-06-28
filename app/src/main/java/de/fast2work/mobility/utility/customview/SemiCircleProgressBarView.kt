package de.fast2work.mobility.utility.customview

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.textColor
import de.fast2work.mobility.utility.preference.EasyPref

class SemiCircleProgressBarView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var progress = 0f
    private var strokeWidth = 40f

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
            context.resources.getColor(R.color.color_primary_dark,context.theme)
        } else {
            Color.parseColor("#274072")
           // Color.parseColor(BaseApplication.tenantSharedPreference.getTenantPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)?.brandingInfo?.primaryColor)
        }
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeWidth = this@SemiCircleProgressBarView.strokeWidth
        strokeCap = Paint.Cap.ROUND // For rounded corners
    }


    private val rectF = RectF()


    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = getContext().getColor(R.color.color_progress_background) // Dark blue color
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeWidth = this@SemiCircleProgressBarView.strokeWidth
        strokeCap = Paint.Cap.ROUND // For rounded corners
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height.toFloat()

        val radius = width / 2f - strokeWidth / 2f



        val cornerRadius = strokeWidth / 2f

        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius - cornerRadius * 2)

        // Draw background
        backgroundPaint.strokeWidth = strokeWidth
        canvas.drawArc(rectF, 180f, 180f, false, backgroundPaint)



        //Draw progress circle
        val angle = 180 * progress / 100
        canvas.drawArc(rectF, 180f, angle, false, progressPaint)



    }

    fun setProgress(progress: Float) {
        if (progress in 0.0f..100.0f) {
            this.progress = progress
            invalidate()
        }
    }

    fun setStrokeWidth(strokeWidth: Float) {
        this.strokeWidth = strokeWidth
        progressPaint.strokeWidth = strokeWidth
        invalidate()
    }

    fun setAnimation() {
        val progressValue = this.progress.toInt()
        val animation = ProgressAnimation(progressValue)
        animation.duration = 1500 // Animation duration in milliseconds
        startAnimation(animation)
    }

    inner class ProgressAnimation(private var progressValue: Int) : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            super.applyTransformation(interpolatedTime, t)
            setProgress(interpolatedTime * progressValue) // progress over the animation duration
        }
    }
}