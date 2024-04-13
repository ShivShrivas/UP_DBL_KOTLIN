package com.nagarnikay.up_dbl.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProjectData {
    @SerializedName("ProjectId")
    @Expose
    var projectId: Int? = null

    @SerializedName("ProjectName")
    @Expose
    var projectName: String? = null

    @SerializedName("ApprovedCost")
    @Expose
    var approvedCost: Double? = null

    @SerializedName("ReleaseAmount")
    @Expose
    var releaseAmount: Double? = null

    @SerializedName("PaymentAmount")
    @Expose
    var paymentAmount: Double? = null

    @SerializedName("PhysicalProgress")
    @Expose
    var physicalProgress: Double? = null

    @SerializedName("ApprovedDate")
    @Expose
    var approvedDate: String? = null

    @SerializedName("ApproveCopy")
    @Expose
    var approveCopy: String? = null

    @SerializedName("GONo")
    @Expose
    var gONo: String? = null

    @SerializedName("OfficeName")
    @Expose
    var officeName: String? = null

    @SerializedName("DistrictName")
    @Expose
    var districtName: String? = null

    @SerializedName("FinYearId")
    @Expose
    var finYearId: Int? = null

    @SerializedName("FinYearText")
    @Expose
    var finYearText: Any? = null
}
