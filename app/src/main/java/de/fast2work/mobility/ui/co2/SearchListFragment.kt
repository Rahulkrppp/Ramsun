package de.fast2work.mobility.ui.co2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.R
import de.fast2work.mobility.databinding.FragmentSearchListBinding
import de.fast2work.mobility.ui.co2.adapter.SearchAdapter
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.utility.customview.DividerItemDecorator
import de.fast2work.mobility.utility.extension.imageTickTint1
import de.fast2work.mobility.utility.extension.showKeyboard
import de.fast2work.mobility.utility.extension.toBlankString
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchListFragment  : BaseVMBindingFragment<FragmentSearchListBinding, CalculateCo2ViewModel>(
    CalculateCo2ViewModel::class.java) {

    private var searchAdapter:SearchAdapter? = null
    private var searchJob: Job? = null
    private val coroutineScope = lifecycle.coroutineScope
    var sourceDestination=false
    var isInnerList = false
    companion object {

        const val PARAM_SEND_MODEL="sendData"
        const val PARAM_RESULT="RESULT_OK"
        const val PARAM_SET_SOURCE="set"
        const val PARAM_SET_INNER_SOURCE="set_inner"

        fun newInstance( source:Boolean=false
        ) = SearchListFragment().apply {
            this.arguments = Bundle().apply {
                this.putBoolean(PARAM_SET_SOURCE,source)
            }
        }

        fun newInstance(innerList:Boolean,source:Boolean=false
        ) = SearchListFragment().apply {
            this.arguments = Bundle().apply {
                this.putBoolean(PARAM_SET_INNER_SOURCE,innerList)
                this.putBoolean(PARAM_SET_SOURCE,source)
            }
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return generateBinding(FragmentSearchListBinding.inflate(inflater), container)
    }

    override fun attachObservers() {
        viewModel.sourceDestinationLiveData.observe(this){
//            binding!!.telSearch.isFocusableInTouchMode = true
//            binding!!.telSearch.requestFocus()
//            binding!!.telSearch.showKeyboard()
            if (it.data!!.geocode_ng!!.state==200) {
                if (it.data.geocode_ng!!.results!=null) {
                    viewModel.searchList.clear()
                    binding!!.recyclerView.isVisible=true
                    binding!!.clNoData.isVisible=false
                    viewModel.searchList.addAll(it.data.geocode_ng.results)
                    searchAdapter?.notifyDataSetChanged()
                }
            }else{
                viewModel.searchList.clear()
                searchAdapter?.notifyDataSetChanged()
                //showErrorMessage("Something went wrong")
            }
            if (it.data.geocode_ng!!.state==404) {
                binding!!.recyclerView.isVisible=false
                binding!!.clNoData.isVisible=true
                }
//            Handler().postDelayed({
//                if (it.data!!.geocode_ng!!.state==404) {
//                    binding!!.telSearch.showKeyboard()
//                }else{
//                        binding!!.telSearch.showKeyboard()
//                }
//            },600)

        }
    }

    override fun initComponents() {
        sourceDestination= arguments?.getBoolean(PARAM_SET_SOURCE)?:false
        isInnerList = arguments?.getBoolean(PARAM_SET_INNER_SOURCE)?:false
        initRecyclerView()
//        viewModel.keyboardShowHide=true
//        if (viewModel.keyboardShowHide){
//            binding!!.telSearch.showKeyboard()
//        }
        binding!!.telSearch.showKeyboard()
        binding!!.ivNoData.imageTickTint1(tenantInfoData?.brandingInfo?.primaryColor)
        binding!!.telSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0)
    }

    override fun setClickListener() {

        binding!!.ivClear.clickWithDebounce {
            binding!!.telSearch.setText("")
            viewModel.callSourceDestinationApi("")
        }

        binding?.telSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toBlankString().isEmpty()) {
                    binding!!.telSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0)
                    binding!!.ivClear.isVisible = false
                    //handler.removeCallbacks(runnable)
                   // viewModel.callSourceDestinationApi("")
                } else {
                    binding!!.ivClear.isVisible = true
                    binding!!.telSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                }
//                if (viewModel.keyboardShowHide){
//                    binding!!.telSearch.showKeyboard()
//                }
//                 handler.removeCallbacks(runnable)
            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length >= 3) {
                    searchJob = coroutineScope.launch {
                        delay(2000)
                        viewModel.callSourceDestinationApi(s.toBlankString())
                    }
                } else if (s.isEmpty()) {
                    searchJob = coroutineScope.launch {
                        delay(2000)
                        viewModel.callSourceDestinationApi("")
                    }
                }
            }

            override fun toString(): String {
                return "`<no name provided>`()"
            }

        })
        binding!!.tvCancel.clickWithDebounce {
            popFragment()
        }
    }
    private fun initRecyclerView() {
//        if (viewModel.keyboardShowHide){
//            binding!!.telSearch.showKeyboard()
//        }
        searchAdapter = SearchAdapter(requireContext(), viewModel.searchList) { view, model, position ->
            when (view.id) {
                R.id.cl_main_search -> {
                    val returnBundle = Bundle()
                    returnBundle.putBoolean(PARAM_SET_SOURCE,sourceDestination)
                    returnBundle.putParcelable(PARAM_SEND_MODEL, model)
                    returnBundle.putBoolean(PARAM_SET_INNER_SOURCE,isInnerList)
                    setFragmentResult(PARAM_RESULT, returnBundle)
                    popFragment()

                }
            }
        }
        binding!!.recyclerView.layoutManager = LinearLayoutManager(activity)
        val dividerItemDecoration: RecyclerView.ItemDecoration = DividerItemDecorator(ContextCompat.getDrawable(requireActivity(), R.drawable.divider)!!)
        binding!!.recyclerView.addItemDecoration(dividerItemDecoration)
        binding!!.recyclerView.adapter = searchAdapter

    }

    override fun onStart() {
        super.onStart()
        hideTabs()
    }

}