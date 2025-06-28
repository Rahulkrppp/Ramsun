package de.fast2work.mobility.ui.sidemenu.notification

import android.content.res.ColorStateList
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.R
import de.fast2work.mobility.data.eventbus.UpdateNotificationCount
import de.fast2work.mobility.data.response.NotificationData
import de.fast2work.mobility.data.response.NotificationItem
import de.fast2work.mobility.databinding.ActivityLoginBinding
import de.fast2work.mobility.databinding.FragmentNotificationBinding
import de.fast2work.mobility.ui.authentication.login.LoginViewModel
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.ui.core.BaseVMBindingActivity
import de.fast2work.mobility.ui.invoice.invoicedetails.InvoiceDetailsFragment
import de.fast2work.mobility.ui.sidemenu.notification.adapter.NotificationAdapter
import de.fast2work.mobility.ui.upload.UploadInvoiceFragment
import de.fast2work.mobility.utility.customview.DividerItemDecorator
import de.fast2work.mobility.utility.customview.toolbar.ToolbarConfig
import de.fast2work.mobility.utility.dialog.DialogUtil
import de.fast2work.mobility.utility.extension.buttonTextColorNotification
import de.fast2work.mobility.utility.extension.getAndroidDeviceId
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.getString
import org.greenrobot.eventbus.EventBus

class NotificationActivity : BaseVMBindingActivity<FragmentNotificationBinding, NotificationViewModel>(NotificationViewModel::class.java) {

    private var notificationAdapter: NotificationAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBindingView(FragmentNotificationBinding.inflate(layoutInflater))

    }

    override fun attachObservers() {
        viewModel.notificationListApiLiveData.observe(this) {
            viewModel.notificationArrayList.clear()
            it.data?.data?.let { it1 -> viewModel.notificationArrayList.addAll(it1) }
            notificationAdapter?.notifyDataSetChanged()
            setData()

        }

        viewModel.notificationDeleteLiveData.observe(this) {
            //  binding!!.tvMsgCountDisplay.text= getString(R.string.message, notificationAdapter?.itemCount.toString() + " " + "message")
            setData()
        }

        viewModel.notificationCountListLiveData.observe(this) {
            if (it.isSuccess){
                BaseApplication.notificationCount.postValue(it.data?.unreadNotificationCount)
                EventBus.getDefault().post(UpdateNotificationCount(it.data?.unreadNotificationCount))
//                if ((it.data?.unreadNotificationCount ?: 0) > 0) {
//                    BaseApplication.isUnreadNotificationAvailable = true
//                    EventBus.getDefault().post(UpdateNotificationCount(it.data?.unreadNotificationCount))
//                } else {
//                    BaseApplication.isUnreadNotificationAvailable = false
//                    EventBus.getDefault().post(UpdateNotificationCount(it.data?.unreadNotificationCount))
//                }
//                    //BaseApplication.isUnreadNotificationAvailable = true
////                    EventBus.getDefault().post(UpdateNotificationCount(it.data?.unreadNotificationCount))
            }
        }

        viewModel.notificationReadUnreadLiveData.observe(this) {
            viewModel.callNotificationCountApi()
//            notificationAdapter?.notifyDataSetChanged()
//            viewModel.callNotificationListApi()

        }
        viewModel.notificationMarkAllReadApiApiLiveData.observe(this){
            viewModel.callNotificationListApi()
            viewModel.callNotificationCountApi()
            // notificationAdapter?.notifyDataSetChanged()
        }
    }

    private fun setData() {
        binding!!.clNotification.isVisible = true
        binding!!.clNoData.isVisible = false
        if (notificationAdapter?.itemCount == 0) {
            binding!!.clNotification.isVisible = false
            binding!!.clNoData.isVisible = true
        } else if (notificationAdapter?.itemCount!! > 1) {
            binding!!.tvMsgCountDisplay.text = getString(R.string.message, viewModel.notificationArrayList.size.toString() + " " + getString(R.string.messages_captial))
        } else {
            binding!!.tvMsgCountDisplay.text = getString(R.string.message, viewModel.notificationArrayList.size.toString() + " " + getString(
                R.string.message_without_s
            ))
        }
    }

    override fun initComponents() {
        setToolbar()
        //setThemeForButtonTextView(binding!!.tvMsgCountDisplay)
        binding!!.tvMsgCountDisplay.buttonTextColorNotification(tenantInfoData?.brandingInfo?.primaryColor)
        initRecyclerView()
        viewModel.callNotificationListApi()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun setClickListener() {
        binding?.apply {
            tvMakeAllAsRead.clickWithDebounce {
                viewModel.notificationArrayList.forEachIndexed { index, notificationItem ->
                    if (notificationItem.isRead==getString(R.string._0)){
                        viewModel.callNotificationMarkAllReadApi()

                    }
                }
                viewModel.callNotificationCountApi()
            }
            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing=false
                viewModel.callNotificationListApi()
            }
        }
    }

    private fun setToolbar() {
        binding!!.toolbar.let {
            overrideToolbar(it, ToolbarConfig().apply {
                showBackButton = true
                showWhiteBg = true
                showViewLine = true
                centerTitle = getString(R.string.notifications)
            })
            it.ivBack.clickWithDebounce {
                finish()
            }

        }
    }

    private fun initRecyclerView() {
        notificationAdapter = NotificationAdapter(this, viewModel.notificationArrayList) { view, model, position ->
            when (view.id) {
                R.id.iv_delete -> {
                    DialogUtil.showDialog(supportFragmentManager, getString(R.string.delete_), getString(R.string.are_you_sure_want_to_delete_the_notification) ,
                        getString(R.string.yes), getString(R.string.no), object : DialogUtil.IL {
                            override fun onSuccess() {
                                if (model.isRead == "0") {
                                    viewModel.callNotificationReadUnReadApi(model.notificationId, "1")
                                    viewModel.callNotificationCountApi()
                                }
                                val idsList = ArrayList<Int>()
                                model.notificationId?.let { idsList.add(it) }
                                viewModel.callDeleteNotificationApi(idsList, getAndroidDeviceId())
                                viewModel.notificationArrayList.remove(model)
                                notificationAdapter?.notifyItemRemoved(position)
                            }

                            override fun onCancel(isNeutral: Boolean) {
                            }
                        }, isCancelShow = false)

                }

                R.id.cl_main_notification -> {
                    val bgView = view.findViewById<ConstraintLayout>(R.id.cl_main_notification)
                    bgView.backgroundTintList = (ColorStateList.valueOf(getColorFromAttr(com.google.android.material.R.attr.backgroundColor)))

                    if (model.isRead == "0") {
                        viewModel.callNotificationReadUnReadApi(model.notificationId, "1")
                    }else{
                        manageNotificationRedirection(model)
                    }
                }
            }
        }
        binding!!.rvNotification.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration: RecyclerView.ItemDecoration = DividerItemDecorator(ContextCompat.getDrawable(this, R.drawable.divider)!!)
        binding!!.rvNotification.addItemDecoration(dividerItemDecoration)
        binding!!.rvNotification.adapter = notificationAdapter

    }

    private fun manageNotificationRedirection(notification: NotificationItem) {
        when (notification.notificationData?.refType) {
            NotificationData.INVOICE_DETAIL -> {
//                pushFragment(InvoiceDetailsFragment.newInstance(notification.notificationData.refId.toString()))
            }

            NotificationData.UPLOAD_INVOICE -> {
//                pushFragment(UploadInvoiceFragment())
            }
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.addToBackStack(null) // Optional: add to back stack
        fragmentTransaction.commit()
    }
}