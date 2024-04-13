package com.nagarnikay.up_dbl.Model



import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FIData {
    @SerializedName("YearId")
    @Expose
    var yearId: Int? = null

    @SerializedName("YearText")
    @Expose
    var yearText: String? = null
}
