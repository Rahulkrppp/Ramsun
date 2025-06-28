package de.fast2work.mobility.ui.co2.bottom

import android.content.res.Configuration
import android.os.Bundle
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
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.ItemTransportDetails
import de.fast2work.mobility.databinding.FragmentCo2ModeOfTransportBottomSheetBinding
import de.fast2work.mobility.ui.co2.CalculateCo2ViewModel
import de.fast2work.mobility.ui.co2.adapter.TransportDetailsAdapter
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.utility.customview.DividerItemDecorator
import de.fast2work.mobility.utility.extension.SCREEN_HEIGHT
import de.fast2work.mobility.utility.extension.parcelable
import de.fast2work.mobility.utility.extension.parcelableArrayList

/**
 * Fragment used for TransportDetailBottomSheetFragment
 * */
class TransportDetailBottomSheetFragment() : BottomSheetDialogFragment(){

    lateinit var binding:FragmentCo2ModeOfTransportBottomSheetBinding
    private var transportDetailsAdapter: TransportDetailsAdapter? = null
    var sendClickListener: (model: ItemTransportDetails) -> Unit = {}
    private var transportDetailsList:ArrayList<ItemTransportDetails> = arrayListOf()
//    var modelTransportDetails: ItemTransportDetails?=null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCo2ModeOfTransportBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme2)
    }

    companion object {
        const val PARAM_TRANSPORT_DETAILS = "transportDetailsList"
//        const val PARAM_MODEL_TRANSPORT_DETAILS = "ModelTransport"

        fun newInstance(transportDetailsList:ArrayList<ItemTransportDetails>/*,model: ItemTransportDetails?*/) = TransportDetailBottomSheetFragment().apply {
            this.arguments = Bundle().apply {
                putParcelableArrayList(PARAM_TRANSPORT_DETAILS, transportDetailsList)
//                putParcelable(PARAM_MODEL_TRANSPORT_DETAILS,model)
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

    private fun setListeners() {

    }

    private fun initComponent() {
//        if (arguments!=null) {
//            modelTransportDetails = arguments?.parcelable(PARAM_MODEL_TRANSPORT_DETAILS)
//        }
        binding.tvTitle.text= getString(R.string.select_transport_detail)
        initRecyclerView()
        if (arguments!=null) {
            transportDetailsList.clear()
            transportDetailsList.addAll(arguments?.parcelableArrayList<ItemTransportDetails>(PARAM_TRANSPORT_DETAILS)!!)
            transportDetailsAdapter?.notifyDataSetChanged()
        }
    }
    private fun initRecyclerView() {
        transportDetailsAdapter = TransportDetailsAdapter(requireContext(), transportDetailsList) { view, model, position ->
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
        binding.recyclerView.adapter = transportDetailsAdapter

    }
}