package de.fast2work.mobility.ui.co2.bottom

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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import de.fast2work.mobility.R
import de.fast2work.mobility.data.model.PreferredLanguageModel
import de.fast2work.mobility.data.response.ModeOfTransport
import de.fast2work.mobility.databinding.FragmentCo2ModeOfTransportBottomSheetBinding
import de.fast2work.mobility.ui.co2.CalculateCo2ViewModel
import de.fast2work.mobility.ui.co2.adapter.Co2ModeOfTransportAdapter
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBottomSheetDialogFragment
import de.fast2work.mobility.ui.profile.bottom.PreferredLanguageBottomSheetFragment
import de.fast2work.mobility.utility.customview.DividerItemDecorator
import de.fast2work.mobility.utility.extension.SCREEN_HEIGHT
import de.fast2work.mobility.utility.extension.parcelable
/**
 * Fragment used for Co2ModeOfTransportBottomSheetFragment
 * */
@AndroidEntryPoint
class Co2ModeOfTransportBottomSheetFragment (val viewModel: Co2ModeOfTransportViewModel) : BottomSheetDialogFragment() {
    lateinit var binding:FragmentCo2ModeOfTransportBottomSheetBinding
    private var co2ModeOfTransportAdapter: Co2ModeOfTransportAdapter? = null
    var sendClickListener: (model: ModeOfTransport) -> Unit = {}
    var model: ModeOfTransport?=null
    var transportModeKey=""
    var invoiceScreen=false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCo2ModeOfTransportBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme2)

    }

    companion object {
        const val PARAM_MODEL_MODE_OF_TRAN = "preferredLanguageData"
        const val PARAM_TRANSPORT_MODE = "transportModeKey"
        const val PARAM_INVOICE = "invoice"
        fun newInstance(viewModel: Co2ModeOfTransportViewModel,model:ModeOfTransport?,transportModeKey:String="",invoice:Boolean=false) = Co2ModeOfTransportBottomSheetFragment(viewModel).apply {
            this.arguments = Bundle().apply {
                putParcelable(PARAM_MODEL_MODE_OF_TRAN, model)
                putString(PARAM_TRANSPORT_MODE,transportModeKey)
                putBoolean(PARAM_INVOICE,invoice)
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
           // dialog?.setCanceledOnTouchOutside(arguments?.getBoolean("canceledOnTouchOutside", true)!!)
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
      /*  val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior: BottomSheetBehavior<View> = BottomSheetBehavior.from(bottomSheet!!)
        behavior.isDraggable = false*/
        transportModeKey= arguments?.getString(PARAM_TRANSPORT_MODE,"").toString()
        initComponent()
        setListeners()
        attachObservers()
    }

    private fun setListeners() {

    }
     fun attachObservers() {
         viewModel.modeOfTransportLiveData.observe(this){
             if (it.data!=null){
                 viewModel.modeOfTransportList.clear()
                 viewModel.modeOfTransportList.addAll(it.data!!)
                 viewModel.modeOfTransportList.forEachIndexed { index, modeOfTransport ->
                    // modeOfTransport.isSelected=false
                     if (model?.key==modeOfTransport.key||transportModeKey==modeOfTransport.key){
                         modeOfTransport.isSelected=true
                     }
                 }
                 co2ModeOfTransportAdapter?.notifyDataSetChanged()
             }
         }
    }
    private fun initComponent() {
        if (arguments!=null){
            model=arguments?.parcelable(PARAM_MODEL_MODE_OF_TRAN)
            invoiceScreen= arguments?.getBoolean(PARAM_INVOICE,false) == true
        }
        initRecyclerView()
        if (invoiceScreen){
            viewModel.callModeOfTransportRecpictApi(true)
        }else {
            viewModel.callModeOfTransportApi(true)
        }



    }
    private fun initRecyclerView() {
          co2ModeOfTransportAdapter = Co2ModeOfTransportAdapter(requireContext(), viewModel.modeOfTransportList) { view, model, position ->
            when (view.id) {
                R.id.cl_main_mode_of_transport -> {
                    sendClickListener(model)
                    dismiss()
                }
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        val dividerItemDecoration: RecyclerView.ItemDecoration = DividerItemDecorator(ContextCompat.getDrawable(requireActivity(), R.drawable.divider)!!)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        binding.recyclerView.adapter = co2ModeOfTransportAdapter

    }

}