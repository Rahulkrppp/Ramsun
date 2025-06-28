package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize

data class BudgetGroup(
    @field:SerializedName("data")
    val data: ArrayList<BudgetItem> = arrayListOf(),

    ) : Parcelable

