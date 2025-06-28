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
import android.webkit.WebView
import android.webkit.WebViewClient
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.User
import de.fast2work.mobility.databinding.ActivityStaticPageBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.preference.EasyPref
import de.fast2work.mobility.utility.util.IConstantsIcon


class WebViewFragment : BaseVMBindingFragment<ActivityStaticPageBinding, DTicketViewModel>(
    DTicketViewModel::class.java
) {

    private var productId = ""
    private var ticketName=""

    companion object {
        const val PARAM_PRODUCT_ID = "productId"
        const val PARAM_TICKET = "TicketName"

        fun newInstance(productId: String?, ticketName: String = "") = WebViewFragment().apply {
            this.arguments = Bundle().apply {
                putString(PARAM_PRODUCT_ID, productId)
                putString(PARAM_TICKET, ticketName)

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
        productId = arguments?.getString(PARAM_PRODUCT_ID).toString()
        ticketName = arguments?.getString(PARAM_TICKET).toString()
        setToolbar()
        showProgressDialog(true)
        Handler(Looper.getMainLooper()).postDelayed({
            showProgressDialog(false)
        }, 2000)

        binding?.apply {
            Log.e(
                "",
                "initComponents77777777: https://fastlink.themobilitybox.com/TEST-65b9a9/checkout?mobilitybox_product_ids%5B%5D=$productId&order_reference=mbm-1733206793&view=fullscreen&skip_successor_selection=true",
            )

            webView.setWebViewClient(object : WebViewClient() {


                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    //Log.e("DATA","URL IS $url")
                    super.onPageStarted(view, url, favicon)
                    if (url != null) {
                        if (url.startsWith("https://biketour.guide")) {
                            val uri = Uri.parse(url)
//                            Log.e("", "mobilitybox_coupon_ids:${uri.getQueryParameter("mobilitybox_coupon_ids")} ", )
//                            Log.e("", "mobilitybox_order_id:${uri.getQueryParameter("mobilitybox_order_id")} ", )
//                            Log.e("", "order_reference:${uri.getQueryParameter("order_reference")} ", )
//                            Log.e("", "mobilitybox_restore_secret:${uri.getQueryParameter("mobilitybox_restore_secret")} ", )

                            BaseApplication.sharedPreference.setPref(EasyPref.D_TICKET, IConstantsIcon.PENDING)
                            showSuccessMessage(getString(R.string.you_will_be_notified_when_your_ticket_is_ready))
                            /*uri.getQueryParameter("mobilitybox_coupon_ids")?.let { BaseApplication.sharedPreference.setPref(EasyPref.COUPON_ID,
                                    it
                                )
                            }*/

                            popFragment(2)
                        }
                    }
                }
                override fun onPageFinished(view: WebView?, url: String?) {
                    //Log.e("DATA","URL IS $url")
                    super.onPageFinished(view, url)
                }

            })

            webView.loadUrl(
                "https://fastlink.themobilitybox.com/TEST-65b9a9/checkout?mobilitybox_product_ids%5B%5D=$productId&order_reference=${
                    BaseApplication.sharedPreference.getPref(EasyPref.order_reference, "")}&skip_successor_selection=true"
            )

            //webView.webViewClient = WebViewClient()


            webView.settings.javaScriptEnabled = true
        }
    }

    override fun setClickListener() {

    }

    private fun setToolbar() {
        binding!!.customToolbar.let {
            overrideToolbar(it, ToolbarConfig().apply {
                showBackButton = true
                centerTitle=ticketName
            })

            it.ivBack.clickWithDebounce {
                popFragment()
            }

        }
    }
}