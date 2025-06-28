package de.fast2work.mobility.utility.extension

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.util.Base64
import android.widget.ImageView
import androidx.core.widget.ImageViewCompat
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import de.fast2work.mobility.BuildConfig
import de.fast2work.mobility.R
import de.fast2work.mobility.ui.core.BaseApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


/**
 * Extention function for load image with glide
 */
fun ImageView.loadImage(
    context: Context,
    url: String?,
    placeHolder: Int? = R.drawable.ic_no_image
) {
    if (placeHolder != null) {
        Glide.with(context).load(url).centerCrop().placeholder(placeHolder).into(this)
    } else {
        Glide.with(context).load(url).into(this)
    }
}

fun ImageView.loadImage(
    context: Context,
    bytes: ByteArray?,
    placeHolder: Int? = R.drawable.ic_no_image
) {
    if (placeHolder != null) {
        Glide.with(context).load(bytes).centerCrop().placeholder(placeHolder).into(this)
    } else {
        Glide.with(context).load(bytes).into(this)
    }
}

fun ImageView.loadCircleImage(
    context: Context,
    bytes: ByteArray?,
    placeHolder: Int = R.drawable.ic_no_image
) {
    Glide.with(context).load(bytes)
        .apply(RequestOptions.circleCropTransform().placeholder(placeHolder).dontAnimate())
        .into(this)
}

fun ImageView.loadImage(context: Context, url: String?) {
    Glide.with(context).load(url).placeholder(R.drawable.ic_no_image).into(this)
}

/**
 * Extention function for load image with glide
 */
fun ImageView.loadImage(
    context: Context,
    url: Uri?,
    placeHolder: Int? = R.drawable.ic_no_image
) {
    if (placeHolder != null) {
        Glide.with(context).load(url).centerCrop().placeholder(placeHolder).into(this)
    } else {
        Glide.with(context).load(url).into(this)
    }
}

/**
 * Extention function for load image with glide
 */
fun ImageView.loadCircleImage(
    context: Context,
    url: String?,
    placeHolder: Int = R.drawable.ic_no_image
) {
    Glide.with(context).load(url)
        .apply(RequestOptions.circleCropTransform().placeholder(placeHolder).dontAnimate())
        .into(this)
}

/**
 * This function used to set tint to image
 *
 * @param colorRes
 */
fun ImageView.setTint(colorRes: String) {
    try {
        if (BaseApplication.themeValue == Configuration.UI_MODE_NIGHT_NO) {
            ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(Color.parseColor(colorRes)))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun ImageView.loadCircleImageFromBase64(context: Context, base64string: String) {
    val imageBytes = Base64.decode(base64string, Base64.DEFAULT)
    if (imageBytes != null) {
        this.loadCircleImage(context, imageBytes)
    }
}

fun ImageView.loadImageFromBase64(context: Context, base64string: String) {
    val imageBytes = Base64.decode(base64string, Base64.DEFAULT)
    if (imageBytes != null) {
        this.loadImage(context, imageBytes)
    }
}

fun ImageView.loadImageIcons(context: Context, iconName: String?){
    val url = BuildConfig.BASE_URL + "analyticsApi/getIcon/${iconName}?width=100"
//    this.imageTint(BaseApplication.tenantSharedPreference.getPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)?.brandingInfo?.primaryColor)
    this.loadImage(context, url)
}
fun ImageView.loadUrl(url: String) {
    val imageLoader = ImageLoader.Builder(context)
        .components { add(SvgDecoder.Factory()) }
        .build()

    val request = ImageRequest.Builder(context)
        .crossfade(true)
        .crossfade(500)
        .placeholder(R.drawable.placeholder)
        .data(url)
        .target(this)
        .build()

    imageLoader.enqueue(request)
}

fun compressImage(context: Context, imageUri: Uri): File {

    val inputStream = context.contentResolver.openInputStream(imageUri)
    val originalBitmap = BitmapFactory.decodeStream(inputStream)

    val compressedFile = File(context.cacheDir, findUrlName(imageUri.toString()))
    val outputStream = FileOutputStream(compressedFile)

    originalBitmap.compress(Bitmap.CompressFormat.JPEG, 35, outputStream)

    // Compress to 50% quality


    outputStream.flush()
    outputStream.close()
    return compressedFile
}
