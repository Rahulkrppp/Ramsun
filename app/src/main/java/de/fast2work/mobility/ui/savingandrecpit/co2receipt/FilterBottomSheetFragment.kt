package de.fast2work.mobility.ui.savingandrecpit.co2receipt

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.fast2work.mobility.R
import de.fast2work.mobility.data.model.FilterModel
import de.fast2work.mobility.data.response.ItemTransportDetails
import de.fast2work.mobility.data.response.UniqueVehiclesRes
import de.fast2work.mobility.databinding.PreferredLangFragmentBinding
import de.fast2work.mobility.ui.co2.bottom.TransportDetailBottomSheetFragment.Companion.PARAM_TRANSPORT_DETAILS
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBottomSheetDialogFragment
import de.fast2work.mobility.ui.profile.bottom.CountryCodeViewModel
import de.fast2work.mobility.ui.savingandrecpit.adapter.FilterAdapter
import de.fast2work.mobility.utility.customview.DividerItemDecorator
import de.fast2work.mobility.utility.extension.SCREEN_HEIGHT
import de.fast2work.mobility.utility.extension.parcelableArrayList


class FilterBottomSheetFragment : BaseVMBottomSheetDialogFragment<PreferredLangFragmentBinding, CountryCodeViewModel>(CountryCodeViewModel::class.java) {


    private var filterAdapter: FilterAdapter? = null
     var sendClickListener: (model: FilterModel) -> Unit = {}
    private var list: ArrayList<FilterModel> = arrayListOf()
    private var isAllType = false
    private var isSelected = false
    private var selectedName = ""
    var uniqueVehiclesList:ArrayList<UniqueVehiclesRes> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = PreferredLangFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme2)
    }

    companion object {
        const val PARAM_TITTLE = "title"
        const val PARAM_SELECTED = "isSelected"
        const val PARAM_ALL_TYPE = "allType"
        const val PARAM_ALL_MODULE_LIST = "allModuleList"
        fun newInstance(isAllType: Boolean = false, title: String, selectedName:String = "",uniqueVehiclesList:ArrayList<UniqueVehiclesRes> = arrayListOf()) = FilterBottomSheetFragment().apply {
            this.arguments = Bundle().apply {
                putBoolean(PARAM_ALL_TYPE, isAllType)
                putString(PARAM_SELECTED, selectedName)
                putString(PARAM_TITTLE, title)
                putParcelableArrayList(PARAM_ALL_MODULE_LIST,uniqueVehiclesList)
            }
        }

    }

    override fun onStart() {
        super.onStart()
        setDialog()
    }

    private fun setDialog() {
        if (dialog != null) {
            val height = (SCREEN_HEIGHT * 0.75).toInt()
            dialog?.window?.setGravity(Gravity.BOTTOM)
            if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
                dialog?.window?.setBackgroundDrawableResource(R.color.color_primary_dark_text_80)
            }else{
                dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            }
            dialog?.setCanceledOnTouchOutside(arguments?.getBoolean("canceledOnTouchOutside", true)!!)
            val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet!!).peekHeight = height
            BottomSheetBehavior.from(bottomSheet).peekHeight = height
            val layoutParams = bottomSheet.layoutParams
            if (layoutParams != null) {
                layoutParams.height = (SCREEN_HEIGHT * 0.75).toInt()
            }
            bottomSheet.layoutParams = layoutParams
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
            val behavior: BottomSheetBehavior<View> = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState > BottomSheetBehavior.STATE_DRAGGING) bottomSheet.post { behavior.state = BottomSheetBehavior.STATE_EXPANDED }
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post {
            val bottomSheet = (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from<View>(bottomSheet!!).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        initComponent()
        setListeners()
    }

    private fun initComponent() {
        binding.clSearch.isVisible = false
        binding.recyclerView.isNestedScrollingEnabled = false
        viewModel.tittle = requireArguments().getString(PARAM_TITTLE, "")
        isAllType = requireArguments().getBoolean(PARAM_ALL_TYPE, false)
        selectedName = requireArguments().getString(PARAM_SELECTED, "")
        uniqueVehiclesList.clear()
        if (arguments!=null) {
            arguments?.parcelableArrayList<UniqueVehiclesRes>(
                PARAM_ALL_MODULE_LIST
            )?.let {
                uniqueVehiclesList.addAll(
                    it
                )
            }
        }

        initRecyclerView()
        setDataList()
    }

    private fun setDataList() {
        binding.tvTitle.text = viewModel.tittle
        if (isAllType) {
            list.add(FilterModel( getString(R.string.all_types),"all",isSelected))
            list.add(FilterModel( getString(R.string.private_), "private",isSelected))
            list.add(FilterModel( getString(R.string.business), "business",isSelected))

            list.forEachIndexed { _, filterModel ->
                filterModel.isSelected = false
                if (filterModel.key == selectedName){
                    filterModel.isSelected = true
                }
            }
        } else {
            list.add(FilterModel(getString(R.string.all_modes),"all",isSelected))
            uniqueVehiclesList.forEachIndexed { index, uniqueVehiclesRes ->
                list.add(FilterModel(uniqueVehiclesRes.vehicleTypeLabel.toString(),uniqueVehiclesRes.vehicleType.toString(),isSelected))
            }

            list.forEachIndexed { _, filterModel ->
                filterModel.isSelected = false
                if (filterModel.key == selectedName){
                    filterModel.isSelected = true
                }
            }
        }
        filterAdapter?.notifyDataSetChanged()
    }

    private fun setListeners() {

    }

    private fun initRecyclerView() {
        filterAdapter = FilterAdapter(requireContext(), list) { view, model, position ->
            when (view.id) {
                R.id.cl_main_code -> {
                    model.isSelected=true
                    sendClickListener(model)
                    Log.e("TAG", "initMyConnectionRecyclerView=======: $model")
                    dismiss()
                }
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        val dividerItemDecoration: RecyclerView.ItemDecoration = DividerItemDecorator(ContextCompat.getDrawable(requireActivity(), R.drawable.divider)!!)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        binding.recyclerView.adapter = filterAdapter

    }

}