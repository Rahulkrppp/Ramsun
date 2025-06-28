package de.fast2work.mobility.data.remote.cit

import com.google.gson.annotations.SerializedName
import de.fast2work.mobility.data.remote.cit.HBBaseResponse

class WSGenericResponse<T> : HBBaseResponse() {
    @SerializedName("data")
    var data: T? = null
}
