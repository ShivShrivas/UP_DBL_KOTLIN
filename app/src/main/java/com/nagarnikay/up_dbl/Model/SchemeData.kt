package com.nagarnikay.up_dbl.Model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SchemeData {
    @SerializedName("respCode")
    @Expose
    var respCode: String? = null

    @SerializedName("respMessage")
    @Expose
    var respMessage: String? = null

    @SerializedName("Schemename")
    @Expose
    var schemename: String? = null

    @SerializedName("SchemeId")
    @Expose
    var schemeId: Int? = null
}
