package com.nagarnikay.up_dbl.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SchemeItem {
    @SerializedName("Schemename")
    @Expose
    var schemename: String? = null

    @SerializedName("SchemeId")
    @Expose
    var schemeId: Int? = null
}
