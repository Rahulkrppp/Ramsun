package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardMaster(

	@SerializedName("cardId") var cardId: Int? = 0,
	@SerializedName("cardName") var cardName: String? = null,
	@SerializedName("cardCode") var cardCode: String? = null,
	@SerializedName("cardDescription") var cardDescription: String? = null,
	@SerializedName("status") var status: String? = null,
) : Parcelable
