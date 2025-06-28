package de.fast2work.mobility.ui.core

import android.view.View
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * This method contains code for implement view binding on Activity
 *
 * @param T
 */
open class BaseBindingBottomSheetDialogFragment<T : ViewBinding> : BottomSheetDialogFragment() {

    lateinit var binding: T

    protected fun generateBinding(viewBinding:T): View {
        binding = viewBinding
        return binding.root
    }

}
