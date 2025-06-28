package de.fast2work.mobility.ui.core

import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding

/**
 * this class contains common things used in dialog
 *
 * @param T
 */
abstract class BaseDialogFragment <T : ViewBinding>: DialogFragment() {

    lateinit var binding: T

    protected fun generateBinding(viewBinding:T): View {
        binding = viewBinding
        return binding.root
    }

}