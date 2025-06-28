package de.fast2work.mobility.utility.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.FileProvider
import de.fast2work.mobility.BuildConfig
import de.fast2work.mobility.R
import de.fast2work.mobility.ui.core.BaseActivity
import de.fast2work.mobility.utility.helper.DownloadFile
import de.fast2work.mobility.utility.helper.IOnDownloadListener
import java.io.*
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs


/**
 * All app-wide utility methods are implemented here.
 */
object Util {

    fun dismissKeyboard(activity: Activity) {
        try {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (activity.currentFocus != null) {
                imm.hideSoftInputFromWindow(activity.currentFocus!!.applicationWindowToken, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun dpToPx(dp: Float): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    private fun appInstalledOrNot(context: Context, uri: String): Boolean {
        val pm = context.packageManager
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return false
    }

    fun startDownloading(context: BaseActivity, pdf: String, isPDFFile: Boolean) {
        if (!isPDFFile) {
            val fileName: String = pdf.substring(pdf.lastIndexOf('/') + 1)

            val folder = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

            val downloadedPDFFile = File(folder, fileName)
            if (downloadedPDFFile.exists() && downloadedPDFFile.length() > 0) {
                openFile(context, downloadedPDFFile)
                Toast.makeText(context, "File Already Exist", Toast.LENGTH_SHORT).show()
                return
            }

            context.showProgressDialog(true)
            DownloadFile(context).downloadPDF(pdf, fileName, object : IOnDownloadListener {
                override fun onSuccess(filePath: String) {
                    context.showProgressDialog(false)
                    Toast.makeText(context, "File Downloaded", Toast.LENGTH_SHORT).show()
                    openFile(context, File(filePath))
                }

                override fun onFailure() {
                    context.showProgressDialog(false)
                }

                override fun onInProgress() {
                }
            })
        } else {
            openFile(context, Uri.parse(pdf))
        }
    }

    fun openFile(context: Activity, file: File) {
        try {
            var isAppInstalled = false
            val type = Media.MIME_TYPE_PDF
//            val packageName = "com.google.android.apps.pdfviewer"
            if (appInstalledOrNot(context, "com.google.android.apps.pdfviewer")) {
                isAppInstalled = true
            } else if (appInstalledOrNot(context, "com.adobe.reader")) {
                isAppInstalled = true
            } else if (appInstalledOrNot(context, "com.google.android.apps.docs")) {
                isAppInstalled = true
            }
            if (isAppInstalled) {
                //This intent will help you to launch if the package is already installed
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    val contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file)
                    intent.setDataAndType(contentUri, type)
                } else {
                    intent.setDataAndType(Uri.fromFile(file), type)
                }
                context.startActivity(intent)
            }
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun openFile(context: Activity, file: Uri) {
        try {
            var isAppInstalled = false
            val type = "application/pdf"
//            val packageName = "com.google.android.apps.pdfviewer"
            if (appInstalledOrNot(context, "com.google.android.apps.pdfviewer")) {
                isAppInstalled = true
            } else if (appInstalledOrNot(context, "com.adobe.reader")) {
                isAppInstalled = true
            } else if (appInstalledOrNot(context, "com.google.android.apps.docs")) {
                isAppInstalled = true
            }
            if (isAppInstalled) {
                //This intent will help you to launch if the package is already installed
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    intent.setDataAndType(file, type)
                } else {
                    intent.setDataAndType(file/*Uri.fromFile(file)*/, type)
                }
                context.startActivity(intent)
            }
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }

    fun checkTime(startTime: String, endTime: String, checkTime: String): Boolean {
        //val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm aa", Locale.ENGLISH)

        val time1: Date = SimpleDateFormat("hh:mm aa", Locale.ENGLISH).parse(startTime) as Date
        val calendar1 = Calendar.getInstance(TimeZone.getDefault())
        calendar1.time = time1
        calendar1.set(Calendar.DATE, 0)

        val time2 = SimpleDateFormat("hh:mm aa", Locale.ENGLISH).parse(endTime)
        val calendar2 = Calendar.getInstance(TimeZone.getDefault())
        calendar2.time = time2!!
        calendar1.set(Calendar.DATE, 0)

        val d = SimpleDateFormat("hh:mm aa", Locale.ENGLISH).parse(checkTime)
        val calendar3 = Calendar.getInstance(TimeZone.getDefault())
        calendar3.time = d!!
        calendar1.set(Calendar.DATE, 0)

        val x = calendar3.time

        if (x.after(calendar1.time) && x.before(calendar2.time)) {
            println(true)
            return true
        }
        return false
    }

    fun printDifference(startDate: Date, endDate: Date): Int {
        //milliseconds
        var different = endDate.time - startDate.time
//        println("startDate : $startDate")
//        println("endDate : $endDate")
//        println("different : $different")
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24
        val elapsedDays = different / daysInMilli
        different %= daysInMilli
        val elapsedHours = different / hoursInMilli
        different %= hoursInMilli
        val elapsedMinutes = different / minutesInMilli
        different %= minutesInMilli
        val elapsedSeconds = different / secondsInMilli
        System.out.printf("%d days, %d hours, %d minutes, %d seconds%n", elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds

        )
        return elapsedHours.toInt()

    }

    fun getTimeDurationInSeconds(startDate: Date?, endDate: Date): Int {
        val different = endDate.time - startDate?.time!!
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone("UTC")
        calendar.time = startDate
        val calendar2 = Calendar.getInstance()
        calendar2.timeZone = TimeZone.getTimeZone("UTC")
        calendar2.time = endDate
        val diffInSec: Long = TimeUnit.MILLISECONDS.toSeconds(different)
        return abs(diffInSec.toInt())

    }


    fun getColorHexCodeFromColorDrawable(v: View): String {
        val viewColor: ColorDrawable = v.background as ColorDrawable
        val colorId: Int = viewColor.color
        return String.format("#%06X", 0xFFFFFF and colorId)
    }

    fun storeImage(image: File, context: Context): File {
        val pictureFile = File(context.filesDir, (Calendar.getInstance().timeInMillis.toString() + ".gif"))
        try {
            val input = FileInputStream(image)
            val output = FileOutputStream(pictureFile)
            val inputChannel: FileChannel = input.channel
            val outputChannel: FileChannel = output.channel
            inputChannel.transferTo(0, inputChannel.size(), outputChannel)
            output.close()
            input.close()

            return pictureFile
            //  viewModel.callImgTaskApi(selectedImgFile, eventStartDate, selectedSlot, binding!!.etStartTime.getTrimText(), binding!!.etEndTime.getTrimText())
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return pictureFile
    }

    fun getUtcDeviceDateTimeOffsetDifference(): Long {
        val deviceDateTimeOffset = Calendar.getInstance().timeInMillis + TimeZone.getDefault().getOffset(
            Calendar.getInstance().timeInMillis)
        val utcDateTimeOffset = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH).parse(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH).format(System.currentTimeMillis()))?.time ?: 0
        return (deviceDateTimeOffset - utcDateTimeOffset) / 1000
    }
}