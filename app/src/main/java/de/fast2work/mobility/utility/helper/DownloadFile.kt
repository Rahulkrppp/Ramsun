package de.fast2work.mobility.utility.helper

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException

/**
 * This class contains code for Downloading file
 *
 * @constructor
 * TODO
 *
 * @param activity
 */
class DownloadFile(activity: Activity) {

    private var mDownloadManager: DownloadManager? = null
    private var activity: Activity? = activity
    private var documentFolder: File? = null
    private var mDownloadedFileID: Long = 0
    private var mRequest: DownloadManager.Request? = null

    init {
//        documentFolder = activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        documentFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        //documentFolder=android.os.Environment.getExternalStorageDirectory()
        mDownloadManager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    /**
     * This method contains code for download file
     *
     * @param url
     * @param pdfFileName
     * @param onDownloadListener
     */
    fun downloadPDF(url: String, pdfFileName:String, onDownloadListener: IOnDownloadListener) {

        var selectedFile = File(documentFolder, pdfFileName)
        selectedFile = if (selectedFile.exists()){
            selectedFile.delete()
            File(documentFolder, pdfFileName)
        }else{
            File(documentFolder, pdfFileName)
        }

        Log.d("downloadFile", "started")

        // Function is called once download completes.
        val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // Prevents the occasional unintentional call. I needed this.
                if (mDownloadedFileID == -1L) return

                onDownloadListener.onSuccess(selectedFile.absolutePath)

                // Sets up the prevention of an unintentional call. I found it necessary. Maybe not for others.
                mDownloadedFileID = -1
            }
        }
        // Registers function to listen to the completion of the download.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity!!.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED)
        }else{
            activity!!.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }

        try {
            mDownloadManager = activity!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri = Uri.parse(url)
            mRequest = DownloadManager.Request(downloadUri)
            mRequest!!.setMimeType("application/vnd.android.package-archive")
            mRequest!!.setTitle("Download")
            mRequest!!.setDescription("Downloading...")
            // set destination
            mRequest!!.setDestinationUri(Uri.fromFile(selectedFile))
            mRequest!!.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
            // Enqueue a new download and same the referenceId
            // Adds the request to the DownloadManager queue to be executed at the next available opportunity.
            mDownloadedFileID = mDownloadManager!!.enqueue(mRequest)
        } catch (e: IOException) {
            e.printStackTrace()
            onDownloadListener.onFailure()
            Log.d("Error....", e.toString())
        }
        Log.d("downloadFile", "inProgress")

        onDownloadListener.onInProgress()
    }
}