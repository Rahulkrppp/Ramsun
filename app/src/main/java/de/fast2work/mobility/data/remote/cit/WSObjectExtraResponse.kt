package de.fast2work.mobility.data.remote.cit

import com.google.gson.annotations.SerializedName
import de.fast2work.mobility.data.remote.cit.HBBaseResponse

class WSObjectExtraResponse<T> : HBBaseResponse() {
    @SerializedName("extraData")
    var extraData: T? = null
}
