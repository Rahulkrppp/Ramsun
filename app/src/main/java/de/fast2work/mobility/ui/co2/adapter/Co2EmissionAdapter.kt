package de.fast2work.mobility.ui.co2.adapter

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.text.Editable
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.R
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.databinding.ItemCo2AddressModeBinding
import de.fast2work.mobility.db.ModelStops
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.utility.extension.SCREEN_WIDTH
import de.fast2work.mobility.utility.extension.buttonTextColorText
import de.fast2work.mobility.utility.extension.clickWithDebounce
import de.fast2work.mobility.utility.extension.getColorFromAttr
import de.fast2work.mobility.utility.extension.imageTickTintCheckBox
import de.fast2work.mobility.utility.extension.setVisible
import de.fast2work.mobility.utility.preference.EasyPref


class Co2EmissionAdapter(
    var isView: Boolean = false, var context: Context, var list: ArrayList<ModelStops>,
    var clickListener: (view: View, model: ModelStops, position: Int) -> Unit = { _: View, _: ModelStops, _: Int -> }
) : RecyclerView.Adapter<Co2EmissionAdapter.ViewHolder>() {

    var popupWindow: PopupWindow? = null
    var tenantInfoData = BaseApplication.tenantSharedPreference.getTenantPrefModel(
        EasyPref.TENANT_DATA,
        TenantInfoModel::class.java
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCo2AddressModeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    //var mode="Single"
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemBinding.tvNoOfPeople.setTag(position)
        val item = list[position]
        val pos = position + 1

        holder.itemBinding.apply {
            tvJourney.text = context.getString(R.string.journey, pos.toString())
            holder.itemBinding.objStops = item

            if (item.transportMode.equals("Single", true)) {
                tlMeansOfTransport.setVisible(false)
                tlTransportDetails.setVisible(false)
            } else {
                tlTransportDetails.setVisible(true)
                tlMeansOfTransport.setVisible(true)
            }
            if (item.transportDetails?.label == "-") {
                tlTransportDetails.setVisible(false)
            } else {
                if (item.transportMode == "multi" && item.workingMode == "office") {
                    tlTransportDetails.setVisible(true)
                }
            }
            ivDelete.setVisible(false)

            if (list.size <= 1) {
                ivDelete.setVisible(false)
            } else {
                if (position == list.size - 1) {
                    if (!isView) {
                        ivDelete.setVisible(true)
                    }
                }
            }

            if (item.isShareVisible) {
                //if(item.isShared) {
                llShared.setVisible(true)
                tvShared.setVisible(true)
                tvNoOfPeople.setVisible(true)
                tlNoOfPeople.setVisible(true)
                if (item.isShared) {
                    ivYes.setImageResource(R.drawable.ic_selected_mode)
                    ivYes.imageTickTintCheckBox(
                        BaseApplication.tenantSharedPreference.getTenantPrefModel(
                            EasyPref.TENANT_DATA,
                            TenantInfoModel::class.java
                        )?.brandingInfo?.primaryColor
                    )
                    tvYes.buttonTextColorText(tenantInfoData?.brandingInfo?.primaryColor)
                    tvYes.typeface =
                        ResourcesCompat.getFont(context, R.font.poppins_semi_bold_600)

                    ivNo.setImageResource(R.drawable.ic_checkbox_unselected_circle)
                    context.getColorFromAttr(R.attr.colorTextView).let { ivNo.setColorFilter(it) }
                    tvNo.setTextColor(context.getColorFromAttr(R.attr.colorTextView))
                    tvNo.typeface = ResourcesCompat.getFont(context, R.font.poppins_medium_500)

                    tvNoOfPeople.setVisible(true)
                    telNoOfTransport.setVisible(true)

                } else {
                    ivNo.setImageResource(R.drawable.ic_selected_mode)
                    ivNo.imageTickTintCheckBox(
                        BaseApplication.tenantSharedPreference.getTenantPrefModel(
                            EasyPref.TENANT_DATA,
                            TenantInfoModel::class.java
                        )?.brandingInfo?.primaryColor
                    )
                    tvNo.buttonTextColorText(tenantInfoData?.brandingInfo?.primaryColor)
                    tvNo.typeface =
                        ResourcesCompat.getFont(context, R.font.poppins_semi_bold_600)

                    ivYes.setImageResource(R.drawable.ic_checkbox_unselected_circle)
                    context.getColorFromAttr(R.attr.colorTextView).let { ivYes.setColorFilter(it) }
                    tvYes.setTextColor(context.getColorFromAttr(R.attr.colorTextView))
                    tvYes.typeface = ResourcesCompat.getFont(context, R.font.poppins_medium_500)

                    tvNoOfPeople.setVisible(false)
                    telNoOfTransport.setVisible(false)
                    telNoOfTransport.setText("")

                }
            } else {
                llShared.setVisible(false)
                tvShared.setVisible(false)
                tvNoOfPeople.setVisible(false)
                tlNoOfPeople.setVisible(false)
                if (isView) {
                    if (item.workingMode == "office") {
                        if (!item.transportMeans?.items.isNullOrEmpty()) {
                        llShared.setVisible(true)
                        tvShared.setVisible(true)
                        ivNo.setImageResource(R.drawable.ic_selected_mode)
                        ivNo.imageTickTintCheckBox(
                            BaseApplication.tenantSharedPreference.getTenantPrefModel(
                                EasyPref.TENANT_DATA,
                                TenantInfoModel::class.java
                            )?.brandingInfo?.primaryColor
                        )
                        tvNo.buttonTextColorText(tenantInfoData?.brandingInfo?.primaryColor)
                        tvNo.typeface =
                            ResourcesCompat.getFont(context, R.font.poppins_semi_bold_600)
                        ivYes.setImageResource(R.drawable.ic_checkbox_unselected_circle)
                        context.getColorFromAttr(R.attr.colorTextView)
                            .let { ivYes.setColorFilter(it) }
                        tvYes.setTextColor(context.getColorFromAttr(R.attr.colorTextView))
                        tvYes.typeface =
                            ResourcesCompat.getFont(context, R.font.poppins_medium_500)
                    }

                    }
                }
            }



            ivDelete.clickWithDebounce {
                clickListener(ivDelete, item, holder.absoluteAdapterPosition)
            }


            if (!isView) {

//                telNoOfTransport.clickWithDebounce {
//                    clickListener(telNoOfTransport,item,position)
//                }
                //telNoOfTransport.setSelection(item.maximumNoOfPeople)


                telNoOfTransport.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        telNoOfTransport.setKeyListener(DigitsKeyListener.getInstance(getFilterNumber(item.maximumNoOfPeople)))
                        if (telNoOfTransport.text.toString().isNotEmpty()) {
                            if (telNoOfTransport.text.toString()
                                    .toInt() <= item.maximumNoOfPeople
                            ) {
                                Log.e("", "onTextChanged: if ${position}")
                                item.numberOfPeople = telNoOfTransport.text.toString()
                            }
                        }
                    }
                }

//                telNoOfTransport.addTextChangedListener(object : TextWatcher {
//                    override fun beforeTextChanged(
//                        s: CharSequence?,
//                        start: Int,
//                        count: Int,
//                        after: Int
//                    ) {
//
//                    }
//
//                    override fun onTextChanged(
//                        s: CharSequence?,
//                        start: Int,
//                        before: Int,
//                        count: Int
//                    ) {
//                        if (s.toString().isNotEmpty()) {
//                            Log.e("", "onTextChanged: if ${item.maximumNoOfPeople}")
//                            if (s.toString().toInt() <= item.maximumNoOfPeople) {
//                                Log.e("", "onTextChanged: if ${position}")
//                                item.numberOfPeople = s.toString()
//                            } else {
//                               // item.numberOfPeople = ""
//                                telNoOfTransport.setText("")
//                                Log.e("", "onTextChanged: else")
//                            }
//                            //
//                        }
//                    }
//
//                    override fun afterTextChanged(s: Editable?) {
//
//                    }
//                }
//                )
            }

            telTransportDetails.clickWithDebounce {
                clickListener(telTransportDetails, item, holder.absoluteAdapterPosition)
            }
            meansTransport.clickWithDebounce {
                clickListener(meansTransport, item, holder.absoluteAdapterPosition)
            }
            telStopAddress.clickWithDebounce {
                clickListener(telStopAddress, item, holder.absoluteAdapterPosition)
            }
            tvYes.clickWithDebounce {
                clickListener(tvYes, item, holder.absoluteAdapterPosition)
            }
            ivYes.clickWithDebounce {
                clickListener(ivYes, item, holder.absoluteAdapterPosition)
            }
            tvNo.clickWithDebounce {
                clickListener(tvNo, item, holder.absoluteAdapterPosition)
            }
            ivNo.clickWithDebounce {
                clickListener(ivNo, item, holder.absoluteAdapterPosition)
            }

            var isClickable = true
            tvNoOfPeople.clickWithDebounce {
                if (isClickable) {
                    isClickable = false
                    toShowInfoPopup(
                        tvNoOfPeople,
                        "${context.getString(R.string.maximum_number_of_people_for_sharing_ride)} ${item.maximumNoOfPeople}"
                    )
                } else {
                    isClickable = true
                    popupWindow?.dismiss()
                }
            }
            if (position == list.size - 1) {
                telStartAddress.isEnabled = false
                telStopAddress.isEnabled = false
            } else {
                tlStartAddress.isEnabled = false
                telStopAddress.isEnabled = true
            }
            if (isView) {
                telStartAddress.isEnabled = false
                telStopAddress.isEnabled = false
                telTransportDetails.isEnabled = false
                telNoOfTransport.isEnabled = false
                meansTransport.isEnabled = false
                tvNoOfPeople.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            }
        }
    }

    private fun toShowInfoPopup(view: View, msg: String) {
        val layoutInflater: LayoutInflater = context.getSystemService(
            LAYOUT_INFLATER_SERVICE
        ) as LayoutInflater
        val popupView: View = layoutInflater.inflate(R.layout.layout_info, null)
        val tvMessage: AppCompatTextView =
            popupView.findViewById(R.id.tvMessage) as AppCompatTextView
        tvMessage.setText(msg)
        popupWindow = PopupWindow(
            popupView,
            (SCREEN_WIDTH * 0.55).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow?.showAsDropDown(view, 50, 50, Gravity.TOP)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getFilterNumber(number: Int):String{
        var str=""
        for(i in 1..number){
            str+="$i"
        }
        return str
    }

    class ViewHolder(val itemBinding: ItemCo2AddressModeBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}