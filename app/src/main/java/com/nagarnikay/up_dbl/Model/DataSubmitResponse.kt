package com.nagarnikay.up_dbl.Model



import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DataSubmitResponse {
    @SerializedName("respCode")
    @Expose
    var respCode: String? = null

    @SerializedName("respMessage")
    @Expose
    var respMessage: String? = null
}
