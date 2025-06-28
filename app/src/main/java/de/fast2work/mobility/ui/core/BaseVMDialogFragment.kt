package de.fast2work.mobility.ui.core

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

/**
 * This class contains code for binding bottom sheet fragment with view model
 *
 * @param T
 * @param VM
 * @property viewModelClass
 */
abstract class BaseVMDialogFragment<T : ViewBinding, VM : BaseViewModel>(private var viewModelClass: Class<VM>) : DialogFragment() {

    lateinit var binding: T
    lateinit var viewModel: VM

    protected fun generateBinding(viewBinding: T): View {
        binding = viewBinding
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this)[viewModelClass]

    }
    fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}
