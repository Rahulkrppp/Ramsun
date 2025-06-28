package de.fast2work.mobility.ui.category

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import de.fast2work.mobility.R
import de.fast2work.mobility.data.eventbus.UpdateNotificationCount
import de.fast2work.mobility.data.response.BudgetGroupInfoItem
import de.fast2work.mobility.databinding.FragmentCategoryBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.invoice.adapter.InvoiceAdapter
import de.fast2work.mobility.ui.invoice.invoicedetails.InvoiceDetailsFragment
import de.fast2work.mobility.ui.sidemenu.notification.NotificationFragment
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
//import de.fast2work.mobility.utility.extension.MMM_YYYY
import de.fast2work.mobility.utility.extension.MM_FORMAT
import de.fast2work.mobility.utility.extension.SERVER_FORMAT
import de.fast2work.mobility.utility.extension.YYYY_FORMAT
import de.fast2work.mobility.utility.extension.capitalized
import de.fast2work.mobility.utility.extension.displayDate
import de.fast2work.mobility.utility.extension.formatCurrency
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.imageTickTint
import de.fast2work.mobility.utility.extension.loadImageIcons
import de.fast2work.mobility.utility.extension.parcelable
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.preference.EasyPref
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Fragment used for Category list
 * */
class CategoryFragment : BaseVMBindingFragment<FragmentCategoryBinding, CategoryViewModel>(CategoryViewModel::class.java) {

    private var invoiceAdapter: InvoiceAdapter? = null

    companion object {
        const val BUDGET_GROUP_ID = "budgetGroupId"
        const val CATEGORY_ID = "categoryId"
        const val PAY_PERIOD_MONTH = "payPeriodMonth"
        const val IS_FROM_CATEGORY = "isFromCategory"
        const val PARAM_CATEGORY_MODEL = "categoryModel"
        const val PARAM_CATEGORY_TYPE_NAME = "categoryTypeName"
        const val PARAM_BUDGET_ID = "budgetGroupId"
        const val PARAM_CATEGORY_DATE = "categoryDate"
        const val PARAM_CATEGORY_ID = "categoryId"
        fun newInstance(
            model: BudgetGroupInfoItem, categoryId: Int, categoryTypeName: String, budgetGroupId: String,
            categoryDate: String,
        ) = CategoryFragment().apply {
            this.arguments = Bundle().apply {
                this.putParcelable(PARAM_CATEGORY_MODEL, model)
                this.putInt(PARAM_CATEGORY_ID, categoryId)
                this.putString(PARAM_CATEGORY_TYPE_NAME, categoryTypeName)
                this.putString(PARAM_BUDGET_ID, budgetGroupId)
                this.putString(PARAM_CATEGORY_DATE, categoryDate)

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return generateBinding(FragmentCategoryBinding.inflate(inflater), container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isFirstTimeLoad) {
            initRecyclerView()
            viewModel.calInvoiceListApi(viewModel.categoryDate)
        }
    }

    override fun attachObservers() {
        BaseApplication.notificationCount.observe(this){
           /* if (it > 0) {
                binding!!.toolbar.tvNotificationCount.visibility = View.VISIBLE
                binding!!.toolbar.ivNotification.visibility = View.VISIBLE
                binding!!.toolbar.tvNotificationCount.text = it.toBlankString()
            } else {
                binding!!.toolbar.tvNotificationCount.visibility = View.GONE
                binding!!.toolbar.ivNotification.visibility = View.VISIBLE
            }*/
        }
        viewModel.invoiceListLiveData.observe(this) {
            if (it.data?.data?.size!! > 0) {
                binding!!.rvInvoice.isVisible = true
                binding!!.clNoData.isVisible = false
                viewModel.invoiceList.clear()
                it.data?.data?.let { it1 -> viewModel.invoiceList.addAll(it1) }
                invoiceAdapter?.notifyDataSetChanged()
            } else {
                binding!!.rvInvoice.isVisible = false
                binding!!.clNoData.isVisible = true
            }
        }
        viewModel.categoryInfoLiveData.observe(this) {
            binding?.apply {
                progress.progress = it.data?.categoryData?.get(0)?.getBudgetProgressValue()?.roundToInt()!!
                tvCategoryPrice.text = it.data?.categoryData?.get(0)?.unusedBudget?.formatCurrency(it.data?.categoryData?.get(0)?.currencySymbol?:"")
                tvCategory.text = it.data?.categoryData?.get(0)?.categoryName
                tvBudgetName.text = it.data?.categoryData?.get(0)?.categoryName.toBlankString().capitalized()
                tvTotalBudget.text =  it.data?.categoryData?.get(0)?.allocatedBudget?.formatCurrency(viewModel.budgetGroupInfoItem.currencySymbol?:"")
                tvRemainingBudget.text = it.data?.categoryData?.get(0)?.unusedBudget?.formatCurrency(viewModel.budgetGroupInfoItem.currencySymbol?:"")
                if (it.data?.categoryData?.get(0)?.categoryIcon != null){
                    ivCategory.setColorFilter(requireActivity().getColorFromAttr(R.attr.colorTextView))
                    ivCategory.loadImageIcons(requireActivity(), it.data?.categoryData?.get(0)?.categoryIcon)
                }
            }
        }
        viewModel.errorLiveData.observe(this) {
            showErrorMessage(it)
        }
    }


    /**
     * This method contains code to handle initial
     *
     */
    override fun initComponents() {
        setToolbar()
        viewModel.budgetGroupInfoItem = requireArguments().parcelable(PARAM_CATEGORY_MODEL)!!
        viewModel.categoryTypeName = requireArguments().getString(PARAM_CATEGORY_TYPE_NAME, "")
        viewModel.budgetGopId = requireArguments().getString(PARAM_BUDGET_ID, "")
        viewModel.categoryDate = requireArguments().getString(PARAM_CATEGORY_DATE, "")
        viewModel.categoryId = requireArguments().getInt(PARAM_CATEGORY_ID, 0)

        setThemeForTextView(binding?.tvAllInvoice!!)
        binding!!.noData.ivNoData.imageTickTint(tenantInfoData?.brandingInfo?.primaryColor)
        binding!!.noData.ivNoDataBg.imageTickTint(tenantInfoData?.brandingInfo?.primaryColor)
        setData()
    }
    /**
     * This method contains set data
     *
     */
    private fun setData() {
        binding?.apply {
            progress.progress = viewModel.budgetGroupInfoItem.getBudgetProgressValue().roundToInt()
            progress.setIndicatorColor(Color.parseColor(viewModel.budgetGroupInfoItem.colorCode))
            tvBackground.backgroundTintList= ColorStateList.valueOf(Color.parseColor(viewModel.budgetGroupInfoItem.colorCode))
            tvCategoryPrice.text = viewModel.budgetGroupInfoItem.unusedBudget.formatCurrency(viewModel.budgetGroupInfoItem.currencySymbol?:"")
            tvTotalBudget.text = viewModel.budgetGroupInfoItem.allocatedBudget.formatCurrency(viewModel.budgetGroupInfoItem.currencySymbol?:"")
            tvRemainingBudget.text = viewModel.budgetGroupInfoItem.unusedBudget.formatCurrency(viewModel.budgetGroupInfoItem.currencySymbol?:"")
            tvCategory.text=viewModel.budgetGroupInfoItem.categoryName
            tvBudgetName.text = viewModel.budgetGroupInfoItem.categoryName?.capitalized()
            tvCalendar.text=viewModel.categoryDate.displayDate(SERVER_FORMAT, SimpleDateFormat("MMM yyyy", if (BaseApplication.languageSharedPreference.getLanguagePref(
                    EasyPref.CURRENT_LANGUAGE, "").equals("de", true)) Locale.GERMAN else Locale.ENGLISH))
            if (viewModel.budgetGroupInfoItem.categoryIcon != null) {
                ivCategory.setColorFilter(requireActivity().getColorFromAttr(R.attr.colorTextView))
                ivCategory.loadImageIcons(requireActivity(), viewModel.budgetGroupInfoItem.categoryIcon)
            }
        }
    }

    /**
     * This method contains code for all the clickListener in our app
     *
     */
    override fun setClickListener() {
        binding?.apply {
            tvCalendar.clickWithDebounce {
                val pd = de.fast2work.mobility.utility.customview.MonthYearPickerDialog(viewModel.categoryDate.displayDate(SERVER_FORMAT, MM_FORMAT).toInt(), viewModel.categoryDate.displayDate(SERVER_FORMAT, YYYY_FORMAT).toInt())
                pd.setListener { _, p1, p2, _ ->
                    viewModel.categoryDate = "$p1-${String.format("%02d", p2)}-01"
                    //showToast("$p1-${String.format("%02d", p2)}-01")
                    viewModel.callCategoryInfoApi(viewModel.categoryDate)
                    viewModel.calInvoiceListApi(viewModel.categoryDate)
                    binding!!.tvCalendar.text = viewModel.categoryDate.displayDate(SERVER_FORMAT, SimpleDateFormat("MMM yyyy", if (BaseApplication.languageSharedPreference.getLanguagePref(EasyPref.CURRENT_LANGUAGE, "").equals("de", true)) Locale.GERMAN else Locale.ENGLISH))
                }
                pd.show(parentFragmentManager, "MonthYearPickerDialog")
            }

            tvAllInvoice.clickWithDebounce {
//            switchTab(INDEX_INVOICE)
                val bundle = Bundle()
                bundle.putString(BUDGET_GROUP_ID, viewModel.budgetGopId)
                bundle.putInt(CATEGORY_ID, viewModel.categoryId)
                bundle.putString(PAY_PERIOD_MONTH, "")
//                bundle.putString(PAY_PERIOD_MONTH, viewModel.categoryDate)
                bundle.putBoolean(IS_FROM_CATEGORY, true)
                BaseApplication.sharedPreference.setPref(IS_FROM_CATEGORY, true)
                switchTab(1, bundle)
//            pushFragment(InvoiceFragment.newInstance(viewModel.budgetGopId, viewModel.categoryId, viewModel.categoryDate))
            }
            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing=false
                setCalenderDate()
                viewModel.callCategoryInfoApi(viewModel.categoryDate)
                viewModel.calInvoiceListApi(viewModel.categoryDate)
            }
        }

    }
    /**
     * This method contains setCalenderDate
     *
     */
    private fun setCalenderDate() {
        val cal= Calendar.getInstance()
        val year= cal[Calendar.YEAR]
        val month= cal[Calendar.MONTH]
        viewModel.categoryDate=("$year-0${month+1}-01")
        binding!!.tvCalendar.text=viewModel.categoryDate.displayDate(SERVER_FORMAT,SimpleDateFormat("MMM yyyy", if (BaseApplication.languageSharedPreference.getLanguagePref(EasyPref.CURRENT_LANGUAGE, "").equals("de", true)) Locale.GERMAN else Locale.ENGLISH))
    }

    private fun setToolbar() {
        binding!!.toolbar.let {
            overrideToolbar(it, ToolbarConfig().apply {
                showBackButton = true
                showWhiteBg = true
                centerTitle = getString(R.string.category_details)
                showNotificationIcon = false
                showViewLine = true
            })

            it.ivBack.clickWithDebounce {
                popFragment()
            }
            it.ivNotification.clickWithDebounce {
                pushFragment(NotificationFragment())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        hideTabs()
        EventBus.getDefault().register(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerView() {
        binding?.apply {
            invoiceAdapter = InvoiceAdapter(requireContext(), viewModel.invoiceList, true) { view, model, position ->
                when (view.id) {
                    R.id.clMain -> {
                        pushFragment(InvoiceDetailsFragment.newInstance((model.invoiceId ?: 0).toBlankString()))
                    }
                }
            }
            rvInvoice.layoutManager = LinearLayoutManager(requireContext())
            rvInvoice.adapter = invoiceAdapter
        }
    }

    /**
     * Notification count update
     *
     * @param updateNotificationCount
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun notificationCountUpdate(updateNotificationCount: UpdateNotificationCount) {
        /*if ( updateNotificationCount.pushNotificationCount!! > 0) {
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