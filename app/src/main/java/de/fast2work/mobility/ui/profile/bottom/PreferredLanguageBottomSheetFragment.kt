package de.fast2work.mobility.ui.profile.bottom

import android.content.res.Configuration
import android.graphics.Color
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
import de.fast2work.mobility.data.model.PreferredLanguageModel
import de.fast2work.mobility.databinding.FragmentCountryCodeBottomSheetBinding
import de.fast2work.mobility.databinding.PreferredLangFragmentBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBottomSheetDialogFragment
import de.fast2work.mobility.ui.profile.adapter.PreferredLanguageAdapter
import de.fast2work.mobility.utility.customview.DividerItemDecorator
import de.fast2work.mobility.utility.extension.SCREEN_HEIGHT
import de.fast2work.mobility.utility.extension.getColorFromAttr


class PreferredLanguageBottomSheetFragment : BaseVMBottomSheetDialogFragment<PreferredLangFragmentBinding, CountryCodeViewModel>(CountryCodeViewModel::class.java) {


    private var preferredLanguageAdapter: PreferredLanguageAdapter? = null
     var sendClickListener: (model: PreferredLanguageModel) -> Unit = {}
    private var preferredLanguageList: ArrayList<PreferredLanguageModel> = arrayListOf()
    private var preferredLanguageData = false
    private var languageData=""
    private var amount=""
    private var isEditSelectedEn=false
    private var isEditSelectedDe=false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = PreferredLangFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme2)
    }

    companion object {
        const val PARAM_LANGUAGE = "preferredLanguageData"
        const val PARAM_TITTLE = "tittle"
        const val PARAM_LANGUAGE_SET_DATA="preferredLanguageSetData"
        const val PARAM_AMOUNT_SET_DATA="Amount"
        fun newInstance(preferredLanguageData: Boolean = false, tittle: String,languageData:String,amount:String="") = PreferredLanguageBottomSheetFragment().apply {
            this.arguments = Bundle().apply {
                putBoolean(PARAM_LANGUAGE, preferredLanguageData)
                putString(PARAM_TITTLE, tittle)
                putString(PARAM_LANGUAGE_SET_DATA, languageData)
                putString(PARAM_AMOUNT_SET_DATA, amount)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setDialog()
    }

    private fun setDialog() {
        if (dialog != null) {
            val height = (SCREEN_HEIGHT * 0.25).toInt()
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
                layoutParams.height = (SCREEN_HEIGHT * 0.25).toInt()
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
        preferredLanguageData = requireArguments().getBoolean(PARAM_LANGUAGE, false)
        languageData=requireArguments().getString(PARAM_LANGUAGE_SET_DATA,"")
        amount=requireArguments().getString(PARAM_AMOUNT_SET_DATA,"")
        initRecyclerView()
        setDataList()
    }

    private fun setDataList() {
        binding.tvTitle.text = viewModel.tittle
        if (preferredLanguageData) {
            if (languageData.isNotEmpty()) {
                    if (languageData == getString(R.string.en)) {
                        isEditSelectedEn=true
                    } else {
                        isEditSelectedDe=true
                    }
            }
            preferredLanguageList.add(PreferredLanguageModel("GB", getString(R.string.english),isEditSelectedEn))
            preferredLanguageList.add(PreferredLanguageModel("DE", getString(R.string.german),isEditSelectedDe))

        } else {
            if (amount.isNotEmpty()) {
                if (amount == getString(R.string.en)) {
                    isEditSelectedEn=true
                } else {
                    isEditSelectedDe=true
                }
            }
            preferredLanguageList.add(PreferredLanguageModel("", getString(R.string.english_100_000_00),isEditSelectedEn))
            preferredLanguageList.add(PreferredLanguageModel("", getString(R.string.german_100_00_00),isEditSelectedDe))
        }
        preferredLanguageAdapter?.notifyDataSetChanged()
    }

    private fun setListeners() {

    }

    private fun initRecyclerView() {
        preferredLanguageAdapter = PreferredLanguageAdapter(requireContext(), preferredLanguageList) { view, model, position ->
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
        binding.recyclerView.adapter = preferredLanguageAdapter

    }

}