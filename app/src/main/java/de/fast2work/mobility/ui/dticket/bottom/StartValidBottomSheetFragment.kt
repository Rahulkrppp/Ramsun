package de.fast2work.mobility.ui.dticket.bottom


import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.fast2work.mobility.R
import de.fast2work.mobility.databinding.PreferredLangFragmentBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBottomSheetDialogFragment
import de.fast2work.mobility.ui.dticket.adapter.StartValidAdapter
import de.fast2work.mobility.ui.profile.bottom.CountryCodeViewModel
import de.fast2work.mobility.utility.customview.DividerItemDecorator
import de.fast2work.mobility.utility.extension.SCREEN_HEIGHT


class StartValidBottomSheetFragment :
    BaseVMBottomSheetDialogFragment<PreferredLangFragmentBinding, CountryCodeViewModel>(
        CountryCodeViewModel::class.java
    ) {

    private var startValidAdapter: StartValidAdapter? = null
    var sendClickListener: (model: StartDateModel) -> Unit = {}
    private var startDateModelList: ArrayList<StartDateModel> = arrayListOf()
    var timeSelect = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PreferredLangFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        const val PARAM_TIME = "time"

        fun newInstance(preferredLanguageData: String = "") =
            StartValidBottomSheetFragment().apply {
                this.arguments = Bundle().apply {
                    putString(PARAM_TIME, preferredLanguageData)

                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme2)
    }

    override fun onStart() {
        super.onStart()
        setDialog()
    }

    private fun setDialog() {
        if (dialog != null) {
            val height = (SCREEN_HEIGHT * 0.40).toInt()
            dialog?.window?.setGravity(Gravity.BOTTOM)
            if (BaseApplication.themeValue == Configuration.UI_MODE_NIGHT_YES) {
                dialog?.window?.setBackgroundDrawableResource(R.color.color_primary_dark_text_80)
            } else {
                dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            }
            dialog?.setCanceledOnTouchOutside(
                arguments?.getBoolean(
                    "canceledOnTouchOutside",
                    true
                )!!
            )
            val bottomSheet =
                dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet!!).peekHeight = height
            BottomSheetBehavior.from(bottomSheet).peekHeight = height
            val layoutParams = bottomSheet.layoutParams
            if (layoutParams != null) {
                layoutParams.height = (SCREEN_HEIGHT * 0.40).toInt()
            }
            bottomSheet.layoutParams = layoutParams
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
            val behavior: BottomSheetBehavior<View> = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState > BottomSheetBehavior.STATE_DRAGGING) bottomSheet.post {
                        behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post {
            val bottomSheet =
                (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from<View>(bottomSheet!!).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        initComponent()

    }

    private fun initComponent() {
        timeSelect = requireArguments().getString(PARAM_TIME, "")
        initRecyclerView()
        binding.tvTitle.text=getString(R.string.select_start_of_validity)
        startDateModelList.add(StartDateModel(getString(R.string.immediately)))
        startDateModelList.add(StartDateModel("Next month"))
       // startDateModelList.add(StartDateModel("In 48 hours"))
        startDateModelList.forEachIndexed { index, startDateModel ->
            if (timeSelect == startDateModel.time) {
                startDateModel.isSelected = true
            }
        }
        startValidAdapter?.notifyDataSetChanged()
    }

    private fun initRecyclerView() {
        startValidAdapter =
            StartValidAdapter(requireContext(), startDateModelList) { view, model, position ->
                when (view.id) {
                    R.id.cl_main_code -> {
                        model.isSelected = true
                        sendClickListener(model)
                        Log.e("TAG", "initMyConnectionRecyclerView=======: $model")
                        dismiss()
                    }
                }
            }
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        val dividerItemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecorator(ContextCompat.getDrawable(requireActivity(), R.drawable.divider)!!)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        binding.recyclerView.adapter = startValidAdapter

    }
}