package de.fast2work.mobility.utility.helper

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import de.fast2work.mobility.R
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.utility.dialog.DialogUtil.addTouchEvent


class DialogFragmentHelper<VB : ViewBinding>(private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB) : DialogFragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!

    var callback: ((binding: VB, context: Context, dialogFragment: DialogFragment) -> Unit)? = null

    companion object {

        const val FULL_WIDTH = 1
        const val FULL_HEIGHT = 2
        const val FULL_WIDTH_HEIGHT = 3
        private const val ARG_IS_CANCELLABLE_ON_TOUCH_OUTSIDE = "IS_CANCELLABLE_ON_TOUCH_OUTSIDE"
        private const val ARG_FULL_SCREEN_TYPE = "FULL_SCREEN_TYPE"

        fun <VB : ViewBinding> with(inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB, fullScreenType: Int = FULL_WIDTH_HEIGHT,
                                    isCancellable: Boolean = true,
                                    callback: (binding: VB, context: Context, dialogFragment: DialogFragment) -> Unit): DialogFragment {
            val dialog = DialogFragmentHelper(inflate).apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_CANCELLABLE_ON_TOUCH_OUTSIDE, isCancellable)
                    putInt(ARG_FULL_SCREEN_TYPE, fullScreenType)
                }
            }
            dialog.isCancelable = isCancellable
            dialog.callback = callback
            return dialog
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callback?.invoke(binding, requireContext(), this)
        if (callback == null) {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (BaseApplication.themeValue == Configuration.UI_MODE_NIGHT_YES) {
            dialog.window!!.setBackgroundDrawableResource(R.color.color_primary_dark_text_80)
        } else {
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        }
        dialog.setCanceledOnTouchOutside(arguments?.getBoolean(ARG_IS_CANCELLABLE_ON_TOUCH_OUTSIDE) ?: true)
        dialog.addTouchEvent()
        return dialog
    }

    override fun onStart() {
        super.onStart()

        if (dialog != null && arguments?.containsKey(ARG_FULL_SCREEN_TYPE) == true) {

            when (arguments?.getInt(ARG_FULL_SCREEN_TYPE, FULL_WIDTH) ?: FULL_WIDTH) {
                FULL_WIDTH -> {
                    dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                }
                FULL_HEIGHT -> {
                    dialog?.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                }
                FULL_WIDTH_HEIGHT -> {
                    dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}