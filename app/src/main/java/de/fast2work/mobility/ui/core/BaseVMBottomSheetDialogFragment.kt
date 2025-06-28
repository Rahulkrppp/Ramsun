package de.fast2work.mobility.ui.core

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import de.fast2work.mobility.utility.dialog.progress.ProgressDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * This class contains code for binding bottom sheet fragment with view model
 *
 * @param T
 * @param VM
 * @property viewModelClass
 */
open class BaseVMBottomSheetDialogFragment<T : ViewBinding, VM : BaseViewModel>(private var viewModelClass: Class<VM>) : BottomSheetDialogFragment() {
    private val progressDialogFragment: ProgressDialogFragment by lazy { ProgressDialogFragment.newInstance() }
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

    /**
     * Hide show progress
     *
     * @param isShow
     */
    fun showProgressDialog(isShow: Boolean) {
        if (isShow) {
            try {
                progressDialogFragment.dismissAllowingStateLoss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            progressDialogFragment.show(activity?.supportFragmentManager!!, ProgressDialogFragment.FRAGMENT_TAG)
        } else {
            try {
                progressDialogFragment.dismissAllowingStateLoss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
