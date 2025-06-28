package de.fast2work.mobility.ui.dticket

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import de.fast2work.mobility.databinding.ActivityStaticPageBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.preference.EasyPref
import de.fast2work.mobility.utility.util.IConstantsIcon

class SubscriptionWebFragment : BaseVMBindingFragment<ActivityStaticPageBinding, DTicketViewModel>(
    DTicketViewModel::class.java
) {

    private var url = ""


    companion object {
        const val PARAM_PRODUCT_ID = "url"


        fun newInstance(url: String?) = SubscriptionWebFragment().apply {
            this.arguments = Bundle().apply {
                putString(PARAM_PRODUCT_ID, url)


            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return generateBinding(ActivityStaticPageBinding.inflate(inflater), container)
    }

    override fun attachObservers() {

    }

    override fun onStart() {
        super.onStart()
        hideTabs()
    }

    override fun initComponents() {
        url = arguments?.getString(PARAM_PRODUCT_ID).toString()

        setToolbar()
        showProgressDialog(true)
        Handler(Looper.getMainLooper()).postDelayed({
            showProgressDialog(false)
        }, 2000)

        binding?.apply {


            webView.setWebViewClient(object : WebViewClient() {


                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    //Log.e("DATA","URL IS $url")
                    super.onPageStarted(view, url, favicon)
                  //  Log.e("", "mobilitybox_coupon_ids000000000:${url} ", )

                }

                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                   // Log.d("WebView", "URL loading: $url")
                    checkForCancelUrl(url)
                    return false // Let the WebView handle the URL
                }

            })
            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
            webView.settings.javaScriptCanOpenWindowsAutomatically = true
            webView.loadUrl(url)

            //webView.webViewClient = WebViewClient()



        }

    }
    private fun checkForCancelUrl(url: String) {
        if (url.contains("cancel", ignoreCase = true)) {
            //Log.d("CancelURL", "Cancel URL detected: $url")
            BaseApplication.sharedPreference.setPref(EasyPref.D_TICKET, IConstantsIcon.cancelled)
            // Handle the cancel URL here
        }
    }
    override fun setClickListener() {

    }

    private fun setToolbar() {
        binding!!.customToolbar.let {
            overrideToolbar(it, ToolbarConfig().apply {
                showBackButton = true
                //centerTitle = ticketName
            })

            it.ivBack.clickWithDebounce {
                popFragment()
            }

        }
    }
}