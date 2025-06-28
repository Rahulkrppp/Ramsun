package de.fast2work.mobility.ui.upload.step

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.SelectCategoryDataItem
import de.fast2work.mobility.databinding.FragmentUploadInvoiceStepTwoBinding
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.upload.UploadInvoiceViewModel
import de.fast2work.mobility.ui.upload.step.adapter.SelectCategoryAdapter
import de.fast2work.mobility.utility.extension.imageTickTint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Use the [UploadInvoiceStepTwoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UploadInvoiceStepTwoFragment : BaseVMBindingFragment<FragmentUploadInvoiceStepTwoBinding, CommonUploadInvoiceModel>(CommonUploadInvoiceModel::class.java) {

    lateinit var parentFragmentViewModel: UploadInvoiceViewModel
    var selectCategoryAdapter:SelectCategoryAdapter?=null
    private var searchJob: Job? = null
    private val coroutineScope = lifecycle.coroutineScope

    companion object{
        const val IS_LOADED = "isLoaded"
        fun newInstance(isLoaded : Boolean = false) = UploadInvoiceStepTwoFragment().apply{
            this.arguments = Bundle().apply {
                this.putBoolean(IS_LOADED, isLoaded)
            }
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return generateBinding(FragmentUploadInvoiceStepTwoBinding.inflate(inflater), container)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        parentFragmentViewModel = parentFragment?.let {
            ViewModelProvider(it)[UploadInvoiceViewModel::class.java]
        }!!
        super.onCreate(savedInstanceState)
    }
    override fun attachObservers() {
        parentFragmentViewModel.categoryCategorySubCategoryListLiveData.observe(this) {
            if (it.data!=null && it.data?.size!! > 0){
                binding!!.rvCategory.isVisible = true
                binding!!.clNoData.isVisible = false
                parentFragmentViewModel.selectCategoryDataItemArraylist.clear()
                parentFragmentViewModel.selectCategoryDataItemArraylist.addAll(it.data as ArrayList<SelectCategoryDataItem>)
                selectCategoryAdapter?.notifyDataSetChanged()

            }else {
                binding!!.rvCategory.isVisible = false
                binding!!.clNoData.isVisible = true
            }
        }
    }

    override fun onStart() {
        super.onStart()
        hideTabs()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isFirstTimeLoad || !requireArguments().getBoolean(IS_LOADED)) {
            if (parentFragmentViewModel.isFirstTimeLoad) {
                parentFragmentViewModel.callCategoryCategorySubCategoryListApi()
                parentFragmentViewModel.isFirstTimeLoad=false
            }
            initRecyclerView()
        }
    }

    override fun initComponents() {
        binding!!.noData.ivNoData.imageTickTint(tenantInfoData?.brandingInfo?.primaryColor)
        binding!!.noData.ivNoDataBg.imageTickTint(tenantInfoData?.brandingInfo?.primaryColor)
    }

    override fun setClickListener() {

        binding?.apply {
            ivSearch.clickWithDebounce {
                showSearchBar()
            }
            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing=false
                parentFragmentViewModel.addInvoice.categoryId = 0
                parentFragmentViewModel.addInvoice.subCategoryId = 0
                parentFragmentViewModel.callCategoryCategorySubCategoryListApi()
                initRecyclerView()
                telSearch.setText("")
            }
            ivClear.clickWithDebounce {
                if (telSearch.text!!.isEmpty()){
                    hideSearchBar()
                }else{
                    telSearch.setText("")
                }
            }
            telSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //handler.removeCallbacks(runnable)
                }

                override fun afterTextChanged(s: Editable?) {
                   // lastTextEdit = System.currentTimeMillis()
                    //handler.postDelayed(runnable, delay)
                    if (isFirstTimeLoad) {
                        searchJob?.cancel()
                        if (s!!.length >= 3) {
                            searchJob = coroutineScope.launch {
                                delay(500L)
                                selectCategoryAdapter?.filter?.filter(s)
                                Log.e("", "afterTextChanged:${parentFragmentViewModel.selectCategoryDataItemArraylist.size} ", )
                                delay(500L)
                                if (selectCategoryAdapter?.itemCount!! ==0){
                                    rvCategory.isVisible = false
                                    clNoData.isVisible = true
                                    selectCategoryAdapter?.notifyDataSetChanged()
                                }else{
                                    rvCategory.isVisible = true
                                    clNoData.isVisible = false
                                    selectCategoryAdapter?.notifyDataSetChanged()
                                }
                            }
                        } else if (s.isEmpty()) {
                            searchJob = coroutineScope.launch {
                                selectCategoryAdapter?.filter?.filter(s)
                                delay(500L)
                                if (selectCategoryAdapter?.itemCount!! ==0){
                                    rvCategory.isVisible = false
                                    clNoData.isVisible = true
                                    selectCategoryAdapter?.notifyDataSetChanged()
                                }else{
                                    rvCategory.isVisible = true
                                    clNoData.isVisible = false
                                    selectCategoryAdapter?.notifyDataSetChanged()
                                }
                            }
                        }

                        /*if (selectCategoryAdapter?.itemCount!! <=1){
                            rvCategory.isVisible = false
                            clNoData.isVisible = true
                            selectCategoryAdapter?.notifyDataSetChanged()
                        }else{
                            rvCategory.isVisible = true
                            clNoData.isVisible = false
                            selectCategoryAdapter?.notifyDataSetChanged()
                        }*/
                    }
                }

            })
        }
    }

    private fun hideSearchBar() {
        binding?.apply {
            tvSelectCategory.visibility=View.VISIBLE
            ivSearch.visibility=View.VISIBLE
            tlSearch.visibility=View.INVISIBLE
            ivClear.isVisible=false
        }
    }

    private fun showSearchBar() {
        binding?.apply {
            tlSearch.visibility=View.VISIBLE
            ivClear.visibility=View.VISIBLE
            tvSelectCategory.visibility=View.INVISIBLE
            ivSearch.visibility=View.INVISIBLE

            telSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,0, 0)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerView() {
        binding?.apply {
            selectCategoryAdapter = SelectCategoryAdapter(tenantInfoData?.brandingInfo?.secondaryColor,parentFragmentViewModel.addInvoice.categoryId?.toInt(),
                parentFragmentViewModel.addInvoice.subCategoryId,requireContext(), parentFragmentViewModel.selectCategoryDataItemArraylist,
                parentFragmentViewModel.addInvoice.postion) {
                    view, model,
                    position,
                    subCategoryId,
                ->
                when (view.id) {
                    R.id.rg_expense ->{
                        parentFragmentViewModel.addInvoice.postion=position
                        parentFragmentViewModel.addInvoice.categoryId = model.categoryId
                        parentFragmentViewModel.addInvoice.subCategoryId = subCategoryId
                        parentFragmentViewModel.addInvoice.benefitCode = model.benefitCode
                        parentFragmentViewModel.addInvoice.benefitId = model.benefitId
                    }
                }
            }
            rvCategory.layoutManager = LinearLayoutManager(activity)
            rvCategory.adapter = selectCategoryAdapter
        }
    }
    fun  hideLayout(){
        binding!!.clMainStepTwo.isVisible=false
    }
}