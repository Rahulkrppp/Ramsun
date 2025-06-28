package de.fast2work.mobility.utility.extension

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import de.fast2work.mobility.R
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.daysUntil
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


var SERVER_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
var YYYY_FORMAT = SimpleDateFormat("yyyy", Locale.ENGLISH)
var MM_FORMAT = SimpleDateFormat("MM", Locale.ENGLISH)
//var MMM_YYYY = SimpleDateFormat("MMM yyyy", Locale.ENGLISH)
var MMM_YY = SimpleDateFormat("MMM yy", Locale.getDefault())
var SERVER_INPUT_FORMAT_TIME = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
var SERVER_INPUT_FORMAT_YYYY_MM_DD_HH_MM_SS = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
var EVENT_FILTER_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
var NOTIFICATION_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)

var DISPLAY_FORMAT_TIME = SimpleDateFormat("HH:mm", Locale.getDefault())
var DISPLAY_FORMAT = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
var DISPLAY_FORMAT_DD_MMM_YYYY = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
var DISPLAY_FORMAT_DD_MMM = SimpleDateFormat("dd MMM", Locale.getDefault())
var DISPLAY_FORMAT_H_MM_A = SimpleDateFormat("hh:mm a", Locale.getDefault())
val DATE_FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy/MM/dd, HH:mm:ss"
var DISPLAY_FORMAT_EEE = SimpleDateFormat("EEE", Locale.getDefault())
var DISPLAY_FORMAT_EE_dd_yyyy = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
var DISPLAY_FORMAT__dd_m = SimpleDateFormat(" dd/MM", Locale.getDefault())
const val DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_FOR_FILENAME = "yyyyMMdd_HHmmss"
var DISPLAY_FORMAT__dd_mmm_yyyy_hh_mmm = SimpleDateFormat("EE dd MMM yyyy hh:mm a", Locale.getDefault())
var DISPLAY_FORMAT_NEW = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ" , Locale.getDefault())
var DISPLAY_FORMAT_HH_MM = SimpleDateFormat("yyyy-MM-dd HH:MM:SS" , Locale.getDefault())
var DISPLAY_FORMAT_D_TICKET_END_DATE = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ" , Locale.getDefault())
var DISPLAY_FORMAT_D_TICKET_DISPLAY= SimpleDateFormat("d MMM yyyy" , Locale.getDefault())
var DISPLAY_FORMAT_D_TICKET_START_DATE= SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy" , Locale.getDefault())
var FORMAT = SimpleDateFormat("yyyy-MM", Locale.ENGLISH)


fun Date.asDateString(format: String? = DATE_FORMAT_YYYY_MM_DD_HH_MM_SS): String = SimpleDateFormat(format, Locale.getDefault()).format(this)




fun Long.asDateString(format: String? = DATE_FORMAT_YYYY_MM_DD_HH_MM_SS): String = Date(this).asDateString(format)





fun changeDateFormat(date: String, originalFormat: SimpleDateFormat, targetFormat: SimpleDateFormat): String {
    return if (date.isNotEmpty()) {
        var dateObj: Date? = null
        try {
            dateObj = originalFormat.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        targetFormat.format(dateObj)
    } else {
        ""
    }
}

fun String.displayDate(originalFormat: SimpleDateFormat, targetFormat: SimpleDateFormat): String {
    return try {
        val formatter: DateFormat = originalFormat
        val date = formatter.parse(this)
        val dateFormat: DateFormat = targetFormat
        dateFormat.format(date!!)
    } catch (exp: Exception) {
       exp.printStackTrace().toString()
    }
}

fun Long.millisToStringTime(targetFormat: SimpleDateFormat): String {
    val date = Date(this)
    return targetFormat.format(date)
}

fun Long.millisToStringTime1(targetFormat: SimpleDateFormat): String {
    val date = Date(this)
    return targetFormat.format(date);
}

fun String.displayServerDate(originalFormat: SimpleDateFormat, targetFormat: SimpleDateFormat): String {
    val dateTime = originalFormat.parse(this)
    return targetFormat.format(dateTime!!)
}

fun String.isCurrentDate(originalFormat: SimpleDateFormat): Boolean {
    val formatter: DateFormat = originalFormat
    val currentDate: String = formatter.format(Calendar.getInstance().timeInMillis)
    return currentDate == this
}

fun Date.getUTCDateTime(): String {
    val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    return dateFormat.format(this)
}

fun getUTCOffset(): String {
    return SimpleDateFormat("ZZZZZ", Locale.getDefault()).format(System.currentTimeMillis())
}

/*fun Long.conversationLastMsgDate(context: Context): String {
    if (this == 0L) {
        return ""
    }
    val today = DateUtils.isToday(this)
    return if (today) asTimeWith12String().replace("am", "AM").replace("pm", "PM")
    else if (isYesterday()) context.getString(R.string.yesterday)
    else asDateForConversation()
}*/

fun Long.isYesterday(): Boolean {
    val now = Calendar.getInstance()
    val cdate = Calendar.getInstance()
    cdate.timeInMillis = this
    now.add(Calendar.DATE, -1)
    return now[Calendar.YEAR] === cdate[Calendar.YEAR] && now[Calendar.MONTH] === cdate[Calendar.MONTH] && now[Calendar.DATE] === cdate[Calendar.DATE]
}

private fun Long.asDateForConversation(): String = SimpleDateFormat( "MMM dd, yyyy", Locale.getDefault()).format(Date(this))

/*fun Long.asMsgDateString(context: Context): String {
    if (this == 0L) {
        return ""
    }
    val today = DateUtils.isToday(this)
    if (today) return "Today"
    else if (isYesterday()) return context.getString(R.string.yesterday)
    else {
        val dateFormat = "MMM dd, yyyy"
        return SimpleDateFormat(dateFormat, Locale.getDefault()).format((this))
    }
}*/
fun Long.asTimeWith12String(): String = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(this))

fun String.getDateInTimeInMillisFromString(simpleDateFormat: SimpleDateFormat): Long {
    try {
        return simpleDateFormat.parse(this).time
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return 0
}

fun Date.getDateInTimeInMillisFromDateObj(simpleDateFormat: SimpleDateFormat): Long {
    try {
        return simpleDateFormat.format(this).getDateInTimeInMillisFromString(simpleDateFormat)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return 0
}


fun Long.asMessageOnlyDateString(): String {
    try {
        if (this == 0L) {
            return ""
        }

        val instant = Instant.fromEpochMilliseconds(this)
        val now = Clock.System.now()
        val timeZone = kotlinx.datetime.TimeZone.currentSystemDefault()
        val days: Int = instant.daysUntil(now, timeZone)

//    val dateFormat = if (days == 0) "H:mm" else "dd-MM-yyyy H:mm"
        val dateFormat = "dd-MM-yyyy"
        return SimpleDateFormat(dateFormat, Locale.getDefault()).format(Date(this))
    } catch (e: Exception) {
        return ""
    }
}

/**
 * This function is used to get UTC Data and Time
 *
 * @param dateFormat
 * @return
 */
fun String.getUTCDateTime(dateFormat: SimpleDateFormat): String {
    dateFormat.timeZone = TimeZone.getDefault()
    val date = dateFormat.parse(this)
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    return dateFormat.format(date)
}

/**
 * This method is used to convert date to UTC to local
 *
 * @return
 */
fun String.getNotificationDateUTCtoLocal(): Date {
    val dateFormat = NOTIFICATION_DATE_FORMAT
//    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val dateTime = dateFormat.parse(this)
    dateFormat.timeZone = TimeZone.getDefault()
    return try {
        val dateString = dateFormat.format(dateTime)
        dateFormat.parse(dateString)
    } catch (e: Exception) {
        e.printStackTrace()
        Date()
    }
}
/**
 * This is method returns timestamp based on current date and time
 *
 * @return
 */
@SuppressLint("StringFormatMatches")
fun String.getShortDateFromTimeStamp(context: Context): String {
    val sb = StringBuilder()
    val currentUnixTime = System.currentTimeMillis()
    var diffInSeconds: Long = 0
    try {
        diffInSeconds = (currentUnixTime - this?.getNotificationDateUTCtoLocal()?.time!!) / 1000
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    val sec = if (diffInSeconds >= 60) diffInSeconds % 60 else diffInSeconds
    val min = if ((diffInSeconds / 60).also {
            diffInSeconds = it
        } >= 60) diffInSeconds % 60 else diffInSeconds
    val hrs = if ((diffInSeconds / 60).also {
            diffInSeconds = it
        } >= 24) diffInSeconds % 24 else diffInSeconds

    val days = if ((diffInSeconds / 24).also {
            diffInSeconds = it
        } >= 30) diffInSeconds % 30 else diffInSeconds
    val months = if ((diffInSeconds / 30).also {
            diffInSeconds = it
        } >= 12) diffInSeconds % 12 else diffInSeconds
    val years = (diffInSeconds / 12).also { diffInSeconds = it }
    if (years > 0) {
        sb.append(years.toString() + context.getString(R.string.year_ago))
    } else if (months > 0) {
        sb.append(months.toString() + context.getString(R.string.month_ago))
    }
    else if (days > 0) {
        if (days in 7..29) {
            if ((days/7).toInt() == 1){
                sb.append(context.getString(R.string.week_ago, (days / 7).toString()))
               // sb.append((days / 7).toString() + context.getString(R.string.week_ago))
            }else {
                sb.append(context.getString(R.string.weeks_ago, (days / 7).toString()))
                //sb.append((days / 7).toString() + context.getString(R.string.weeks_ago))
            }
        } else {
            if (days.toInt() == 1){
                sb.append(context.getString(R.string.day_ago))
            }else{
                sb.append(context.getString(R.string.days_ago, days))

            }
        }
    }
    /* else if (days > 0) {
        sb.append(days.toString() + "d")
    }*/ else if (hrs > 0) {
        if (hrs.toInt() == 1){
            sb.append(context.getString(R.string.hour_ago, hrs))
        }else{
            sb.append(context.getString(R.string.hours_ago, hrs))
        }
    } else if (min > 0) {
        sb.append(context.getString(R.string.min_ago, min))
    } else {
        sb.append(context.getString(R.string.just_now))
    }
    return sb.toString()
}

/**
 * This method contains code to return time ago
 *
 * @return
 */

fun String.getShortDateFromTimeStampWithAgo(): String {
    val sb = StringBuilder()
    val currentUnixTime = System.currentTimeMillis()
    var diffInSeconds: Long = 0
    try {
        diffInSeconds = (currentUnixTime - this?.getNotificationDateUTCtoLocal()?.time!!) / 1000
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    val sec = if (diffInSeconds >= 60) diffInSeconds % 60 else diffInSeconds
    val min = if ((diffInSeconds / 60).also {
            diffInSeconds = it
        } >= 60) diffInSeconds % 60 else diffInSeconds
    val hrs = if ((diffInSeconds / 60).also {
            diffInSeconds = it
        } >= 24) diffInSeconds % 24 else diffInSeconds
    val days = if ((diffInSeconds / 24).also {
            diffInSeconds = it
        } >= 30) diffInSeconds % 30 else diffInSeconds
    val months = if ((diffInSeconds / 30).also {
            diffInSeconds = it
        } >= 12) diffInSeconds % 12 else diffInSeconds
    val years = (diffInSeconds / 12).also { diffInSeconds = it }
    if (years > 1) {
        sb.append(formatDate(EVENT_FILTER_DATE_FORMAT, DISPLAY_FORMAT))
    } else if (months > 1) {
        sb.append(formatDate(EVENT_FILTER_DATE_FORMAT, DISPLAY_FORMAT))
    } else if (months > 0) {
        sb.append(months.toString() + "mo ago")
    }
    else if (days > 0) {
        if (days in 7..29) {
            sb.append(days.toString() + "w ago")
        } else {
            sb.append(days.toString() + "d ago")
        }
    }
    /*else if (days > 0) {
        sb.append(days.toString() + "d ago")
    }*/ else if (hrs > 0) {
        sb.append(hrs.toString() + "h ago")
    } else if (min > 0) {
        sb.append(min.toString() + "min ago")
    } else {
        sb.append("Just now")
    }
    return sb.toString()
}
/**
 * This function is used to format date from one format to another
 *
 * @param inputFormat
 * @param outputFormat
 * @return
 */
fun String.formatDate(inputFormat: SimpleDateFormat, outputFormat: SimpleDateFormat): String {
    try {
        var str = this.replace("Z", "+00:00")
        inputFormat.timeZone = TimeZone.getDefault()
        val date = inputFormat.parse(str)
        outputFormat.timeZone = TimeZone.getDefault()
        return outputFormat.format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return this
}

fun getCurrentDateTimeWithoutSpaces(): String {
    val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    val currentTime = System.currentTimeMillis()
    val currentDate = Date(currentTime)
    return dateFormat.format(currentDate)
}

fun getHourDate(hour:Int):String{

    val calendar = Calendar.getInstance()
    calendar.setTime(Date())
    calendar.add(Calendar.HOUR, hour)


    val datenew = calendar.time.time.millisToStringTime(DISPLAY_FORMAT_HH_MM)
    println("Time here " + datenew)
    return datenew



}

fun getCurrentDatePlusMonth(month: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.setTime(Date())
    calendar.add(Calendar.MONTH, month)
    return calendar.time
}
