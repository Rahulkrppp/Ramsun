package de.fast2work.mobility.ui.upload

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import de.fast2work.mobility.utility.util.FragNavController
import de.fast2work.mobility.utility.util.FragNavLogger
import de.fast2work.mobility.utility.util.FragNavSwitchController
import de.fast2work.mobility.utility.util.FragNavTransactionOptions
import de.fast2work.mobility.utility.util.UniqueTabHistoryStrategy

import de.fast2work.mobility.R
import de.fast2work.mobility.data.eventbus.UpdateNotificationCount
import de.fast2work.mobility.data.request.AddInvoiceReq
import de.fast2work.mobility.databinding.FragmentUploadInvoiceBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseFragment
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.dashboard.INDEX_INVOICE
import de.fast2work.mobility.ui.upload.step.UploadInvoiceStepOneFragment
import de.fast2work.mobility.ui.upload.step.UploadInvoiceStepThreeFragment
import de.fast2work.mobility.ui.upload.step.UploadInvoiceStepTwoFragment
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.extension.backgroundColor
import de.fast2work.mobility.utility.extension.backgroundColorTint
import de.fast2work.mobility.utility.extension.getMultipartBody
import de.fast2work.mobility.utility.extension.getRequestBody
import de.fast2work.mobility.utility.extension.hideKeyboard
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.extension.toFieldRequestBodyMap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Locale


/**
 * A simple [Fragment] subclass.
 * Use the [UploadInvoiceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

const val INDEX_STEP_ONE = FragNavController.TAB1
const val INDEX_STEP_TWO = FragNavController.TAB2
const val INDEX_STEP_THREE = FragNavController.TAB3

class UploadInvoiceFragment : BaseVMBindingFragment<FragmentUploadInvoiceBinding, UploadInvoiceViewModel>(UploadInvoiceViewModel::class.java), FragNavController.TransactionListener, FragNavController.RootFragmentListener {

    var fragNavController: FragNavController? = null
    var isFirstTimeCall = true

    //    var primaryColor : String=""
//    var secondaryColor : String=""
    companion object {
        const val FIRST_STEP = "FIRST_STEP"
        const val SECOND_STEP = "SECOND_STEP"
        const val THIRD_STEP = "THIRD_STEP"
        const val IS_LOAD = "load"
        const val UPLOAD_INVOICE = "uploadInvoice"
        fun newInstance(isLoad: Boolean = true) = UploadInvoiceFragment().apply {
            this.arguments = Bundle().apply {
                this.putBoolean(IS_LOAD, isLoad)


            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return generateBinding(FragmentUploadInvoiceBinding.inflate(inflater), container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//            primaryColor=BaseApplication.tenantSharedPreference.getPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)?.brandingInfo?.primaryColor.toBlankString()
//            secondaryColor=BaseApplication.tenantSharedPreference.getPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)?.brandingInfo?.secondaryColor.toBlankString()
        fragNavController = FragNavController(childFragmentManager, R.id.fl_container)
        isFirstTimeCall = BaseApplication.sharedPreference.getPref(IS_LOAD, false)
        if (isFirstTimeCall) {
            BaseApplication.scannerFile = ""
            viewModel.addInvoice.file = null
            viewModel.addInvoice.categoryId = 0
            viewModel.isFirstTimeLoad = true
            viewModel.addInvoice.subCategoryId = 0
            viewModel.addInvoice.cardId = ""
            viewModel.addInvoice.type = ""
            viewModel.addInvoice.expenseType = ""
            viewModel.addInvoice.employeeRemarks = ""
            initFragNav(savedInstanceState)
            setStepper(FIRST_STEP)
        }
    }

    override fun attachObservers() {
        /* BaseApplication.notificationCount.observe(this){
            if (it > 0) {
                binding!!.toolbar.tvNotificationCount.visibility = View.VISIBLE
                binding!!.toolbar.ivNotification.visibility = View.VISIBLE
                binding!!.toolbar.tvNotificationCount.text = it.toBlankString()
            } else {
                binding!!.toolbar.tvNotificationCount.visibility = View.GONE
                binding!!.toolbar.ivNotification.visibility = View.VISIBLE
            }
        }*/
        viewModel.addInvoiceListLiveData.observe(this) {
            if (it.isSuccess) {
                popFragment()
                //fragNavController?.popFragment()
                switchTab(INDEX_INVOICE)
                viewModel.addInvoice.file = null
                viewModel.addInvoice.categoryId = 0
                viewModel.addInvoice.subCategoryId = 0
                viewModel.addInvoice.selectedFileImageList.clear()
                BaseApplication.sharedPreference.setPref(UPLOAD_INVOICE, true)
            }
        }
        viewModel.errorLiveData.observe(this) {
            showErrorMessage(it.toBlankString())
        }
    }

    override fun initComponents() {
        setToolbar()
        setThemeForView(binding!!.btnNext)
        setThemeForButtonTextView(binding!!.btnPrevious)
        setBackgroundColorTintForAlpha(binding!!.btnPrevious)
    }

    override fun setClickListener() {
        setNextListener()
        setPreviousListener()
    }

    private fun setPreviousListener() {
        binding!!.btnPrevious.clickWithDebounce {
            /*when (fragNavController?.currentFrag) {
                is UploadInvoiceStepThreeFragment -> {
                    selectBottomTab(INDEX_STEP_TWO,UploadInvoiceStepTwoFragment())
                }
                is UploadInvoiceStepTwoFragment -> {
                    selectBottomTab(INDEX_STEP_ONE,UploadInvoiceStepOneFragment())
                }
            }*/
            redirectToPreviousScreen()
        }
    }

    private fun setNextListener() {
        binding?.apply {
            btnNext.clickWithDebounce {
                when (fragNavController?.currentFrag) {
                    is UploadInvoiceStepOneFragment -> {
                        if (viewModel.addInvoice.file?.exists() == true) {
                            // (fragNavController!!.currentFrag as UploadInvoiceStepOneFragment).hideLayout()
                            // selectBottomTab(INDEX_STEP_TWO,UploadInvoiceStepTwoFragment())
                            fragNavController?.pushFragment(UploadInvoiceStepTwoFragment.newInstance(true))
                        } else {
                            showErrorMessage(getString(R.string.please_choose_capture_a_file_to_upload))
                        }
                    }

                    is UploadInvoiceStepTwoFragment -> {
                        if (viewModel.addInvoice.categoryId == 0 && viewModel.addInvoice.subCategoryId == 0) {
                            showErrorMessage(getString(R.string.please_select_a_category_to_proceed))
                        } else {
                            // selectBottomTab(INDEX_STEP_THREE,UploadInvoiceStepThreeFragment())
                            fragNavController?.pushFragment(UploadInvoiceStepThreeFragment())
                        }
                    }

                    else -> {

                        if (viewModel.addInvoice.benefitCode!=null) {
                            if (viewModel.addInvoice.power?.isEmpty() == true){
                                if (viewModel.addInvoice.benefitCode == "travel_allowance") {
                                    showErrorMessage(getString(R.string.please_enter_power_consumption_kwh))
                                } else if (viewModel.addInvoice.benefitCode == "home_charging_allowance") {
                                    showErrorMessage(getString(R.string.please_enter_no_of_km_s_travelled))
                                }
                            }else {
                                callAddInvoiceApi()
                            }
                        }else{
                            callAddInvoiceApi()
                        }


                    }
                }
            }
        }
    }

    private fun callAddInvoiceApi() {
        binding?.apply {
            /*val param = AddInvoiceReq().apply {
                this.type = viewModel.addInvoice.type.toBlankString().lowercase(Locale.ROOT)
                this.fileType = "single"
                this.processType = "automatic"
                this.paidBy = viewModel.addInvoice.paidBy
                this.expenseType = viewModel.addInvoice.expenseType
                this.categoryId = viewModel.addInvoice.categoryId.toBlankString()
                this.subCategoryId = viewModel.addInvoice.subCategoryId.toBlankString()
                this.employeeRemarks = viewModel.addInvoice.employeeRemarks.toBlankString()
                this.cardId = viewModel.addInvoice.cardId

                if (viewModel.addInvoice.benefitCode!=null) {
                    if (viewModel.addInvoice.benefitCode == "travel_allowance") {
                        this.noOfKms = viewModel.addInvoice.power
                    } else if (viewModel.addInvoice.benefitCode == "home_charging_allowance") {
                        this.noOfKws = viewModel.addInvoice.power
                    }
                }

                @field:SerializedName("type") var type: String? = "",
    @field:SerializedName("categoryId") var categoryId: String? = "",
    @field:SerializedName("subCategoryId") var subCategoryId: String? = "",
    @field:SerializedName("paidBy") var paidBy: String? = "",
    @field:SerializedName("expenseType") var expenseType: String? = "",
    @field:SerializedName("fileType") var fileType: String? = "",
    @field:SerializedName("processType") var processType: String? = "",
    @field:SerializedName("employeeRemarks") var employeeRemarks: String? = "",
    @field:SerializedName("cardId") var cardId: String? = "",
    @field:SerializedName("noOfKws") var noOfKws: String? = "",
    @field:SerializedName("noOfKms") var noOfKms: String? = ""

            }*/
            var multiPartImage: MultipartBody.Part? = null
            if (viewModel.addInvoice.file != null && viewModel.addInvoice.file?.exists() == true) {
                multiPartImage = getMultipartBody("invoiceFiles", viewModel.addInvoice.file)
            }

            var multiPartImageList: ArrayList<MultipartBody.Part?> = arrayListOf()
            if (viewModel.addInvoice.selectedFileImageList.isNotEmpty()) {
                var multiPartImageSingle: MultipartBody.Part? = null
                multiPartImageList.clear()
                viewModel.addInvoice.selectedFileImageList.forEachIndexed { index, file ->
                    multiPartImageSingle=getMultipartBody("invoiceRefDoc", file)
                    multiPartImageList.add(multiPartImageSingle)
                }


                Log.e("", "callAddInvoiceApi-=========================: ${multiPartImageList}", )
            }
            val params = HashMap<String, RequestBody>()
            params["type"]=viewModel.addInvoice.type.toBlankString().lowercase(Locale.ROOT).getRequestBody()
            params["fileType"]="single".getRequestBody()
            params["processType"]="automatic".getRequestBody()
            params["paidBy"]=viewModel.addInvoice.paidBy.getRequestBody()
            params["expenseType"]=viewModel.addInvoice.expenseType.getRequestBody()
            params["categoryId"]=viewModel.addInvoice.categoryId.toBlankString().getRequestBody()
            params["subCategoryId"]=viewModel.addInvoice.subCategoryId.toBlankString().getRequestBody()
            params["employeeRemarks"]=viewModel.addInvoice.employeeRemarks.toBlankString().getRequestBody()
            params["cardId"]=viewModel.addInvoice.cardId.toBlankString().getRequestBody()
            if (viewModel.addInvoice.benefitCode!=null) {
                if (viewModel.addInvoice.benefitCode == "travel_allowance") {
                    params["noOfKms"]=viewModel.addInvoice.power.toBlankString().getRequestBody()
                } else if (viewModel.addInvoice.benefitCode == "home_charging_allowance") {
                    params["noOfKws"]=viewModel.addInvoice.power.toBlankString().getRequestBody()

                }
            }

            viewModel.callAddInvoiceApi(params, multiPartImage,multiPartImageList)
        }

    }

    override fun onStart() {
        super.onStart()
        hideTabs()
        EventBus.getDefault().register(this)
    }

    private fun setToolbar() {
        binding!!.toolbar.let {
            overrideToolbar(it, ToolbarConfig().apply {
                showBackButton = true
                showWhiteBg = true
                showNotificationIcon = false
                showViewLine = true
                centerTitle = getString(R.string.upload_invoice)
            })

            it.ivBack.clickWithDebounce {
                popFragment()
            }

            it.ivNotification.clickWithDebounce {
                /*BaseApplication.sharedPreference.setPref(IS_LOAD,false)
                startActivity(Intent(requireContext(),NotificationActivity::class.java))*/
                //pushFragment(NotificationFragment())
            }

        }
    }

    private fun setStepper(step: String /*stepperTextView: AppCompatTextView, stepperView: View*/) {
        binding?.apply {
            when (step) {
                FIRST_STEP -> {
                    binding?.apply {
//                        viewModel.addInvoice.file =null
//                        viewModel.addInvoice.categoryId =""
//                        viewModel.addInvoice.subCategoryId =""
                        //viewStepOne.setBackgroundColor(requireActivity().getColorFromAttr(R.attr.color_primary_alpha_25))
                        //tvStepTwo.backgroundTintList=(ColorStateList.valueOf(requireActivity().getColorFromAttr(R.attr.color_primary_alpha_25)))
                        tvStepOne.backgroundColorTint(tenantInfoData?.brandingInfo?.primaryColor)
                        btnPrevious.isVisible = false
                        tvStepOne.text = getString(R.string._1)
                        tvStepTwo.text = getString(R.string._2)
                        tvStepTwo.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white)))
                        tvStepOne.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white)))
                        viewStepOne.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color_primary_25))
                        viewStepTwo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color_primary_25))
                        tvStepTwo.backgroundTintList = (ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_primary_25)))
                        tvStepThree.backgroundTintList = (ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_primary_25)))
                    }
                }

                SECOND_STEP -> {
                    tvStepOne.text = "\u2713"
                    //tvStepOne.textColor(tenantInfoData?.brandingInfo?.secondaryColor)
                    tvStepOne.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_secondary)))
                    viewStepOne.backgroundColor(tenantInfoData?.brandingInfo?.primaryColor)
                    tvStepTwo.backgroundColorTint(tenantInfoData?.brandingInfo?.primaryColor)


                    btnPrevious.isVisible = true
                    // viewStepTwo.setBackgroundColor(requireActivity().getColorFromAttr(R.attr.color_primary_alpha_25))
                    //tvStepThree.backgroundTintList=(ColorStateList.valueOf(requireActivity().getColorFromAttr(R.attr.color_primary_alpha_25)))
                    viewStepTwo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color_primary_25))
                    tvStepThree.backgroundTintList = (ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_primary_25)))
                    btnNext.text = getString(R.string.next)
                }

                THIRD_STEP -> {
                    tvStepTwo.text = "\u2713"
                    // tvStepTwo.textColor(tenantInfoData?.brandingInfo?.secondaryColor)
                    tvStepTwo.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_secondary)))
                    viewStepTwo.backgroundColor(tenantInfoData?.brandingInfo?.primaryColor)
                    tvStepThree.backgroundColorTint(tenantInfoData?.brandingInfo?.primaryColor)
                    btnPrevious.isVisible = true
                    btnNext.text = getString(R.string.submit)
                }
            }
        }
    }

    private fun initFragNav(savedInstanceState: Bundle?) {
        fragNavController?.apply {
            transactionListener = this@UploadInvoiceFragment
            rootFragmentListener = this@UploadInvoiceFragment
            createEager = true
            fragNavLogger = object : FragNavLogger {
                override fun error(message: String, throwable: Throwable) {

                }
            }

            defaultTransactionOptions = FragNavTransactionOptions.newBuilder().build()
            fragmentHideStrategy = FragNavController.DETACH

            navigationStrategy = UniqueTabHistoryStrategy(object : FragNavSwitchController {
                override fun switchTab(index: Int, transactionOptions: FragNavTransactionOptions?) {
                    //selectTabAtPosition(index)
                    fragNavController?.switchTab(index)
                }
            })
        }
        fragNavController?.initialize(FragNavController.TAB1, savedInstanceState)

        val initial = savedInstanceState == null

        if (initial) {
            UploadInvoiceStepOneFragment()
            // fragNavController?.initialize(INDEX_STEP_ONE, savedInstanceState)
            //selectBottomTab(INDEX_STEP_ONE,UploadInvoiceStepOneFragment())
        }
    }

    override val numberOfRootFragments: Int
        get() = 1

    override fun getRootFragment(index: Int): Fragment {
        return UploadInvoiceStepOneFragment()
    }

    override fun onFragmentTransaction(fragment: Fragment?, transactionType: FragNavController.TransactionType) {
        fragNavController?.isRootFragment?.not()?.let { baseActivity.supportActionBar?.setDisplayHomeAsUpEnabled(it) }
        binding?.apply {
            when (fragment) {
                is UploadInvoiceStepOneFragment -> {
                    setStepper(FIRST_STEP)
                }

                is UploadInvoiceStepTwoFragment -> {
                    setStepper(SECOND_STEP)
                }

                is UploadInvoiceStepThreeFragment -> {
                    setStepper(THIRD_STEP/*, stepperTextView = tvStepThree, stepperView = */)
                }
            }
        }
    }

    /* override val numberOfRootFragments: Int = 3*/
    override fun onTabTransaction(fragment: Fragment?, index: Int) {
        fragNavController?.isRootFragment?.not()?.let { baseActivity.supportActionBar?.setDisplayHomeAsUpEnabled(it) }
    }

    private fun redirectToPreviousScreen() {
        val currentFragment = fragNavController?.currentFrag
        hideKeyboard(requireActivity())
        if (currentFragment != null && currentFragment is BaseFragment && currentFragment.onBackPressed()) {
            return
        } else if (fragNavController?.popFragment()?.not() == true) {
            popFragment()
        }
    }

    private fun selectBottomTab(newPos: Int, fragment: Fragment) {/*if (viewModel.currentTabPosition == newPos) {
            fragNavController?.clearStack()
            return
        }*/
        viewModel.currentTabPosition = newPos
        // fragment?.let { fragNavController?.replaceFragment(fragment = it) }
        fragNavController?.switchTab(newPos)
        //fragNavController?.replaceFragment(fragment!!)
        selectTabAtPosition(newPos)
    }

    private fun selectTabAtPosition(index: Int) {
        viewModel.currentTabPosition = index
        when (index) {
            INDEX_STEP_ONE -> {
                setStepper(FIRST_STEP)
            }

            INDEX_STEP_TWO -> {
                setStepper(SECOND_STEP)
            }

            INDEX_STEP_THREE -> {
                setStepper(THIRD_STEP)
            }
        }
    }

    /**
     * Notification count update
     *
     * @param updateNotificationCount
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun notificationCountUpdate(updateNotificationCount: UpdateNotificationCount) {/*if (updateNotificationCount.pushNotificationCount!! > 0) {
            binding!!.toolbar.tvNotificationCount.visibility = View.VISIBLE
            binding!!.toolbar.ivNotification.visibility = View.VISIBLE
            binding!!.toolbar.tvNotificationCount.visibility = updateNotificationCount.pushNotificationCount?:0
        } else {
            binding!!.toolbar.tvNotificationCount.visibility = View.GONE
            binding!!.toolbar.ivNotification.visibility = View.VISIBLE
        }*/
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

}