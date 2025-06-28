package de.fast2work.mobility.utility.extension

import android.app.*
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Html
import android.text.Spanned
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager

import de.fast2work.mobility.R
import java.net.URL
import java.util.*


/**
 * This class contain alert message helper
 */
object DialogUtil {
    var singleAlertDialog: AlertDialog? = null

    interface IL {
        fun onSuccess()

        fun onCancel(isNeutral: Boolean)
    }

    /**
     * This method is used to show alert dialog box for force close application
     *
     * @param context - Object of Context, appContext from where the activity is going
     * to start.
     * @param msg     - Message String that represents alert box message
     */
    fun alert(context: Context, msg: String) {
        try {
            val alertDialogBuilder = getBuilder(context)
            alertDialogBuilder.setMessage(fromHtml(msg))
            alertDialogBuilder.setPositiveButton(android.R.string.ok) { dialog, id -> dialog.dismiss() }
            alertDialogBuilder.setOnKeyListener { dialog, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK) dialog.dismiss()
                false
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    private fun fromHtml(html: String): Spanned {
        return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
    }



    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveFileUsingMediaStore(context: Context, url: String, fileName: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            URL(url).openStream().use { input ->
                resolver.openOutputStream(uri).use { output ->
                    input.copyTo(output!!, DEFAULT_BUFFER_SIZE)
                }
            }
        }
    }

    private fun getBuilder(context: Context): AlertDialog.Builder {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setCancelable(false)
        return alertDialogBuilder
    }


    interface OnDatePickerDateSet {
        fun onDateSet(long: Long)
    }

    interface OnTimePickerDateSet {
        fun onTimeSet(long: Long, hourOfDay: Int, minute: Int)
    }

    fun showDatePickerDialog(
        context: Context, minDate: Long = 0L, selectedDate: Long = System.currentTimeMillis(),
        onDatePickerDateSet: OnDatePickerDateSet? = null, maxDate: Long = 0L,
        myCalendar1: Calendar = Calendar.getInstance(),
    ) {

        val myCalender = Calendar.getInstance()

        myCalender.timeInMillis = selectedDate
        val listener = DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar[Calendar.YEAR] = year
            calendar[Calendar.MONTH] = month
            calendar[Calendar.DATE] = dayOfMonth
//        calendar[Calendar.HOUR] = 0
//        calendar[Calendar.MINUTE] = 0
//        calendar[Calendar.SECOND] = 0
            onDatePickerDateSet?.onDateSet(calendar.timeInMillis)
        }
        val departDialog = DatePickerDialog(
            context, R.style.CalanderTheme, listener, myCalender.get(Calendar.YEAR),
            myCalender.get(Calendar.MONTH), myCalender.get(Calendar.DAY_OF_MONTH)
        )
        val minCalendar = Calendar.getInstance()
        minCalendar.add(Calendar.YEAR, -100)

        departDialog.datePicker.minDate = if (minDate == 0L) minCalendar.timeInMillis else minDate
        if (maxDate > 0L) departDialog.datePicker.maxDate = maxDate

        departDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Ok", departDialog)
        departDialog.setButton(
            DatePickerDialog.BUTTON_NEGATIVE,
            context.getString(R.string.cancel),
            null as DialogInterface.OnClickListener?
        )
        departDialog.show()
    }


}
