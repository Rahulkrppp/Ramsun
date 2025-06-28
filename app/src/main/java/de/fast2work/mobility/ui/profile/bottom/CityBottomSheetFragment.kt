package de.fast2work.mobility.ui.profile.bottom

import android.os.Bundle
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
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.CityResponse
import de.fast2work.mobility.databinding.FragmentCountryCodeBottomSheetBinding
import de.fast2work.mobility.ui.profile.ProfileViewModel
import de.fast2work.mobility.ui.profile.adapter.CityAdapter
import de.fast2work.mobility.utility.customview.DividerItemDecorator
import de.fast2work.mobility.utility.extension.SCREEN_HEIGHT
import de.fast2work.mobility.utility.extension.clickWithDebounce
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.util.IConstants
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * CountryCode user selection bottom sheet fragment
 *
 * @constructor Create empty CountryCode user selection bottom sheet fragment
 */
@AndroidEntryPoint
class CityBottomSheetFragment(val viewModel: ProfileViewModel) : BottomSheetDialogFragment() {
    lateinit var binding: FragmentCountryCodeBottomSheetBinding
    private val coroutineScope = lifecycle.coroutineScope
    private var searchJob: Job? = null
    var cityAdapter: CityAdapter? = null
    var cityList: ArrayList<CityResponse.City> = arrayListOf()
    var sendClickListener: (model: CityResponse.City) -> Unit = {}
    var countryClickListener: (country: String) -> Unit = {}

    //    var selectedCityId = 0
    var isCity = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCountryCodeBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme2)
    }

    companion object {
        const val CITY_ID = "city_id"
        const val IS_CITY = "is_city"

        fun newInstance(viewModel: ProfileViewModel, cityId: Int, isCity: Boolean) = CityBottomSheetFragment(viewModel).apply {
            this.arguments = Bundle().apply {
                this.putInt(CITY_ID, cityId)
                this.putBoolean(IS_CITY, isCity)
            }
        }

    }

    override fun onStart() {
        super.onStart()
        setDialog()
    }

    private fun setDialog() {
        if (dialog != null) {
            val height = if (isCity) {
                (SCREEN_HEIGHT * 0.65).toInt()
            } else {
                (SCREEN_HEIGHT * 0.20).toInt()
            }
            dialog?.window?.setGravity(Gravity.BOTTOM)
            dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog?.setCanceledOnTouchOutside(arguments?.getBoolean("canceledOnTouchOutside", true)!!)
            val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet!!).peekHeight = height
            BottomSheetBehavior.from(bottomSheet).peekHeight = height
            val layoutParams = bottomSheet.layoutParams
            if (layoutParams != null) {
                layoutParams.height =if (isCity) { (SCREEN_HEIGHT * 0.65).toInt() } else{ (SCREEN_HEIGHT * 0.20).toInt()}
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
//        selectedCityId  = arguments?.getInt(CITY_ID)?:0
        isCity = arguments?.getBoolean(IS_CITY) ?: false
        if (isCity) {
            binding.grpCity.visibility = View.VISIBLE
            binding.layoutCountry.clMainCode.visibility = View.GONE
            binding.tvTitle.text = "Select City"
            initRecyclerView()
            viewModel.callCityListApi(pageNo = 1, limit = IConstants.PAGE_LIMIT_20, keyword = "")
            initComponent()
            setListeners()
        } else {
            binding.grpCity.visibility = View.GONE
            binding.layoutCountry.clMainCode.visibility = View.VISIBLE
            binding.tvTitle.text = getString(R.string.select_country)
            binding.layoutCountry.apply {
                ivCountry.visibility = View.GONE
                tvCountryName.text = getString(R.string.country_germany)
                tvCountryCode.visibility = View.GONE
                clMainCode.clickWithDebounce {
                    countryClickListener(tvCountryName.text.toBlankString())
                    dismiss()
                }
            }
        }
    }

    private fun initComponent() {

        viewModel.cityLiveData.observe(this) {
            cityList.clear()
            cityList.addAll(it.data?.data as ArrayList<CityResponse.City>)
            cityAdapter?.notifyDataSetChanged()
        }
    }

    private fun setListeners() {
        binding.ivClear.clickWithDebounce {
            binding.telSearch.setText("")
            viewModel.callCityListApi(pageNo = 1, limit = IConstants.PAGE_LIMIT_20, keyword = "")
        }
        binding.telSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toBlankString().isEmpty()) {
                    binding.telSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_search, 0)
                    binding.ivClear.isVisible = false
                } else {
                    binding.ivClear.isVisible = true
                    binding.telSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                if (s!!.length >= 3) {
                    searchJob = coroutineScope.launch {
                        delay(500L)
//                        cityAdapter?.filter?.filter(s)
                        viewModel.callCityListApi(pageNo = 1, limit = IConstants.PAGE_LIMIT_20, keyword = s.toBlankString())
                    }
                } else if (s.isEmpty()) {
                    searchJob = coroutineScope.launch {
//                        cityAdapter?.filter?.filter(s)
                        viewModel.callCityListApi(pageNo = 1, limit = IConstants.PAGE_LIMIT_20, keyword = "")
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

    private fun initRecyclerView() {
        cityAdapter = CityAdapter(requireContext(), cityList) { view, model, position ->
            when (view.id) {
                R.id.cl_main_code -> {
                    sendClickListener(model)
                    Log.e("TAG", "initMyConnectionRecyclerView=======: $model")
                    dismiss()
                }
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        val dividerItemDecoration: ItemDecoration = DividerItemDecorator(ContextCompat.getDrawable(requireActivity(), R.drawable.divider)!!)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
        binding.recyclerView.adapter = cityAdapter

    }

}