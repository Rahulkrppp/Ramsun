package de.fast2work.mobility.ui.core

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

/**
 * This class contains code for binding activity with view model
 *
 * @param T
 * @param VM
 * @property viewModelClass
 */
abstract class BaseVMBindingActivity<T : ViewBinding, VM : BaseViewModel>(private var viewModelClass: Class<VM>) : BaseBindingActivity<T>() {

    lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[viewModelClass]
        attachObservers()
        viewModel.loadingLiveData.observe(this) {
            showProgressDialog(it)
        }

        viewModel.errorLiveData.observe(this) {
            showErrorMessage(it)
        }

        viewModel.networkLiveData.observe(this) { isConnected ->
            if (!isConnected) {
                onNetworkDisconnected()
            }
        }
    }

    abstract fun attachObservers()

}