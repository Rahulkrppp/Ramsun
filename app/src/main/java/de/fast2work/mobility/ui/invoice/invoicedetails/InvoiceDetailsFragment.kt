package de.fast2work.mobility.ui.invoice.invoicedetails

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.CompoundButtonCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.R
import de.fast2work.mobility.data.eventbus.UpdateNotificationCount
import de.fast2work.mobility.data.request.InvoiceCalculateCo2Req
import de.fast2work.mobility.data.request.InvoiceFuelTypeReq
import de.fast2work.mobility.data.response.InvoiceDetailsApiResponse.InvoiceTransaction
import de.fast2work.mobility.data.response.InvoiceRefDocListRes
import de.fast2work.mobility.data.response.ItemTransportDetails
import de.fast2work.mobility.data.response.ModeOfTransport
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.databinding.FragmentInvoiceDetailsBinding
import de.fast2work.mobility.ui.co2.SearchListFragment
import de.fast2work.mobility.ui.co2.bottom.Co2ModeOfTransportBottomSheetFragment
import de.fast2work.mobility.ui.co2.bottom.Co2ModeOfTransportViewModel
import de.fast2work.mobility.ui.co2.bottom.TransportDetailBottomSheetFragment
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.home.adapter.CategoryAdapter
import de.fast2work.mobility.ui.invoice.DownLoadPdfFragment
import de.fast2work.mobility.ui.invoice.adapter.InvoiceDetailsAdapter
import de.fast2work.mobility.ui.invoice.invoicedetails.adapter.ShowDocImageAdapter
import de.fast2work.mobility.ui.invoice.invoicedetails.bottom.FuelTypeBottomSheetFragment
import de.fast2work.mobility.ui.sidemenu.notification.NotificationFragment
import de.fast2work.mobility.ui.upload.step.UploadInvoiceStepThreeFragment
import de.fast2work.mobility.ui.upload.step.adapter.UploadImageAdapter
import de.fast2work.mobility.utility.customview.ThreeItemsLinearLayoutManager
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.extension.capitalized
import de.fast2work.mobility.utility.extension.constraintLayoutBackgroundColor
import de.fast2work.mobility.utility.extension.findUrlName
import de.fast2work.mobility.utility.extension.formatCurrency
import de.fast2work.mobility.utility.extension.formatWithOutCurrency
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.getFileSizeFromUrl
import de.fast2work.mobility.utility.extension.getString
import de.fast2work.mobility.utility.extension.getTrimText
import de.fast2work.mobility.utility.extension.parcelable
import de.fast2work.mobility.utility.extension.textColor
import de.fast2work.mobility.utility.extension.toBlankString
import de.fast2work.mobility.utility.extension.toDDMMYYYY
import de.fast2work.mobility.utility.helper.DownloadFile
import de.fast2work.mobility.utility.helper.IOnDownloadListener
import de.fast2work.mobility.utility.preference.EasyPref
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class InvoiceDetailsFragment :
    BaseVMBindingFragment<FragmentInvoiceDetailsBinding, InvoiceDetailsViewModel>(
        InvoiceDetailsViewModel::class.java
    ) {

    private var invoiceId = ""
    private var pdfFileName = ""
    private var pdfFileUrl = ""
    private val co2ModeOfTransportViewModel: Co2ModeOfTransportViewModel by viewModels()
    private var modelModeOfTransport: ModeOfTransport? = null
    private var invoiceCo2Type = ""
    private var vehicleSubType:String? = null
    var scrollPostionLast:Boolean=false
    var distanceZeroError=true
    var fuelZeroError=true
    var invoiceDetailsAdapter:InvoiceDetailsAdapter?=null
    var invoiceList:ArrayList<InvoiceTransaction> = arrayListOf()
    private  var showDocImageAdapter: ShowDocImageAdapter? =null
    private var invoiceRefDocListRes:ArrayList<InvoiceRefDocListRes> = arrayListOf()
    private var sourceDestination=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return generateBinding(FragmentInvoiceDetailsBinding.inflate(inflater), container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isFirstTimeLoad) {
            scrollPostionLast= arguments?.getBoolean(NOTIFICATION,false) == true
            arguments?.let {
                if (it.containsKey(INVOICE_ID)) {
                    invoiceId = it.getString(INVOICE_ID, "")
                    viewModel.callInvoiceDetailsApi(invoiceId)
                }
            }

        }
    }
    override fun onResume() {
        super.onResume()
        setFragmentResultListener(SearchListFragment.PARAM_RESULT) { _: String, bundle: Bundle ->
            sourceDestination=bundle.getBoolean(SearchListFragment.PARAM_SET_SOURCE)
            viewModel.sourceDestination = bundle.parcelable(SearchListFragment.PARAM_SEND_MODEL)
            if (sourceDestination){
                binding!!.telSearchSource.setText(viewModel.sourceDestination?.compressed_address)
                viewModel.fromAddressLocal=viewModel.sourceDestination?.compressed_address.toBlankString()
                viewModel.fromLatitudeLocal=viewModel.sourceDestination?.lat.toBlankString()
                viewModel.fromLongitudeLocal=viewModel.sourceDestination?.long.toBlankString()
                searchIconSetSource()
            }else{
                binding!!.telSearchDestination.setText(viewModel.sourceDestination?.compressed_address)
                viewModel.toAddressLocal=viewModel.sourceDestination?.compressed_address.toBlankString()
                viewModel.toLatitudeLocal=viewModel.sourceDestination?.lat.toBlankString()
                viewModel.toLongitudeLocal=viewModel.sourceDestination?.long.toBlankString()
                searchIconSetDestination()
            }
            if (binding!!.telSearchSource.text?.isNotEmpty() == true && binding!!.telSearchDestination.text?.isNotEmpty() == true){
                /*if (binding!!.telSearchDestination.fuelTypeLabel==it.label){
                    btnCalculate.backgroundTintList=(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorSearchHint)))
                    btnCalculate.isEnabled=false
                }else{
                    btnCalculate.isEnabled=true
                    setThemeForView(btnCalculate)
                }*/
                if (binding!!.telTransportMode.text?.isEmpty() == true){
                    binding!!.tvTransportModeError.isVisible = true
                    binding!!.tvTransportModeError.text =
                        getString(R.string.please_select_a_transport_mode)
                }else{
                    binding!!.tvTransportModeError.isVisible = false
                    binding!!.btnCalculate.isEnabled=true
                    setThemeForView(binding!!.btnCalculate)
                    viewModel.callDistanceApi(binding!!.telSearchSource.getTrimText(),binding!!.telSearchDestination.getTrimText(),binding!!.telTransportMode.getTrimText())

                }

            }else{
                binding!!.tlTransportType.isVisible=false
            }
        }
    }
    private fun searchIconSetSource(){
        if (binding!!.telSearchSource.text?.isEmpty() == true){
            binding!!.telSearchSource.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_search, 0)
            binding!!.ivClearSource.isVisible = false
        } else {
            binding!!.ivClearSource.isVisible = false
            binding!!.telSearchSource.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
        }
    }
    private fun searchIconSetDestination(){
        if (binding!!.telSearchDestination.text?.isEmpty() == true){
            binding!!.telSearchDestination.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_search, 0)
            binding!!.ivClearDestination.isVisible = false
        } else {
            binding!!.ivClearDestination.isVisible = false
            binding!!.telSearchDestination.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
        }
    }
    override fun attachObservers() {
        BaseApplication.notificationCount.observe(this) {
            /*if (it > 0) {
                binding!!.toolbar.tvNotificationCount.visibility = View.VISIBLE
                binding!!.toolbar.ivNotification.visibility = View.VISIBLE
                binding!!.toolbar.tvNotificationCount.text = it.toString()
            } else {
                binding!!.toolbar.tvNotificationCount.visibility = View.GONE
                binding!!.toolbar.ivNotification.visibility = View.VISIBLE
            }*/
        }
        viewModel.distanceApiErrorLiveData.observe(this) {
            binding?.telDistanceKm?.setText("0.0")
            showErrorMessage(it)
        }
        viewModel.distanceLiveData.observe(this){
            Log.e("", "attachObservers:${it.data} ")
            binding!!.telDistanceKm.setText(it.data?.distance.toBlankString())
        }
        viewModel.modeOfTransportLiveData.observe(this){
            if (it.data!=null){
                viewModel.modeOfTransportList.clear()

                viewModel.modeOfTransportList.addAll(it.data!!)
                Log.e("", "modeOfTransportList==:${viewModel.modeOfTransportList} ", )
                viewModel.modeOfTransportList.forEachIndexed { index, modeOfTransport ->
                    //Log.e("", "modeOfTransport====:${modeOfTransport}======${viewModel.transportMode}===== ${modelModeOfTransport?.key}", )
                    //Log.e("", "viewModel.transportMode0000====:${viewModel.transportMode}", )
                   // Log.e("", "modeOfTranspor7777t====:${modeOfTransport.key}", )
                    if (viewModel.transportMode==modeOfTransport.key){
                        //Log.e("", "modeOfTransport55555===========:${viewModel.transportMode}======${modeOfTransport.key}=====", )
                        modelModeOfTransport = modeOfTransport
                        //modelModeOfTransport?.items?.addAll(modeOfTransport.items)
                        //Log.e("", "items888===========:${modelModeOfTransport?.items}=====", )
                        //Log.e("", "modeOfTransport.items888===========:======${modeOfTransport.items}=====", )
                        modelModeOfTransport?.items?.forEach {
                            if (viewModel.transportType==it.key){
                                //Log.e("", "transportType99===========:${viewModel.transportType}======${it.key}=====", )
                                it.isSelected=true
                            }
                        }
                    }


                    // modeOfTransport.isSelected=false
                    /*if (model?.key==modeOfTransport.key||transportModeKey==modeOfTransport.key){
                        modeOfTransport.isSelected=true
                    }*/
                }

            }
        }

        viewModel.invoiceCalculateCo2LiveData.observe(this) {
            Log.e("", "attachObservers==: ${it.data}", )
            if (it.data!=null) {
                binding!!.telCo2EmissionsKg.setText((it.data!!.co2Kg ?: "0.00").toString())
                binding!!.telCo2EmissionsKgFuel.setText((it.data!!.co2Kg ?: "0.00").toString())
                viewModel.callInvoiceDetailsApi(invoiceId)
                showSuccessMessage(it.responseMessage)
            }
        }
        viewModel.fuelTypeListLiveData.observe(this) {
            viewModel.fuelTypeList.addAll(it.data!!)
        }
        viewModel.invoiceDetailsLiveData.observe(this) {
            it?.data?.let { invoiceDetails ->
                binding?.apply {
                    viewModel.invoiceDetailsData=invoiceDetails
                    pdfFileName = invoiceDetails.invoiceFile?.substring(
                        invoiceDetails.invoiceFile.lastIndexOf('/') + 1
                    ) ?: ""
                    invoiceRefDocListRes.clear()

                    invoiceRefDocListRes.addAll(invoiceDetails.invoiceRefDocList)
                    clAdditional.isVisible = invoiceDetails.invoiceRefDocList.isNotEmpty()
                    showDocImageAdapter?.notifyDataSetChanged()

                    pdfFileUrl = invoiceDetails.invoiceFile ?: ""
                    if (pdfFileUrl.contains(".pdf", true)) {
                        ivUrlLogo.setImageResource(R.drawable.ic_pdf_display)
                    } else {
                        ivUrlLogo.setImageResource(R.drawable.ic_img_display)
                    }
                    tvFileSize.text = /*String.format("%.2f",*/
                        invoiceDetails.invoiceFile?.getFileSizeFromUrl()/*)*//* + " MB"*/
//                    if(pdfFileName.contains(".pdf")){
//                        binding?.wvBill?.loadUrl("https://docs.google.com/gview?embedded=true&url=${invoiceDetails.invoiceFile}")
//                    }else{
//                        binding?.wvBill?.loadUrl(invoiceDetails.invoiceFile ?: "")
//                    }
                    invoiceId = invoiceDetails.invoiceId.toString()
                    txtBillName.text = invoiceDetails.invoiceName
                    tvPdfName.text = invoiceDetails.invoiceName
                    invoiceCo2Type = invoiceDetails.invoiceCo2Type.toString()

                    if (invoiceDetails.taxBenefitInvoice=="yes"){
                        if (invoiceDetails.invoiceTaxInfo?.benefitCode=="home_charging_allowance"){
                            tvInvoicePower.text=getString(R.string.power_consumption_kwh)
                            tvPowerValue.text=invoiceDetails.invoiceTaxInfo?.noOfKws?.toDouble()?.formatWithOutCurrency()
                        }else{
                            tvInvoicePower.text=getString(R.string.no_of_km_s_travelled)
                            tvPowerValue.text=invoiceDetails.invoiceTaxInfo?.noOfKms?.toDouble()?.formatWithOutCurrency()
                        }
                    }else{
                        tvInvoicePower.isVisible=false
                        tvPowerValue.isVisible=false
                        view10.isVisible=false
                    }


                    if (invoiceDetails.approvalStatus.toString().lowercase() == "rejected"|| (invoiceDetails.approvalStatus.toString().lowercase() == "approved") ){
                        if (invoiceDetails.approvalStatus.toString().lowercase() == "rejected") {

                            txtBillStatus.text = getString(
                                R.string.text_bill_status,
                                getString(R.string.rejected).capitalized()
                            )
                        } else if (invoiceDetails.approvalStatus.toString()
                                .lowercase() == "approved"
                        ) {
                            txtBillStatus.text = getString(
                                R.string.text_bill_status,
                                getString(R.string.approved).capitalized()
                            )

                        }
                        when (invoiceDetails.approvalStatus?.lowercase()) {
                            "pending" -> {
                                txtBillStatus.setTextColor(
                                    ContextCompat.getColor(
                                        requireActivity(),
                                        R.color.color_pending
                                    )
                                )
                            }

                            "inreview" -> {
                                txtBillStatus.setTextColor(
                                    ContextCompat.getColor(
                                        requireActivity(),
                                        R.color.color_pending
                                    )
                                )
                            }

                            "rejected" -> {
                                txtBillStatus.setTextColor(
                                    ContextCompat.getColor(
                                        requireActivity(),
                                        R.color.color_rejected
                                    )
                                )
                                clRejectionComments.isVisible = true
                            }

                            "approved" -> { txtBillStatus.setTextColor(ContextCompat.getColor(requireActivity(), R.color.color_approved))
                            }
                        }
                    } else{
                        if (invoiceDetails.captureCo2) {
                            txtBillStatus.text = getString(
                                R.string.text_bill_status,
                                getString(R.string.co2_data_missing).capitalized()
                            )
                            txtBillStatus.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.color_80A500
                                )
                            )
                            /* getString(
                                R.string.text_bill_status,
                                getString(R.string.pending).capitalized()
                            )*/
                        } else {
                            if (invoiceDetails.approvalStatus == "pending" || invoiceDetails.approvalStatus == "inreview") {
                                txtBillStatus.text = getString(
                                    R.string.text_bill_status,
                                    getString(R.string.pending).capitalized()
                                )
                            }/* else if (invoiceDetails.approvalStatus.toString().lowercase() == "rejected") {

                            txtBillStatus.text = getString(
                                R.string.text_bill_status,
                                getString(R.string.rejected).capitalized()
                            )
                        } else if (invoiceDetails.approvalStatus.toString()
                                .lowercase() == "approved"
                        ) {
                            txtBillStatus.text = getString(
                                R.string.text_bill_status,
                                getString(R.string.approved).capitalized()
                            )

                        }*/

                            when (invoiceDetails.approvalStatus?.lowercase()) {
                                "pending" -> {
                                    txtBillStatus.setTextColor(
                                        ContextCompat.getColor(
                                            requireActivity(),
                                            R.color.color_pending
                                        )
                                    )
                                }

                                "inreview" -> {
                                    txtBillStatus.setTextColor(
                                        ContextCompat.getColor(
                                            requireActivity(),
                                            R.color.color_pending
                                        )
                                    )
                                }

                               /* "rejected" -> {
                                    txtBillStatus.setTextColor(
                                        ContextCompat.getColor(
                                            requireActivity(),
                                            R.color.color_rejected
                                        )
                                    )
                                    clRejectionComments.isVisible = true
                                }

                                "approved" -> { txtBillStatus.setTextColor(ContextCompat.getColor(requireActivity(), R.color.color_approved))
                                }*/
                            }
                        }
                    }





                    if (invoiceDetails.generateCo2Block == true){

                        if (invoiceDetails.approvalStatus.toString().lowercase() == "approved" || invoiceDetails.approvalStatus.toString().lowercase() == "rejected"){
                            if (invoiceDetails.fuelTypeLabel.isNullOrEmpty()){
                                tvFuelTypeValue.text=getString(R.string.n_a)
                            }else {
                                tvFuelTypeValue.text = invoiceDetails.fuelTypeLabel
                            }
                            if (invoiceDetails.fuelQuantity.isNullOrEmpty()){
                                tvTvQuantityLitersValue.text=getString(R.string.n_a)
                            }else {
                                tvTvQuantityLitersValue.text=invoiceDetails.fuelQuantity
                            }

                            tvTvCo2EmissionsKgValue.text= (invoiceDetails.co2Value?:getString(R.string.n_a)).toString()
                            if (invoiceDetails.distanceInKm.isNullOrEmpty()){
                                tvDistanceKmValue.text =getString(R.string.n_a)
                            }else {
                                tvDistanceKmValue.text = invoiceDetails.distanceInKm
                            }
                            if (invoiceDetails.vehicleTypeLabel.isNullOrEmpty()){
                                tvTransportModeValue.text =getString(R.string.n_a)
                            }else {
                                tvTransportModeValue.text = invoiceDetails.vehicleTypeLabel
                            }

                            if (invoiceDetails.vehicleSubTypeLabel?.isEmpty() == true){
                                tvTvTransportTypeValue.text=getString(R.string.n_a)
                            }else {
                                tvTvTransportTypeValue.text = invoiceDetails.vehicleSubTypeLabel
                            }

                            when (invoiceDetails.invoiceCo2Type) {
                                "transport" -> {
                                    clCo2ApprovedDetails.isVisible = true
                                    groupDetailCo2.isVisible = true
                                    groupDetailDistanceKm.isVisible = true
                                    groupDetailFuel.isVisible = false
                                }

                                "fuel" -> {
                                    clCo2ApprovedDetails.isVisible = true
                                    groupDetailCo2.isVisible = false
                                    groupDetailDistanceKm.isVisible = false
                                    groupDetailFuel.isVisible = true
                                }

                                else -> {
                                    clCo2ApprovedDetails.isVisible = false
                                }
                            }
                        }else {
                            viewModel.fuelType=invoiceDetails.fuelType?:""
                            viewModel.transportMode= invoiceDetails.vehicleType?:""
                            viewModel.transportType= invoiceDetails.vehicleSubType?:""

                            telFuelQuantity.setText(invoiceDetails.fuelQuantity)
                            telTransportMode.setText(invoiceDetails.vehicleTypeLabel)
                            if(invoiceDetails.distanceInKm==null){
                                telDistanceKm.setText("")
                            }else {
                                if (invoiceDetails.distanceInKm.toString().toDouble() == 0.0) {
                                    telDistanceKm.setText("")
                                } else {
                                    telDistanceKm.setText(invoiceDetails.distanceInKm)
                                }
                            }
                            telCo2EmissionsKg.setText((invoiceDetails.co2Value?:"0.00").toString())
                            telCo2EmissionsKgFuel.setText((invoiceDetails.co2Value?:"0.00").toString())


                            telFuelType.setText(invoiceDetails.fuelTypeLabel)

                            vehicleSubType=invoiceDetails.vehicleTypeLabel


                           /* if (invoiceDetails.vehicleTypeLabel?.isNotEmpty() == true) {
                                if (invoiceDetails.vehicleSubTypeLabel?.isEmpty() == true) {
                                    telTransportType.setText("-")
                                    telTransportType.isEnabled = true
                                } else {
                                    telTransportType.isEnabled = true
                                    telTransportType.setText(invoiceDetails.vehicleSubTypeLabel)
                                }
                            }*/

                            when (invoiceDetails.invoiceCo2Type) {
                                "transport" -> {
                                    clCo2.isVisible = true
                                    groupFuel.isVisible = false
                                    groupCo2.isVisible = true
                                    clRadio.isVisible=true


                                    //viewModel.callModeOfTransportApi(false)
                                    telCo2EmissionsKg.isVisible = invoiceDetails.co2Value != null
                                    if (invoiceDetails.vehicleTypeLabel?.isNullOrEmpty() == true){
                                        btnCalculate.text=getString(R.string.save)
                                        btnCalculate.alpha=1f
                                        setThemeForView(btnCalculate)
                                        btnCalculate.isEnabled=true
                                        rbKilometer.isChecked=true
                                        //setRadioButtonChecked(rbKilometer)
                                        viewModel.distanceTypeData="km"

                                    }else{
                                        btnCalculate.text="Update"
                                        btnCalculate.backgroundTintList=(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorSearchHint)))
                                        btnCalculate.isEnabled=false
                                    }

                                    if (invoiceDetails.distanceDataType=="address"){
                                        rbAddress.isChecked=true
                                        //setRadioButtonChecked(rbAddress)
                                        viewModel.distanceTypeData=invoiceDetails.distanceDataType
                                        telSearchSource.setText(invoiceDetails.fromAddress)

                                        viewModel.fromLatitudeLocal=invoiceDetails.fromLatitude.toBlankString()
                                        viewModel.fromLongitudeLocal=invoiceDetails.fromLongitude.toBlankString()
                                        telSearchDestination.setText(invoiceDetails.toAddress)
                                        viewModel.toLatitudeLocal=invoiceDetails.toLatitude.toBlankString()
                                        viewModel.toLongitudeLocal=invoiceDetails.toLongitude.toBlankString()
                                    } else{
                                        rbKilometer.isChecked=true
                                        //setRadioButtonChecked(rbKilometer)
                                        viewModel.distanceTypeData=invoiceDetails.distanceDataType
                                    }

                                    if (rbKilometer.isChecked){
                                        clAddress.isVisible=false

                                    }else{
                                        clAddress.isVisible=true
                                    }

                                    if (invoiceDetails.vehicleSubType.isNullOrEmpty()){
                                        telTransportType.setText("-")
                                        tlTransportType.visibility=View.GONE
                                    }else{
                                        telTransportType.setText(invoiceDetails.vehicleSubType)
                                        tlTransportType.visibility=View.VISIBLE
                                    }
                                }

                                "fuel" -> {
                                    groupCo2.isVisible = false
                                    clAddress.isVisible=false
                                    clRadio.isVisible=false
                                    clCo2.isVisible = true
                                    groupFuel.isVisible = true

                                    telCo2EmissionsKgFuel.isVisible =
                                        invoiceDetails.co2Value != null
                                    if (invoiceDetails.fuelTypeLabel?.isNullOrEmpty() == true){
                                        btnCalculate.text=getString(R.string.save)
                                        setThemeForView(btnCalculate)
                                        btnCalculate.isEnabled=true
                                    }else{
                                        btnCalculate.text="Update"
                                        btnCalculate.backgroundTintList=(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorSearchHint)))
                                        btnCalculate.isEnabled=false
                                    }
                                }

                                else -> {
                                    clCo2.isVisible = false
                                }
                            }


                        }
                    }else{
                        clCo2ApprovedDetails.isVisible = false
                        clCo2.isVisible = false
                    }



                    invoiceList.clear()
                    invoiceList.addAll(invoiceDetails.invoiceTransactions)
                    initRecyclerView()

                    /*tvCategoryValue.text =
                        if (!invoiceDetails.invoiceTransactions?.first()?.categoryName.isNullOrEmpty()) {
                            invoiceDetails.invoiceTransactions?.first()?.categoryName
                        } else {
                            getString(R.string.n_a)
                        }*/
                    tvCurrencyValue.text =
                        if (invoiceDetails.currencyCode.isNullOrEmpty() && invoiceDetails.currencySymbol.isNullOrEmpty()) {
                            "EUR - (€)"
                        } else {
                            invoiceDetails.currencyCode + " - (" + invoiceDetails.currencySymbol + ")"
                        }
                    /*tvSubCategoryValue.text =
                        if (!invoiceDetails.invoiceTransactions?.first()?.subCategoryName.isNullOrEmpty()) {
                            invoiceDetails.invoiceTransactions?.first()?.subCategoryName
                        } else {
                            getString(R.string.n_a)
                        }*/
                    txtInvoiceNumberValue.text =
                        invoiceDetails.invoiceNo?.ifBlank { getString(R.string.n_a) }
                    tvInvoiceDateValue.text =
                        invoiceDetails.invoiceDate?.toDDMMYYYY() ?: getString(R.string.n_a)
                    if (invoiceDetails.supplierVatNo?.isEmpty() == true) {
                        tvVatNoValue.text = getString(R.string.n_a)
                    } else {
                        tvVatNoValue.text = invoiceDetails.supplierVatNo ?: getString(R.string.n_a)
                    }
//                    tvServiceDateValue.text = if (invoiceDetails.serviceDate?.toDDMMYYYY() == null) {
//                        getString(R.string.n_a)
//                    } else {
//                        invoiceDetails.serviceDate.toDDMMYYYY()
//                    }
                    tvRejectionCommentsValue.text =
                        if (!invoiceDetails.rejectionRemarks.isNullOrEmpty()) {
                            invoiceDetails.rejectionRemarks.toString()
                        } else {
                            getString(R.string.n_a)
                        }

                    if (invoiceDetails.employeeRemarks.isNullOrEmpty()) {
                        clSelfComments.isVisible=false
                    } else {
                        clSelfComments.isVisible=true
                        tvSelfCommentsValue.text =invoiceDetails.employeeRemarks.toString()
                        //getString(R.string.n_a)
                    }

                    if (invoiceDetails.grossAmount.isNullOrEmpty()){
                        tvTotalGrossValue.text=getString(R.string.n_a)
                    }else{
                        tvTotalGrossValue.text=invoiceDetails.grossAmount.toFloat().formatCurrency(invoiceDetails.currencySymbol)
                    }
                    if (invoiceDetails.taxAmount.isNullOrEmpty()){
                        tvTotalTaxValue.text=getString(R.string.n_a)
                    }else{
                        tvTotalTaxValue.text=invoiceDetails.taxAmount.toFloat().formatCurrency(invoiceDetails.currencySymbol)
                    }
                    if (invoiceDetails.netAmount.isNullOrEmpty()){
                        txtTvTotalNetValue.text=getString(R.string.n_a)
                    }else{
                        txtTvTotalNetValue.text=invoiceDetails.netAmount.toFloat().formatCurrency(invoiceDetails.currencySymbol)
                    }

                    tvSupplierAddressValue.text =
                        if (invoiceDetails.supplierAddress.isNullOrEmpty()) {
                            getString(R.string.n_a)
                        } else {
                            invoiceDetails.supplierAddress.capitalized()
                        }
                    tvSupplierNameValue.text = if (invoiceDetails.supplierName.isNullOrEmpty()) {
                        getString(R.string.n_a)
                    } else {
                        invoiceDetails.supplierName.capitalized()
                    }
                    tvUploadedDateValue.text = invoiceDetails.uploadedDate?.toDDMMYYYY()
                   /* if (invoiceDetails.invoiceTransactions?.first()?.description?.isNullOrEmpty() == true) {
                        tvProductDescriptionValue.text = getString(R.string.n_a)
                    } else {
                        tvProductDescriptionValue.text =
                            invoiceDetails.invoiceTransactions?.first()?.description
                                ?: getString(R.string.n_a)
                    }*/
                   /* if (invoiceDetails.currencySymbol.isNullOrEmpty()) {
                        tvNetAmountValue.text = invoiceDetails.netAmount?.toFloat()?.formatCurrency(
                            "€" ?: ""
                        )
                        tvGrossAmountValue.text =
                            invoiceDetails.grossAmount?.toFloat()?.formatCurrency(
                                "€" ?: ""
                            )
                        tvTaxAmountValue.text = invoiceDetails.taxAmount?.toFloat()?.formatCurrency(
                            "€" ?: ""
                        )
                    } else {
                        tvNetAmountValue.text = invoiceDetails.netAmount?.toFloat()?.formatCurrency(
                            invoiceDetails.currencySymbol ?: ""
                        )
                        tvGrossAmountValue.text =
                            invoiceDetails.grossAmount?.toFloat()?.formatCurrency(
                                invoiceDetails.currencySymbol ?: ""
                            )
                        tvTaxAmountValue.text = invoiceDetails.taxAmount?.toFloat()?.formatCurrency(
                            invoiceDetails.currencySymbol ?: ""
                        )
                    }*/


                  /*  tvPaidByValue.text =
                        invoiceDetails.invoiceTransactions?.first()?.paidBy?.capitalized()
                            ?.ifEmpty { getString(R.string.n_a) }
                    tvExpenseForValue.text =
                        invoiceDetails.invoiceTransactions?.first()?.expenseType?.capitalized()
                            ?.ifEmpty { getString(R.string.n_a) }

                    tvStartDateValue.text =
                        if (invoiceDetails.invoiceTransactions?.first()?.startDate?.toDDMMYYYY() == null) {
                            getString(R.string.n_a)
                        } else {
                            invoiceDetails.invoiceTransactions.first()?.startDate?.toDDMMYYYY()
                        }

                    tvEndDateValue.text =
                        if (invoiceDetails.invoiceTransactions?.first()?.endDate?.toDDMMYYYY() == null) {
                            getString(R.string.n_a)
                        } else {
                            invoiceDetails.invoiceTransactions.first()?.endDate?.toDDMMYYYY()
                        }*/

                    Handler(Looper.getMainLooper()).postDelayed({
                        if (scrollPostionLast){
                            nsView.smoothScrollBy(0, Int.MAX_VALUE)
                            telTransportMode.requestFocus()
                            telFuelType.requestFocus()
                        }
                    }, 1000)
                }
            }
        }
    }

    override fun setClickListener() {
        binding?.apply {
            ivDownload.clickWithDebounce {
                showProgressDialog(true)
                DownloadFile(baseActivity).downloadPDF(
                    pdfFileUrl,
                    pdfFileName,
                    object : IOnDownloadListener {
                        override fun onSuccess(filePath: String) {
                            Log.e("Download============", "${pdfFileName}")
                            showProgressDialog(false)
//                        pushFragment(DownLoadPdfFragment.newInstance(pdfFileUrl,pdfFileName))
                            showSuccessMessage(getString(R.string.file_downloaded_to_documents_folder))
                        }

                        override fun onFailure() {
                            showProgressDialog(false)
                            Log.e("Download", "onFailure")
                        }

                        override fun onInProgress() {
                            Log.e("Downloaded", "InProgress")
                        }
                    })
            }

            clDownload.clickWithDebounce {
                pushFragment(DownLoadPdfFragment.newInstance(pdfFileUrl, pdfFileName))
            }
            btnCalculate.clickWithDebounce {
                if (isValid()) {
                    when (invoiceCo2Type) {
                        "transport" -> {
                            if (distanceZeroError) {
                                tvDistanceKmError.isVisible = false
                                val invoiceParam = InvoiceCalculateCo2Req().apply {
                                    this.invoiceid = invoiceId.toInt()
                                    this.vehicleType = /*telTransportMode.getTrimText()*/
                                        viewModel.transportMode
                                    this.vehicleSubType = /*telTransportType.getTrimText()*/
                                        viewModel.transportType
                                    this.distanceInKm = telDistanceKm.getTrimText().toDouble()
                                    this.fromAddress=telSearchSource.getTrimText()
                                    this.fromLatitude=viewModel.fromLatitudeLocal.toBlankString()
                                    this.fromLongitude=viewModel.fromLongitudeLocal.toBlankString()
                                    this.toAddress= telSearchDestination.getTrimText()
                                    this.toLatitude=viewModel.toLatitudeLocal.toBlankString()
                                    this.toLongitude=viewModel.toLongitudeLocal.toBlankString()
                                    this.distanceDataType=viewModel.distanceTypeData
                                }
                                viewModel.callInvoiceCalculateCo2Api(invoiceParam)
                            }else{
                                tvDistanceKmError.isVisible = true
                                tvDistanceKmError.text =
                                    getString(R.string.distance_cannot_be_zero_please_enter_a_valid_distance)
                            }
                        }

                        "fuel" -> {
                            if (fuelZeroError) {
                                tvFuelQuantityError.isVisible = false
                                val invoiceParam = InvoiceFuelTypeReq().apply {
                                    this.invoiceid = invoiceId.toInt()
                                    this.fuelType = viewModel.fuelType
                                    this.fuelQuantity = telFuelQuantity.getTrimText().toDouble()
                                }
                                viewModel.callInvoiceCalculateCo2FuelTypeApi(invoiceParam)
                            }else{
                                tvFuelQuantityError.isVisible = true
                                tvFuelQuantityError.text =
                                    getString(R.string.fuel_quantity_cannot_be_zero_please_enter_a_valid_fuel_quantity)
                            }
                        }

                        else -> {

                        }
                    }
                }

            }
            telFuelType.clickWithDebounce {
                viewModel.fuelTypeList.forEach {
                    if (viewModel.fuelType==it.code){
                        it.isSelected=true
                    }
                }
                val dialog = FuelTypeBottomSheetFragment.newInstance(viewModel.fuelTypeList)
                dialog.sendClickListener = {
                    telFuelType.setText(it.label)
                    if (viewModel.invoiceDetailsData.fuelTypeLabel==it.label){
                        btnCalculate.backgroundTintList=(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorSearchHint)))
                        btnCalculate.isEnabled=false
                    }else{
                        btnCalculate.isEnabled=true
                        setThemeForView(btnCalculate)
                    }
                    viewModel.fuelType = it.code
                }
                dialog.show(childFragmentManager, "")
            }
            telTransportMode.clickWithDebounce {
                /*modelModeOfTransport?.key=viewModel.transportMode
                Log.e("", "setClickListener: ${modelModeOfTransport}======${viewModel.transportMode}", )*/
                val dialog = Co2ModeOfTransportBottomSheetFragment.newInstance(co2ModeOfTransportViewModel, modelModeOfTransport,viewModel.transportMode,true)
                dialog.sendClickListener = {
                    modelModeOfTransport = it
                    telTransportMode.setText(it.label)
                    vehicleSubType=it.label
                    viewModel.transportMode = it.key
                    if (viewModel.invoiceDetailsData.vehicleTypeLabel==it.label){
                        btnCalculate.backgroundTintList=(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorSearchHint)))
                        btnCalculate.isEnabled=false
                    }else{
                        btnCalculate.isEnabled=true
                        setThemeForView(btnCalculate)
                    }
                    if (it.items.isEmpty()){
                        tlTransportType.visibility=View.GONE
                        telTransportType.setText("-")
                        viewModel.transportType=""
                    }else{
                        telTransportType.setText("")
                        tlTransportType.visibility=View.VISIBLE
                    }
                    tvTransportTypeError.isVisible=false
                }
                dialog.show(childFragmentManager, "")
            }

            telTransportType.clickWithDebounce {
                /*if (vehicleSubType?.isEmpty() == true){
                    tvTransportModeError.isVisible = true
                    tvTransportModeError.text =
                        getString(R.string.please_select_a_transport_mode_to_enable_this)
                }else{
                    Log.e("", "setClickListener: ${modelModeOfTransport?.items}======${viewModel.transportMode}", )
                    modelModeOfTransport?.items?.let { openBottomSheetWithSelected(it) }
                }*/
                modelModeOfTransport?.items?.let { openBottomSheetWithSelected(it) }
            }
            telDistanceKm.doOnTextChanged { text, start, before, count ->
                if (invoiceCo2Type=="transport") {
                    if (!text.isNullOrEmpty() && text.toString().toDouble()==0.0) {
                        distanceZeroError=false
                        telDistanceKm.filters = arrayOf<InputFilter>(
                            InputFilter.LengthFilter(
                               5
                            )
                        )
                        tvDistanceKmError.isVisible = true
                        tvDistanceKmError.text =
                            getString(R.string.distance_cannot_be_zero_please_enter_a_valid_distance)
                    }else{
                        distanceZeroError=true
                        tvDistanceKmError.isVisible = false
                    }
                }
                btnCalculate.isEnabled=true
                setThemeForView(btnCalculate)
            }
            telFuelQuantity.doOnTextChanged { text, start, before, count ->
                if (invoiceCo2Type=="fuel") {
                    if (!text.isNullOrEmpty() && text.toString().toDouble()==0.0) {
                        fuelZeroError=false
                        telFuelQuantity.filters = arrayOf<InputFilter>(
                            InputFilter.LengthFilter(
                                5
                            )
                        )
                        tvFuelQuantityError.isVisible = true
                        tvFuelQuantityError.text =
                            getString(R.string.fuel_quantity_cannot_be_zero_please_enter_a_valid_fuel_quantity)
                    }else{
                        fuelZeroError=true
                        tvFuelQuantityError.isVisible = false
                    }
                }
                btnCalculate.isEnabled=true
                setThemeForView(btnCalculate)
            }
            rgEmp.setOnCheckedChangeListener { _, checkedId ->
                setRgExpenseValue(checkedId)
            }
            rbAddress.clickWithDebounce {
                telDistanceKm.setText("")
                telCo2EmissionsKg.setText("")
                tlCo2EmissionsKg.isVisible=false
                telSearchSource.setText("")
                telSearchDestination.setText("")
                /*telCo2EmissionsKg.setText((viewModel.invoiceDetailsData.co2Value?:"0.00").toString())
                telSearchSource.setText(viewModel.invoiceDetailsData.fromAddress)
                telSearchDestination.setText(viewModel.invoiceDetailsData.toAddress)*/


                binding!!.btnCalculate.isEnabled=true
                setThemeForView(binding!!.btnCalculate)
                setRadioButtonChecked(rbAddress)
            }
            rbKilometer.clickWithDebounce {
                 telDistanceKm.setText("")
                    telCo2EmissionsKg.setText("")
                setRadioButtonChecked(rbKilometer)
                binding!!.btnCalculate.isEnabled=true
                tlCo2EmissionsKg.isVisible=false
                setThemeForView(binding!!.btnCalculate)
            }
            ivClearSource.clickWithDebounce {
                telSearchSource.setText("")
                searchIconSetSource()
            }
            ivClearDestination.clickWithDebounce {
                telSearchDestination.setText("")
                searchIconSetDestination()
            }
            telSearchSource.setOnClickListener {
                if (binding!!.telTransportMode.text?.isEmpty() == true){
                    binding!!.tvTransportModeError.isVisible = true
                    binding!!.tvTransportModeError.text =
                        getString(R.string.please_select_a_transport_mode)
                }else {
                    binding!!.tvTransportModeError.isVisible = false
                    pushFragment(SearchListFragment.newInstance(true))
                }
            }
            telSearchDestination.setOnClickListener {
                pushFragment(SearchListFragment.newInstance(false))
            }
        }
    }


    /**
     * This method contains code openBottomSheetWithSelected and date set
     *
     */
    private fun openBottomSheetWithSelected(itemTransportDetailsList: ArrayList<ItemTransportDetails>) {
        itemTransportDetailsList.forEach {
            if (viewModel.transportType==it.key){
                it.isSelected=true
            }
        }

        binding?.apply {
            val dialog = TransportDetailBottomSheetFragment.newInstance(itemTransportDetailsList)

            dialog.sendClickListener = {
                telTransportType.setText(it.label)
                viewModel.transportType = it.key
                if (viewModel.invoiceDetailsData.vehicleSubTypeLabel==it.label){
                    btnCalculate.backgroundTintList=(ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorSearchHint)))
                    btnCalculate.isEnabled=false
                }else{
                    btnCalculate.isEnabled=true
                    setThemeForView(btnCalculate)
                }
                dialog.dismiss()
            }
            dialog.show(childFragmentManager, "")

        }
    }

    override fun initComponents() {
        setToolbar()
        setUpWebView()
        themingSet()
        if (isFirstTimeLoad) {
            viewModel.callGetFuelTypeId()
        }
        initRecyclerImageView()
    }

    private fun themingSet() {
        binding?.apply {
            clInvoiceDetails.constraintLayoutBackgroundColor(
                BaseApplication.tenantSharedPreference.getTenantPrefModel(
                    EasyPref.TENANT_DATA, TenantInfoModel::class.java
                )?.brandingInfo?.primaryColor
            )
            clExpenseDetails.constraintLayoutBackgroundColor(
                BaseApplication.tenantSharedPreference.getTenantPrefModel(
                    EasyPref.TENANT_DATA,
                    TenantInfoModel::class.java
                )?.brandingInfo?.primaryColor
            )
            clProductDetails.constraintLayoutBackgroundColor(
                BaseApplication.tenantSharedPreference.getTenantPrefModel(
                    EasyPref.TENANT_DATA,
                    TenantInfoModel::class.java
                )?.brandingInfo?.primaryColor
            )
            clSelfComments.constraintLayoutBackgroundColor(
                BaseApplication.tenantSharedPreference.getTenantPrefModel(
                    EasyPref.TENANT_DATA,
                    TenantInfoModel::class.java
                )?.brandingInfo?.primaryColor
            )
            clRejectionComments.constraintLayoutBackgroundColor(
                BaseApplication.tenantSharedPreference.getTenantPrefModel(
                    EasyPref.TENANT_DATA,
                    TenantInfoModel::class.java
                )?.brandingInfo?.primaryColor
            )
            clSupplierInformation.constraintLayoutBackgroundColor(
                BaseApplication.tenantSharedPreference.getTenantPrefModel(
                    EasyPref.TENANT_DATA,
                    TenantInfoModel::class.java
                )?.brandingInfo?.primaryColor
            )
            setThemeForView(btnCalculate)
        }
    }

    private fun setUpWebView() {
//        val webView = binding?.wvBill
//        val webSettings = webView?.settings
//        webSettings?.javaScriptEnabled = true
//        webSettings?.useWideViewPort = true
//        webSettings?.loadWithOverviewMode = true
//
//        binding?.wvBill?.webViewClient = object : WebViewClient() {
//            override fun onPageFinished(view: WebView?, url: String?) {
//                super.onPageFinished(view, url)
//                Log.e("WebView", "Page finished loading :$url ")
//                showProgressDialog(false)
//
//            }
//            override fun onReceivedError(
//                view: WebView?,
//                request: WebResourceRequest?,
//                error: WebResourceError?
//            ) {
//                super.onReceivedError(view, request, error)
//                Log.e("WebView", "Error loading URL: $error")
//
//                Toast.makeText(context, "Failed to load PDF", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    private fun setToolbar() {
        binding?.toolbar?.let {
            overrideToolbar(it, ToolbarConfig().apply {
                showBackButton = true
                showWhiteBg = true
                showViewLine = true
                showNotificationIcon = false
                centerTitle = resources.getString(R.string.invoice_details)
            })

            it.ivBack.clickWithDebounce {
                popFragment()
            }
            it.ivNotification.clickWithDebounce {
                pushFragment(NotificationFragment())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        hideTabs()
        EventBus.getDefault().register(this)
    }

    companion object {
        const val INVOICE_ID = "invoice_id"
        const val NOTIFICATION = "NOTIFICATION"

        @JvmStatic
        fun newInstance(invoiceId: String = "",isNotification:Boolean=false) = InvoiceDetailsFragment().apply {
            arguments = Bundle().apply {
                putString(INVOICE_ID, invoiceId)
                putBoolean(NOTIFICATION, isNotification)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    /**
     * Notification count update
     *
     * @param updateNotificationCount
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun notificationCountUpdate(updateNotificationCount: UpdateNotificationCount) {
        /* if (updateNotificationCount.pushNotificationCount!! > 0) {
             binding!!.toolbar.tvNotificationCount.visibility = View.VISIBLE
             binding!!.toolbar.ivNotification.visibility = View.VISIBLE
             binding!!.toolbar.tvNotificationCount.visibility = updateNotificationCount.pushNotificationCount?:0
         } else {
             binding!!.toolbar.tvNotificationCount.visibility = View.GONE
             binding!!.toolbar.ivNotification.visibility = View.VISIBLE
         }*/
    }

    private fun isValid(): Boolean {
        var valid = true
        binding?.apply {

            when (invoiceCo2Type) {
                "transport" -> {
                   tvTransportModeError.isVisible=false
                    tvTransportTypeError.isVisible=false
                    tvDistanceKmError.isVisible=false
                    if (telDistanceKm.getTrimText().isEmpty()) {
                        tvDistanceKmError.isVisible = true
                        tvDistanceKmError.text = getString(R.string.please_enter_the_distance_value)
                        valid = false
                    }
                    if (telTransportType.getTrimText().isEmpty()) {
                        tvTransportTypeError.isVisible = true
                        tvTransportTypeError.text =
                            getString(R.string.please_select_a_transport_type)
                        valid = false
                    }
                    if (telTransportMode.getTrimText().isEmpty()) {
                        tvTransportModeError.isVisible = true
                        tvTransportModeError.text =
                            getString(R.string.please_select_a_transport_mode)
                        valid = false
                    }

                }
                "fuel" -> {
                    tvFuelTypeError.isVisible=false
                    tvFuelQuantityError.isVisible=false
                    if (telFuelType.getTrimText().isEmpty()) {
                        tvFuelTypeError.isVisible = true
                        tvFuelTypeError.text = getString(R.string.please_select_a_fuel_type)
                        valid = false
                    }
                    if (telFuelQuantity.getTrimText().isEmpty()) {
                        tvFuelQuantityError.isVisible = true
                        tvFuelQuantityError.text =
                            getString(R.string.please_enter_the_fuel_quantity)
                        valid = false
                    }

                }
                else -> {

                }
            }
        }
        return valid
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerView() {
        binding?.apply {
            invoiceDetailsAdapter = viewModel.invoiceDetailsData.currencySymbol?.let {
                InvoiceDetailsAdapter(requireContext(), invoiceList, it,viewModel.invoiceDetailsData.currencyCode.toString(),viewModel.invoiceDetailsData.employeeRemarks.toString()) { view, model, position ->
                    when (view.id) {

                    }
                }
            }
            rvDetails.layoutManager = LinearLayoutManager(requireContext())
            rvDetails.adapter = invoiceDetailsAdapter

        }
    }

    private fun setRadioButtonChecked(rb: AppCompatRadioButton) {
        rb.setTextColor(requireActivity().getColorFromAttr(com.google.android.material.R.attr.editTextColor))
        CompoundButtonCompat.setButtonTintList(rb, ColorStateList.valueOf(requireActivity().getColorFromAttr(com.google.android.material.R.attr.editTextColor)))
        rb.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular_400)
        if (rb.isChecked) {
            rb.textColor(tenantInfoData?.brandingInfo?.secondaryColor)
            rb.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_medium_500)
            if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES){
                CompoundButtonCompat.setButtonTintList(rb, ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.color_secondary)))
            }else{
                CompoundButtonCompat.setButtonTintList(rb,ColorStateList.valueOf(Color.parseColor(tenantInfoData?.brandingInfo?.secondaryColor)))
            }
        }
    }
    private fun setRgExpenseValue(checkedId: Int) {
        binding?.apply {
            when (checkedId) {
                R.id.rb_kilometer-> {
                    setRadioButtonChecked(rbKilometer)
                    setRadioButtonChecked(rbAddress)
                        clAddress.isVisible=false
                    viewModel.distanceTypeData="km"
             /*       binding!!.btnCalculate.isEnabled=true
                    setThemeForView(binding!!.btnCalculate)*/


                }
                R.id.rb_address -> {
                    setRadioButtonChecked(rbKilometer)
                    setRadioButtonChecked(rbAddress)
                    clAddress.isVisible=true
                    viewModel.distanceTypeData="address"

                }

            }
        }
    }


    private fun initRecyclerImageView() {

        showDocImageAdapter = ShowDocImageAdapter(requireContext(), invoiceRefDocListRes) { view, model, position ->
            when (view.id) {
                R.id.iv_close -> {
                    showProgressDialog(true)
                    DownloadFile(baseActivity).downloadPDF(
                        model.url.toString(),
                        findUrlName(model.url.toString()),
                        object : IOnDownloadListener {
                            override fun onSuccess(filePath: String) {
                               // Log.e("Download============", "${pdfFileName}")
                                showProgressDialog(false)
//                        pushFragment(DownLoadPdfFragment.newInstance(pdfFileUrl,pdfFileName))
                                showSuccessMessage(getString(R.string.file_downloaded_to_documents_folder))
                            }

                            override fun onFailure() {
                                showProgressDialog(false)
                                Log.e("Download", "onFailure")
                            }

                            override fun onInProgress() {
                                Log.e("Downloaded", "InProgress")
                            }
                        })
                }
                R.id.card_view_doc ->{
                    pushFragment(DownLoadPdfFragment.newInstance(model.url.toString(),findUrlName(model.url.toString())))
                }
            }
        }
        binding!!.rvAdditional.layoutManager = LinearLayoutManager(activity)
        binding!!.rvAdditional.adapter = showDocImageAdapter

    }
}