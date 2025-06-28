package de.fast2work.mobility.ui.core

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

/**
 * This class contains method to implement view binding and common methods in fragment
 *
 * @param T
 */
abstract class BaseBindingFragment<T : ViewBinding> : BaseFragment() {

    var binding: T? = null

    protected fun generateBinding(viewBinding: T, container: ViewGroup?): View {
        isFirstTimeLoad = false
        if (binding == null) {
            binding = viewBinding
            isFirstTimeLoad = true
        } else if (binding?.root?.parent != null) {
            container?.removeView(binding?.root)
        }
        return binding!!.root
    }



    /**
     * sets required methods
     *
     * @param view
     * @param savedInstanceState
     */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
        setClickListener()
    }

    abstract fun initComponents()
    abstract fun setClickListener()
}
