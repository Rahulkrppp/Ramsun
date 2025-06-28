package de.fast2work.mobility.ui.core

import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

/**
 * This class contains code for binding fragment with view model
 *
 * @param T
 * @param VM
 * @property viewModelClass
 */
abstract class BaseVMBindingFragment<T : ViewBinding, VM : BaseViewModel>(private var viewModelClass: Class<VM>) : BaseBindingFragment<T>() {

    lateinit var viewModel: VM

    override fun onLoad() {
        super.onLoad()
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