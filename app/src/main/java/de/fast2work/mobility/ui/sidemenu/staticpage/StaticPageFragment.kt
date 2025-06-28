package de.fast2work.mobility.ui.sidemenu.staticpage


import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import de.fast2work.mobility.R
import de.fast2work.mobility.data.eventbus.UpdateNotificationCount
import de.fast2work.mobility.data.request.StaticPageReq
import de.fast2work.mobility.databinding.ActivityStaticPageBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.sidemenu.notification.NotificationFragment
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.extension.getString
import de.fast2work.mobility.utility.extension.hideKeyboard
import de.fast2work.mobility.utility.helper.DownloadFile
import de.fast2work.mobility.utility.helper.IOnDownloadListener
import de.fast2work.mobility.utility.preference.EasyPref
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 *  Fragment used for static page(Privacy policy, Terms & condition, About us, End user agreement)
 */
class StaticPageFragment : BaseVMBindingFragment<ActivityStaticPageBinding, StaticPageViewModel>(StaticPageViewModel::class.java) {

    companion object {
        const val PARAM_PAGE_CODE = "page_code"
        const val PARAM_ISFROM = "isFrom"
        const val PARAM_EMP_REP = "employeeReportPdfFile"
        fun newInstance(pageCode: String?, isFromSettings: Boolean,employeeReportPdfFile:String="") = StaticPageFragment().apply {
            this.arguments = Bundle().apply {
                putString(PARAM_PAGE_CODE, pageCode)
                putBoolean(PARAM_ISFROM, isFromSettings)
                putString(PARAM_EMP_REP, employeeReportPdfFile)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return generateBinding(ActivityStaticPageBinding.inflate(inflater), container)
    }

    override fun attachObservers() {
        BaseApplication.notificationCount.observe(this){
           /* if (it > 0) {
                binding!!.customToolbar.tvNotificationCount.visibility = View.VISIBLE
                binding!!.customToolbar.ivNotification.visibility = View.VISIBLE
                binding!!.customToolbar.tvNotificationCount.text = it.toString()
            } else {
                binding!!.customToolbar.tvNotificationCount.visibility = View.GONE
                binding!!.customToolbar.ivNotification.visibility = View.VISIBLE
            }*/
        }
       viewModel.staticPageLiveData.observe(this) { response ->
            if (response?.data != null) {
                val data = response.data!!
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
                binding!!.webView.clearCache(true)
                binding!!.webView.setBackgroundColor(Color.TRANSPARENT)
                binding?.webView?.settings?.javaScriptEnabled = true
                binding?.webView?.isVerticalScrollBarEnabled = false
                binding!!.webView.loadDataWithBaseURL(null, stringHtml/*.replace("color:#31344B", "")*/, "text/html", "UTF-8", null)

            } else {
                //binding?.loadingView?.error(response.settings?.message)
            }
        }
    }

    override fun initComponents() {
        viewModel.isFrom = arguments?.getBoolean(PARAM_ISFROM, false) ?: false
        viewModel.employeeReportPdfFile = arguments?.getString(PARAM_EMP_REP).toString()
        getData()
        setToolBar()
//        binding?.webView?.setBackgroundColor(Color.TRANSPARENT)
        if (viewModel.isFrom){
            openHtmlPageInWebview()
        }else{
            callStaticPageApi()
        }
    }
    private fun openHtmlPageInWebview() {
        Log.e("=============","webView:: Load url:: ${viewModel.pageCodeToSend}")
        binding!!.webView.settings.builtInZoomControls = true
        binding!!.webView.settings.displayZoomControls = false
        binding!!.webView.settings.useWideViewPort = true
        binding!!.webView.setInitialScale(50)
        binding!!.webView.settings.defaultZoom = WebSettings.ZoomDensity.FAR
        binding?.webView?.settings?.javaScriptEnabled = true
        binding?.webView?.isVerticalScrollBarEnabled = false
        binding?.webView?.settings?.allowFileAccess = true
        binding?.webView?.settings?.allowFileAccessFromFileURLs = true
        binding?.webView?.setBackgroundColor(Color.WHITE)

        viewModel.pageCodeToSend = viewModel.pageCodeToSend/*.ifEmpty {
            "https://d1a6mr3nq6i4pn.cloudfront.net/co2Reports/Ola%20company/employee/49/index.html"
        }*/


        val filename = viewModel.pageCodeToSend.substring(viewModel.pageCodeToSend.lastIndexOf('/') + 1)

        // Load the URL
        binding!!.webView.webViewClient = WebViewClient()

        showProgressDialog(true)
        DownloadFile(baseActivity).downloadPDF(viewModel.pageCodeToSend, filename, object : IOnDownloadListener {
            override fun onSuccess(filePath: String) {
                showProgressDialog(false)
                binding!!.webView.loadUrl(filePath)
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

    override fun setClickListener() {

    }

    private fun getData() {
        viewModel.apply {
            pageCodeToSend = arguments?.getString(PARAM_PAGE_CODE).toString()

            when (pageCodeToSend) {
                StaticPageReq.PRIVACYPOLICY ->{
                    pageTitle = getString(R.string.title_privacy_policy)
                    viewModel.pageIndex=2
                }
                StaticPageReq.ABOUTUS -> {
                    pageTitle = getString(R.string.imprint)
                    viewModel.pageIndex=4
                }
                StaticPageReq.TERMSOFUSE -> {
                    viewModel.pageIndex=1
                    pageTitle = getString(R.string.terms_of_use)
                }
                StaticPageReq.DECLARATION -> {
                    viewModel.pageIndex=3
                    pageTitle = getString(R.string.declaration)
                }

            }
        }
    }

    private fun setToolBar() {
        viewModel.apply {
            if (arguments != null) {
                isFromMyProfile = arguments?.getBoolean("No", false) ?: false
            }

            binding?.customToolbar?.let {
                overrideToolbar(it, ToolbarConfig().apply {
                    /*if (isFromMyProfile) {
                        showBackButton = true
                    } else {
                        showMenuButton = true
                    }*/
                    if(viewModel.isFrom){
                        showNotificationIcon=true
                        it.ivNotification.setImageResource(R.drawable.ic_download)

                    }

                    showBackButton=true
                    if (viewModel.isFrom){
                        centerTitle = getString(R.string.view_report)
                    }else{
                        centerTitle = pageTitle
                    }
                    showViewLine = true
                })

                /*it.ivMenu.setOnClickListener {
                    toggleDrawer()
                }*/

                it.ivNotification.clickWithDebounce {
                    showProgressDialog(true)
                    DownloadFile(baseActivity).downloadPDF(viewModel.employeeReportPdfFile, viewModel.employeeReportPdfFile?.substring(viewModel.employeeReportPdfFile.lastIndexOf('/') + 1) ?: "", object : IOnDownloadListener {
                        override fun onSuccess(filePath: String) {
                            Log.e("Download============", "${viewModel.employeeReportPdfFile}")
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
                   // pushFragment(NotificationFragment())
                }

                it.ivBack.clickWithDebounce {
                    popFragment()
                }
            }
        }
    }

    private fun callStaticPageApi() {
        hideKeyboard(activity = requireActivity())
            viewModel.callStaticPageApi(viewModel.pageIndex)

    }

    override fun onStart() {
        super.onStart()
        hideTabs()
        //EventBus.getDefault().register(this)
    }

    /**
     * Notification count update
     *
     * @param updateNotificationCount
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun notificationCountUpdate(updateNotificationCount: UpdateNotificationCount) {
       /* if (updateNotificationCount.pushNotificationCount!! > 0) {
            binding!!.customToolbar.tvNotificationCount.visibility = View.VISIBLE
            binding!!.customToolbar.ivNotification.visibility = View.VISIBLE
            binding!!.customToolbar.tvNotificationCount.visibility = updateNotificationCount.pushNotificationCount?:0
        } else {
            binding!!.customToolbar.tvNotificationCount.visibility = View.GONE
            binding!!.customToolbar.ivNotification.visibility = View.VISIBLE
        }*/
    }

    override fun onStop() {
        super.onStop()
        //EventBus.getDefault().unregister(this)
    }
}