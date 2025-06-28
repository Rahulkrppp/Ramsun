package de.fast2work.mobility.data.remote

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import de.fast2work.mobility.BuildConfig
import de.fast2work.mobility.data.remote.cit.WSListResponse
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.ChangePasswordReq
import de.fast2work.mobility.data.request.ChartReq
import de.fast2work.mobility.data.request.CityReq
import de.fast2work.mobility.data.request.Co2SurveryReq
import de.fast2work.mobility.data.request.ContactUsReq
import de.fast2work.mobility.data.request.CreateTokenReq
import de.fast2work.mobility.data.request.DTicketActivatedReq
import de.fast2work.mobility.data.request.DTicketCreatePaymentIntent
import de.fast2work.mobility.data.request.EnableDisablePushNotificationReq
import de.fast2work.mobility.data.request.ForgotPasswordReq
import de.fast2work.mobility.data.request.InvoiceCalculateCo2Req
import de.fast2work.mobility.data.request.InvoiceFuelTypeReq
import de.fast2work.mobility.data.request.InvoiceReq
import de.fast2work.mobility.data.request.Login2FAReq
import de.fast2work.mobility.data.request.LoginRequest
import de.fast2work.mobility.data.request.LogoutReq
import de.fast2work.mobility.data.request.NotificationDeleteReq
import de.fast2work.mobility.data.request.NotificationReadUnReadReq
import de.fast2work.mobility.data.request.ResetPasswordReq
import de.fast2work.mobility.data.request.SignUpReq
import de.fast2work.mobility.data.request.VerifyOtpReq
import de.fast2work.mobility.data.response.ActiveTicket
import de.fast2work.mobility.data.response.AddInvoice
import de.fast2work.mobility.data.response.AppUpdate
import de.fast2work.mobility.data.response.BudgetGroup
import de.fast2work.mobility.data.response.BudgetGroupInfo
import de.fast2work.mobility.data.response.CardData
import de.fast2work.mobility.data.response.CategoryApiResponse
import de.fast2work.mobility.data.response.CategoryInfoRes
import de.fast2work.mobility.data.response.ChartRes
import de.fast2work.mobility.data.response.CityResponse
import de.fast2work.mobility.data.response.Co2CalculateRes
import de.fast2work.mobility.data.response.Co2Source
import de.fast2work.mobility.data.response.Co2SurveyGetDataResp
import de.fast2work.mobility.data.response.DTicketActivatedRes
import de.fast2work.mobility.data.response.DTicketCreatePaymentRes
import de.fast2work.mobility.data.response.DTicketRes
import de.fast2work.mobility.data.response.DistanceRes
import de.fast2work.mobility.data.response.FuelTypeRes
import de.fast2work.mobility.data.response.InvoiceDataList
import de.fast2work.mobility.data.response.InvoiceDetailsApiResponse
import de.fast2work.mobility.data.response.ModeOfTransport
import de.fast2work.mobility.data.response.Notification
import de.fast2work.mobility.data.response.NotificationReadUnread
import de.fast2work.mobility.data.response.RefreshToken
import de.fast2work.mobility.data.response.SelectCategoryDataItem
import de.fast2work.mobility.data.response.StaticPage
import de.fast2work.mobility.data.response.SurveyDataList
import de.fast2work.mobility.data.response.SurveyResp
import de.fast2work.mobility.data.response.SurveyStatisticsResp
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.data.response.TicketInfoRes
import de.fast2work.mobility.data.response.User
import de.fast2work.mobility.data.response.VerifyMobileOtp
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiService {

    @GET("tenantApi/tenant/view/{tenantName}")
    suspend fun callGetTenantThemeApi(@Path("tenantName") tenantName: String): Response<WSObjectResponse<TenantInfoModel>>

    @POST("userApi/auth/login")
    suspend fun callLoginApi(@Body params: LoginRequest): Response<WSObjectResponse<User>>

    @POST("userApi/auth/verifyTwoFactorAuthCode")
    suspend fun call2FALoginApi(@Body params: Login2FAReq): Response<WSObjectResponse<User>>

    @POST("analyticsApi/dashboard/recentInvoices")
    suspend fun calInvoiceListApi(@Body params: InvoiceReq): Response<WSObjectResponse<InvoiceDataList>>

    @GET("analyticsApi/dashboard/budgetGroups")
    suspend fun callBudgetGroupApi(@Query("payPeriodStartMonth") payPeriodStartMonth: String): Response<WSObjectResponse<BudgetGroup>>

    @GET("analyticsApi/dashboard/budgetGroupInfo")
    suspend fun callBudgetGroupInfoApi(
        @Query("payPeriodStartMonth") payPeriodStartMonth: String,
        @Query("budgetGroupId") budgetGroupId: String,
    ): Response<WSObjectResponse<BudgetGroupInfo>>

    @POST("/userApi/forgotPassword/verifyMobileOtp")
    suspend fun callVerifyOtpApi(@Body params: VerifyOtpReq): Response<WSObjectResponse<VerifyMobileOtp>>

    @POST("/userApi/forgotPassword/verifyEmail")
    suspend fun callForgotPasswordApi(@Body params: ForgotPasswordReq): Response<WSObjectResponse<JsonElement>>

    @POST("/userApi/forgotPassword/resetMobilePassword")
    suspend fun callResetPasswordApi(@Body params: ResetPasswordReq): Response<WSObjectResponse<JsonElement>>
    @GET("/analyticsApi/dashboard/categoryInfo")
    suspend fun callCategoryInfoApi(@Query("categoryId") categoryId: String, @Query("budgetGroupId") budgetGroupId: String,@Query("payPeriodStartMonth") payPeriodStartMonth: String)
    : Response<WSObjectResponse<CategoryInfoRes>>

    @GET("/userApi/user/{userId}")
    suspend fun callGetUserProfileApi(@Path("userId") userId: String): Response<WSObjectResponse<User>>
    @PUT("/userApi/profile/update")
    @Multipart
    suspend fun callUpdateProfileApi(@PartMap params: HashMap<String, RequestBody>,
                                     @Part image: MultipartBody.Part?): Response<WSObjectResponse<User>>
    @GET("masterApi/category")
    suspend fun callCategoryListApi(@QueryMap params: HashMap<String,Any?>): Response<WSObjectResponse<CategoryApiResponse>>

    @GET("invoiceApi/invoiceMaster/{invoiceId}")
    suspend fun callInvoiceDetailsApi(@Path("invoiceId") invoiceId: String,@Query("refType") refType: String): Response<WSObjectResponse<InvoiceDetailsApiResponse>>

    @POST("/userApi/changePassword")
    suspend fun callChangePasswordApi(@Body params: ChangePasswordReq): Response<WSObjectResponse<JsonElement>>
    @GET("/masterApi/staticpages/{pageIndex}")
    suspend fun callStaticPageApi(@Path("pageIndex") pageIndex: Int): Response<WSObjectResponse<StaticPage>>

    @POST("/masterApi/tickets")
    suspend fun callContactUsdApi(@Body params: ContactUsReq): Response<WSObjectResponse<JsonElement>>

    @POST("/userApi/logout")
    suspend fun callLogoutApi(
        @Body params: LogoutReq,
        ): Response<WSObjectResponse<JsonElement>>
    @GET("/masterApi/notification")
    suspend fun callNotificationListApi(): Response<WSObjectResponse<Notification>>

    @PUT("/masterApi/notification")
    suspend fun callNotificationMarkAllReadApi(): Response<WSObjectResponse<JsonElement>>

    @GET("masterApi/category/categorySubCategory/{userId}")
    suspend fun callCategoryCategorySubCategoryListApi(@Path("userId")userId: String): Response<WSListResponse<SelectCategoryDataItem>>

    @POST("/invoiceApi/invoice/uploadInvoiceByEmployee")
    @Multipart
    suspend fun callAddInvoiceApi(@PartMap params: HashMap<String, RequestBody>, @Part image: MultipartBody.Part? ,@Part image1: ArrayList<MultipartBody.Part?>): Response<WSObjectResponse<AddInvoice>>

    @PUT("/userApi/user/enableDisableNotification/{userId}")
    suspend fun callEnableDisableNotificationApi(
        @Path("userId") userId: Int?,
        @Body params: EnableDisablePushNotificationReq,
    ): Response<WSObjectResponse<Int>>

    @POST("/masterApi/notification/deleteNotifications")
    suspend fun callDeleteNotificationApi(@Body params: NotificationDeleteReq): Response<WSObjectResponse<Int>>

    @PUT("/masterApi/notification/{notificationId}")
    suspend fun callNotificationReadUnReadApi(
        @Path("notificationId") notificationId: Int?,
        @Body params: NotificationReadUnReadReq,
    ): Response<WSObjectResponse<NotificationReadUnread>>

    @POST("userApi/fcm/createToken")
    suspend fun callCreateFCMTokenApi(
        @Body params: CreateTokenReq,
    ): Response<WSObjectResponse<JsonElement>>

    @GET("masterApi/cardMaster")
    suspend fun callCardMasterApi(): Response<WSObjectResponse<CardData>>
    
    @POST("/masterApi/notification/getUnreadCount")
    suspend fun callNotificationCountApi(): Response<WSObjectResponse<Notification>>

    @POST("/userApi/profile/deleteAccount")
    suspend fun callDeleteAccountApi(): Response<WSObjectResponse<JsonElement>>

    @GET("/userApi/appVersion")
    suspend fun callAppVersionApi(@Query("appType") appType:String): Response<WSObjectResponse<AppUpdate>>

    @GET("mbm_geocode")
    suspend fun callSourceDistanceApi(@Query("search") search:String ,@Query("mbm") mbm:Boolean,@Query("apikey") apikey:String): Response<Co2Source>

    @GET("/analyticsApi/co2TransportTypes")
    suspend fun callModeOfTransportApi(): Response<WSListResponse<ModeOfTransport>>

    @GET("/analyticsApi/co2TransportTypes/receipt")
    suspend fun callModeOfTransportReceiptApi(): Response<WSListResponse<ModeOfTransport>>

    @POST("/userApi/co2Survey/storeSurvey")
    suspend fun callCo2SurveyApi(@Body params: Co2SurveryReq/* @PartMap  params: HashMap<String, RequestBody>*/): Response<WSObjectResponse<JsonElement>>

    @Headers("Content-type: application/json")
    @POST("/userApi/co2Survey/storeSurvey")
    suspend fun callCo2SurveyApiNew(@Body params: JsonObject/* @PartMap  params: HashMap<String, RequestBody>*/):
            Response<WSObjectResponse<JsonElement>>


    @GET("/userApi/co2Survey/getUserSurveyList")
    suspend fun callGetUserSurveyApi(@Query("pageNo") pageNo:Int ,@Query("limit") limit:Int): Response<WSObjectResponse<SurveyDataList>>

    @GET("/userApi/co2Survey/inProgreesSurvey")
    suspend fun callInProgressSurveyApi(): Response<WSObjectResponse<SurveyResp>>

    @GET("/userApi/co2Survey/getStoredUserSurvey/{surveyId}")
    suspend fun callGetStoredUserSurveyListApi(@Path("surveyId")surveyId: Int): Response<WSObjectResponse<Co2SurveyGetDataResp>>


    @GET("/userApi/co2Survey/getSurveyStatisticsByUserId")
    suspend fun callGetSurveyStatisticsByUserIdApi(): Response<WSListResponse<SurveyStatisticsResp>>

    @GET("/userApi/co2Survey/getSurveyStatusByUserId/{surveyId}")
    suspend fun callGetSurveyStatusByUserId(@Path("surveyId")surveyId: Int): Response<WSListResponse<SurveyResp>>

    @POST(BuildConfig.BASE_URL+"userApi/token/generateNewAccessToken")
    fun callGenerateNewAccessToken(@HeaderMap param: HashMap<String,String>,@Body params: HashMap<String,String>): Call<WSObjectResponse<RefreshToken>>

    @POST("/userApi/profile/distanceCalculattion")
    @Multipart
    suspend fun callDistanceApi(@PartMap params: HashMap<String, RequestBody>): Response<WSObjectResponse<DistanceRes>>

    @POST("/masterApi/city/list")
    suspend fun callCityListApi(
        @Body params: CityReq
    ): Response<WSObjectResponse<CityResponse>>

    @POST("/invoiceApi/invoiceMaster/calculateCo2")
    suspend fun callInvoiceCalculateCo2Api(@Body params: InvoiceCalculateCo2Req): Response<WSObjectResponse<Co2CalculateRes>>

    @POST("/invoiceApi/invoiceMaster/calculateCo2")
    suspend fun callInvoiceCalculateCo2FuelTypeApi(@Body params1: InvoiceFuelTypeReq): Response<WSObjectResponse<Co2CalculateRes>>

    @GET("/masterApi/general/getFuelTypes")
    suspend fun callGetFuelType(): Response<WSListResponse<FuelTypeRes>>

    @POST("/analyticsApi/dashboard/co2Receipts")
    suspend fun callChartCo2Api(@Body params: ChartReq): Response<WSObjectResponse<ChartRes>>
    @GET("/userApi/dtickets/products_list")
    suspend fun callDTicketApi(): Response<WSListResponse<DTicketRes>>

    @POST("/userApi/prospectEmp/createAccount")
    suspend fun callCreateAccountApi(@Body params: SignUpReq): Response<WSObjectResponse<JsonElement>>


    @POST("ticketing/coupons/{id}/activate.json")
    suspend fun callDTicketActivatedtApi(@Path("id")surveyId: String,@Body params: DTicketActivatedReq): Response<DTicketActivatedRes>

    @POST("/userApi/dtickets/create_payment_intent")
    suspend fun callDTicketCreatePaymentIntentApi(@Body params: DTicketCreatePaymentIntent): Response<WSObjectResponse<DTicketCreatePaymentRes>>

    @GET("/userApi/dtickets/active_ticket_info")
    suspend fun callActiveTicketInfoUserId(): Response<WSObjectResponse<TicketInfoRes>>



}