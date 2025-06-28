package de.fast2work.mobility.ui.authentication.signup.bottom

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.StaticPage
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.databinding.FragmentTermAndConditionBottomSheetBinding
import de.fast2work.mobility.ui.category.CategoryFragment
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBottomSheetDialogFragment
import de.fast2work.mobility.ui.profile.bottom.CountryCodeViewModel
import de.fast2work.mobility.ui.sidemenu.staticpage.StaticPageFragment
import de.fast2work.mobility.utility.extension.SCREEN_HEIGHT
import de.fast2work.mobility.utility.extension.backgroundColorTint
import de.fast2work.mobility.utility.extension.clickWithDebounce
import de.fast2work.mobility.utility.extension.parcelable
import de.fast2work.mobility.utility.helper.DownloadFile
import de.fast2work.mobility.utility.helper.IOnDownloadListener
import de.fast2work.mobility.utility.preference.EasyPref


class TermAndConditionBottomSheetFragment : BaseVMBottomSheetDialogFragment<FragmentTermAndConditionBottomSheetBinding, CountryCodeViewModel>(
    CountryCodeViewModel::class.java){

    var staticPage=StaticPage()
    var sendClickListener: (click:Boolean) -> Unit = {}
    private var buttonHide=false
    companion object {
        const val PARAM_STATIC = "page"
        const val PARAM_TERM = "termsAccepted"
        const val PARAM_EMP_REP = "employeeReportPdfFile"

        fun newInstance(staticPage: StaticPage,termsAccepted:Boolean,employeeReportPdfFile:String="") = TermAndConditionBottomSheetFragment().apply {
            this.arguments = Bundle().apply {
                putParcelable(PARAM_STATIC, staticPage)
                putBoolean(PARAM_TERM,termsAccepted)
                putString(StaticPageFragment.PARAM_EMP_REP, employeeReportPdfFile)
            }
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTermAndConditionBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
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
            val height = (SCREEN_HEIGHT * 0.85).toInt()
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
                layoutParams.height = (SCREEN_HEIGHT * 0.85).toInt()
            }
            bottomSheet.layoutParams = layoutParams
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
            val behavior: BottomSheetBehavior<View> = BottomSheetBehavior.from(bottomSheet)
            behavior.isDraggable=false
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

    }

    private fun initComponent() {
        binding.ivDownload.isVisible=true
        val tenantInfoData = BaseApplication.tenantSharedPreference.getTenantPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)
        staticPage = requireArguments().parcelable(PARAM_STATIC)!!
        buttonHide =  requireArguments().getBoolean(PARAM_TERM, false)
        viewModel.employeeReportPdfFile = arguments?.getString(PARAM_EMP_REP).toString()

        setData()
        binding.tvDone.backgroundColorTint(tenantInfoData?.brandingInfo?.primaryColor)
        if (buttonHide){
            binding.llBtn.isVisible=false
        }else{
            binding.llBtn.isVisible=true
            binding.tvDone.isEnabled=false
            binding.tvDone.alpha=0.5f
        }
        binding.tvDone.clickWithDebounce {
            buttonHide=true
            sendClickListener(true)
            dismiss()
        }
        binding.btnClear.clickWithDebounce {
            dismiss()
        }
        binding.ivDownload.clickWithDebounce {
            showProgressDialog(true)
            DownloadFile(requireActivity()).downloadPDF(viewModel.employeeReportPdfFile, viewModel.employeeReportPdfFile?.substring(viewModel.employeeReportPdfFile.lastIndexOf('/') + 1) ?: "", object :
                IOnDownloadListener {
                override fun onSuccess(filePath: String) {
                    Log.e("Download============", "${viewModel.employeeReportPdfFile}")
                    showProgressDialog(false)
//                        pushFragment(DownLoadPdfFragment.newInstance(pdfFileUrl,pdfFileName))
                    //showSuccessMessage(getString(R.string.file_downloaded_to_documents_folder))
                    Toast.makeText(requireContext(), R.string.file_downloaded_to_documents_folder, Toast.LENGTH_SHORT).show()
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

    }

    private fun setData() {
        val data = staticPage
        val pish = "<html><head><style type=\"text/css\">@font-face {font-family: poppins;font-size:12px;src: url('file:///android_asset/fonts/roboto_medium_500.ttf')}body {font-family: roboto;}</style></head><body>"
        val pas = "</body></html>"

        val myHtmlString = if (BaseApplication.languageSharedPreference.getLanguagePref(EasyPref.CURRENT_LANGUAGE, "").equals("de", true)){
            pish + ("<font>" + data.pageContentDe + "</font>") + pas
        }else{
            pish + ("<font>" + data.pageContent + "</font>") + pas
        }
        val stringHtml = if (BaseApplication.themeValue== Configuration.UI_MODE_NIGHT_YES) {
            "<font color= 'white'>$myHtmlString</font>"
        } else {
            "<font color= 'black'>$myHtmlString</font>"

        }
        binding.webView.clearCache(true)
        binding.webView.setBackgroundColor(Color.TRANSPARENT)
        binding.webView.settings?.javaScriptEnabled = true
        binding.webView.isVerticalScrollBarEnabled = false
        binding.webView.loadDataWithBaseURL(null, stringHtml/*.replace("color:#31344B", "")*/, "text/html", "UTF-8", null)

        binding.webView.setOnScrollChangeListener(View.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

            Log.e("", "setData:${scrollY} ", )
            Log.e("", "height:${binding.webView.bottom - binding.webView.height} ", )
            Log.e("", "scrollY:${scrollY - binding.webView.height} ", )
            // Check if the ScrollView has reached the bottom
            if (!v.canScrollVertically(1)) {
                // Reached the bottom
                if (buttonHide){
                    binding.llBtn.isVisible=false
                }else{
                    binding.llBtn.isVisible=true
                    binding.tvDone.alpha=1f
                    binding.tvDone.isEnabled=true
                }
                binding.llBtn.isVisible = !buttonHide

            }
        })
    }

}