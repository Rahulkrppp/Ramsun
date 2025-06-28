package de.fast2work.mobility.utility.extension

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import de.fast2work.mobility.R
import de.fast2work.mobility.utility.util.LocalConfig.GLIDE_TIMEOUT
class ImageLoadResult {
    var isSuccess: Boolean = false
    var glideException: GlideException? = null
    var drawable: Drawable? = null
}

fun ImageView.loadImage(context: Context, url: String?, @DrawableRes placeHolder: Int?, cornerRadius: Int = 0,
                        loadCircleCrop: Boolean = false, useSizeMultiplier: Boolean = false,
                        onLoadListener: (imageLoadResult: ImageLoadResult) -> Unit = {}) {
    Glide.with(context).apply {
        this.load(url).apply {
            if (placeHolder != null) {
                this.placeholder(placeHolder)
            }
            if (cornerRadius > 0) {
                this.transform(CenterCrop(), RoundedCorners(cornerRadius))
            }
            if (loadCircleCrop) {
                this.centerCrop().circleCrop()
            }
            this.listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    onLoadListener(ImageLoadResult().apply {
                        this.isSuccess = false
                        this.glideException = e
                    })
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?,
                                             isFirstResource: Boolean): Boolean {
                    onLoadListener(ImageLoadResult().apply {
                        this.isSuccess = true
                        this.glideException = null
                        this.drawable = resource
                    })
                    return false
                }

            })
            if (useSizeMultiplier) {
                this.thumbnail(0.33f)
            }
            this.into(this@loadImage)
        }
    }
}

fun ImageView.loadGIF(context: Context, url: String?, placeHolder: Int? = R.drawable.placeholder) {
    if (placeHolder != null) {
        Glide.with(context).asGif().load(url).placeholder(placeHolder).into(this)
    } else {
        Glide.with(context).asGif().load(url).into(this)
    }
}


/**
 * Extention function for load image with glide
 */
fun ImageView.loadImage(context: Context, url: String?, placeHolder: Int? = R.drawable.placeholder, catchImage: Boolean = false) {
    if (placeHolder != null) {
        Glide.with(context).load(url).centerCrop().placeholder(placeHolder).diskCacheStrategy(
            if (catchImage) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE).timeout(GLIDE_TIMEOUT).into(this)
    } else {
        Glide.with(context).load(url).diskCacheStrategy(
            if (catchImage) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE).timeout(GLIDE_TIMEOUT).into(this)
    }
}

fun ImageView.loadBlurImage(url: String = "", placeHolder: Int? = R.drawable.placeholder) {
    //Glide.with(context).load(url).apply(RequestOptions.bitmapTransform(BlurTransformation(25, 5))).placeholder(placeHolder!!).into(this)
}


fun ImageView.loadImage(context: Context, url: String?, catchImage: Boolean = false) {
    Glide.with(context).load(url).placeholder(R.drawable.placeholder).diskCacheStrategy(
        if (catchImage) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE).timeout(GLIDE_TIMEOUT).into(this)
}

/**
 * Extention function for load image with glide
 */
fun ImageView.loadImage(context: Context, url: Uri?, placeHolder: Int? = R.drawable.placeholder, catchImage: Boolean = false) {
    if (placeHolder != null) {
        Glide.with(context).load(url).centerCrop().placeholder(placeHolder).diskCacheStrategy(
            if (catchImage) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE).timeout(GLIDE_TIMEOUT).into(this)
    } else {
        Glide.with(context).load(url).diskCacheStrategy(
            if (catchImage) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE).timeout(GLIDE_TIMEOUT).into(this)
    }

}

/**
 * Extention function for load image with glide
 */
fun ImageView.loadImageWithoutPlaceHolder(context: Context, url: String?) {
    Glide.with(context).load(url).centerCrop().into(this)

}


/**
 * Extention function for load image with glide
 */
fun ImageView.loadRoundedCornerCircleImage(context: Context, url: String?, placeHolder: Int = R.drawable.placeholder,
                                           radius: Int = context.resources.getDimension(R.dimen._10sdp).toInt(),
                                           catchImage: Boolean = false) {

    Glide.with(context).load(url).transform(CenterCrop(), RoundedCorners(radius)).placeholder(
        ContextCompat.getDrawable(context, placeHolder)).diskCacheStrategy(
        if (catchImage) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE).timeout(GLIDE_TIMEOUT).into(this)
}

fun ImageView.loadRoundedCornerCircleImage(context: Context, uri: Uri?, placeHolder: Int = R.drawable.placeholder,
                                           radius: Int = context.resources.getDimension(R.dimen._10sdp).toInt(),
                                           catchImage: Boolean = false) {

    Glide.with(context).load(uri).transform(CenterCrop(), RoundedCorners(radius)).placeholder(
        ContextCompat.getDrawable(context, placeHolder)).diskCacheStrategy(
        if (catchImage) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE).timeout(GLIDE_TIMEOUT).into(this)
}

fun ImageView.loadCircleCropImage(context: Context, image: String, placeHolder: Int? = R.drawable.placeholder,
                                  catchImage: Boolean = false) {
    if (placeHolder != null) {
        Glide.with(context).load(image).centerCrop().circleCrop().placeholder(placeHolder).diskCacheStrategy(
            if (catchImage) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE).into(this)
    } else {
        Glide.with(context).load(image).centerCrop().circleCrop().diskCacheStrategy(
            if (catchImage) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE).timeout(GLIDE_TIMEOUT).into(this)
    }
}


/**
 * Extention function for load image with glide
 */
fun ImageView.loadCircleImage(context: Context, url: String?, placeHolder: Int = R.drawable.placeholder,
                              catchImage: Boolean = false) {
    Glide.with(context).load(url).apply(RequestOptions.circleCropTransform().placeholder(placeHolder).dontAnimate())
        .diskCacheStrategy(if (catchImage) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE).timeout(GLIDE_TIMEOUT).into(this)

}