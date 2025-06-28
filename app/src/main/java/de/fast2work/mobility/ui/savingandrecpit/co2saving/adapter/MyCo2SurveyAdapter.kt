package de.fast2work.mobility.ui.savingandrecpit.co2saving.adapter

import android.content.Context
import android.content.res.Configuration
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.fast2work.mobility.data.response.SurveyResp
import de.fast2work.mobility.databinding.ItemSurveyBinding
import de.fast2work.mobility.ui.core.BaseApplication
import de.fast2work.mobility.utility.extension.clickWithDebounce
import de.fast2work.mobility.utility.extension.toDDMMYYYY
import de.fast2work.mobility.utility.extension.toDDMYYYY
import de.fast2work.mobility.utility.preference.EasyPref
import java.util.Locale


class MyCo2SurveyAdapter(var context: Context, var list: ArrayList<SurveyResp>,
                         var clickListener: (view: View, model: SurveyResp, position: Int) -> Unit = { _: View, _: SurveyResp, _: Int -> }) : RecyclerView.Adapter<MyCo2SurveyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSurveyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val item=list[holder.absoluteAdapterPosition]
        holder.itemBinding.apply {
           tvSurveyName.text=item.surveyName
            tvDateDisplay.text=item.createdDate.toDDMYYYY()
            tvDisplayMoney.text=item.savedCo2
            tvViewSurvey.setPaintFlags(tvViewSurvey.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
            tvViewReport.setPaintFlags(tvViewSurvey.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
            tvViewReport.setPaintFlags(tvViewSurvey.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)



            tvViewSurvey.clickWithDebounce {
                clickListener(tvViewSurvey,item,holder.absoluteAdapterPosition)
            }
            tvViewReport.clickWithDebounce {
                clickListener(tvViewReport,item,holder.absoluteAdapterPosition)
            }
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }
    class ViewHolder(val itemBinding: ItemSurveyBinding) : RecyclerView.ViewHolder(itemBinding.root)
}