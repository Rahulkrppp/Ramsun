package de.fast2work.mobility.data.remote.cit


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import de.fast2work.mobility.data.remote.cit.HBBaseResponse
import kotlinx.parcelize.Parcelize
import java.util.*
@Parcelize
class WSListResponse<T> : HBBaseResponse(), Parcelable {
    @SerializedName("data")
    var data: ArrayList<T>? = null
}
