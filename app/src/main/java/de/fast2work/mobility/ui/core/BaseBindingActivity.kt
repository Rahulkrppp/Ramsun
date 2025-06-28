package de.fast2work.mobility.ui.core

import android.content.res.Configuration
import android.content.res.Resources
import androidx.viewbinding.ViewBinding
import de.fast2work.mobility.R


/**
 * This class have common methods used ny all the activities in the app
 *
 * @param T
 */
abstract class BaseBindingActivity<T : ViewBinding> : BaseActivity() {
    lateinit var binding: T

    fun setBindingView(viewBinding: T) {

        binding = viewBinding


        setContentView(binding.root)
        initComponents()
        setClickListener()
    }

    abstract fun initComponents()
    abstract fun setClickListener()


}