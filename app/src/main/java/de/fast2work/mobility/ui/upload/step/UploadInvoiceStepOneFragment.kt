package de.fast2work.mobility.ui.upload.step


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yalantis.ucrop.UCrop
import com.zynksoftware.documentscanner.ui.DocumentScanner
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.databinding.DialogAttachmentBinding
import de.fast2work.mobility.databinding.FragmentUploadInvoiceStepOneBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.upload.UploadInvoiceViewModel
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.getFileFromContentUri
import de.fast2work.mobility.utility.extension.getFileSize
import de.fast2work.mobility.utility.extension.setTint
import de.fast2work.mobility.utility.preference.EasyPref
import de.fast2work.mobility.utility.util.ImageUtil
import de.fast2work.mobility.utility.util.LocalConfig
import de.fast2work.mobility.utility.util.Media
import de.fast2work.mobility.utility.util.Media.MIME_TYPE_PDF
import de.fast2work.mobility.utility.util.Media.MIME_TYPE_TEXT
import de.fast2work.mobility.utility.util.currentDate
import java.io.File
import java.math.RoundingMode


/**
 * create an instance of this fragment.
 */
 class UploadInvoiceStepOneFragment : BaseVMBindingFragment<FragmentUploadInvoiceStepOneBinding, CommonUploadInvoiceModel>(CommonUploadInvoiceModel::class.java)/*,ClickInterface*/ {

    lateinit var parentFragmentViewModel: UploadInvoiceViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return generateBinding(FragmentUploadInvoiceStepOneBinding.inflate(inflater), container)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        parentFragmentViewModel = parentFragment?.let {
            ViewModelProvider(it)[UploadInvoiceViewModel::class.java]
        }!!


        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (BaseApplication.scannerFile.isNotEmpty()) {
            viewModel.imageFile = File(BaseApplication.scannerFile)
            val fileSize = viewModel.imageFile?.getFileSize()
            if (fileSize?.toFloat()?.toInt()!! < 10) {
                viewModel.selectedFile = viewModel.imageFile
                binding?.apply {
                    cardViewDoc.visibility = View.VISIBLE
                    tvPdfName.text = viewModel.selectedFile?.name.toString()
                    tvFileSize.text = "${fileSize?.toBigDecimal()?.setScale(2, RoundingMode.UP)?.toDouble()} MB"
                    Glide.with(requireContext()).load(R.drawable.ic_img_display).placeholder(
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_img_display)).into(ivUrlLogo)
                }
                parentFragmentViewModel.addInvoice.file = viewModel.selectedFile

            } else {
                showErrorMessage(getString(R.string.file_size_should_not_exceed_10mb))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        hideTabs()
    }
    override fun attachObservers() {

    }
    override fun initComponents() {
        val configuration = DocumentScanner.Configuration()
        configuration.imageQuality = 100
        configuration.imageSize = 1000000 // 1 MB
        configuration.imageType = Bitmap.CompressFormat.JPEG
        DocumentScanner.init(requireContext(), configuration) // or simply DocumentScanner.init(this)
        if (isFirstTimeLoad) {
            binding?.apply {
                ivChooseFile.setTint(BaseApplication.tenantSharedPreference.getTenantPrefModel(EasyPref.TENANT_DATA,
                    TenantInfoModel::class.java)?.brandingInfo?.primaryColor.toString())
                if (viewModel.selectedFile != null) {
                    cardViewDoc.visibility = View.VISIBLE
                }
            }
        }

    }

    override fun setClickListener() {
        binding?.clChooseFile?.clickWithDebounce {
            checkPermission()
        }

        binding?.ivClose?.clickWithDebounce {
            binding?.cardViewDoc?.visibility = View.GONE
            parentFragmentViewModel.addInvoice.file = null
            viewModel.selectedFile = null
        }
    }


    private fun checkPermission() {
        requestMultiplePermissions.launch(viewModel.permissions)
    }
    @RequiresApi(Build.VERSION_CODES.O)
  private  val pickMultipleVisualMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uris ->

        if(uris!=null){

        BaseApplication.scannerFile = ""
        val resultUri = if (viewModel.isCameraSelected) {
            Uri.fromFile(viewModel.imageFile)
        } else {
            uris
        }
            startCrop(resultUri!!)
            }
        // Process URIs
    }
    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach {
            if (it.key == Manifest.permission.CAMERA) {
                if (it.value) {
                    selectImgBottomSheet()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun selectImgBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())

        val binding = DialogAttachmentBinding.inflate(LayoutInflater.from(requireActivity()))
        val view = binding.root
        dialog.apply {
            setOnShowListener {
                val bottomSheet = findViewById<View?>(com.google.android.material.R.id.design_bottom_sheet)
                if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
                    bottomSheet?.setBackgroundResource(R.color.color_primary_dark_text_80)
                }else{
                    bottomSheet?.setBackgroundResource(android.R.color.transparent)
                }
            }
        }

        if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
            dialog.window?.setBackgroundDrawableResource(R.color.color_primary_dark_text_80)
        }else{
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }

        binding.llFiles.visibility = View.VISIBLE

        binding.llFiles.setOnClickListener {
            openFiles()
            dialog.dismiss()
        }

        binding.llCamera.setOnClickListener {
            AppScanActivity().start(requireActivity())
            dialog.dismiss()
        }
        binding.llGallery.setOnClickListener {
            viewModel.isCameraSelected = false


// Launching the photo picker (photos & video included)
            pickMultipleVisualMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            dialog.dismiss()
//            openGallery()
//            dialog.dismiss()
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        launchSelectImageIntent.launch(intent)
    }

    private fun openFiles() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        val mimeTypes = arrayOf(/*MIME_TYPE_TEXT*/MIME_TYPE_PDF)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        launchFilesIntent.launch(intent)
    }


    /**
     * Contains image from camera
     */
//    private fun getImageFromCamera() {
//        try { // create Intent to take a picture and return control to the calling application
//           // val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            // Create a File reference to access to future access
//            viewModel.imageFile = ImageUtil.getPhotoFileUri(requireActivity(),
//                "Invoice_${getCurrentDateTimeWithoutSpaces()}" + ".${Media.EXTENSION_JPEG}")
//            // wrap File object into a content provider
//            val fileProvider = FileProvider.getUriForFile(requireActivity(), BuildConfig.APPLICATION_ID + ".fileprovider",
//                viewModel.imageFile!!)
//            //intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
//            //launchSelectImageIntent.launch(intent)
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val launchSelectImageIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            BaseApplication.scannerFile = ""
            val resultUri = if (viewModel.isCameraSelected) {
                Uri.fromFile(viewModel.imageFile)
            } else {
                result.data!!.data
            }

//            val fileSize = if (viewModel.isCameraSelected) {
//                imageFile?.getFileSize()
//            } else {
//                resultUri?.getFileSize(requireActivity())
//            }
            startCrop(resultUri!!)

        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // Use the returned uri.
            val uriContent = result.uriContent
            val uriFilePath = result.getUriFilePath(requireContext()) // optional usage
            val fileSize = if (viewModel.isCameraSelected) {
                viewModel.imageFile?.getFileSize()
            } else {
                uriContent?.getFileSize(requireActivity())
            }
            if (fileSize?.toFloat()?.toInt()!! < 10) {
                viewModel.selectedFile = if (viewModel.isCameraSelected) {
                    viewModel.imageFile
                } else {
                    uriContent?.getFileFromContentUri(requireActivity())
                }
                binding?.apply {
                    cardViewDoc.visibility = View.VISIBLE
                    tvPdfName.text = viewModel.selectedFile?.name.toString()
                    tvFileSize.text = "${fileSize?.toBigDecimal()?.setScale(2, RoundingMode.UP)?.toDouble()} MB"
                    //viewModel.selectedProfileHistory = ProfileHistoryResponse()
                    Glide.with(requireContext()).load(R.drawable.ic_img_display).placeholder(
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_img_display)).into(ivUrlLogo)
                }
                parentFragmentViewModel.addInvoice.file = viewModel.selectedFile

//            }
            } else {
                showErrorMessage(getString(R.string.file_size_should_not_exceed_10mb))
            }
        } else {
            // An error occurred.
            val exception = result.error
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startCrop(resultUri: Uri) {
        // Start cropping activity for pre-acquired image saved on the device and customize settings.
        cropImage.launch(CropImageContractOptions(uri = resultUri, CropImageOptions(
            activityBackgroundColor = requireActivity().getColorFromAttr(com.google.android.material.R.attr.backgroundColor),
            toolbarTintColor = requireActivity().getColorFromAttr(R.attr.colorNavBar),
            toolbarColor = requireActivity().getColorFromAttr(R.attr.colorNavBar),
            toolbarBackButtonColor = requireActivity().getColorFromAttr(R.attr.colorTextView),
            activityMenuIconColor = requireActivity().getColorFromAttr(R.attr.colorTextView),
            allowFlipping = false,
        )))
    }
    /**
     * Contains crop image from camera and photo
     */
    private fun cropImage(resultUri: Uri) {
        val options = UCrop.Options()
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
        options.withMaxResultSize(LocalConfig.PROFILE_IMAGE_SIZE, LocalConfig.PROFILE_IMAGE_SIZE)
        options.setToolbarTitle(getString(R.string.crop_image))
        options.setHideBottomControls(true)
        options.setShowCropFrame(false)
        options.setShowCropGrid(false)
        options.setCircleDimmedLayer(false)

        val uCrop = UCrop.of(resultUri, Uri.fromFile(File(requireActivity().cacheDir, currentDate)))
        uCrop.withAspectRatio(0f, 0f)
        uCrop.withOptions(options)
        launchCroppedImageIntent.launch(uCrop.getIntent(requireActivity()))
    }

    private val launchCroppedImageIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri: Uri? = UCrop.getOutput(result.data!!)

            val bitmap = ImageUtil.getResizeProfileImage(requireActivity(), ImageUtil.ScalingLogic.CROP, resultUri!!.path!!, resultUri)
            if (bitmap != null) {
                viewModel.selectedFile = ImageUtil.getBitmapToFile(requireActivity(), bitmap, Media.PREFIX_PROFILE)

                binding?.apply {
                    cardViewDoc.visibility = View.VISIBLE
                    tvPdfName.text = viewModel.selectedFile?.name.toString()
                    tvFileSize.text = "${viewModel.selectedFile?.getFileSize()} MB"
                    Glide.with(requireContext()).load(R.drawable.ic_img_display).placeholder(
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_img_display)).into(ivUrlLogo)
                }

                parentFragmentViewModel.addInvoice.file = viewModel.selectedFile

            }
        }
    }


    private val launchFilesIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            BaseApplication.scannerFile = ""
            val resultUri = result.data!!.data

            if (resultUri != null) {
                val fileSize = resultUri.getFileSize(requireActivity()).toString()

                if (fileSize.toFloat().toInt() < 10) {
                viewModel.selectedFile = resultUri.getFileFromContentUri(requireActivity())
                binding?.apply {
                    cardViewDoc.visibility = View.VISIBLE
                    tvPdfName.text = viewModel.selectedFile?.name.toString()
                    val fileName=viewModel.selectedFile.toString()
                    tvFileSize.text = "${fileSize.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()} MB"
                    //viewModel.selectedProfileHistory = ProfileHistoryResponse()

                    if (fileName.contains(".pdf",true)){
                        Glide.with(requireContext()).load(R.drawable.ic_pdf_display).placeholder(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_pdf_display)).into(ivUrlLogo)
                    }else{
                        Glide.with(requireContext()).load(R.drawable.ic_img_display).placeholder(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_img_display)).into(ivUrlLogo)
                    }
                    Log.e("", "pdf: ${viewModel.selectedFile}", )
                }
                parentFragmentViewModel.addInvoice.file = viewModel.selectedFile

                } else {
                    showErrorMessage(getString(R.string.file_size_should_not_exceed_10mb))
                }
            }

        }
    }

    fun  hideLayout(){
        binding!!.clMainStepOne.isVisible=false
    }


}