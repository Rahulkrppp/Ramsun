package de.fast2work.mobility.utility.extension

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.SystemClock
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import com.shashank.platform.fancyflashbarlib.Flashbar
import com.shashank.platform.fancyflashbarlib.anim.FlashAnim
import de.fast2work.mobility.R
import de.fast2work.mobility.ui.core.BaseApplication
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * to show keyboard
 */
fun TextView.underline() {
    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
}

fun TextView.getTrimmedText(): String {
    return this.text.toString().trim()
}

fun AppCompatEditText?.getTrimText(): String = this?.text.toString().trim()

/**
 * to hide keyboard
 */
fun View.hideKeyBoard() {
    val inputManager = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}


fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.clickWithDebounce(debounceTime: Long = 700L, action: (view: View) -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action(v)

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

fun Group.setAllOnClickListener(listener: View.OnClickListener?) {
    referencedIds.forEach { id ->
        rootView.findViewById<View>(id).setOnClickListener(listener)
    }
}

/**
 * change the font style of tabs based on position
 * @param tabPosition Int
 * @param fontFamilyRes Int
 */
fun TabLayout.setTabItemFontFamily(tabPosition: Int, @FontRes fontFamilyRes: Int) {
    val tab = (getChildAt(0) as ViewGroup).getChildAt(tabPosition) as ViewGroup
    val tabTextView = tab.getChildAt(1) as TextView
    val typeface = ResourcesCompat.getFont(context, fontFamilyRes)
    tabTextView.typeface = typeface
}

/**
 * This function is used to set tab margins
 *
 * @param left
 * @param top
 * @param right
 * @param bottom
 */
fun TabLayout.setTabMargins(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    for (i in 0 until tabCount) {
        val tab = (getChildAt(0) as ViewGroup).getChildAt(i)
        val p = tab.layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(left, top, right, bottom)
        tab.requestLayout()
    }
}

/**
 * This function contains code to change drawable color
 *
 * @param colorString
 */
fun Drawable.overrideColor(colorString: String?) {
    try {
        if (colorString == null) return

        when (this) {
            is GradientDrawable -> setColor(Color.parseColor(colorString))
            is ShapeDrawable -> paint.color = Color.parseColor(colorString)
            is ColorDrawable -> color = Color.parseColor(colorString)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * this method contains code to change color and background for drawable
 *
 * @param tagBgColor
 * @param tagTextColor
 */
fun Drawable.overrideColorWithBackground(tagBgColor: String?, tagTextColor: String?) {
    try {
        if (tagBgColor == null) return
        if (tagTextColor == null) return
        when (this) {
            is GradientDrawable -> {
                setStroke(2, Color.parseColor(tagBgColor))
                setColor(Color.parseColor(tagTextColor))
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * This method contains code set text color
 *
 * @param colorString
 */
fun TextView.textColor(colorString: String?) {
    try {
            if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
                this.setTextColor(context.getColor(R.color.color_secondary_dark))
            } else {
                this.setTextColor(Color.parseColor(colorString))
            }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun TextView.buttonTextColor(colorString: String?) {
    try {
        if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
            this.setTextColor(context.getColorFromAttr(R.attr.color_button_text_alpha_80))
        } else {
            this.setTextColor(ColorUtils.setAlphaComponent(Color.parseColor(colorString), 0XCC))
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun TextView.buttonTextColorNotification(colorString: String?) {
    try {
        if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
            this.setTextColor(context.getColorFromAttr(R.attr.colorTextView))
        } else {
            this.setTextColor(ColorUtils.setAlphaComponent(Color.parseColor(colorString), 0XCC))
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}
fun TextView.buttonTextColorText(colorString: String?) {
    try {
        if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
            this.setTextColor(context.getColorFromAttr(com.google.android.material.R.attr.colorSecondary))
        } else {
            this.setTextColor(ColorUtils.setAlphaComponent(Color.parseColor(colorString), 0XCC))
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun ConstraintLayout.constraintLayoutBackgroundColor(colorString: String?) {
    try {
        if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
            this.backgroundTintList = (ColorStateList.valueOf(
                context.getColorFromAttr(R.attr.colorCardView)))

        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * This method contains code to change background color for selected view
 *
 * @param colorString
 */
fun View.backgroundColor(colorString: String?) {
    try {
            if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
                this.setBackgroundColor(context.getColor(R.color.color_primary_dark))
            } else {
                this.setBackgroundColor(Color.parseColor(colorString))
            }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.backgroundColorTint(colorString: String?) {
    try {
            if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
                this.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color_primary_dark))
            } else {
                this.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorString))
            }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.backgroundColorTintForAlpha(colorString: String?) {
    try {
        if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
            this.backgroundTintList = ColorStateList.valueOf(context.getColorFromAttr(R.attr.color_button_background_alpha_15))
        } else {
            this.backgroundTintList = ColorStateList.valueOf(ColorUtils.setAlphaComponent(Color.parseColor(colorString), 0X26))
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun ImageView.imageTint(colorString: String?) {
    try {
            if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
                this.setColorFilter(context.getColorFromAttr(R.attr.colorEditTextBorder))
            } else {
                this.setColorFilter(Color.parseColor(colorString), PorterDuff.Mode.SRC_IN)
            }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}
fun ImageView.imageTickTint(colorString: String?) {
    try {
        if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
            this.setColorFilter(context.getColorFromAttr(com.dc.retroapi.R.attr.colorPrimary))
        } else {
            this.setColorFilter(Color.parseColor(colorString), PorterDuff.Mode.SRC_IN)
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}
fun ImageView.imageTickTint1(colorString: String?) {
    try {
        if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
            this.setColorFilter(context.getColorFromAttr(com.google.android.material.R.attr.colorAccent))
        } else {
            this.setColorFilter(Color.parseColor(colorString), PorterDuff.Mode.SRC_IN)
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}fun ImageView.imageTickTintCheckBox(colorString: String?) {
    try {
        if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
            this.setColorFilter(context.getColorFromAttr(com.google.android.material.R.attr.colorSecondary))
        } else {
            this.setColorFilter(Color.parseColor(colorString), PorterDuff.Mode.SRC_IN)
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun AppCompatCheckBox.drawableColorTint(colorString: String?) {
    try {
            if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
                this.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color_primary_dark))
            } else {
                this.compoundDrawableTintList = ColorStateList.valueOf(Color.parseColor(colorString))
            }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}



fun TextInputLayout.setThemeForTextInputLayout(colorString: String?, isFromTenant : Boolean = false) {
    try {
            if (BaseApplication.themeValue == Configuration.UI_MODE_NIGHT_YES) {
                this.boxStrokeColor = context.getColorFromAttr(R.attr.colorEditTextBorder)
                this.hintTextColor = ColorStateList.valueOf(context.getColorFromAttr(R.attr.colorEditTextBorder))
            } else {
                if (!isFromTenant) {
                    this.boxStrokeColor = Color.parseColor(colorString)
                    this.hintTextColor = ColorStateList.valueOf(Color.parseColor(colorString))
                }else{
                    this.boxStrokeColor = context.getColorFromAttr(R.attr.colorEditTextBorder)
                    this.hintTextColor = ColorStateList.valueOf(context.getColorFromAttr(R.attr.colorEditTextBorder))
                }
            }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}



/*private fun setThemeForNotificationIcon(){
    val drawable = ContextCompat.getDrawable(requireActivity(), R.drawable.bg_notification_count) as GradientDrawable

    // Change the fill color of the drawable
    val newFillColor = if (BaseApplication.themeValue == Configuration.UI_MODE_NIGHT_YES) {
        ContextCompat.getColor(requireActivity(),R.color.color_primary_dark)
    }else{
        Color.parseColor(tenantInfoData?.brandingInfo?.primaryColor)
    }
    drawable.setColor(newFillColor)

}*/


fun View.overrideGradientDrawableColor(colorStart: String?, colorEnd: String?, index: Int = 0) {
    try {
        val drawable = this.background as LayerDrawable
        val item1 = drawable.getDrawable(index) as GradientDrawable
        if (colorStart != null && colorEnd != null) item1.colors = intArrayOf(Color.parseColor(colorStart), Color.parseColor(colorEnd))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun AppCompatEditText.phoneTextFormatter() {
    val mPattern = "### ## ####"
    val maxLength = mPattern.length
    val lengthFilter: InputFilter = InputFilter.LengthFilter(maxLength)
    filters = arrayOf(lengthFilter)

    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val phone = StringBuilder(s)

            var isValid = true
            for (i in phone.indices) {
                val c = mPattern[i]
                if (c == '#') continue
                if (c != phone[i]) {
                    isValid = false
                }
            }

            if (count > 0 && !isValid) {
                for (i in phone.indices) {
                    val c = mPattern[i]
                    if (c != '#' && c != phone[i]) {
                        phone.insert(i, c)
                    }
                }
                setText(phone)
                text?.let { setSelection(it.length) }
            }

        }

        override fun afterTextChanged(s: Editable?) {
            if (s.toString().length == 1 && !s.toString().startsWith("5")) {
                s?.clear()
            }
        }

    })
}

/**
 * Apply same action on batch objects which is arraylist
 */
fun <T> applyArrayAction(anys: ArrayList<T>, onAction: (any: T) -> Unit) {
    for (i in anys) {
        onAction(i)
    }
}

/**
 * this method contains code to hide password
 *
 * @param editText
 * @param hidePass
 * @param showPass
 */
fun hidePassword(editText: AppCompatEditText, hidePass: AppCompatImageView, showPass: AppCompatImageView) {
    editText.transformationMethod = PasswordTransformationMethod.getInstance()
    editText.setSelection(editText.text!!.length)
    hidePass.visibility = View.INVISIBLE
    showPass.visibility = View.VISIBLE
}

/**
 * This method contains code to show password
 *
 * @param editText
 * @param hidePass
 * @param showPass
 */
fun showPassword(editText: AppCompatEditText, hidePass: AppCompatImageView, showPass: AppCompatImageView) {
    editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
    editText.setSelection(editText.text!!.length)
    hidePass.visibility = View.VISIBLE
    showPass.visibility = View.INVISIBLE
}

fun View.slideUp(duration: Int = 500) {
    visibility = View.VISIBLE
    val animate = TranslateAnimation(0f, 0f, this.height.toFloat(), 0f)
    animate.duration = duration.toLong()
    animate.fillAfter = true
    this.startAnimation(animate)
}

fun View.slideDown(duration: Int = 500) {
    visibility = View.VISIBLE
    val animate = TranslateAnimation(0f, 0f, 0f, this.height.toFloat())
    animate.duration = duration.toLong()
    animate.fillAfter = true
    this.startAnimation(animate)
}

/**
 * This method contains for show keyboard
 *
 */
fun View.showKeyboard() {
    this.requestFocus()
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

@ColorInt
fun Context.getColorFromAttr( @AttrRes attrColor: Int
): Int {
    val typedArray = theme.obtainStyledAttributes(intArrayOf(attrColor))
    val textColor = typedArray.getColor(0, 0)
    typedArray.recycle()
    return textColor
}

@DrawableRes
fun Context.getDrawableFromAttr(@AttrRes attr: Int): Int {
    val typedArray = obtainStyledAttributes(intArrayOf(attr))
    val resourceId = typedArray.getResourceId(0, 0)
    typedArray.recycle()
    return resourceId
}
fun hideKeyboard(activity: Activity?) {
    val view = activity?.findViewById<View>(android.R.id.content)
    if (view != null) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}


var SNACKBAR_TYPE_ERROR = 1
var SNACKBAR_TYPE_SUCCESS = 2
var SNACKBAR_TYPE_MESSAGE = 3
var SNACKBAR_TYPE_ALERT = 4

fun String.showSnackBar(context: Activity?, type: Int = SNACKBAR_TYPE_ERROR, duration: Long = 4000, title: String? = "") {
    if (context != null) {
        var drawable = R.drawable.rounded_corner_red
        var iconDrawable = R.drawable.ic_eye_off
        when (type) {
            SNACKBAR_TYPE_ERROR -> {
                drawable = R.drawable.rounded_corner_red
                iconDrawable = R.drawable.ic_error
            }
            SNACKBAR_TYPE_SUCCESS -> {
                drawable = R.drawable.rounded_corner_green
                iconDrawable = R.drawable.ic_ssuccessful
            }
            SNACKBAR_TYPE_MESSAGE -> {
                drawable = R.drawable.rounded_corner_blue
                iconDrawable = R.drawable.ic_error
            }
            SNACKBAR_TYPE_ALERT -> {
                drawable = R.drawable.rounded_corner_yellow
                iconDrawable = R.drawable.ic_alert
            }
        }

       /* val builder = Flashbar.Builder(context).gravity(Flashbar.Gravity.TOP)
            .message(this).backGroundDrawable(ContextCompat.getDrawable(context, drawable)!!).iconDrawable(ContextCompat.getDrawable(context, iconDrawable)!!).enableSwipeToDismiss().duration(duration).enterAnimation(
                FlashAnim.with(context).animateBar().duration(750).alpha().overshoot()).exitAnimation(FlashAnim.with(context).animateBar().duration(400).accelerateDecelerate())
*/


        val builder = Flashbar.Builder(context).gravity(Flashbar.Gravity.TOP)
//            .title(context.getString(R.string.app_name))
            .message(this).backGroundDrawable(ContextCompat.getDrawable(context, drawable)!!).iconDrawable(ContextCompat.getDrawable(context, iconDrawable)!!).enableSwipeToDismiss().duration(duration).enterAnimation(FlashAnim.with(context).animateBar().duration(750).alpha().overshoot()).exitAnimation(FlashAnim.with(context).animateBar().duration(400).accelerateDecelerate())


        val flashbar = builder.build()
        if (!(flashbar.isShowing() || flashbar.isShown())) {
            flashbar.show()
        }
    }
}

fun String?.getRequestBody(): RequestBody {
    val MEDIA_TYPE_TEXT = "text/plain".toMediaTypeOrNull()
    return (this ?: "").toRequestBody(MEDIA_TYPE_TEXT)
}
