package de.fast2work.mobility.ui.invoice

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import de.fast2work.mobility.R
import de.fast2work.mobility.databinding.FragmentDownLoadPdfBinding
import de.fast2work.mobility.databinding.FragmentInvoiceBinding
import de.fast2work.mobility.databinding.FragmentProfileBinding
import de.fast2work.mobility.ui.core.BaseVMBindingFragment
import de.fast2work.mobility.ui.sidemenu.notification.NotificationFragment
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import java.net.URLEncoder

/**
 * A simple [Fragment] subclass.
 * Use the [DownLoadPdfFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DownLoadPdfFragment : BaseVMBindingFragment<FragmentDownLoadPdfBinding, InvoiceViewModel>(InvoiceViewModel::class.java) {


    private var  filePathDown=""
    private var  pdfName=""
    companion object {
        const val PARAM_FILE_PATH="filePath"
        const val PARAM_PDF_NAME="pdfName"

        fun newInstance(filePath: String,pdfName:String) = DownLoadPdfFragment().apply {
            this.arguments = Bundle().apply {
                this.putString(PARAM_FILE_PATH, filePath)
                this.putString(PARAM_PDF_NAME, pdfName)

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return generateBinding(FragmentDownLoadPdfBinding.inflate(inflater), container)
    }
    override fun attachObservers() {
    }

    override fun initComponents() {
            filePathDown = requireArguments().getString(PARAM_FILE_PATH, "")
            pdfName = requireArguments().getString(PARAM_PDF_NAME, "")
            setData()
            setToolbar()

    }

    private fun setData() {
        binding?.apply {

            if(filePathDown.contains(".pdf",true)){
                webView.loadUrl("https://docs.google.com/gview?embedded=true&url=${filePathDown}")
            }else{
                webView.loadUrl(filePathDown)
            }
            webView.settings.builtInZoomControls = true
            webView.settings.displayZoomControls = false
            webView.settings.useWideViewPort = true
            webView.setInitialScale(50)
            webView.settings.defaultZoom = WebSettings.ZoomDensity.FAR

            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {

                    val url = "${request.url}"
                    view.loadUrl(url)
                    return false
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        hideTabs()
    }
    override fun setClickListener() {
    }
    private fun setToolbar() {
        binding!!.toolbar.let {
            overrideToolbar(it, ToolbarConfig().apply {
                showWhiteBg = true
                showBackButton=true
                showViewLine = true
                centerTitle= pdfName
            })


            it.ivBack.clickWithDebounce {
                    popFragment()
            }
        }
    }
}