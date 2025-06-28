package de.fast2work.mobility.ui.invoice

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.res.Configuration
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.fast2work.mobility.R
import de.fast2work.mobility.data.eventbus.UpdateNotificationCount
import de.fast2work.mobility.data.request.InvoiceReq
import de.fast2work.mobility.data.response.CategoryApiResponse
import de.fast2work.mobility.data.response.InvoiceApiResponse
import de.fast2work.mobility.databinding.DialogInvoiceFilterBinding
import de.fast2work.mobility.databinding.DialogInvoiceLegendBinding
import de.fast2work.mobility.databinding.FragmentInvoiceBinding
import de.fast2work.mobility.ui.category.CategoryFragment.Companion.BUDGET_GROUP_ID
import de.fast2work.mobility.ui.category.CategoryFragment.Companion.CATEGORY_ID
import de.fast2work.mobility.ui.category.CategoryFragment.Companion.IS_FROM_CATEGORY
import de.fast2work.mobility.ui.category.CategoryFragment.Companion.PAY_PERIOD_MONTH
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.invoice.adapter.InvoiceAdapter
import de.fast2work.mobility.ui.invoice.adapter.InvoiceCategoryAdapter
import de.fast2work.mobility.ui.invoice.adapter.InvoiceStatusAdapter
import de.fast2work.mobility.ui.invoice.invoicedetails.InvoiceDetailsFragment
import de.fast2work.mobility.ui.invoice.model.InvoiceStatusModel
import de.fast2work.mobility.ui.sidemenu.notification.NotificationFragment
import de.fast2work.mobility.ui.upload.UploadInvoiceFragment
import de.fast2work.mobility.utility.customrecyclerview.Paginate
import de.fast2work.mobility.utility.customview.DividerItemDecorator
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.extension.SCREEN_HEIGHT
import de.fast2work.mobility.utility.extension.backgroundColorTint
import de.fast2work.mobility.utility.extension.getAndroidDeviceId
import de.fast2work.mobility.utility.extension.imageTickTint
import de.fast2work.mobility.utility.extension.setVisible
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.util.IConstants.Companion.PAGE_LIMIT_20
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.Locale

class InvoiceFragment : BaseVMBindingFragment<FragmentInvoiceBinding, InvoiceViewModel>(InvoiceViewModel::class.java) {


    companion object {/* const val BUDGET_GROUP_ID = "budgetGroupId"
        const val CATEGORY_ID = "categoryId"
        const val PAY_PERIOD_MONTH = "payPeriodMonth"
        const val IS_FROM_CATEGORY = "isFromCategory"*/

        fun newInstance(/*budgetGroupId: String, categoryId: Int, payPeriodMonth: String*/) = InvoiceFragment().apply {
            this.arguments = Bundle().apply {
                /* this.putString(BUDGET_GROUP_ID, budgetGroupId)
                 this.putInt(CATEGORY_ID, categoryId)
                 this.putString(PAY_PERIOD_MONTH, payPeriodMonth)*/
            }
        }
    }

    private var invoiceAdapter: InvoiceAdapter? = null
    private var invoiceList: ArrayList<InvoiceApiResponse> = arrayListOf()
    private var invoiceStatusList: ArrayList<InvoiceStatusModel> = arrayListOf()
    private var invoiceStatusAdapter: InvoiceStatusAdapter? = null
    private var invoiceCategoryAdapter: InvoiceCategoryAdapter? = null
    private lateinit var dialogFilterBinding: DialogInvoiceFilterBinding
    private var dialog: BottomSheetDialog? = null
    private lateinit var dialogLegendBinding: DialogInvoiceLegendBinding
    private var dialogLegend: BottomSheetDialog? = null
    private var isFilterStatusVisible = true
    private var isFilterCategoryVisible = true
    private var isFilterSupplierVisible = true
    private var isFilterDateVisible = true
    private var categoryList: ArrayList<CategoryApiResponse.Category> = arrayListOf()
    private var filterStartDate = ""
    private var filterEndDate = ""
    private var filterSupplierName = ""
    private var lastTextEdit: Long = 0
    private var delay: Long = 1000
    private var handler: Handler = Handler()
    var budgetGroupId: String? = ""
    var payPeriodMonth: String? = ""
    var refTypeString="invoice"
    var categoryIdList: ArrayList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onStop() {
        super.onStop()
        budgetGroupId = ""
        payPeriodMonth = ""
        categoryIdList.clear()
        binding!!.telSearch.setText("")
        binding!!.telSearch.clearFocus()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return generateBinding(FragmentInvoiceBinding.inflate(inflater), container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initFilterAdapters()

//        if (isFirstTimeLoad) {
//            initRecyclerView()
//            initFilterAdapters()
//            val invoiceListParam = InvoiceReq().apply {
//                pageNo = 1
//                limit = PAGE_LIMIT_20
//                categoryId = categoryIdList
//                budgetGroupId = this@InvoiceFragment.budgetGroupId
//            }
//            viewModel.callInvoiceListApi(invoiceListParam)
//            val categoryListParam = HashMap<String, Any?>().apply {
//                put("sortBy", "categoryName")
//                put("sortOrder", "asc")
//            }
//            viewModel.callCategoryListApi(categoryListParam)
//        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initFilterAdapters() {
        invoiceStatusAdapter = InvoiceStatusAdapter(baseActivity, dummyInvoiceStatusData()) { view, model, position ->
            when (view.id) {
                R.id.cl_item -> {
                    model.isSelected = !model.isSelected
                    invoiceStatusAdapter?.notifyDataSetChanged()
                }
            }
        }
        invoiceCategoryAdapter = InvoiceCategoryAdapter(baseActivity, categoryList) { view, model, position ->
            when (view.id) {
                R.id.cl_item -> {
                    model.isSelected = !model.isSelected
                    invoiceCategoryAdapter?.notifyDataSetChanged()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerView() {
        binding?.apply {
            invoiceAdapter = InvoiceAdapter(requireContext(), invoiceList) { view, model, position ->
                when (view.id) {
                    R.id.clMain -> {
                        if (model.approvalStatus.toString().lowercase() == "rejected" || model.approvalStatus.toString().lowercase() == "approved") {
                            pushFragment(InvoiceDetailsFragment.newInstance((model.invoiceId ?: 0).toBlankString()))
                        }else{
                            if (model.captureCo2){
                                pushFragment(InvoiceDetailsFragment.newInstance((model.invoiceId ?: 0).toBlankString(),true))
                            }else{
                                pushFragment(InvoiceDetailsFragment.newInstance((model.invoiceId ?: 0).toBlankString()))
                            }
                        }
                    }
                    R.id.tv_invoice_type ->{

                    }
                }
            }
            rvInvoice.layoutManager = LinearLayoutManager(baseActivity)
            rvInvoice.adapter = invoiceAdapter

            rvInvoice.setListPagination(object : Paginate.Callbacks {
                override fun onLoadMore() {
                    viewModel.isLoading = true
                    viewModel.currentInvoicePage++
                    val selectedStatusList = invoiceStatusList.filter { it.isSelected }
                    val selectedCategoryList = categoryList.filter { it.isSelected }
                    filterSupplierName = if (::dialogFilterBinding.isInitialized) dialogFilterBinding.telSupllierName.text.toBlankString() else ""
                    val invoiceListParam = InvoiceReq().apply {
                        pageNo = viewModel.currentInvoicePage
                        limit = PAGE_LIMIT_20
                        filterFrom = filterStartDate
                        filterTo = filterEndDate
                        supplierName = if(filterSupplierName.isNotEmpty()){ arrayListOf(filterSupplierName) } else arrayListOf()
                        approvalStatus = selectedStatusList.map { it.title }.toCollection(ArrayList())
                        categoryId = selectedCategoryList.mapNotNull { it.categoryId }.toCollection(ArrayList())
                        keyword = binding?.telSearch?.text?.toBlankString()
                        deviceId = requireActivity().getAndroidDeviceId()
                    }
                    viewModel.callInvoiceListApi(invoiceListParam,false)
                }

                override fun isLoading(): Boolean {
                    return viewModel.isLoading
                }

                override fun hasLoadedAllItems(): Boolean {
                    return !viewModel.hasLoadedAllInvoiceData
                }

            })
        }
    }

    override fun attachObservers() {
        BaseApplication.notificationCount.observe(this){
            if (it > 0) {
                binding!!.toolbar.tvNotificationCount.visibility = View.VISIBLE
                binding!!.toolbar.ivNotification.visibility = View.VISIBLE
                binding!!.toolbar.tvNotificationCount.text = it.toBlankString()
            } else {
                binding!!.toolbar.tvNotificationCount.visibility = View.GONE
                binding!!.toolbar.ivNotification.visibility = View.VISIBLE
            }
        }
        viewModel.invoiceListLiveData.observe(this) {
//            if (it.data?.data?.size!! > 0) {
            viewModel.isLoading = false

            viewModel.hasLoadedAllInvoiceData = it.data?.currentPage != it.data?.totalPages
            if (it.data?.currentPage == 1) {
                invoiceList.clear()
            }
            it.data?.data?.let { it1 -> invoiceList.addAll(it1) }
            invoiceAdapter?.notifyDataSetChanged()

            if (invoiceAdapter?.itemCount!! > 0) {
                binding?.rvInvoice?.isVisible = true
                binding?.clNoData?.isVisible = false
            } else {
                binding?.rvInvoice?.isVisible = false
                binding?.clNoData?.isVisible = true
            }

            if (arguments != null && requireArguments().containsKey(IS_FROM_CATEGORY)) {
                requireArguments().putBoolean(IS_FROM_CATEGORY, false)
            }
        }
        viewModel.categoryListLiveData.observe(this) {
            if ((it.data?.data?.size ?: 0) > 0) {
                categoryList.clear()
                it.data?.data?.let { it1 -> categoryList.addAll(it1) }
                invoiceCategoryAdapter?.notifyDataSetChanged()
                categoryList.forEachIndexed { index, category ->
                    categoryIdList.forEachIndexed { index, i ->
                        if (category.categoryId==i){
                            category.isSelected=true
                        }
                    }
                }
                if (viewModel.isCategoryScreen){
                    toCallInvoiceListingApi()
                }
            }
        }
    }


    override fun initComponents() {
        val categoryListParam = HashMap<String, Any?>().apply {
            put("sortBy", "categoryName")
            put("sortOrder", "asc")
        }
        viewModel.callCategoryListApi(categoryListParam)
        setToolbar()
        binding!!.noData.ivNoData.imageTickTint(tenantInfoData?.brandingInfo?.primaryColor)
        binding!!.noData.ivNoDataBg.imageTickTint(tenantInfoData?.brandingInfo?.primaryColor)
        binding!!.tvFilterCount.backgroundColorTint(tenantInfoData?.brandingInfo?.primaryColor)
        binding!!.telSearch.clearFocus()
        if (arguments != null && requireArguments().containsKey(IS_FROM_CATEGORY)) {
             viewModel.isCategoryScreen = requireArguments().getBoolean(IS_FROM_CATEGORY, false)
            if (viewModel.isCategoryScreen) {
                val categoryId = requireArguments().getInt(CATEGORY_ID, 0)
                budgetGroupId = requireArguments().getString(BUDGET_GROUP_ID, "")
                payPeriodMonth = requireArguments().getString(PAY_PERIOD_MONTH, "")
                categoryIdList.clear()
                categoryIdList.add(categoryId)
                refTypeString="category"
                binding!!.tvFilterCount.isVisible = viewModel.isCategoryScreen
            } else {
                budgetGroupId = ""
                payPeriodMonth = ""
                categoryIdList.clear()
                categoryList.clear()
                refTypeString="invoice"
                binding!!.telSearch.setText( "")
                binding!!.tvFilterCount.isVisible = false
                toCallInvoiceListingApi()
            }

        } else {
            binding!!.telSearch.setText( "")
//            if (isFirstTimeLoad) {
                toCallInvoiceListingApi()
                binding!!.tvFilterCount.isVisible=false
//            }
        }



    }

    private fun toCallInvoiceListingApi() {
//        if (isFirstTimeLoad) {
        val selectedCategoryList = categoryList.filter { it.isSelected }


        val invoiceListParam = InvoiceReq().apply {
            pageNo = 1
            limit = PAGE_LIMIT_20
            categoryId = selectedCategoryList.mapNotNull { it.categoryId }.toCollection(ArrayList())
            payPeriodStartMonth = this@InvoiceFragment.payPeriodMonth
            budgetGroupId = this@InvoiceFragment.budgetGroupId
            refType=refTypeString
            deviceId = requireActivity().getAndroidDeviceId()
        }
        viewModel.callInvoiceListApi(invoiceListParam,true)

//        }
    }

    private fun toCallSearchApi() {
        val selectedStatusList = invoiceStatusList.filter { it.isSelected }
        val selectedCategoryList = categoryList.filter { it.isSelected }
        filterSupplierName = if (::dialogFilterBinding.isInitialized) dialogFilterBinding.telSupllierName.text.toBlankString() else ""
        viewModel.currentInvoicePage = 1
        viewModel.isLoading = false
        viewModel.hasLoadedAllInvoiceData = false
        val invoiceListParam = InvoiceReq().apply {
            pageNo = viewModel.currentInvoicePage
            limit = PAGE_LIMIT_20
            filterFrom = filterStartDate
            filterTo = filterEndDate
            supplierName = if(filterSupplierName.isNotEmpty()){ arrayListOf(filterSupplierName) } else arrayListOf()
            approvalStatus = selectedStatusList.map { it.title }.toCollection(ArrayList())
            categoryId = selectedCategoryList.mapNotNull { it.categoryId }.toCollection(ArrayList())
            keyword = binding?.telSearch?.text?.toBlankString()
            refType=refTypeString
            deviceId = requireActivity().getAndroidDeviceId()
        }
        viewModel.callInvoiceListApi(invoiceListParam,false)
    }

    private var runnable = Runnable {
        if (System.currentTimeMillis() > (lastTextEdit + delay - 500)) {
            val selectedStatusList = invoiceStatusList.filter { it.isSelected }
            val selectedCategoryList = categoryList.filter { it.isSelected }
            filterSupplierName = if (::dialogFilterBinding.isInitialized) dialogFilterBinding.telSupllierName.text.toString() else ""
            viewModel.currentInvoicePage = 1

            val invoiceListParam = InvoiceReq().apply {
                pageNo = viewModel.currentInvoicePage
                limit = PAGE_LIMIT_20
                filterFrom = filterStartDate
                filterTo = filterEndDate
                supplierName = if(filterSupplierName.isNotEmpty()){ arrayListOf(filterSupplierName) } else arrayListOf()
                approvalStatus = selectedStatusList.map { it.title }.toCollection(ArrayList())
                categoryId = selectedCategoryList.mapNotNull { it.categoryId }.toCollection(ArrayList())
                keyword = binding?.telSearch?.text?.toBlankString()
                refType=refTypeString
                deviceId = requireActivity().getAndroidDeviceId()
            }
            viewModel.callInvoiceListApi(invoiceListParam,false)
        }
    }

    override fun setClickListener() {
        binding?.apply {
            ivFilterInvoice.clickWithDebounce {
                showFilterDialog()
            }
            ivLegend.clickWithDebounce {
                showLegendDialog()
            }
        }

        binding!!.ivClear.clickWithDebounce {
            binding!!.telSearch.setText("")
            toCallSearchApi()
        }
        binding?.telSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s:  CharSequence?, start: Int, before: Int, count: Int) {
                Log.e("=============","is focused:: ${binding!!.telSearch.isFocused}")
                Log.e("=============","s.toString():: ${s.toString()}")
                if (s.toString().isEmpty() && binding!!.telSearch.isFocused) {
                    binding!!.telSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_search, 0)
                    binding!!.ivClear.isVisible = false
                    handler.removeCallbacks(runnable)
                    toCallSearchApi()
                } else {
                    if (binding!!.telSearch.isFocused){
                        binding!!.ivClear.isVisible = true
                        binding!!.telSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    }else{
                        binding!!.ivClear.isVisible = false
                        binding!!.telSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_search, 0)
                    }
                }
//                 handler.removeCallbacks(runnable)
            }

            override fun afterTextChanged(s: Editable?) {
                lastTextEdit = System.currentTimeMillis()
//                handler.postDelayed(runnable, delay)
                if (s!!.count() > 3) {
                    handler.postDelayed(runnable, delay)
                }
//                toCallSearchApi()
            }

        })
        binding?.srInvoice?.setOnRefreshListener {
//            binding!!.tvFilterCount.isVisible=false
            binding?.srInvoice?.isRefreshing = false
            toCallSearchApi()
//            viewModel.currentInvoicePage = 1
//            viewModel.isLoading = false
//            viewModel.hasLoadedAllInvoiceData = false
//            val invoiceListParam = InvoiceReq().apply {
//                pageNo = viewModel.currentInvoicePage
//                limit = PAGE_LIMIT_20
//                keyword = binding!!.telSearch.text.toBlankString()
//                deviceId = requireActivity().getAndroidDeviceId()
//            }
//            viewModel.callInvoiceListApi(invoiceListParam,true)
        }
    }

    private fun showFilterDialog() {
        categoryList.forEachIndexed { index, category ->
            categoryIdList.forEachIndexed { index, i ->
                if (category.categoryId==i){
                    category.isSelected=true
                }
            }
        }
        val inflater = LayoutInflater.from(baseActivity)
        dialogFilterBinding = DialogInvoiceFilterBinding.inflate(inflater)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog = BottomSheetDialog(requireContext())
        dialog?.setContentView(dialogFilterBinding.root)
        dialog?.setCancelable(false)
        if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
            dialog?.window?.setBackgroundDrawableResource(R.color.color_primary_dark_text_80)
        }else{
            dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
        setThemeForView(dialogFilterBinding.btnApply)
        setThemeForButtonTextView(dialogFilterBinding.btnClear)
        setBackgroundColorTintForAlpha(dialogFilterBinding.btnClear)
        showBottomSheetSmallHeight()

        dialogFilterBinding.rvFilterStatus.layoutManager = LinearLayoutManager(baseActivity)
        val dividerItemDecoration1: RecyclerView.ItemDecoration = DividerItemDecorator(ContextCompat.getDrawable(requireActivity(), R.drawable.divider)!!)
        dialogFilterBinding.rvFilterStatus.addItemDecoration(dividerItemDecoration1)
        dialogFilterBinding.rvFilterStatus.adapter = invoiceStatusAdapter

        dialogFilterBinding.rvFilterCategory.layoutManager = LinearLayoutManager(baseActivity)
        val dividerItemDecoration: RecyclerView.ItemDecoration = DividerItemDecorator(ContextCompat.getDrawable(requireActivity(), R.drawable.divider)!!)
        dialogFilterBinding.rvFilterCategory.addItemDecoration(dividerItemDecoration)
//        dialogFilterBinding.rvFilterCategory.setHasFixedSize(true)
        dialogFilterBinding.rvFilterCategory.adapter = invoiceCategoryAdapter

        if (filterStartDate.isNotEmpty()) {
            dialogFilterBinding.tvStartDate.text = filterStartDate
        }
        if (filterEndDate.isNotEmpty()) {
            dialogFilterBinding.tvEndDate.text = filterEndDate
        }

        dialogFilterBinding.telSupllierName.setText(filterSupplierName)

        manageFilterCardVisibility()
        dialogFilterBinding.cvStatus.clickWithDebounce {
            isFilterStatusVisible = !isFilterStatusVisible
            manageFilterCardVisibility()
        }
        dialogFilterBinding.cvCategories.clickWithDebounce {
            isFilterCategoryVisible = !isFilterCategoryVisible
            manageFilterCardVisibility()
        }
        dialogFilterBinding.cvSupplierName.clickWithDebounce {
            isFilterSupplierVisible = !isFilterSupplierVisible
            manageFilterCardVisibility()
        }
        dialogFilterBinding.cvDateRange.clickWithDebounce {
            isFilterDateVisible = !isFilterDateVisible
            manageFilterCardVisibility()
        }
        dialogFilterBinding.tvStartDate.clickWithDebounce {
            datePickerDialog("START")
        }
        dialogFilterBinding.tvEndDate.clickWithDebounce {
            if (filterStartDate.isNotEmpty()) {
                datePickerDialog("END")
            } else {
                showErrorMessage(getString(R.string.please_select_start_date))
            }
        }

        dialogFilterBinding.btnClear.clickWithDebounce {
            filterStartDate = ""
            filterEndDate = ""
            filterSupplierName = ""
            refTypeString="invoice"
            viewModel.currentInvoicePage = 1
            invoiceStatusList.map { it.isSelected = false }
            categoryList.map { it.isSelected = false }
            val invoiceListParam = InvoiceReq().apply {
                pageNo = viewModel.currentInvoicePage
                limit = PAGE_LIMIT_20
                filterFrom = ""
                filterTo = ""
                supplierName = arrayListOf()
                approvalStatus = arrayListOf()
                categoryId = arrayListOf()
                refType=refTypeString
                keyword = binding?.telSearch?.text?.toBlankString()
                deviceId = requireActivity().getAndroidDeviceId()
            }
            binding!!.tvFilterCount.isVisible=false
            viewModel.callInvoiceListApi(invoiceListParam,true)
            dialog?.dismiss()
        }

//        if (dialog?.isShowing == false){
//            filterStartDate = ""
//            filterEndDate = ""
//            filterSupplierName = ""
//            viewModel.currentInvoicePage = 1
//            invoiceStatusList.map { it.isSelected = false }
//            categoryList.map { it.isSelected = false }
//            binding!!.tvFilterCount.isVisible=false
//        }

        dialogFilterBinding.btnApply.clickWithDebounce {

            viewModel.currentInvoicePage = 1

            //Call Api according to filters applied
            val selectedStatusList = invoiceStatusList.filter { it.isSelected }
            val selectedCategoryList = categoryList.filter { it.isSelected }
            filterSupplierName = dialogFilterBinding.telSupllierName.text.toBlankString()
            val invoiceListParam = InvoiceReq().apply {
                pageNo = viewModel.currentInvoicePage
                limit = PAGE_LIMIT_20
                filterFrom = filterStartDate
                filterTo = filterEndDate
                supplierName = if(filterSupplierName.isNotEmpty()){ arrayListOf(filterSupplierName) } else arrayListOf()
                approvalStatus = selectedStatusList.map { it.title }.toCollection(ArrayList())
                categoryId = selectedCategoryList.mapNotNull { it.categoryId }.toCollection(ArrayList())
                keyword = binding?.telSearch?.text?.toBlankString()
                deviceId = requireActivity().getAndroidDeviceId()
            }
            binding!!.tvFilterCount.isVisible = selectedStatusList.isNotEmpty()||selectedCategoryList.isNotEmpty()||filterSupplierName.isNotEmpty()||filterStartDate.isNotEmpty()||filterEndDate.isNotEmpty()
            viewModel.callInvoiceListApi(invoiceListParam,true)

            dialog?.dismiss()
        }

        dialog?.show()
//        dialog?.setOnDismissListener {
//            viewModel.currentInvoicePage = 1
//
//            //Call Api according to filters applied
//            val selectedStatusList = invoiceStatusList.filter { it.isSelected }
//            val selectedCategoryList = categoryList.filter { it.isSelected }
//            filterSupplierName = dialogFilterBinding.telSupllierName.text.toBlankString()
//            val invoiceListParam = InvoiceReq().apply {
//                pageNo = viewModel.currentInvoicePage
//                limit = PAGE_LIMIT_20
//                filterFrom = filterStartDate
//                filterTo = filterEndDate
//                supplierName = arrayListOf(filterSupplierName)
//                approvalStatus =
//                    selectedStatusList.mapNotNull { it.statusTitle }.toCollection(ArrayList())
//                categoryId =
//                    selectedCategoryList.mapNotNull { it.categoryId }.toCollection(ArrayList())
//                keyword = binding?.telSearch?.text?.toBlankString()
//            }
//            viewModel.callInvoiceListApi(invoiceListParam)
//        }

    }

    private fun datePickerDialog(selectionType: String = "START") {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        setLocale(requireContext(), Locale.ENGLISH)
        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val date = Calendar.getInstance()
            date.set(Calendar.YEAR, selectedYear)
            date.set(Calendar.MONTH, selectedMonth)
            date.set(Calendar.DAY_OF_MONTH, selectedDay)
            when (selectionType) {
                "START" -> {
                    filterStartDate = dateFormat.format(date.time)
                    dialogFilterBinding.tvStartDate.text = filterStartDate
                }
                "END" -> {
                    filterEndDate = dateFormat.format(date.time)
                    dialogFilterBinding.tvEndDate.text = filterEndDate
                }
            }
        }, year, month, day)
//        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
        if (filterStartDate.isNotEmpty() && selectionType == "END") {
            calendar.time = dateFormat.parse(filterStartDate)
            datePickerDialog.datePicker.minDate = calendar.timeInMillis


        }
        datePickerDialog.show()
    }
    private fun setLocale(context: Context, locale: Locale) {
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
    private fun showLegendDialog() {
        val inflater = LayoutInflater.from(baseActivity)
        dialogLegendBinding = DialogInvoiceLegendBinding.inflate(inflater)

        dialogLegend = BottomSheetDialog(requireContext())
        dialogLegend?.setContentView(dialogLegendBinding.root)
        dialogLegend?.setCancelable(true)

        dialogLegend?.show()
    }

    private fun manageFilterCardVisibility() {
        if (isFilterStatusVisible) {
            dialogFilterBinding.ivStatusArrow.rotation = 180F
            dialogFilterBinding.rvFilterStatus.setVisible(true)
        } else {
            dialogFilterBinding.ivStatusArrow.rotation = 0F
            dialogFilterBinding.rvFilterStatus.setVisible(false)
        }

        if (isFilterCategoryVisible) {
            dialogFilterBinding.ivCategoryArrow.rotation = 180F
            dialogFilterBinding.rvFilterCategory.setVisible(true)
        } else {
            dialogFilterBinding.ivCategoryArrow.rotation = 0F
            dialogFilterBinding.rvFilterCategory.setVisible(false)
        }

        if (isFilterSupplierVisible) {
            dialogFilterBinding.ivSupplierArrow.rotation = 180F
            dialogFilterBinding.cvSubSupplier.setVisible(true)
        } else {
            dialogFilterBinding.ivSupplierArrow.rotation = 0F
            dialogFilterBinding.cvSubSupplier.setVisible(false)
        }

        if (isFilterDateVisible) {
            dialogFilterBinding.ivDateRangeArrow.rotation = 180F
            dialogFilterBinding.cvSubDateRange.setVisible(true)
        } else {
            dialogFilterBinding.ivDateRangeArrow.rotation = 0F
            dialogFilterBinding.cvSubDateRange.setVisible(false)
        }
    }

    private fun dummyInvoiceStatusData(): ArrayList<InvoiceStatusModel> {
        invoiceStatusList.clear()
        invoiceStatusList.add(InvoiceStatusModel(statusTitle = getString(R.string.approved), "approved",isSelected = false))
        invoiceStatusList.add(InvoiceStatusModel(statusTitle = getString(R.string.rejected), "rejected",isSelected = false))
        invoiceStatusList.add(InvoiceStatusModel(statusTitle = getString(R.string.pending), "inreview",isSelected = false))
        invoiceStatusList.add(InvoiceStatusModel(statusTitle = getString(R.string.co2_data_missing), "inreview",isSelected = false))
        return invoiceStatusList
    }

    private fun setToolbar() {
        binding?.toolbar?.let {
            overrideToolbar(it, ToolbarConfig().apply {
                showMenuButton = true
                showWhiteBg = true
                showWhiteBg = true
                showNotificationIcon = true
                showLogoIcon = true
                showViewLine = true
            })

            it.ivBack.clickWithDebounce {

            }

            it.ivMenu.clickWithDebounce {
                toggleDrawer()
            }
            it.ivNotification.clickWithDebounce {
                pushFragment(NotificationFragment())
            }
        }
    }

    private fun showBottomSheetSmallHeight() {
        val height = (SCREEN_HEIGHT * 0.75).toInt()
        val layoutParams = dialogFilterBinding.constraintLayout.layoutParams
        layoutParams.height = height
        dialogFilterBinding.constraintLayout.layoutParams = layoutParams
        dialogFilterBinding.constraintLayout.requestLayout()
//        when {
//            isFilterDateVisible -> {
//                val layoutParams = dialogFilterBinding.nestedScrollView.layoutParams
//                layoutParams.height = resources.getDimensionPixelSize(R.dimen._300sdp)
//                dialogFilterBinding.nestedScrollView.layoutParams = layoutParams
//                dialogFilterBinding.nestedScrollView.requestLayout()
//            }
//
//            isFilterCategoryVisible -> {
//                val layoutParams = dialogFilterBinding.nestedScrollView.layoutParams
//                layoutParams.height = resources.getDimensionPixelSize(R.dimen._400sdp)
//                dialogFilterBinding.nestedScrollView.layoutParams = layoutParams
//                dialogFilterBinding.nestedScrollView.requestLayout()
//            }
//
//            isFilterSupplierVisible -> {
//                val layoutParams = dialogFilterBinding.nestedScrollView.layoutParams
//                layoutParams.height = resources.getDimensionPixelSize(R.dimen._300sdp)
//                dialogFilterBinding.nestedScrollView.layoutParams = layoutParams
//                dialogFilterBinding.nestedScrollView.requestLayout()
//            }
//
//            isFilterStatusVisible -> {
//                val layoutParams = dialogFilterBinding.nestedScrollView.layoutParams
//                layoutParams.height = resources.getDimensionPixelSize(R.dimen._300sdp)
//                dialogFilterBinding.nestedScrollView.layoutParams = layoutParams
//                dialogFilterBinding.nestedScrollView.requestLayout()
//            }
//
//            else ->{
//                val layoutParams = dialogFilterBinding.nestedScrollView.layoutParams
//                layoutParams.height = resources.getDimensionPixelSize(R.dimen._300sdp)
//                dialogFilterBinding.nestedScrollView.layoutParams = layoutParams
//                dialogFilterBinding.nestedScrollView.requestLayout()
//            }
//        }

    }

    /**
     * Notification count update
     *
     * @param updateNotificationCount
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun notificationCountUpdate(updateNotificationCount: UpdateNotificationCount) {
        if (updateNotificationCount.pushNotificationCount!! > 0) {
            binding!!.toolbar.tvNotificationCount.visibility = View.VISIBLE
            binding!!.toolbar.ivNotification.visibility = View.VISIBLE
            binding!!.toolbar.tvNotificationCount.text = updateNotificationCount.pushNotificationCount.toBlankString()
           toCallInvoiceListingApi()
        } else {
            binding!!.toolbar.tvNotificationCount.visibility = View.GONE
            binding!!.toolbar.ivNotification.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        if (BaseApplication.sharedPreference.getPref(UploadInvoiceFragment.UPLOAD_INVOICE,false)){
            showAlertMessage(getString(R.string.invoice_will_be_listed_once_processed_please_wait))
            BaseApplication.sharedPreference.setPref(UploadInvoiceFragment.UPLOAD_INVOICE,false)
        }
        EventBus.getDefault().register(this)
    }
}