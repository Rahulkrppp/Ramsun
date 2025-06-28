package de.fast2work.mobility.ui.upload.step

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.CompoundButtonCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yalantis.ucrop.UCrop
import de.fast2work.mobility.BuildConfig
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.CardMaster
import de.fast2work.mobility.databinding.DialogAttachmentBinding
import de.fast2work.mobility.databinding.FragmentUploadInvoiceStepThreeBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.upload.UploadInvoiceViewModel
import de.fast2work.mobility.ui.upload.step.adapter.UploadImageAdapter
import de.fast2work.mobility.utility.extension.compressImage
import de.fast2work.mobility.utility.extension.findUrlName
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.getFileFromContentUri
import de.fast2work.mobility.utility.extension.getFileSize
import de.fast2work.mobility.utility.extension.textColor
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.util.ImageUtil
import de.fast2work.mobility.utility.util.LocalConfig
import de.fast2work.mobility.utility.util.Media
import de.fast2work.mobility.utility.util.Media.MIME_TYPE_PDF
import de.fast2work.mobility.utility.util.currentDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


/**
 * A simple [Fragment] subclass.
 * Use the [UploadInvoiceStepThreeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UploadInvoiceStepThreeFragment :
    BaseVMBindingFragment<FragmentUploadInvoiceStepThreeBinding, CommonUploadInvoiceModel>(
        CommonUploadInvoiceModel::class.java
    ) {

    lateinit var parentFragmentViewModel: UploadInvoiceViewModel
    private var uploadImageAdapter: UploadImageAdapter? = null
    var isExpanded = false

    companion object {

        const val SELF = "self"
        const val EMPLOYER = "employer"
        const val CARD = "card"
        const val PRIVATE = "private"
        const val BUSINESS = "business"
        const val INVOICE = "invoice"
        const val RECEIPT = "receipt"

        fun newInstance() = UploadInvoiceStepThreeFragment().apply {
            this.arguments = Bundle().apply {


            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return generateBinding(FragmentUploadInvoiceStepThreeBinding.inflate(inflater), container)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        parentFragmentViewModel = parentFragment?.let {
            ViewModelProvider(it)[UploadInvoiceViewModel::class.java]
        }!!
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    override fun attachObservers() {
        parentFragmentViewModel.cardListLiveData.observe(this) {
            val list: ArrayList<CardMaster> = arrayListOf()
            list.addAll(it?.data?.data as ArrayList<CardMaster>)
            binding?.apply {
                rgCard.removeAllViews()
                if (it.data?.data?.size!! > 0) {
                    rbMyCard.visibility = View.VISIBLE
                    view.visibility = View.VISIBLE
                } else {
                    rbMyCard.visibility = View.GONE
                    view.visibility = View.GONE
                }

                list.forEachIndexed { index, cardData ->
                    val rbn = AppCompatRadioButton(context)
                    rbn.id = cardData.cardId!!
                    rbn.text = cardData.cardName
                    val padding = resources.getDimensionPixelOffset(R.dimen._1sdp)
                    val layoutParams = RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.MATCH_PARENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.setMargins(
                        resources.getDimensionPixelOffset(R.dimen._45sdp), padding, 0,
                        padding
                    ) // left, top, right, bottom
                    rbn.layoutParams = layoutParams
                    addDivider(rgCard, requireContext())
                    rgCard.addView(rbn)
                }

                if (parentFragmentViewModel.addInvoice.cardId?.isNotEmpty() == true) {
                    val selectedRadioButton = parentFragmentViewModel.addInvoice.cardId?.toInt()
                        ?.let { rgCard.findViewById<AppCompatRadioButton>(it) }
                    if (selectedRadioButton != null) {
                        selectedRadioButton.isChecked = true
                        setRadioButtonSelectedColor(selectedRadioButton, requireActivity())
                    }
                }


            }

        }

    }

    override fun initComponents() {
        setDataCheckBox()

        if (isFirstTimeLoad) {
            parentFragmentViewModel.callCardMasterApi()
        }
        Log.e("", "initComponents:${parentFragmentViewModel.addInvoice} ")
        when (parentFragmentViewModel.addInvoice.benefitCode) {
            null -> {
                binding!!.clPower.isVisible = false
            }

            "home_charging_allowance" -> {
                binding!!.clPower.isVisible = true
                binding!!.tvPower.text = getString(R.string.power_consumption_kwh)
                binding!!.tlPower.hint = getString(R.string.enter_kw)
            }

            else -> {
                binding!!.tvPower.text = getString(R.string.no_of_km_s_travelled)
                binding!!.clPower.isVisible = true
                binding!!.tlPower.hint = getString(R.string.enter_km)
            }
        }
        initRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        hideTabs()
    }

    override fun setClickListener() {
        binding?.apply {
            rgEmp.setOnCheckedChangeListener { _, checkedId ->
                setRgPaidValue(checkedId)
            }
            rgExpense.setOnCheckedChangeListener { _, checkedId ->
                setRgExpenseValue(checkedId)
            }

            rgFileType.setOnCheckedChangeListener { _, checkedId ->
                setRgFileTypeValue(checkedId)
            }

            tlMessageHere.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    parentFragmentViewModel.addInvoice.employeeRemarks =
                        binding?.tlMessageHere?.text.toString()
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })
            tlPower.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    parentFragmentViewModel.addInvoice.power = binding?.tlPower?.text.toString()
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })

            rbMyCard.setOnClickListener {
                isExpanded = !isExpanded
                rgCard.isVisible = isExpanded

            }

            rbBusiness.setOnClickListener {
                tvNote.isVisible = true
            }

            rbPrivate.setOnClickListener {
                tvNote.isVisible = false
            }

            rgCard.setOnCheckedChangeListener { group, checkedId ->
                for (i in 0 until group.childCount) {
                    val radioButton = group.getChildAt(i) as? AppCompatRadioButton
                    // Check if the child is a RadioButton
                    if (radioButton != null && radioButton.id != checkedId) {
                        // Set text color to black for unchecked radio buttons
                        setRadioButtonChecked(radioButton, requireContext())
                    }
                }


//                clExpense.background = setStrokeDrawable()

                val selectedRadioButton = group.findViewById<AppCompatRadioButton>(checkedId)
                if (selectedRadioButton != null) {
                    setRadioButtonSelectedColor(selectedRadioButton, requireContext())
                }

                parentFragmentViewModel.addInvoice.cardId = checkedId.toString()

            }
            binding?.clChooseFile?.clickWithDebounce {
                checkPermission()
            }

//            binding?.ivClose?.clickWithDebounce {
//                binding?.cardViewDoc?.visibility = View.GONE
//                parentFragmentViewModel.addInvoice.file = null
//                viewModel.selectedFileThreeStep = null
//            }
        }
    }

    private fun checkPermission() {
        requestMultiplePermissions.launch(viewModel.permissions)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val pickMultipleVisualMedia = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
       binding!!.progressImage.isVisible=true
        if (uris != null) {
            // CoroutineScope(Dispatchers.Main).async {
            Log.e("TAG", ": called 1")
            uris.forEach {
                parentFragmentViewModel.addInvoice.selectedFileImageList.add(
                    compressImage(
                        requireActivity(),
                        it
                    )
                )
            }
            Log.e("TAG", ": called 2")

            Handler(Looper.getMainLooper()).postDelayed({
                binding!!.progressImage.isVisible=false
                Log.e("TAG", ": called 3")
            }, 100)
            uploadImageAdapter?.notifyDataSetChanged()


//            BaseApplication.scannerFile = ""
//            val resultUri = if (viewModel.isCameraSelected) {
//                Uri.fromFile(viewModel.imageFileThreeStep)
//            } else {
//                uris
//            }
//            //startCrop(resultUri!!)
        }else{
            binding!!.progressImage.isVisible=false
        }
        // Process URIs
    }


    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
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

        val binding1 = DialogAttachmentBinding.inflate(LayoutInflater.from(requireActivity()))
        val view = binding1.root
        dialog.apply {
            setOnShowListener {
                val bottomSheet =
                    findViewById<View?>(com.google.android.material.R.id.design_bottom_sheet)
                if (BaseApplication.themeValue == Configuration.UI_MODE_NIGHT_YES) {
                    bottomSheet?.setBackgroundResource(R.color.color_primary_dark_text_80)
                } else {
                    bottomSheet?.setBackgroundResource(android.R.color.transparent)
                }
            }
        }

        if (BaseApplication.themeValue == Configuration.UI_MODE_NIGHT_YES) {
            dialog.window?.setBackgroundDrawableResource(R.color.color_primary_dark_text_80)
        } else {
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }

        binding1.llFiles.visibility = View.VISIBLE

        binding1.llFiles.setOnClickListener {
            openFiles()
            dialog.dismiss()
        }

        binding1.llCamera.setOnClickListener {
            //AppScanActivity().start(requireActivity(),true)
            //dialog.dismiss()
            viewModel.isCameraSelected = true
            getImageFromCamera()
            dialog.dismiss()

        }
        binding1.llGallery.setOnClickListener {

            viewModel.isCameraSelected = false
// Launching the photo picker (photos & video included)

            pickMultipleVisualMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            dialog.dismiss()
            binding!!.progressImage.isVisible=true
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startCrop(resultUri: Uri) {
        // Start cropping activity for pre-acquired image saved on the device and customize settings.
        cropImage.launch(
            CropImageContractOptions(
                uri = resultUri, CropImageOptions(
                    activityBackgroundColor = requireActivity().getColorFromAttr(com.google.android.material.R.attr.backgroundColor),
                    toolbarTintColor = requireActivity().getColorFromAttr(R.attr.colorNavBar),
                    toolbarColor = requireActivity().getColorFromAttr(R.attr.colorNavBar),
                    toolbarBackButtonColor = requireActivity().getColorFromAttr(R.attr.colorTextView),
                    activityMenuIconColor = requireActivity().getColorFromAttr(R.attr.colorTextView),
                    allowFlipping = false,
                )
            )
        )
    }

    /**
     * Contains image from camera
     */

    private fun getImageFromCamera() {
        try { // create Intent to take a picture and return control to the calling application
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // Create a File reference to access to future access
            viewModel.imageFileThreeStep = ImageUtil.getPhotoFileUri(
                requireActivity(),
                System.currentTimeMillis().toBlankString() + ".${Media.EXTENSION_JPEG}"
            )
            // wrap File object into a content provider
            val fileProvider = FileProvider.getUriForFile(
                requireActivity(),
                BuildConfig.APPLICATION_ID + ".fileprovider",
                viewModel.imageFileThreeStep!!
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            launchSelectImageIntent.launch(intent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private val launchSelectImageIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultUri = if (viewModel.isCameraSelected) {
                    Uri.fromFile(viewModel.imageFileThreeStep)
                } else {
                    result.data!!.data
                }

                resultUri?.let {
                    compressImage(
                        requireContext(),
                        it
                    )
                }?.let { parentFragmentViewModel.addInvoice.selectedFileImageList.add(it) }


                uploadImageAdapter?.notifyDataSetChanged()
                //cropImage(resultUri!!)
            }
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
        uCrop.withAspectRatio(1f, 1f)
        uCrop.withOptions(options)
        launchCroppedImageIntent.launch(uCrop.getIntent(requireActivity()))
    }

    private val launchCroppedImageIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultUri = UCrop.getOutput(result.data!!)

                val bitmap = ImageUtil.getResizeProfileImage(
                    requireActivity(),
                    ImageUtil.ScalingLogic.CROP,
                    resultUri!!.path!!,
                    resultUri
                )
                if (bitmap != null) {
                    viewModel.selectedFileThreeStep =
                        ImageUtil.getBitmapToFile(requireActivity(), bitmap, Media.PREFIX_PROFILE)
                    viewModel.selectedFileThreeStep?.let {
                        parentFragmentViewModel.addInvoice.selectedFileImageList.add(
                            it
                        )
                    }
                    uploadImageAdapter?.notifyDataSetChanged()
//                binding?.apply {
//                    //viewModel.selectedProfileHistory = ProfileHistoryResponse()
//                    Glide.with(requireContext()).load(bitmap).apply(RequestOptions.circleCropTransform()).placeholder(ContextCompat.getDrawable(requireContext(), R.drawable.placeholder)).into(ivUserProfile)
//                }

                }
            }
        }

    private fun openFiles() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        val mimeTypes = arrayOf(/*MIME_TYPE_TEXT,*/ MIME_TYPE_PDF)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        launchFilesIntent.launch(intent)
    }

    private val launchFilesIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                BaseApplication.scannerFile = ""
                val resultUri = result.data!!.data

                if (resultUri != null) {
                    val fileSize = resultUri.getFileSize(requireActivity()).toString()

                    if (fileSize.toFloat().toInt() < 10) {
                        viewModel.selectedFile = resultUri.getFileFromContentUri(requireActivity())
                        binding?.apply {
//                        cardViewDoc.visibility = View.VISIBLE
//                        tvPdfName.text = viewModel.selectedFileThreeStep?.name.toString()
//                        val fileName=viewModel.selectedFileThreeStep.toString()
//                        tvFileSize.text = "${fileSize.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()} MB"
                            //viewModel.selectedProfileHistory = ProfileHistoryResponse()


                            Log.e("", "pdf: ${viewModel.selectedFileThreeStep}")
                        }
                        //parentFragmentViewModel.addInvoice.file = viewModel.selectedFileThreeStep
                        viewModel.selectedFile?.let {
                            parentFragmentViewModel.addInvoice.selectedFileImageList.add(
                                it
                            )
                        }
                        uploadImageAdapter?.notifyDataSetChanged()

                    } else {
                        showErrorMessage(getString(R.string.file_size_should_not_exceed_10mb))
                    }
                }

            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // Use the returned uri.
            val uriContent = result.uriContent
            val uriFilePath = result.getUriFilePath(requireContext()) // optional usage
            val fileSize = if (viewModel.isCameraSelected) {
                viewModel.imageFileThreeStep?.getFileSize()
            } else {
                uriContent?.getFileSize(requireActivity())
            }
            if (fileSize?.toFloat()?.toInt()!! < 10) {
                viewModel.selectedFileThreeStep = if (viewModel.isCameraSelected) {
                    viewModel.imageFileThreeStep
                } else {
                    uriContent?.getFileFromContentUri(requireActivity())
                }
                binding?.apply {
//                    cardViewDoc.visibility = View.VISIBLE
//                    tvPdfName.text = viewModel.selectedFileThreeStep?.name.toString()
//                    tvFileSize.text = "${fileSize?.toBigDecimal()?.setScale(2, RoundingMode.UP)?.toDouble()} MB"
                    //viewModel.selectedProfileHistory = ProfileHistoryResponse()
//                    Glide.with(requireContext()).load(R.drawable.ic_img_display).placeholder(
//                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_img_display)).into(ivUrlLogo)
                }
                //parentFragmentViewModel.addInvoice.file = viewModel.selectedFile
                viewModel.selectedFileThreeStep?.let {
                    parentFragmentViewModel.addInvoice.selectedFileImageList.add(
                        it
                    )
                }
                uploadImageAdapter?.notifyDataSetChanged()


//            }
            } else {
                showErrorMessage(getString(R.string.file_size_should_not_exceed_10mb))
            }
        } else {
            // An error occurred.
            val exception = result.error
        }
    }

    private fun setDataCheckBox() {
        binding?.apply {
            if (parentFragmentViewModel.addInvoice.paidBy?.isEmpty() == true) {
                rbMySelf.isChecked = true
                parentFragmentViewModel.addInvoice.paidBy = SELF
                setRadioButtonChecked(rbMySelf)
            } else {
                when (parentFragmentViewModel.addInvoice.paidBy) {
                    SELF -> {
                        rbMySelf.isChecked = true
                        setRadioButtonChecked(rbMySelf)
                    }

                    EMPLOYER -> {
                        rbMyEmployer.isChecked = true
                        setRadioButtonChecked(rbMyEmployer)
                    }

                    else -> {
                        rbMyCard.isChecked = true
                        setRadioButtonChecked(rbMyCard)
                    }
                }
            }
            if (parentFragmentViewModel.addInvoice.expenseType?.isEmpty() == true) {
                parentFragmentViewModel.addInvoice.expenseType = PRIVATE
                rbPrivate.isChecked = true
                setRadioButtonChecked(rbPrivate)
            } else {
                if (parentFragmentViewModel.addInvoice.expenseType == PRIVATE) {
                    rbPrivate.isChecked = true
                    setRadioButtonChecked(rbPrivate)
                } else {
                    rbBusiness.isChecked = true
                    setRadioButtonChecked(rbBusiness)
                }
            }
            if (parentFragmentViewModel.addInvoice.type?.isEmpty() == true) {
                parentFragmentViewModel.addInvoice.type = RECEIPT
                rbReceipt.isChecked = true
                setRadioButtonChecked(rbReceipt)
            } else {
                if (parentFragmentViewModel.addInvoice.type == INVOICE) {
                    rbInvoice.isChecked = true
                    setRadioButtonChecked(rbInvoice)
                } else {
                    rbReceipt.isChecked = true
                    setRadioButtonChecked(rbReceipt)
                }
            }
            if (parentFragmentViewModel.addInvoice.cardId?.isNotEmpty() == true) {
                isExpanded = true
                rgCard.isVisible = isExpanded
            } else {
                isExpanded = false
                rgCard.isVisible = isExpanded
            }
            if (parentFragmentViewModel.addInvoice.employeeRemarks?.isNotEmpty() == true) {
                binding?.tlMessageHere?.setText(parentFragmentViewModel.addInvoice.employeeRemarks.toString())
            }
        }
    }

    private fun setRgPaidValue(checkedId: Int) {
        binding?.apply {
            when (checkedId) {
                R.id.rb_my_self -> {
                    parentFragmentViewModel.addInvoice.paidBy = SELF
                    setRadioButtonChecked(rbMySelf)
                    setRadioButtonChecked(rbMyEmployer)
                    setRadioButtonChecked(rbMyCard)
                    rgCard.clearCheck()
                    isExpanded = false
                    rgCard.isVisible = isExpanded
                    parentFragmentViewModel.addInvoice.cardId = ""
                }

                R.id.rb_my_employer -> {
                    parentFragmentViewModel.addInvoice.paidBy = EMPLOYER
                    setRadioButtonChecked(rbMyEmployer)
                    setRadioButtonChecked(rbMyCard)
                    setRadioButtonChecked(rbMySelf)
                    rgCard.clearCheck()
                    isExpanded = false
                    rgCard.isVisible = isExpanded
                    parentFragmentViewModel.addInvoice.cardId = ""
                }

                R.id.rb_my_card -> {
                    parentFragmentViewModel.addInvoice.paidBy = CARD
                    setRadioButtonChecked(rbMyEmployer)
                    setRadioButtonChecked(rbMyCard)
                    setRadioButtonChecked(rbMySelf)
                }
            }
        }
    }

    private fun setRgExpenseValue(checkedId: Int) {
        binding?.apply {
            when (checkedId) {
                R.id.rb_private -> {
                    parentFragmentViewModel.addInvoice.expenseType = PRIVATE
                    setRadioButtonChecked(rbPrivate)
                    setRadioButtonChecked(rbBusiness)


                }

                R.id.rb_business -> {
                    parentFragmentViewModel.addInvoice.expenseType = BUSINESS
                    setRadioButtonChecked(rbPrivate)
                    setRadioButtonChecked(rbBusiness)

                }

            }
        }
    }

    private fun setRgFileTypeValue(checkedId: Int) {
        binding?.apply {
            when (checkedId) {
                R.id.rb_receipt -> {
                    parentFragmentViewModel.addInvoice.type = RECEIPT
                    setRadioButtonChecked(rbReceipt)
                    setRadioButtonChecked(rbInvoice)
                }

                R.id.rb_invoice -> {
                    parentFragmentViewModel.addInvoice.type = INVOICE
                    setRadioButtonChecked(rbReceipt)
                    setRadioButtonChecked(rbInvoice)


                }
            }
        }
    }

    private fun setRadioButtonChecked(rb: AppCompatRadioButton) {
        rb.setTextColor(requireActivity().getColorFromAttr(com.google.android.material.R.attr.editTextColor))
        CompoundButtonCompat.setButtonTintList(
            rb,
            ColorStateList.valueOf(requireActivity().getColorFromAttr(com.google.android.material.R.attr.editTextColor))
        )
        rb.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular_400)
        if (rb.isChecked) {
            rb.textColor(tenantInfoData?.brandingInfo?.secondaryColor)
            rb.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_medium_500)
            if (BaseApplication.themeValue == Configuration.UI_MODE_NIGHT_YES) {
                CompoundButtonCompat.setButtonTintList(
                    rb,
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.color_secondary
                        )
                    )
                )
            } else {
                CompoundButtonCompat.setButtonTintList(
                    rb,
                    ColorStateList.valueOf(Color.parseColor(tenantInfoData?.brandingInfo?.secondaryColor))
                )
            }
        }
    }

    private fun setRadioButtonChecked(rb: AppCompatRadioButton, context: Context) {
        rb.setTextColor(context.getColorFromAttr(com.google.android.material.R.attr.editTextColor))
        CompoundButtonCompat.setButtonTintList(
            rb,
            ColorStateList.valueOf(context.getColorFromAttr(com.google.android.material.R.attr.editTextColor))
        )
        rb.typeface = ResourcesCompat.getFont(context, R.font.poppins_regular_400)
    }

    private fun setRadioButtonSelectedColor(rb: AppCompatRadioButton, context: Context) {
        rb.textColor(tenantInfoData?.brandingInfo?.secondaryColor)
        rb.typeface = ResourcesCompat.getFont(context, R.font.poppins_medium_500)
        if (BaseApplication.themeValue == Configuration.UI_MODE_NIGHT_YES) {
            CompoundButtonCompat.setButtonTintList(
                rb,
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_secondary
                    )
                )
            )
        } else {
            CompoundButtonCompat.setButtonTintList(
                rb,
                ColorStateList.valueOf(Color.parseColor(tenantInfoData?.brandingInfo?.secondaryColor))
            )
        }
    }

    private fun addDivider(parent: RadioGroup, context: Context) {
        val divider = View(context)
        divider.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 5) // Divider height
        divider.setBackgroundColor(requireContext().getColorFromAttr(R.attr.colorLine)) // Set divider color
        parent.addView(divider)
    }

    private fun initRecyclerView() {
        uploadImageAdapter = UploadImageAdapter(
            requireContext(),
            parentFragmentViewModel.addInvoice.selectedFileImageList
        ) { view, model, position ->
            when (view.id) {
                R.id.iv_close -> {
                    parentFragmentViewModel.addInvoice.selectedFileImageList.removeAt(position)
                    uploadImageAdapter?.notifyDataSetChanged()
                }
            }
        }
        binding!!.rvDoc.layoutManager = LinearLayoutManager(activity)
        binding!!.rvDoc.adapter = uploadImageAdapter

    }

}