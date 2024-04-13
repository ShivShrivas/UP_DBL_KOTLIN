package com.nagarnikay.up_dbl.Model



import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProjectReportData {
    @SerializedName("ProjectId")
    @Expose
    var projectId: Int? = null

    @SerializedName("OfficeId")
    @Expose
    var officeId: Int? = null

    @SerializedName("SchemeId")
    @Expose
    var schemeId: Int? = null

    @SerializedName("ProjectName")
    @Expose
    var projectName: String? = null

    @SerializedName("longitude")
    @Expose
    var longitude: String? = null

    @SerializedName("Latitude")
    @Expose
    var latitude: String? = null

    @SerializedName("PaymentAmount")
    @Expose
    var paymentAmount: Double? = null

    @SerializedName("FinYearId")
    @Expose
    var finYearId: Int? = null

    @SerializedName("FinYearText")
    @Expose
    var finYearText: String? = null

    @SerializedName("RemarkPhoto1")
    @Expose
    var remarkPhoto1: String? = null

    @SerializedName("RemarkPhoto2")
    @Expose
    var remarkPhoto2: String? = null

    @SerializedName("SchemeName")
    @Expose
    var schemeName: String? = null

    @SerializedName("YearText")
    @Expose
    var yearText: String? = null

    @SerializedName("ProgressPhoto1")
    @Expose
    var progressPhoto1: String? = null

    @SerializedName("ProgressPhoto2")
    @Expose
    var progressPhoto2: String? = null

    @SerializedName("PhysicalProgress")
    @Expose
    var physicalProgress: Double? = null

    @SerializedName("FinancialProgress")
    @Expose
    var financialProgress: Double? = null
}
