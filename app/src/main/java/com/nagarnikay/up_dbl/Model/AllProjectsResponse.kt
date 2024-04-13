package com.nagarnikay.up_dbl.Model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AllProjectsResponse {
    @SerializedName("respCode")
    @Expose
    var respCode: String? = null

    @SerializedName("respMessage")
    @Expose
    var respMessage: String? = null

    @SerializedName("Data")
    @Expose
    var data: List<ProjectData>? = null
}
