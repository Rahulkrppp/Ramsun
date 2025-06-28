package de.fast2work.mobility.utility.extension

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.content.ContextCompat
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.math.roundToInt

/**
 * This file contains methods for context extension
 *
 * @return
 */
/**
 * This method checks and returns callback for if internet is available or not
 *
 * @return
 */
fun Context.isNetworkAvailable(): Boolean {
    var result = false
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

    cm?.run {
        cm.getNetworkCapabilities(cm.activeNetwork)?.run {
            result = when {
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
    }
    return result
}

fun Context.isWifiConnectedButNoInternet(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

    // Check if the connection is via Wi-Fi
    val isWifi = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)

    // Check for internet capabilities
    val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

    // Check if it's connected but has no validated internet
    val isValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

    // Return true if it's Wi-Fi, has internet capability but no internet access
    return isWifi && hasInternet && !isValidated
}

var SCREEN_HEIGHT = 0
var SCREEN_WIDTH: Int = 0

fun Context.setWindowDimensions() {
    val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val size = Point()
    display.getSize(size)
    SCREEN_WIDTH = size.x
    SCREEN_HEIGHT = size.y
}

/**
 * This method is used to get App Version
 *
 * @return
 */
fun Context.getAppVersion(): String {
    try {
        val pInfo = packageManager.getPackageInfo(packageName, 0);
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pInfo.longVersionCode.toBlankString()
        } else {
            pInfo.versionCode.toBlankString()
        };
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace();
    }
    return "0"
}

fun Context.openExternalBrowser(url: String) {
    var webUrl = url
    if (!url.contains("https")) {
        webUrl = "https://$webUrl"
    }
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
    startActivity(browserIntent)
}

fun Context.openMap(locationUrl: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(locationUrl))
    intent.setPackage("com.google.android.apps.maps");
    startActivity(intent)
}

/**
 * This methid is used to get resource color
 *
 * @param colorId
 * @return
 */
fun Context.getResourceColor(colorId: Int): Int {
    return ContextCompat.getColor(this, colorId)
}

fun Context.getVersionName(): String {
    var versionName = "1.0"
    try {
        val pInfo: PackageInfo = this.packageManager.getPackageInfo(this.packageName, 0)
        versionName = pInfo.versionName.toString()
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return versionName
}

/**
 * This method is used to get device name
 *
 * @return
 */
fun getDeviceName(): String {
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    return if (model.startsWith(manufacturer)) {
        model.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toBlankString() }
    } else manufacturer.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toBlankString() } + " " + model
}

fun Context.copyText(copyText: String) {
    val clipboard: ClipboardManager? = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clip: ClipData = ClipData.newPlainText("label", copyText)
    clipboard?.setPrimaryClip(clip)
}

/**
 * This method is used to convert Dp into Px
 *
 * @param dp
 * @return
 */
fun Context?.dpToPx(dp: Int): Int {
    this?.let {
        val displayMetrics = this.resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }
    return 0
}

/**
 * This function is used to convert Px to Dp
 *
 * @param px
 * @return
 */
fun Context?.pxToDp(px: Int): Int {
    this?.let {
        val displayMetrics = it.resources.displayMetrics
        return (px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }
    return 0
}

/**
 * This function is used to get device Id
 *
 * @return
 */
@SuppressLint("HardwareIds")
fun Context.getAndroidDeviceId(): String {
    return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
}

/**
 * This function is used to convert String to stringpart
 *
 * @return
 */
fun String.toStringPart(): RequestBody {
    return this.toRequestBody("text/plain".toMediaTypeOrNull())
}

fun Window.changeStatusBarColor(color: Int) {
    val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255

    if (Build.VERSION.SDK_INT < 30) {
        var systemUiVisibility = this.decorView.systemUiVisibility
        systemUiVisibility = if (darkness < 0.5) {
            systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        this.decorView.systemUiVisibility = systemUiVisibility
    } else {

        if (darkness > 0.5) {
            val controller = this.insetsController
            controller?.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        } else {

            val controller = this.insetsController
            controller?.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        }

    }

    this.statusBarColor = color
}

fun Context.hasActiveInternetConnection(): Boolean {
    if (this.isNetworkAvailable()) {
        try {
            val urlc = URL("http://www.google.com").openConnection() as HttpURLConnection
            urlc.setRequestProperty("User-Agent", "Test")
            urlc.setRequestProperty("Connection", "close")
            urlc.connectTimeout = 1500
            urlc.connect()
            return urlc.responseCode == 200
        } catch (e: IOException) {
            Log.e("ACTIVE_INTERNET", "Error checking internet connection", e)
            return false
        }
    }
    else {
        Log.e("ACTIVE_INTERNET", "No network available!")
        return false
    }
}



