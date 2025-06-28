package de.fast2work.mobility.utility.customview

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import de.fast2work.mobility.R
import de.fast2work.mobility.utility.extension.getColorFromAttr
import java.util.Calendar


class MonthYearPickerDialog(var month : Int?, var year : Int?, var showTitle :Boolean= true, var showMonthPicker:Boolean =true) :
    DialogFragment() {
    private var listener: DatePickerDialog.OnDateSetListener? = null
    fun setListener(listener: DatePickerDialog.OnDateSetListener?) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(activity)
        // Get the layout inflater
        val inflater: LayoutInflater? = activity?.layoutInflater
        val cal: Calendar = Calendar.getInstance()
        val dialog: View? = inflater?.inflate(R.layout.date_picker_dialog, null)
        val monthPicker = dialog?.findViewById<View>(R.id.picker_month) as NumberPicker
        val yearPicker = dialog.findViewById<View>(R.id.picker_year) as NumberPicker
        val tvTitle = dialog.findViewById<AppCompatTextView>(R.id.tv_title) as AppCompatTextView
        tvTitle.isVisible = showTitle
        monthPicker.isVisible = showMonthPicker
        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.setFormatter { i -> String.format("%02d", i) }
        monthPicker.value = month ?: (cal.get(Calendar.MONTH) + 1)
//        val year: Int = cal.get(Calendar.YEAR)
        yearPicker.minValue = 2022
        yearPicker.maxValue = cal.get(Calendar.YEAR)//MAX_YEAR
        yearPicker.value = year?:cal.get(Calendar.YEAR)
        builder.setView(dialog).setPositiveButton(getString(R.string.ok)) { _, _ -> listener!!.onDateSet(null, yearPicker.value, monthPicker.value, 0)}.setNegativeButton(getString(R.string.cancel)) { _, _ -> this.dialog?.cancel() }
        val alert = builder.create()
        alert.setOnShowListener {
            alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(requireContext().getColorFromAttr(R.attr.colorTextView))
            alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(requireContext().getColorFromAttr(R.attr.colorTextView))
        }
        return alert
    }

    companion object {
        private const val MAX_YEAR = 2099
    }
}