package de.fast2work.mobility.utility.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import android.widget.LinearLayout
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.databinding.CustomBoxViewBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.utility.extension.backgroundColorTint
import de.fast2work.mobility.utility.preference.EasyPref

class CustomOtpView : LinearLayout {
    var binding: CustomBoxViewBinding? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = CustomBoxViewBinding.inflate(inflater, this, true)
        setUpViews()
//        binding?.edt1?.backgroundColorTint(BaseApplication.tenantSharedPreference.getPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)?.brandingInfo?.primaryColor)
//        binding?.edt2?.backgroundColorTint(BaseApplication.tenantSharedPreference.getPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)?.brandingInfo?.primaryColor)
//        binding?.edt3?.backgroundColorTint(BaseApplication.tenantSharedPreference.getPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)?.brandingInfo?.primaryColor)
//        binding?.edt4?.backgroundColorTint(BaseApplication.tenantSharedPreference.getPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)?.brandingInfo?.primaryColor)
//        binding?.edt5?.backgroundColorTint(BaseApplication.tenantSharedPreference.getPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)?.brandingInfo?.primaryColor)
//        binding?.edt6?.backgroundColorTint(BaseApplication.tenantSharedPreference.getPrefModel(EasyPref.TENANT_DATA, TenantInfoModel::class.java)?.brandingInfo?.primaryColor)
    }

    fun clear() {
        binding?.edt1?.setText("")
        binding?.edt2?.setText("")
        binding?.edt3?.setText("")
        binding?.edt4?.setText("")
        binding?.edt5?.setText("")
        binding?.edt6?.setText("")
        binding?.edt1?.requestFocus()
    }
    fun isValid ():Boolean{
        return !(binding?.edt1?.text?.toString()!!.isEmpty()&& binding?.edt2?.text?.toString()!!.isEmpty() && binding?.edt3?.text?.toString()!!.isEmpty() &&binding?.edt4?.text?.toString()!!.isEmpty() &&binding?.edt5?.text?.toString()!!.isEmpty()&&binding?.edt6?.text?.toString()!!.isEmpty())
    }

    var pairingCode: String
        get() = binding?.edt1?.text.toString() +
                binding?.edt2?.text.toString() +
                binding?.edt3?.text.toString() +
                binding?.edt4?.text.toString() +
                binding?.edt5?.text.toString() +
                binding?.edt6?.text.toString()
        set(otp) {
            if (otp.length >= 6) {
                binding?.edt1?.setText(otp[0].toString())
                binding?.edt2?.setText(otp[1].toString())
                binding?.edt3?.setText(otp[2].toString())
                binding?.edt4?.setText(otp[3].toString())
                binding?.edt5?.setText(otp[4].toString())
                binding?.edt6?.setText(otp[5].toString())
            } else {
                binding?.edt1?.setText("")
                binding?.edt2?.setText("")
                binding?.edt3?.setText("")
                binding?.edt4?.setText("")
                binding?.edt5?.setText("")
                binding?.edt6?.setText("")
                binding?.edt1?.requestFocus()
            }
        }

    fun showKeyboard() {
        binding?.edt1?.requestFocus()
    }

    fun setUpViews() {
        val myTextWatcher1 = MyTextWatcher(binding?.edt1, null, binding?.edt2)
        binding?.edt1?.tag = myTextWatcher1
        val myTextWatcher2 = MyTextWatcher(binding?.edt2, binding?.edt1, binding?.edt3)
        binding?.edt2?.tag = myTextWatcher2
        val myTextWatcher3 = MyTextWatcher(binding?.edt3, binding?.edt2, binding?.edt4)
        binding?.edt3?.tag = myTextWatcher3
        val myTextWatcher4 = MyTextWatcher(binding?.edt4, binding?.edt3, binding?.edt5)
        binding?.edt4?.tag = myTextWatcher4
        val  myTextWatcher5 = MyTextWatcher(binding?.edt5, binding?.edt4, binding?.edt6)
        binding?.edt5?.tag = myTextWatcher5
        val  myTextWatcher6 = MyTextWatcher(binding?.edt6, binding?.edt5, null)
        binding?.edt6?.tag = myTextWatcher6
        val onFocusChangeListener = OnFocusChangeListener { v: View, hasFocus: Boolean ->
            if (hasFocus) {
                val editText = v as EditText
                editText.selectAll()
            }
        }

        val onKeyListener = object : OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (event?.action == KeyEvent.ACTION_DOWN) {
                    //check if the right key was pressed
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        val editText = v as EditText
                        if (editText.text.toString().isEmpty()) {
                            (editText.tag as MyTextWatcher).afterStringChange("")
                            return false
                        }
                    }
                }
                return false
            }
        }

        binding?.edt1?.addTextChangedListener(myTextWatcher1)
        binding?.edt2?.addTextChangedListener(myTextWatcher2)
        binding?.edt3?.addTextChangedListener(myTextWatcher3)
        binding?.edt4?.addTextChangedListener(myTextWatcher4)
        binding?.edt5?.addTextChangedListener(myTextWatcher5)
        binding?.edt6?.addTextChangedListener(myTextWatcher6)
        binding?.edt1?.onFocusChangeListener = onFocusChangeListener
        binding?.edt2?.onFocusChangeListener = onFocusChangeListener
        binding?.edt3?.onFocusChangeListener = onFocusChangeListener
        binding?.edt4?.onFocusChangeListener = onFocusChangeListener
        binding?.edt5?.onFocusChangeListener = onFocusChangeListener
        binding?.edt6?.onFocusChangeListener = onFocusChangeListener
        binding?.edt1?.setOnKeyListener(onKeyListener)
        binding?.edt2?.setOnKeyListener(onKeyListener)
        binding?.edt3?.setOnKeyListener(onKeyListener)
        binding?.edt4?.setOnKeyListener(onKeyListener)
        binding?.edt5?.setOnKeyListener(onKeyListener)
        binding?.edt6?.setOnKeyListener(onKeyListener)
    }

    internal inner class MyTextWatcher(private val editText: EditText?, private val editPrev: EditText?, private val editNext: EditText?) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            afterStringChange(s.toString())
            editText?.isSelected = editText?.text.toString().isNotEmpty()
        }

        fun afterStringChange(s: String) {
            if (s.length == 1) {
                editNext?.requestFocus()
            } else {
                editPrev?.requestFocus()
            }
        }
    }
}