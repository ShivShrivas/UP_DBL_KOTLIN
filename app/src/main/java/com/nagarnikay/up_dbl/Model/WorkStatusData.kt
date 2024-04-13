package com.nagarnikay.up_dbl.Model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WorkStatusData(
    @SerializedName("PhotoTypeId")
    @Expose
    var photoTypeId: Int? = null,

    @SerializedName("Type")
    @Expose
    var type: String? = null
)
