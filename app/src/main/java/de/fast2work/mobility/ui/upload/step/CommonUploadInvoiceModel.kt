package de.fast2work.mobility.ui.upload.step

import android.Manifest
import android.os.Build
import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.ui.core.BaseViewModel
import de.fast2work.mobility.ui.dashboard.DashBoardRepository
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CommonUploadInvoiceModel @Inject constructor(private val repository: DashBoardRepository) : BaseViewModel(){


    var permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.CAMERA)
    } else {
        arrayOf(Manifest.permission.CAMERA)
    }
    var isCameraSelected = false
    var imageFile: File? = null
    var selectedFile: File? = null

    var imageFileThreeStep: File? = null
    var selectedFileThreeStep: File? = null


}