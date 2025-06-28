package de.fast2work.mobility.ui.dticket

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.vesputi.mobilitybox_ticketing_android.models.Mobilitybox
import com.vesputi.mobilitybox_ticketing_android.models.MobilityboxCouponCode
import com.vesputi.mobilitybox_ticketing_android.models.MobilityboxTicket
import com.vesputi.mobilitybox_ticketing_android.models.MobilityboxTicketCode
import com.vesputi.mobilitybox_ticketing_android.models.MobilityboxTicketValidity
import com.vesputi.mobilitybox_ticketing_android.views.MobilityboxBottomSheetFragment
import de.fast2work.mobility.R
import de.fast2work.mobility.data.request.DTicketCreatePaymentIntent
import de.fast2work.mobility.data.response.ActiveTicket
import de.fast2work.mobility.data.response.DTicketRes
import de.fast2work.mobility.databinding.FragmentDTicketBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.dticket.adapter.DTicketAdapter
import de.fast2work.mobility.ui.dticket.bottom.StartValidBottomSheetFragment
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.extension.DISPLAY_FORMAT
import de.fast2work.mobility.utility.extension.DISPLAY_FORMAT_D_TICKET_DISPLAY
import de.fast2work.mobility.utility.extension.DISPLAY_FORMAT_D_TICKET_END_DATE
import de.fast2work.mobility.utility.extension.DISPLAY_FORMAT_D_TICKET_START_DATE
import de.fast2work.mobility.utility.extension.DISPLAY_FORMAT_HH_MM
import de.fast2work.mobility.utility.extension.DialogUtil.showDatePickerDialog
import de.fast2work.mobility.utility.extension.FORMAT
import de.fast2work.mobility.utility.extension.SERVER_FORMAT
import de.fast2work.mobility.utility.extension.formatDate
import de.fast2work.mobility.utility.extension.getCurrentDatePlusMonth
import de.fast2work.mobility.utility.extension.getString
import de.fast2work.mobility.utility.extension.getTrimText
import de.fast2work.mobility.utility.extension.hideKeyboard
import de.fast2work.mobility.utility.extension.millisToStringTime
import de.fast2work.mobility.utility.extension.millisToStringTime1
import de.fast2work.mobility.utility.preference.EasyPref
import de.fast2work.mobility.utility.util.IConstantsIcon
import de.fast2work.mobility.utility.util.IConstantsIcon.Companion.ACTIVATED
import de.fast2work.mobility.utility.util.IConstantsIcon.Companion.DISABLE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import java.util.Calendar
import java.util.Date


class DTicketFragment : BaseVMBindingFragment<FragmentDTicketBinding, DTicketViewModel>(
    DTicketViewModel::class.java
) {

    private var dTicketAdapter: DTicketAdapter? = null
    private var dTicketList: ArrayList<DTicketRes> = arrayListOf()
    private var birthDate = ""
    private var myCalendar = Calendar.getInstance()
    private var year: Int = -1
    private var month: Int = -1
    private var day: Int = -1
    var ticketLocal: MobilityboxTicket?=null
    var ticketId = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return generateBinding(FragmentDTicketBinding.inflate(inflater), container)
    }

    override fun attachObservers() {
        viewModel.dTicketLiveData.observe(this) {
            Log.e("", "attachObservers:${it.data} ")

            dTicketList.clear()
            it.data?.let { it1 -> dTicketList.addAll(it1) }
            dTicketAdapter?.notifyDataSetChanged()
        }
        viewModel.dTicketActivatedLiveData.observe(this) {
            Log.e("", "attachObservers:$it ")
            //ticketId= it.ticketId.toString()
            BaseApplication.sharedPreference.setPref(EasyPref.TICKET_ID, it.ticketId.toString())
            Log.e("", "ticketId:${ticketId} ")
            stepFour()
            BaseApplication.sharedPreference.setPref(EasyPref.D_TICKET, IConstantsIcon.ACTIVATED)
        }
        viewModel.dTicketCreatePyamentLiveData.observe(this) {
            it.data?.order_reference_no?.let { it1 ->
                BaseApplication.sharedPreference.setPref(
                    EasyPref.order_reference,
                    it1
                )
            }
            pushFragment(
                WebViewFragment.newInstance(
                    dTicketList[0].productInfo.id,
                    dTicketList[0].productName
                )
            )
        }
    }

    override fun onStart() {
        super.onStart()
        hideTabs()
    }

    override fun initComponents() {
        val cal = Calendar.getInstance().time
        /*val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH]*/


        //viewModel.setDate = ("$year-0${month + 1}-01")
        setThemeForView(binding!!.btnTicket)
        viewModel.clickButtonPurchaseTicket =
            BaseApplication.sharedPreference.getPref(EasyPref.D_TICKET, "")
        //myCalendar.add(Calendar.YEAR, -17)
        year = myCalendar.get(Calendar.YEAR)
        month = myCalendar.get(Calendar.MONTH)
        day = myCalendar.get(Calendar.DAY_OF_MONTH)
        Mobilitybox.setup(requireActivity().getSharedPreferences("AppPref", MODE_PRIVATE))
        when (viewModel.clickButtonPurchaseTicket) {
            DISABLE -> {
                stepOne()
                // pushFragment(StaticPageFragment.newInstance("https://fastlink.themobilitybox.com/TEST-65b9a9/checkout?mobilitybox_product_ids%5B%5D=mobilitybox-product-bf70d2be-70f1-4779-a3ee-8898777b33cf&order_reference=mbm-1733206793&view=fullscreen&skip_successor_selection=true", true,))
            }

            IConstantsIcon.PENDING -> {
                stepThree()
            }

            ACTIVATED -> {
                stepFour()
            }

            IConstantsIcon.cancelled -> {
                stepCancel()
               // stepFive()
            }

        }

        viewModel.callDTicketApi(true)
        MobilityboxTicketCode(
            BaseApplication.sharedPreference.getPrefModel(
                EasyPref.activeTicket,
                ActiveTicket::class.java
            )?.ticketId.toString()
        ).fetchTicket({ ticket ->
            if (ticket.validity() == MobilityboxTicketValidity.VALID) {
                viewModel.startDate = ticket.createdAt.toString().formatDate(DISPLAY_FORMAT_D_TICKET_START_DATE, DISPLAY_FORMAT_D_TICKET_DISPLAY)
                viewModel.endDate = ticket.valid_until.formatDate(DISPLAY_FORMAT_D_TICKET_END_DATE, DISPLAY_FORMAT_D_TICKET_DISPLAY)
                Log.e("", "initComponents: ${viewModel.endDate} =-== ${viewModel.startDate}", )
            }

        }, { error ->

        })

        Handler(Looper.getMainLooper()).postDelayed({
            initBudgetManagementRecyclerView()
        }, 1000)
        viewModel.setDate = Date().time.millisToStringTime(DISPLAY_FORMAT_HH_MM)
        Log.e("", "initComponents888888: ${viewModel.setDate}", )
        setToolbar()

    }

    private fun setToolbar() {
        binding!!.customToolbar.let {
            overrideToolbar(it, ToolbarConfig().apply {
                showBackButton = true
                centerTitle = getString(R.string.ticket_details)
            })

            it.ivBack.clickWithDebounce {
                popFragment()
            }

        }
    }

    override fun setClickListener() {
        binding?.apply {
            btnTicket.clickWithDebounce {
                when (viewModel.clickButtonPurchaseTicket) {
                    DISABLE -> {
                        // pushFragment(StaticPageFragment.newInstance("https://fastlink.themobilitybox.com/TEST-65b9a9/checkout?mobilitybox_product_ids%5B%5D=mobilitybox-product-bf70d2be-70f1-4779-a3ee-8898777b33cf&order_reference=mbm-1733206793&view=fullscreen&skip_successor_selection=true", true,))
                    }

                    "2" -> {
                        stepTwo()
                    }

                    "3" -> {
                        if (isValidOne()) {
                            val param = DTicketCreatePaymentIntent().apply {
                                this.activation_start_datetime = viewModel.setDate
                                this.birth_date =
                                    telDate.getTrimText().formatDate(DISPLAY_FORMAT, SERVER_FORMAT)
                                this.first_name = telFirstName.getTrimText()
                                this.last_name = telLastName.getTrimText()
                                this.german_postal_code = telPostalCode.getTrimText()
                                this.product_id = dTicketList[0].productId
                            }

                            viewModel.callDTicketCreatePaymentIntentApi(param)
                        }
                    }

                    "4" -> {

                    }

                   /* "5" -> {
                        //stepFive()
                    }*/
                }
            }
            telStartTime.clickWithDebounce {
                val dialog = StartValidBottomSheetFragment.newInstance(telStartTime.text.toString())
                dialog.sendClickListener = {
                    telStartTime.setText(it.time)
                    if (it.time == "Immediately") {
                        viewModel.setDate = Date().time.millisToStringTime(DISPLAY_FORMAT_HH_MM)
                        Log.e("", "setClickListener: ${viewModel.setDate}")
                    } else if (it.time == "Next month") {
                        viewModel.setDate=("${getCurrentDatePlusMonth(1).time.millisToStringTime1(FORMAT)}-01 00:00:00")
                        //viewModel.setDate = getHourDate(24)
                        //Log.e("", "setClickListener: ${getCurrentDatePlusMonth(1).time.millisToStringTime(FORMAT)}")
                        //Log.e("", "setClickListener444: ${ viewModel.setDate}")
                    } else {
                       // viewModel.setDate = getHourDate(48)
                    }
                    dialog.dismiss()

                }
                dialog.show(childFragmentManager, "")
            }
            telDate.clickWithDebounce {
                showDateDialog()
            }
            llViewTicket.clickWithDebounce {
                /*MobilityboxTicketCode.fetchTicket(completion: (coupon: MobilityboxTicket) -> (Unit))*/
                CoroutineScope(Dispatchers.Main).launch {
                    MobilityboxTicketCode(
                        BaseApplication.sharedPreference.getPrefModel(
                            EasyPref.activeTicket,
                            ActiveTicket::class.java
                        )?.ticketId.toString()
                    ).fetchTicket({ ticket ->

                        Log.e("", "setClickListener:${ticket} ", )
                        ticketLocal= ticket

                        if (ticket.validity() == MobilityboxTicketValidity.VALID) {
                            val ticketBottomSheet = MobilityboxBottomSheetFragment.newInstance(
                                ticket,
                                BaseApplication.sharedPreference.getPrefModel(
                                    EasyPref.activeTicket,
                                    ActiveTicket::class.java
                                )?.ticketId.toString()
                            )

                            ticketBottomSheet.show(requireActivity().supportFragmentManager, "ticket")
                        }
                        //ticketBottomSheet(childFragmentManager, "Ticket Inspection View")
                    }, { error ->

                    })
                }
            }
            llSubscription.clickWithDebounce {
                BaseApplication.sharedPreference.getPrefModel(
                    EasyPref.activeTicket,
                    ActiveTicket::class.java
                )?.couponId.toString()
                MobilityboxCouponCode(
                    BaseApplication.sharedPreference.getPrefModel(
                        EasyPref.activeTicket,
                        ActiveTicket::class.java
                    )?.couponId.toString()
                ).fetchCoupon({ coupen ->
                    pushFragment(SubscriptionWebFragment.newInstance("https://fastlink.themobilitybox.com/" + "subscriptions/manage?mobilitybox_subscription_ids[]=" + "${coupen.subscription?.id}"))

                    //ticketBottomSheet(childFragmentManager, "Ticket Inspection View")
                }, { error ->

                })

            }
            llAddWallet.clickWithDebounce {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        "https://api.themobilitybox.com/v7/ticketing/passes/google_wallet/${
                            BaseApplication.sharedPreference.getPrefModel(
                                EasyPref.activeTicket,
                                ActiveTicket::class.java
                            )?.ticketId.toString()
                        }"
                    )
                )
                startActivity(browserIntent)
                //pushFragment(SubscriptionWebFragment.newInstance("https://api.themobilitybox.com/v7/ticketing/passes/google_wallet/${BaseApplication.sharedPreference.getPref(EasyPref.TICKET_ID, "")}"))

            }
            llReActivityTicket.clickWithDebounce {
                viewModel.callDTicketApi(
                    BaseApplication.sharedPreference.getPrefModel(
                        EasyPref.activeTicket,
                        ActiveTicket::class.java
                    )?.couponId.toString()
                )
            }
        }
    }

    private fun stepOne() {
        binding?.apply {
            viewModel.clickButtonPurchaseTicket = "2"
            rv.isVisible = true
            btnTicket.text = getString(R.string.proceed)
            btnTicket.isVisible = true

            clNextDate.isVisible = false
            clPhotoId.isVisible = false
            clReViewActivateTicket.isVisible = false

        }
    }

    private fun stepTwo() {
        binding?.apply {
            viewModel.clickButtonPurchaseTicket = "3"
            rv.isVisible = false
            btnTicket.isVisible = true
            btnTicket.text = getString(R.string.purchase_ticket)
            clPhotoId.isVisible = true
            clNextDate.isVisible = false
            clReViewActivateTicket.isVisible = false
        }
    }

    private fun stepThree() {

        binding?.apply {
            binding?.apply {
                rv.isVisible = true


                clNextDate.isVisible = true
                tvNext.visibility = View.INVISIBLE
                tvNextDateValue.visibility = View.INVISIBLE

                ivStatusBullet.isVisible = true
                tvInvoiceStatus.isVisible = true
                tvInvoiceStatus.text = getString(R.string.payment_in_progress)
                tvInvoiceStatus.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.color_rejected
                    )
                )
                ivStatusBullet.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_rejected
                    )
                )

                clPhotoId.isVisible = false
                clReViewActivateTicket.isVisible = false
                llReActivityTicket.isVisible = false
                btnTicket.isVisible = false
            }
        }
    }

    private fun stepFour() {

        binding?.apply {
            rv.isVisible = true
            btnTicket.isVisible = false

            clNextDate.isVisible = true
            tvNext.visibility = View.VISIBLE
            tvNextDateValue.visibility = View.VISIBLE

            ivStatusBullet.isVisible = true
            tvInvoiceStatus.isVisible = true
            tvInvoiceStatus.text = getString(R.string.activated)
            tvInvoiceStatus.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.color_approved
                )
            )
            ivStatusBullet.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_approved
                )
            )


            clPhotoId.isVisible = false

            clReViewActivateTicket.isVisible = true
            llReActivityTicket.isVisible = false
        }
    }
    private fun stepCancel() {

        binding?.apply {
            rv.isVisible = true
            btnTicket.isVisible = false

            clNextDate.isVisible = true
            tvNext.visibility = View.INVISIBLE
            tvNextDateValue.visibility = View.INVISIBLE

            ivStatusBullet.isVisible = true
            tvInvoiceStatus.isVisible = true
            tvInvoiceStatus.text = getString(R.string.activated)
            tvInvoiceStatus.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.color_approved
                )
            )
            ivStatusBullet.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_approved
                )
            )


            clPhotoId.isVisible = false

            clReViewActivateTicket.isVisible = true
            llReActivityTicket.isVisible = false
        }
    }

    private fun stepFive() {

        binding?.apply {
            rv.isVisible = true


            clNextDate.isVisible = true
            ivStatusBullet.isVisible = true
            tvInvoiceStatus.isVisible = true

            ivStatusBullet.isVisible = true
            tvInvoiceStatus.isVisible = true
            tvInvoiceStatus.text = getString(R.string.completed)
            tvInvoiceStatus.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.color_pending
                )
            )
            ivStatusBullet.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_pending
                )
            )



            clReViewActivateTicket.isVisible = true
            llReActivityTicket.isVisible = true

            clPhotoId.isVisible = false
            llSubscription.isVisible = false
            llAddWallet.isVisible = false
            btnTicket.isVisible = false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initBudgetManagementRecyclerView() {

        binding?.apply {
            tvNextDateValue.text=viewModel.endDate
            dTicketAdapter = DTicketAdapter(requireContext(), dTicketList, viewModel.clickButtonPurchaseTicket,viewModel.startDate) { view, model, position ->
                when (view.id) {
                    R.id.cl_mail -> {

                    }
                }
            }
            rv.layoutManager = LinearLayoutManager(activity)
            rv.adapter = dTicketAdapter
        }
    }

    private fun showDateDialog() {
        hideKeyboard(requireActivity())
        var selectedTimeMillis: Long = System.currentTimeMillis()
        if (birthDate.isNotEmpty()) {
            val selectedDate = SERVER_FORMAT.parse(birthDate)
            if (selectedDate != null) {
                selectedTimeMillis = selectedDate.time
            }
        }
        showDatePickerDialog(requireContext(),
            maxDate = myCalendar.timeInMillis,
            selectedDate = selectedTimeMillis,
            onDatePickerDateSet = object :
                de.fast2work.mobility.utility.extension.DialogUtil.OnDatePickerDateSet {
                override fun onDateSet(long: Long) {
                    birthDate = SERVER_FORMAT.format(long)
                    binding!!.telDate.setText(DISPLAY_FORMAT.format(long))
                }
            })
    }

    private fun isValidOne(): Boolean {
        var valid = true

        binding?.apply {
            tvStartOfValidError.isVisible = false
            tvFirstError.isVisible = false
            tvLastError.isVisible = false

            if (telFirstName.getTrimText().isEmpty()) {
                tvFirstError.isVisible = true
                tvFirstError.text = getString(R.string.first_name_is_required)
                valid = false
            }
            if (telLastName.getTrimText().isEmpty()) {
                tvLastError.isVisible = true
                tvLastError.text = getString(R.string.the_last_name_is_required)
                valid = false
            }
            if (telDate.getTrimText().isEmpty()) {
                tvDate.isVisible = true
                tvDate.text = getString(R.string.the_date_is_required)
                valid = false
            }
            if (telPostalCode.getTrimText().isEmpty()) {
                tvPostalCode.isVisible = true
                tvPostalCode.text = getString(R.string.the_postal_code_is_required)
                valid = false
            }/*else if (telPostalCode.text?.toString()?.length!! < 4) {
                tvPostalCode.isVisible = true
                tvPostalCode.text = "Your postal code Must Contain Minimum 5 Characters"
                valid = false
            }*/
            //binding.editText.text.toString().trim()
            if (binding!!.telStartTime.getTrimText().isEmpty()) {
                binding!!.tvStartOfValidError.isVisible = true
                binding!!.tvStartOfValidError.text = getString(R.string.the_start_of_valid_is_required)
                valid = false
            }
        }

        return valid

    }

}