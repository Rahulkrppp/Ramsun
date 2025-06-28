package de.fast2work.mobility.utility.extension

import android.net.Uri
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.utility.preference.EasyPref
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun String.toDDMMYYYY(): String {
    val theDateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    theDateFormat.timeZone = TimeZone.getTimeZone("UTC")
    var date = Date()
    try {
        date = theDateFormat.parse(this) as Date
    } catch (exception: Exception) {
        exception.printStackTrace()
    }

    val format = SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH)
    return format.format(date)
}
fun String.toDDMYYYY(): String {
    val theDateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    theDateFormat.timeZone = TimeZone.getTimeZone("UTC")
    var date = Date()
    try {
        date = theDateFormat.parse(this) as Date
    } catch (exception: Exception) {
        exception.printStackTrace()
    }

    val format = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    return format.format(date)
}

fun String.formatAsPrice(): String {
    val currency = "€"
    return "$currency${this}"
}

fun String.capitalized() = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}

//fun Double.formatCurrency(): String {
//    val currencyFormat = NumberFormat.getCurrencyInstance(Locale(BaseApplication.sharedPreference.getPref(EasyPref.PARAM_LANGUAGE,"")))
//    currencyFormat.currency = Currency.getInstance("EUR") // You can change "USD" to the desired currency code
//    return currencyFormat.format(this)
//}

fun Float.formatCurrency(currencySymbol : String?): String {
    val numberFormat = NumberFormat.getCurrencyInstance(Locale(BaseApplication.sharedPreference.getPref(EasyPref.CURRENCY_FORMAT,"")))
    val decimalFormatSymbols = (numberFormat as DecimalFormat).decimalFormatSymbols
    decimalFormatSymbols.currencySymbol = "" // Set currency symbol to empty string
    (numberFormat as DecimalFormat).decimalFormatSymbols = decimalFormatSymbols
    return currencySymbol + numberFormat.format(this)
}

fun Float.formatCurrencyNew(currencySymbol : String?): String {
    val numberFormat = NumberFormat.getCurrencyInstance(Locale(BaseApplication.sharedPreference.getPref(EasyPref.CURRENCY_FORMAT,"")))
    val decimalFormatSymbols = (numberFormat as DecimalFormat).decimalFormatSymbols
    decimalFormatSymbols.currencySymbol = "" // Set currency symbol to empty string
    (numberFormat as DecimalFormat).decimalFormatSymbols = decimalFormatSymbols
    return  numberFormat.format(this) + currencySymbol
}
fun Double.formatCurrencyNew(currencySymbol : String?): String {
    val numberFormat = NumberFormat.getCurrencyInstance(Locale(BaseApplication.sharedPreference.getPref(EasyPref.CURRENCY_FORMAT,"")))
    val decimalFormatSymbols = (numberFormat as DecimalFormat).decimalFormatSymbols
    decimalFormatSymbols.currencySymbol = "" // Set currency symbol to empty string
    (numberFormat as DecimalFormat).decimalFormatSymbols = decimalFormatSymbols
    return  numberFormat.format(this) + currencySymbol
}
fun Double.formatWithOutCurrency(): String {
    val numberFormat = NumberFormat.getCurrencyInstance(Locale(BaseApplication.sharedPreference.getPref(EasyPref.CURRENCY_FORMAT,"")))
    val decimalFormatSymbols = (numberFormat as DecimalFormat).decimalFormatSymbols
    decimalFormatSymbols.currencySymbol = "" // Set currency symbol to empty string
    (numberFormat as DecimalFormat).decimalFormatSymbols = decimalFormatSymbols
    return  numberFormat.format(this)
}

fun Any?.toBlankString(): String {
    return if (this == null || this == "") {
        ""
    }else{
        this.toString()
    }
}
fun findUrlName (url:String):String{
    val uri = Uri.parse(url)
    val fileName = uri.lastPathSegment
    return fileName.toString()
}