package de.fast2work.mobility.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ScrollView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yalantis.ucrop.UCrop
import de.fast2work.mobility.BuildConfig
import de.fast2work.mobility.R
import de.fast2work.mobility.data.request.UpdateProfileReq
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.data.response.User
import de.fast2work.mobility.databinding.DialogAttachmentBinding
import de.fast2work.mobility.databinding.FragmentProfileBinding
import de.fast2work.mobility.ui.co2.SearchListFragment
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.profile.adapter.AssignedBudgetGroupAdapter
import de.fast2work.mobility.ui.profile.bottom.CityBottomSheetFragment
import de.fast2work.mobility.ui.profile.bottom.CountryCodeBottomSheetFragment
import de.fast2work.mobility.ui.profile.bottom.PreferredLanguageBottomSheetFragment
import de.fast2work.mobility.utility.customview.countrypicker.CountryPicker
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.dialog.DialogUtil
import de.fast2work.mobility.utility.extension.SCREEN_WIDTH
import de.fast2work.mobility.utility.extension.getAndroidDeviceId
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.getMultipartBody
import de.fast2work.mobility.utility.extension.getString
import de.fast2work.mobility.utility.extension.getTrimText
import de.fast2work.mobility.utility.extension.imageTint
import de.fast2work.mobility.utility.extension.isValidFirst
import de.fast2work.mobility.utility.extension.loadCircleCropImage
import de.fast2work.mobility.utility.extension.parcelable
import de.fast2work.mobility.utility.extension.performLogout
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.extension.toFieldRequestBodyMap
import de.fast2work.mobility.utility.preference.EasyPref
import de.fast2work.mobility.utility.util.ImageUtil
import de.fast2work.mobility.utility.util.LocalConfig
import de.fast2work.mobility.utility.util.Media
import de.fast2work.mobility.utility.util.currentDate
import okhttp3.MultipartBody
import java.io.File


/**
 *  Fragment used for ProfileFragment
 */
class ProfileFragment : BaseVMBindingFragment<FragmentProfileBinding, ProfileViewModel>(ProfileViewModel::class.java) {

    private var userData: User? = User()
    private var assignedBudgetGroupAdapter: AssignedBudgetGroupAdapter? = null
    private var sourceDestination=false
    private var isSelected = false
    private var isClicked = true
    private var popupWindow: PopupWindow?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return generateBinding(FragmentProfileBinding.inflate(inflater), container)
    }

    override fun onResume() {
        super.onResume()
        setFragmentResultListener(SearchListFragment.PARAM_RESULT) { _: String, bundle: Bundle ->
            sourceDestination=bundle.getBoolean(SearchListFragment.PARAM_SET_SOURCE)
            viewModel.sourceDestination = bundle.parcelable(SearchListFragment.PARAM_SEND_MODEL)
            if (sourceDestination){
                binding!!.telSearchSource.setText(viewModel.sourceDestination?.compressed_address)
                viewModel.fromAddressLocal=viewModel.sourceDestination?.compressed_address.toBlankString()
                viewModel.fromLatitudeLocal=viewModel.sourceDestination?.lat.toBlankString()
                viewModel.fromLongitudeLocal=viewModel.sourceDestination?.long.toBlankString()
                searchIconSetSource()
            }else{
                binding!!.telSearchDestination.setText(viewModel.sourceDestination?.compressed_address)
                viewModel.toAddressLocal=viewModel.sourceDestination?.compressed_address.toBlankString()
                viewModel.toLatitudeLocal=viewModel.sourceDestination?.lat.toBlankString()
                viewModel.toLongitudeLocal=viewModel.sourceDestination?.long.toBlankString()
                searchIconSetDestination()
            }
            if (binding!!.telSearchSource.text?.isNotEmpty() == true && binding!!.telSearchDestination.text?.isNotEmpty() == true){
                viewModel.callDistanceApi(binding!!.telSearchSource.getTrimText(),binding!!.telSearchDestination.getTrimText())
            }
        }
    }

    override fun attachObservers() {
        /*BaseApplication.notificationCount.observe(this){
            if (it > 0) {
                binding!!.toolbar.tvNotificationCount.visibility = View.VISIBLE
                binding!!.toolbar.ivNotification.visibility = View.VISIBLE
                binding!!.toolbar.tvNotificationCount.text = it.toBlankString()
            } else {
                binding!!.toolbar.tvNotificationCount.visibility = View.GONE
                binding!!.toolbar.ivNotification.visibility = View.VISIBLE
            }
        }*/
        viewModel.sourceDestinationLiveData.observe(this){
            viewModel.fromAddressLocal= it.data?.geocode_ng?.results?.get(0)?.addressline.toBlankString()
            viewModel.fromLatitudeLocal= it.data?.geocode_ng?.results?.get(0)?.lat.toBlankString()
            viewModel.fromLongitudeLocal= it.data?.geocode_ng?.results?.get(0)?.long.toBlankString()
            viewModel.callDistanceApi(binding!!.telSearchSource.getTrimText(),binding!!.telSearchDestination.getTrimText())
        }

        viewModel.getUserProfileLiveData.observe(this) {
             userData = it.data
            setUserData(userData)
        }
        viewModel.updateUserProfileLiveData.observe(this) {
            viewModel.selectedProfileImage = null
            //Toast.makeText(requireContext(), "${it.responseMessage}", Toast.LENGTH_SHORT).show()
            showSuccessMessage(it.responseMessage)
            popFragment()
        }
        viewModel.errorLiveData.observe(this) {
            showErrorMessage(it)
        }
        viewModel.distanceApiErrorLiveData.observe(this) {
            binding?.clSameAddress?.isEnabled = true
            binding?.telDistance?.setText("0.0")
            showErrorMessage(it)
        }
        viewModel.distanceLiveData.observe(this){
            binding?.clSameAddress?.isEnabled = true
            Log.e("", "attachObservers:${it.data} ")
           binding!!.telDistance.setText(it.data?.distance.toBlankString())
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (userData?.isProfileComplete=="0"){
                if (userData?.firstName?.isNotEmpty() == true){
                    showSuccessMessage(getString(R.string.welcome_first_name_please_take_a_moment_to_review_and_complete_your_profile, userData?.firstName))
                }else{
                    showSuccessMessage(getString(R.string.welcome_user_please_take_a_moment_to_review_and_complete_your_profile))
                }
            }
        }, 2000)
        viewModel.logoutLiveData.observe(this) {
           requireContext().performLogout()
        }
    }
    /**
     * This method contains setUserData
     *
     */
    private fun setUserData(userData: User?) {
        binding?.apply {
            tvUserName.text = getString(R.string.user_first_last_name, userData?.firstName.toBlankString() + " " + userData?.lastName)
            telFirstName.setText(userData?.firstName.toBlankString())
            telLastName.setText(userData?.lastName.toBlankString())
            telEmail.setText(userData?.email.toBlankString())
            telPhoneNo.setText(userData?.mobileNo.toBlankString())
            telAddress.setText(userData?.addressLine1.toBlankString())
            telCity.setText(userData?.cityName.toBlankString())
            telZipCode.setText(userData?.zipCode.toBlankString())
            telCountry.setText(userData?.countryName?: getString(R.string.country_germany))
            telPersonalNo.setText(userData?.systemCode.toBlankString())
            viewModel.countryCode = userData?.countryCode.toBlankString()
            viewModel.cityId = userData?.cityId?:0
            ivUserProfile.loadCircleCropImage(requireActivity(), userData?.profilePic.toBlankString(), catchImage = true)
            CountryPicker.getCountryListFromJson(requireActivity()).forEachIndexed { index, countryList ->
                if (userData?.countryCode?.equals(countryList.iso?.replace(Regex("[\\-+]"), ""), true) == true) {
                    binding!!.ivCountryCodeImageSet.setImageBitmap(CountryPicker.loadImageFromAssets(requireContext(), countryList.countryCode.toBlankString()))
                }
            }
            viewModel.assignedBudgetList.clear()
            if (userData?.assignedBudgetGroups?.isNotEmpty() == true) {
                tvAssigned.isVisible = true
                viewModel.assignedBudgetList.addAll(userData.assignedBudgetGroups)
            }else{
                tvAssigned.isVisible = false
            }
            assignedBudgetGroupAdapter?.notifyDataSetChanged()
            tvSetAdd.text = getString(R.string.diplay_role, userData?.displayRole)
            if (userData?.preferredLanguage == getString(R.string.en)) {
                telPreferredLanguages.setText(getString(R.string.english))
                viewModel.languageCode=getString(R.string.en)
            } else {
                viewModel.languageCode=getString(R.string.de)
                telPreferredLanguages.setText(getString(R.string.german))
            }
            if (userData?.currencyFormat == getString(R.string.en)) {
                telAmountFormat.setText(getString(R.string.english_100_000_00))
                viewModel.amountCode = getString(R.string.en)
            } else {
                telAmountFormat.setText(getString(R.string.german_100_00_00))
                viewModel.amountCode = getString(R.string.de)
            }
            telSearchSource.setText(userData?.fromAddress)
            telSearchDestination.setText(userData?.toAddress)
            viewModel.fromLatitudeLocal=userData?.fromLatitude.toBlankString()
            viewModel.fromLongitudeLocal=userData?.fromLongitude.toBlankString()
            viewModel.toLatitudeLocal=userData?.toLatitude.toBlankString()
            viewModel.toLongitudeLocal=userData?.toLongitude.toBlankString()
            if (userData?.distance==null){
                telDistance.setText("0.0")
            }else{
                telDistance.setText(userData.distance)
            }

            searchIconSetSource()
            searchIconSetDestination()
        }
    }
    /**
     * This method contains code to handle initial
     *
     */
    override fun initComponents() {
        userData = BaseApplication.sharedPreference.getPrefModel(EasyPref.USER_DATA, User::class.java)
        if (isFirstTimeLoad){
            viewModel.callGetUserProfileApi(true, userData?.userId.toBlankString())
        }
        if (userData?.isProfileComplete=="0"){
            binding!!.btnUpdateProfile.text= getString(R.string.confirm)
        }else{
            binding!!.btnUpdateProfile.text=getString(R.string.update_profile)
        }
        setToolbar()
        setTheme()
        initRecyclerView()
        searchIconSetSource()
        searchIconSetDestination()
        if (BaseApplication.tenantSharedPreference.getTenantPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)?.tenantInfo?.enabledServices.equals(
                LocalConfig.co2_management,true)){
            binding!!.tvAssigned.isVisible=false
            binding!!.rvAssigned.isVisible=false
            binding!!.tlAmountFormat.isVisible=false

        }else{
            binding!!.tvAssigned.isVisible=true
            binding!!.rvAssigned.isVisible=true
            binding!!.tlAmountFormat.isVisible=true
        }
        binding!!.clSameAddress.isEnabled = true

    }
    /**
     * This method contains setTheme
     *
     */
    private fun setTheme() {
        binding?.apply {
            setThemeForTextInputLayout(tlFirstName)
            setThemeForTextInputLayout(tlLastName)
            setThemeForTextInputLayout(tlEmail1)
            setThemeForTextInputLayout(tlPhoneCode)
            setThemeForTextInputLayout(tlPhoneNo)
            setThemeForTextInputLayout(tlStreetAddress)
            setThemeForTextInputLayout(tlCity)
            setThemeForTextInputLayout(tlSource1)
            setThemeForTextInputLayout(tlCountry)
            setThemeForTextInputLayout(tlPersonalNumber)
            setThemeForTextInputLayout(tlZipCode)
            setThemeForTextInputLayout(tlPreferredLanguage)
            setThemeForTextInputLayout(tlAmountFormat)
            setThemeForView(btnUpdateProfile)
        }
    }

    /**
     * This method contains code for all the clickListener in our app
     *
     */
    override fun setClickListener() {
        binding?.apply {
            btnUpdateProfile.clickWithDebounce {
                if (isValid()) {
                    callUpdateApi()
                }
            }
            ivCamera.clickWithDebounce {
                checkPermission()
            }
            tlPhoneCode.clickWithDebounce {
                openCountrySheetBottomSheet()
            }
            telPhoneCode.clickWithDebounce {
                openCountrySheetBottomSheet()
            }
            telCity.clickWithDebounce {
                openCityBottomSheet(isCity = true)
            }
            telCountry.clickWithDebounce {
                openCityBottomSheet(isCity = false)
            }
            ivCountryCodeImageSet.clickWithDebounce {
                openCountrySheetBottomSheet()
            }
            telPreferredLanguages.clickWithDebounce {
                openLanguageAmountBottomSheet(true,
                    getString(R.string.select_communication_language))
            }
            telAmountFormat.clickWithDebounce {
                openLanguageAmountBottomSheet(false, getString(R.string.select_amount_format))
            }
            ivClearSource.clickWithDebounce {
                telSearchSource.setText("")
                searchIconSetSource()
            }
            ivClearDestination.clickWithDebounce {
                telSearchDestination.setText("")
                searchIconSetDestination()
            }
            telSearchSource.setOnClickListener {
                pushFragment(SearchListFragment.newInstance(true))
            }
            telSearchDestination.setOnClickListener {
                pushFragment(SearchListFragment.newInstance(false))
            }
            telAddress.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (isSelected) {
                        isSelected = false
                        telSearchSource.setText("")
                        ivCheckbox.setImageResource(R.drawable.ic_checkbox_unselected)
                        requireActivity().getColorFromAttr(R.attr.colorTextView).let { it1 -> ivCheckbox.setColorFilter(it1) }
                    }
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
            telZipCode.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (isSelected) {
                        isSelected = false
                        telSearchSource.setText("")
                        ivCheckbox.setImageResource(R.drawable.ic_checkbox_unselected)
                        requireActivity().getColorFromAttr(R.attr.colorTextView).let { it1 -> ivCheckbox.setColorFilter(it1) }
                    }
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
            clSameAddress.setOnClickListener {
                clSameAddress.isEnabled = false
                isSelected = !isSelected

                if (isSelected) {
                    if (isValidForSameAsMainAddress()) {
                        val address = "${telAddress.text.toBlankString()}, ${telZipCode.text.toBlankString()}, ${telCity.text.toBlankString()}"
                        telSearchSource.setText(address)
                        ivCheckbox.setImageResource(R.drawable.ic_checkbox_selected)
                        ivCheckbox.imageTint(BaseApplication.tenantSharedPreference.getTenantPrefModel(EasyPref.TENANT_DATA,
                            TenantInfoModel::class.java)?.brandingInfo?.primaryColor)
                        viewModel.callSourceDestinationApi(address)
                    }
                } else {
//                    if (userData?.fromAddress?.isNotEmpty() == true){
//                        telSearchSource.setText(userData?.fromAddress)
//                    }else {
                        telSearchSource.setText("")
//                    }
                    clSameAddress.isEnabled = true
                    ivCheckbox.setImageResource(R.drawable.ic_checkbox_unselected)
                    requireActivity().getColorFromAttr(R.attr.colorTextView).let { it1 -> ivCheckbox.setColorFilter(it1) }
                }
            }
            tvCommuteDetails.setOnClickListener {
                if (isClicked){
                    isClicked = false
                    toShowInfoPopup(tvCommuteDetails)
                }else{
                    isClicked = true
                    popupWindow?.dismiss()
                }
            }
        }

    }

    private fun scrollToView(scrollViewParent: ScrollView, view: View) {
        // Get deepChild Offset
        val childOffset = Point()
        getDeepChildOffset(scrollViewParent, view.parent, view, childOffset)
        // Scroll to child.
        scrollViewParent.smoothScrollTo(0, childOffset.y)
    }

    /**
     * Used to get deep child offset.
     *
     * 1. We need to scroll to child in scrollview, but the child may not the direct child to scrollview.
     * 2. So to get correct child position to scroll, we need to iterate through all of its parent views till the main parent.
     *
     */
    private fun getDeepChildOffset(mainParent: ViewGroup, parent: ViewParent, child: View, accumulatedOffset: Point) {
        val parentGroup = parent as ViewGroup
        accumulatedOffset.x += child.left
        accumulatedOffset.y += child.top
        if (parentGroup == mainParent) {
            return
        }
        getDeepChildOffset(mainParent, parentGroup.parent, parentGroup, accumulatedOffset)
    }

    private fun toShowInfoPopup(view: View){
        val layoutInflater : LayoutInflater = requireActivity().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = layoutInflater.inflate(R.layout.layout_info, null)
        popupWindow = PopupWindow(popupView, (SCREEN_WIDTH * 0.55).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow?.showAsDropDown(view, 50, 50, Gravity.NO_GRAVITY)
    }

    /**
     * This method contains openCountrySheetBottomSheet
     *
     */
    private fun openCountrySheetBottomSheet() {
        val dialog = CountryCodeBottomSheetFragment.newInstance()
        dialog.sendClickListener = {
            binding!!.ivCountryCodeImageSet.setImageBitmap(CountryPicker.loadImageFromAssets(requireContext(), it.countryCode.toBlankString()))
            viewModel.countryCode = it.iso?.replace(Regex("[\\-+]"), "").toBlankString()
            Log.e("===========", " viewModel.countryCode:: ${it.iso}")
            //binding!!.telPhoneNo.setText(it.iso+userData?.mobileNo)
        }
        dialog.show(childFragmentManager, "")
    }


    private fun openCityBottomSheet(isCity: Boolean = false) {
        val dialog = CityBottomSheetFragment.newInstance(viewModel, viewModel.cityId, isCity)
        dialog.sendClickListener = {
            binding!!.telCity.setText(it.cityName)
            viewModel.cityId = it.cityId!!
        }
        dialog.countryClickListener = {
            binding!!.telCountry.setText(it)
        }
        dialog.show(childFragmentManager, "")
    }

    /**
     * This method contains openLanguageAmountBottomSheet
     *
     */
    private fun openLanguageAmountBottomSheet(languageClick: Boolean, tittle: String) {
        val dialog = PreferredLanguageBottomSheetFragment.newInstance(languageClick, tittle,viewModel.languageCode,viewModel.amountCode)
        dialog.sendClickListener = {
            if (languageClick) {
                binding!!.telPreferredLanguages.setText(it.language)
                if (it.language == getString(R.string.english)) {
                    viewModel.languageCode = getString(R.string.en)
                } else {
                    viewModel.languageCode = getString(R.string.de)
                }
            } else {
                if (it.language == getString(R.string.english_100_000_00)) {
                    viewModel.amountCode = getString(R.string.en)
                } else {
                    viewModel.amountCode = getString(R.string.de)
                }
                binding!!.telAmountFormat.setText(it.language)
            }
        }
        dialog.show(childFragmentManager, "")
    }

    private fun callUpdateApi() {
        binding?.apply {
            val param = UpdateProfileReq().apply {
                this.firstName = telFirstName.getTrimText()
                this.lastName = telLastName.getTrimText()
                this.mobileNo = telPhoneNo.getTrimText()
                this.addressLine1 = telAddress.getTrimText()
                this.countryCode = viewModel.countryCode
                this.preferredLanguage = viewModel.languageCode
                this.currencyFormat = viewModel.amountCode
                this.fromAddress=telSearchSource.getTrimText()
                this.fromLatitude=viewModel.fromLatitudeLocal.toBlankString()
                this.fromLongitude=viewModel.fromLongitudeLocal.toBlankString()
                this.toAddress= telSearchDestination.getTrimText()
                this.toLatitude=viewModel.toLatitudeLocal.toBlankString()
                this.toLongitude=viewModel.toLongitudeLocal.toBlankString()
                this.distance=telDistance.getTrimText()
                this.cityId = viewModel.cityId
                this.countryId = 1
                this.zipCode = telZipCode.getTrimText().toInt()
                this.systemCode = telPersonalNo.getTrimText()
            }
            var multiPartImage: MultipartBody.Part? = null
            if (viewModel.selectedProfileImage != null && viewModel.selectedProfileImage?.exists() == true) {
                multiPartImage = getMultipartBody("profilePic", viewModel.selectedProfileImage)
            }
            viewModel.callUpdateProfileApi(param.toFieldRequestBodyMap(), multiPartImage)
        }

    }

    private fun setToolbar() {
        binding!!.toolbar.let {
            overrideToolbar(it, ToolbarConfig().apply {

                centerTitle=getString(R.string.my_profile)
                if (userData?.isProfileComplete=="0"){
                    showViewLine = true
                    showNotificationIcon = true
                    it.ivNotification.setImageResource(R.drawable.ic_logout)
                }else {
                    showBackButton = true
                    showWhiteBg = true
                    showNotificationIcon = false
                    showViewLine = true
                }
            })

            it.ivNotification.clickWithDebounce {
                DialogUtil.showDialog(childFragmentManager, getString(R.string.logging_out), getString(R.string.alert_logout),
                    getString(R.string.logout), getString(R.string.cancel), object : DialogUtil.IL {
                        override fun onSuccess() {
                            viewModel.callLogoutApi(true, requireActivity().getAndroidDeviceId())
                        }

                        override fun onCancel(isNeutral: Boolean) {
                        }
                    }, isCancelShow = false)
            }
            it.ivBack.clickWithDebounce{
                popFragment()
            }

        }
    }

    private fun checkPermission() {
        requestMultiplePermissions.launch(viewModel.permissions)
    }

    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach {
            if (it.key == Manifest.permission.CAMERA) {
                if (it.value) {
                    selectImgBottomSheet()
                } else {
                    //showToast(getString(R.string.camera_permission_not_allowed))
                }
            }
        }
    }


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

        binding.llCamera.setOnClickListener {
            viewModel.isCameraSelected = true
            getImageFromCamera()
            dialog.dismiss()
        }
        binding.llGallery.setOnClickListener {
            viewModel.isCameraSelected = false
            pickMultipleVisualMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            //openGallery()
            dialog.dismiss()
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private val pickMultipleVisualMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uris ->
        if(uris!=null) {
            BaseApplication.scannerFile = ""
            val resultUri = if (viewModel.isCameraSelected) {
                Uri.fromFile(viewModel.imageFile)
            } else {
                uris
            }
            cropImage(resultUri!!)
        }
    }
    /**
     * Contains image from openGallery
     */
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        launchSelectImageIntent.launch(intent)
    }


    /**
     * Contains image from camera
     */
    private fun getImageFromCamera() {
        try { // create Intent to take a picture and return control to the calling application
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // Create a File reference to access to future access
            viewModel.imageFile = ImageUtil.getPhotoFileUri(requireActivity(), System.currentTimeMillis().toBlankString() + ".${Media.EXTENSION_JPEG}")
            // wrap File object into a content provider
            val fileProvider = FileProvider.getUriForFile(requireActivity(), BuildConfig.APPLICATION_ID + ".fileprovider", viewModel.imageFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            launchSelectImageIntent.launch(intent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private val launchSelectImageIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri = if (viewModel.isCameraSelected) {
                Uri.fromFile(viewModel.imageFile)
            } else {
                result.data!!.data
            }
            cropImage(resultUri!!)
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
        options.setCircleDimmedLayer(true)

        val uCrop = UCrop.of(resultUri, Uri.fromFile(File(requireActivity().cacheDir, currentDate)))
        uCrop.withAspectRatio(1f, 1f)
        uCrop.withOptions(options)
        launchCroppedImageIntent.launch(uCrop.getIntent(requireActivity()))
    }

    private val launchCroppedImageIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)

            val bitmap = ImageUtil.getResizeProfileImage(requireActivity(), ImageUtil.ScalingLogic.CROP, resultUri!!.path!!, resultUri)
            if (bitmap != null) {
                viewModel.selectedProfileImage = ImageUtil.getBitmapToFile(requireActivity(), bitmap, Media.PREFIX_PROFILE)
                binding?.apply {
                    //viewModel.selectedProfileHistory = ProfileHistoryResponse()
                    Glide.with(requireContext()).load(bitmap).apply(RequestOptions.circleCropTransform()).placeholder(ContextCompat.getDrawable(requireContext(), R.drawable.placeholder)).into(ivUserProfile)
                }

            }
        }
    }

    private fun isValidForSameAsMainAddress(): Boolean {
        var valid = true
        binding?.apply {
            tvAddressError.isVisible = false
            tvCityError.isVisible = false
            tvZipCodeError.isVisible = false
            if (telAddress.getTrimText().isEmpty()) {
                tvAddressError.isVisible = true
                tvAddressError.text = getString(R.string.address_is_required)
                valid = false
            }
            if (telCity.getTrimText().isEmpty()) {
                tvCityError.isVisible = true
                tvCityError.text = getString(R.string.city_cannot_be_empty)
                valid = false
            }
            if (telZipCode.getTrimText().isEmpty()) {
                tvZipCodeError.isVisible = true
                tvZipCodeError.text = getString(R.string.zipcode_cannot_be_empty)
                valid = false
            }
        }
        return valid
    }

    private fun isValid(): Boolean {
        var valid = true
        binding?.apply {
            tvFirstError.isVisible = false
            tvLastError.isVisible = false
            tvPersonalError.isVisible=false
            tvCityError.isVisible=false
            tvZipCodeError.isVisible=false
            tvCountryError.isVisible=false
            tvEmailError.isVisible = false
            tvPhoneError.isVisible = false
            tvAddressError.isVisible = false
            tvSourceError.isVisible=false
            tvDestinationError.isVisible=false
            /*if (!telFirstName.getTrimText().isValidFirst()) {
                tvFirstError.isVisible = true
                tvFirstError.text = getString(R.string.first_names_cannot_contain_special_characters_or_numbers)
                valid = false
            }*/
            if (telFirstName.getTrimText().isEmpty()) {
                tvFirstError.isVisible = true
                tvFirstError.text = getString(R.string.first_name_is_required)
                valid = false
            }
           /* if (!telLastName.getTrimText().isValidFirst()) {
                tvLastError.isVisible = true
                tvLastError.text = getString(R.string.the_last_name_cannot_contain_special_characters_or_numbers)
                valid = false
            }*/
            if (telLastName.getTrimText().isEmpty()) {
                tvLastError.isVisible = true
                tvLastError.text = getString(R.string.the_last_name_is_required)
                valid = false
            }
            if (telPersonalNo.getTrimText().isEmpty()) {
                tvPersonalError.isVisible = true
                tvPersonalError.text = getString(R.string.personnel_number_cannot_be_empty)
                valid = false
            }
            if (telPersonalNo.getTrimText().isNotEmpty() && !isEmployeeCodeValid(telPersonalNo.getTrimText())){
                tvPersonalError.isVisible = true
                tvPersonalError.text = getString(R.string.invalid_employee_code)
                valid = false
            }
            if (telPhoneNo.getTrimText().isEmpty()) {
                tvPhoneError.isVisible = true
                tvPhoneError.text = getString(R.string.a_phone_number_is_required)
                valid = false
            }
            if (telAddress.getTrimText().isEmpty()) {
                tvAddressError.isVisible = true
                tvAddressError.text = getString(R.string.address_is_required)
                valid = false
            }
            if (telCity.getTrimText().isEmpty()) {
                tvCityError.isVisible = true
                tvCityError.text = getString(R.string.city_cannot_be_empty)
                valid = false
            }
            if (telZipCode.getTrimText().isEmpty()) {
                tvZipCodeError.isVisible = true
                tvZipCodeError.text = getString(R.string.zipcode_cannot_be_empty)
                valid = false
            }
            if (telSearchSource.getTrimText().isEmpty()){
                tvSourceError.isVisible=true
                tvSourceError.text = getString(R.string.residence_address_cannot_be_empty)
                valid=false
            }
            if (telSearchDestination.getTrimText().isEmpty()){
                tvDestinationError.isVisible=true
                tvDestinationError.text = getString(R.string.workplace_address_cannot_be_empty)
                valid=false
            }
            setScrollFocus()
        }
        return valid
    }

    private fun isEmployeeCodeValid(employeeCode: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9_.-]+$")
        return employeeCode.matches(regex)
    }

    private fun initRecyclerView() {
        assignedBudgetGroupAdapter = AssignedBudgetGroupAdapter(requireContext(), viewModel.assignedBudgetList)
        binding!!.rvAssigned.layoutManager = GridLayoutManager(requireContext(), 4, RecyclerView.VERTICAL, false)
        binding!!.rvAssigned.adapter = assignedBudgetGroupAdapter

    }


    /**
     * Notification count update
     *
     * @param updateNotificationCount
     */
   /* @Subscribe(threadMode = ThreadMode.MAIN)
    fun notificationCountUpdate(updateNotificationCount: UpdateNotificationCount) {
        if (updateNotificationCount.pushNotificationCount!! > 0) {
            binding!!.toolbar.tvNotificationCount.visibility = View.VISIBLE
            binding!!.toolbar.ivNotification.visibility = View.VISIBLE
            binding!!.toolbar.tvNotificationCount.visibility = updateNotificationCount.pushNotificationCount?:0
        } else {
            binding!!.toolbar.tvNotificationCount.visibility = View.GONE
            binding!!.toolbar.ivNotification.visibility = View.VISIBLE
        }
    }*/

    override fun onStart() {
        super.onStart()
        hideTabs()
        //EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        //EventBus.getDefault().unregister(this)
    }
    private fun searchIconSetSource(){
        if (binding!!.telSearchSource.text?.isEmpty() == true){
            binding!!.telSearchSource.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_search, 0)
            binding!!.ivClearSource.isVisible = false
        } else {
            binding!!.ivClearSource.isVisible = false
            binding!!.telSearchSource.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
        }
    }
    private fun searchIconSetDestination(){
        if (binding!!.telSearchDestination.text?.isEmpty() == true){
            binding!!.telSearchDestination.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_search, 0)
            binding!!.ivClearDestination.isVisible = false
        } else {
            binding!!.ivClearDestination.isVisible = false
            binding!!.telSearchDestination.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
        }
    }
    fun setScrollFocus(){
        binding?.apply {
            if (telFirstName.getTrimText().isEmpty()){
                telFirstName.requestFocus()
                scrollToView(svMain, telFirstName)
            } else if (telLastName.getTrimText().isEmpty()){
                telLastName.requestFocus()
                scrollToView(svMain, telLastName)
            } else if (telPersonalNo.getTrimText().isEmpty()){
                telPersonalNo.requestFocus()
                scrollToView(svMain, telPersonalNo)
            } else if (telPhoneNo.getTrimText().isEmpty()){
                telPhoneNo.requestFocus()
                scrollToView(svMain, telPhoneNo)
            } else if (telAddress.getTrimText().isEmpty()){
                telAddress.requestFocus()
                scrollToView(svMain, telAddress)
            }else if(telCity.getTrimText().isEmpty()){
                telCity.requestFocus()
                scrollToView(svMain, telCity)
            }else if (telZipCode.getTrimText().isEmpty()){
                telZipCode.requestFocus()
                scrollToView(svMain, telZipCode)
            } else if (telSearchSource.getTrimText().isEmpty()){
                telSearchSource.requestFocus()
                scrollToView(svMain, telSearchSource)
            } else if (telSearchDestination.getTrimText().isEmpty()){
                telSearchDestination.requestFocus()
                scrollToView(svMain, telSearchDestination)
            }
        }
    }
}