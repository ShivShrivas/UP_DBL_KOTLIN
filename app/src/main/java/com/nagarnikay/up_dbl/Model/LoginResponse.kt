package com.nagarnikay.up_dbl.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginResponse {
    @SerializedName("respCode")
    @Expose
    var respCode: String? = null

    @SerializedName("respMessage")
    @Expose
    var respMessage: String? = null

    @SerializedName("OfficeName")
    @Expose
    var officeName: String? = null

    @SerializedName("MobileNo")
    @Expose
    var mobileNo: Any? = null

    @SerializedName("EmailId")
    @Expose
    var emailId: Any? = null

    @SerializedName("OfficeId")
    @Expose
    var officeId: Int? = null

    @SerializedName("Schemeid")
    @Expose
    var schemeid: Int? = null
}
