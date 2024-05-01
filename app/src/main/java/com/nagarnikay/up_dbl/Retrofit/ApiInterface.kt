package com.nagarnikay.up_dbl.Retrofit

import com.nagarnikay.up_dbl.Model.AllProjectsResponse
import com.nagarnikay.up_dbl.Model.DataSubmitResponse
import com.nagarnikay.up_dbl.Model.FinancialYearResponse
import com.nagarnikay.up_dbl.Model.LoginResponse
import com.nagarnikay.up_dbl.Model.ProjectReportsResponse
import com.nagarnikay.up_dbl.Model.SchemeData
import com.nagarnikay.up_dbl.Model.WorkStatusResponse


import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @GET("District")
    fun getDistrictList(): Call<JsonElement>

    @GET("VenueType")
    fun getVenueTypeList(): Call<JsonElement>

    @GET("EventType")
    fun getVenueTypeIdealList(): Call<JsonElement>

    @GET("VenueCategory")
    fun getVenueCategoryList(): Call<JsonElement>

    @GET("GalleryType")
    fun getVenueImageTypeList(): Call<JsonElement>

    @GET("AdditionalFacilities")
    fun getAdditionalFacilitiesList(): Call<JsonElement>

    @Multipart
    @POST("UploadGallery")
    fun uploadImage(
        @Part file: List<MultipartBody.Part>,
        @Part("Gallery_Id") Gallery_Id: RequestBody,
        @Part("ChannelPartnerReg_Id") ChannelPartnerReg_Id: RequestBody,
        @Part("Caption") Caption: RequestBody
    ): Call<String>

    @FormUrlEncoded
    @POST("MobileApp/GetFinancialYear")
    fun getFinancial(
        @Field("ApiUserName") ApiUserName: String,
        @Field("Token") Token: String,
        @Field("Procid") Procid: String,
        @Field("SchemeId") SchemeId: String
    ): Call<FinancialYearResponse>

    @FormUrlEncoded
    @POST("MobileApp/GetPhotoType")
    fun getWorkStatus(
        @Field("ApiUserName") ApiUserName: String,
        @Field("Token") Token: String,
        @Field("Procid") Procid: String,
        @Field("SchemeId") SchemeId: String
    ): Call<WorkStatusResponse>

    @FormUrlEncoded
    @POST("MobileApp/GetSchemeMaster")
    fun getSchemes(
        @Field("ApiUserName") ApiUserName: String,
        @Field("Token") Token: String,
        @Field("SchemeId") SchemeId: String
    ): Call<SchemeData>

    @FormUrlEncoded
    @POST("MobileApp/GetProjectMaster")
    fun getAllProjects(
        @Field("ApiUserName") ApiUserName: String,
        @Field("Token") Token: String,
        @Field("Procid") Procid: String,
        @Field("OfficeId") OfficeId: String,
        @Field("FinYearId") FinYearId: String,
        @Field("SchemeId") SchemeId: String,
        @Field("UserType") UserType: Int
    ): Call<AllProjectsResponse>

    @Multipart
    @POST("MobileApp/InspectionSite")
    fun submitData(
        @Part("ApiUserName") ApiUserName: String,
        @Part("Token") Token: String,
        @Part("Id") Id: Int,
        @Part("ProjectId") ProjectId: Int,
        @Part("OfficeId") OfficeId: Int,
        @Part("ProgressPhoto") ProgressPhoto: String,
        @Part("ProgressPhoto1") ProgressPhoto1: String,
        @Part("ProgressPhoto2") ProgressPhoto2: String,
        @Part("longitude") longitude: Double,
        @Part("Latitude") Latitude: Double,
        @Part("Remark") Remark: String,
        @Part("Remark2") Remark2: String,
        @Part("FinYearId") FinYearId: Int,
        @Part("ProcId") ProcId: Int,
        @Part("SchemeId") SchemeId: Int,
        @Part("Physicalprogress") Physicalprogress: Float,
        @Part("Financialprogress") Financialprogress: Float,
        @Part("PhotypeTypeId") PhotypeTypeId: Int,
        @Part("ProgressDT") ProgressDT: String,
        @Part part1: MultipartBody.Part,
        @Part part2: MultipartBody.Part,
        @Part part3: MultipartBody.Part
    ): Call<DataSubmitResponse>

    @POST("MobileApp/ProjectAllDetails")
    fun getProjectReports(@Body jsonObject: JsonObject): Call<ProjectReportsResponse>

    @POST("MobileApp/Login")
    fun getLogin(@Body jsonObject: JsonObject): Call<LoginResponse>

    @FormUrlEncoded
    @POST("Registration")
    fun login(@Field("Person_UserName") Query: String, @Field("Person_Password") Scheme: String): Call<JsonElement>

    @FormUrlEncoded
    @POST("Channel_Partner_List")
    fun getList(
        @Field("Tbl_ChannelPartnerReg_Id") RegID: String,
        @Field("District_Id") disid: String,
        @Field("VenueType_Id") VenueType: String
    ): Call<JsonElement>

    @GET("ChannelPartnerGallery/{WaterDetails_Id}")
    fun getGalleryList(@Path("WaterDetails_Id") WaterDetails_Id: String): Call<JsonElement>
}
