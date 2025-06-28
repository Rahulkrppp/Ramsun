package de.fast2work.mobility.ui.sidemenu.staticpage


import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import de.fast2work.mobility.R
import de.fast2work.mobility.data.request.StaticPageReq
import de.fast2work.mobility.databinding.ActivityStaticPageBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingActivity
import de.fast2work.mobility.ui.sidemenu.notification.NotificationFragment
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.extension.hideKeyboard
import de.fast2work.mobility.utility.preference.EasyPref

/**
 *  Activity used for static page(Privacy policy, Terms & condition, About us, End user agreement)
 */
class StaticPageActivity : BaseVMBindingActivity<ActivityStaticPageBinding, StaticPageViewModel>(StaticPageViewModel::class.java) {

    private var pageCodeToSend = ""
    private var pageTitle = ""

    companion object {
        const val PARAM_PAGE_CODE = "page_code"

        fun newInstance(activity: Activity, pageCode: String = "") = Intent(activity, StaticPageActivity::class.java).apply {
            putExtra(PARAM_PAGE_CODE, pageCode)
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBindingView(ActivityStaticPageBinding.inflate(layoutInflater))
        getData()
    }

    override fun attachObservers() {
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
        getData()
        setToolBar()
        binding.webView.setBackgroundColor(Color.TRANSPARENT)
        callStaticPageApi()

    }

    override fun setClickListener() {

    }

    private fun getData() {
        viewModel.apply {
            pageCodeToSend = intent.getStringExtra(StaticPageFragment.PARAM_PAGE_CODE).toString()
            when (pageCodeToSend) {
                StaticPageReq.PRIVACYPOLICY ->{
                    pageTitle = getString(R.string.title_privacy_policy)
                    viewModel.pageIndex=2
                }
                StaticPageReq.ABOUTUS -> {
                    pageTitle = getString(R.string.title_about_us)
                    viewModel.pageIndex=4
                }
                StaticPageReq.TERMSOFUSE -> {
                    viewModel.pageIndex=1
                    pageTitle = getString(R.string.terms_of_use)
                }

            }
        }
    }


    private fun setToolBar() {
        viewModel.apply {
            if (intent != null) {
                isFromMyProfile = intent?.getBooleanExtra("No", false) ?: false
            }

            binding?.customToolbar?.let {
                overrideToolbar(it, ToolbarConfig().apply {
                    /*if (isFromMyProfile) {
                        showBackButton = true
                    } else {
                        showMenuButton = true
                    }*/
                    showNotificationIcon = false
                    showBackButton=true
                    centerTitle = pageTitle
                    showViewLine = true
                })

                /*it.ivMenu.setOnClickListener {
                    toggleDrawer()
                }*/

                it.ivNotification.clickWithDebounce {
                    //pushFragment(NotificationFragment())
                }

                it.ivBack.clickWithDebounce {
                    finish()
                }
            }
        }
    }

    private fun callStaticPageApi() {
        hideKeyboard(activity = this)
        viewModel.callStaticPageApi(viewModel.pageIndex)

    }

}