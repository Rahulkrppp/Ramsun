package de.fast2work.mobility.ui.profile.bottom

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.CountryList
import de.fast2work.mobility.databinding.FragmentCountryCodeBottomSheetBinding
import de.fast2work.mobility.ui.core.BaseVMBottomSheetDialogFragment
import de.fast2work.mobility.ui.profile.adapter.CountryCodeAdapter
import de.fast2work.mobility.utility.customview.DividerItemDecorator
import de.fast2work.mobility.utility.customview.countrypicker.CountryPicker
import de.fast2work.mobility.utility.extension.SCREEN_HEIGHT
import de.fast2work.mobility.utility.extension.clickWithDebounce
import de.fast2work.mobility.utility.extension.toBlankString
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * CountryCode user selection bottom sheet fragment
 *
 * @constructor Create empty CountryCode user selection bottom sheet fragment
 */

class CountryCodeBottomSheetFragment : BaseVMBottomSheetDialogFragment<FragmentCountryCodeBottomSheetBinding, CountryCodeViewModel>(CountryCodeViewModel::class.java) {

    private val coroutineScope = lifecycle.coroutineScope
    private var searchJob: Job? = null
    var userListListener: (list: ArrayList<Int>) -> Unit = {}
    var countryAdapter :CountryCodeAdapter?=null
    var sendClickListener: (model: CountryList) -> Unit = {}
    private var type = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCountryCodeBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme2)
    }

    companion object {

        fun newInstance() = CountryCodeBottomSheetFragment().apply {
            this.arguments = Bundle().apply {

            }
        }

    }

    override fun onStart() {
        super.onStart()
        setDialog()
    }

    private fun setDialog() {
        if (dialog != null) {
            val height = (SCREEN_HEIGHT * 0.65).toInt()
            dialog?.window?.setGravity(Gravity.BOTTOM)
            dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog?.setCanceledOnTouchOutside(arguments?.getBoolean("canceledOnTouchOutside", true)!!)
            val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet!!).peekHeight = height
            BottomSheetBehavior.from(bottomSheet).peekHeight = height
            val layoutParams = bottomSheet.layoutParams
            if (layoutParams != null) {
                layoutParams.height = (SCREEN_HEIGHT * 0.65).toInt()
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
        initRecyclerView()
    }

    private fun setListeners() {
        binding.ivClear.clickWithDebounce {
            binding.telSearch.setText("")
        }
        binding.telSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toBlankString().isEmpty()) {
                    binding.telSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_search, 0)
                    binding.ivClear.isVisible=false
                } else {
                    binding.ivClear.isVisible=true
                    binding.telSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,0, 0)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                if (s!!.length >= 3) {
                    searchJob = coroutineScope.launch {
                        delay(500L)
                        countryAdapter?.filter?.filter(s)
                    }
                } else if (s.isEmpty()) {
                    searchJob = coroutineScope.launch {

                        countryAdapter?.filter?.filter(s)
                    }
                }
            }
        })
        binding.ivClose.clickWithDebounce {
            dismiss()
        }
        binding.telSearch.clickWithDebounce {

        }
    }

    private fun attachObservers() {
    }

    private fun initRecyclerView() {
        countryAdapter = CountryCodeAdapter(requireContext(),  CountryPicker.getCountryListFromJson(requireActivity())) { view, model, position ->
            when (view.id) {
                    R.id.cl_main_code ->{
                        sendClickListener(model)
                        Log.e("TAG", "initMyConnectionRecyclerView=======: $model")
                        dismiss()
                    }
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        val dividerItemDecoration: ItemDecoration = DividerItemDecorator(ContextCompat.getDrawable(requireActivity(), R.drawable.divider)!!)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        //binding.recyclerView.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        binding.recyclerView.adapter = countryAdapter

    }

}