package de.fast2work.mobility.ui.upload.step

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.zynksoftware.documentscanner.ScanActivity
import com.zynksoftware.documentscanner.model.DocumentScannerErrorModel
import com.zynksoftware.documentscanner.model.ScannerResults
import dagger.hilt.android.AndroidEntryPoint
import de.fast2work.mobility.R
import de.fast2work.mobility.data.eventbus.ImageFile
import de.fast2work.mobility.data.model.PreferredLanguageModel
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.upload.UploadInvoiceFragment
import de.fast2work.mobility.utility.util.IConstants.Companion.TAG
import org.greenrobot.eventbus.EventBus
import java.io.File

@AndroidEntryPoint
class AppScanActivity: ScanActivity() {
    var uploadThree:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_scan_activity)
        //inter.
        addFragmentContentLayout()
        uploadThree= intent.extras?.getBoolean("threeStep") == true
    }

    fun start(context: Context,uploadThree:Boolean=false) {
        val intent = Intent(context, AppScanActivity::class.java)
        intent.putExtra("threeStep",uploadThree)
        context.startActivity(intent)
    }

    override fun onError(error: DocumentScannerErrorModel) {

    }

    override fun onSuccess(scannerResults: ScannerResults) {
        Log.e(TAG, "onSuccess: Scanned Result---->${scannerResults}" )
        if (uploadThree){
            BaseApplication.scannerThreeStepFile = scannerResults.originalImageFile.toString()
        }
        else {
            BaseApplication.scannerFile = scannerResults.croppedImageFile.toString()
        }
        finish()
    }

    override fun onClose() {
        finish()
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}