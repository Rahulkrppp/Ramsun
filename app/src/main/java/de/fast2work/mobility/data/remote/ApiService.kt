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

    @GET("")
    suspend fun callGetTenantThemeApi(@Path("tenantName") tenantName: String): Response<WSObjectResponse<TenantInfoModel>>

    @GET("")
    suspend fun callLoginApi(@Body params: LoginRequest): Response<WSObjectResponse<User>>

    @GET("")
    suspend fun call2FALoginApi(@Body params: Login2FAReq): Response<WSObjectResponse<User>>
    @GET("")
    suspend fun calInvoiceListApi(@Body params: InvoiceReq): Response<WSObjectResponse<InvoiceDataList>>

    @GET("")
    suspend fun callBudgetGroupApi(@Query("payPeriodStartMonth") payPeriodStartMonth: String): Response<WSObjectResponse<BudgetGroup>>

    @GET("")
    suspend fun callBudgetGroupInfoApi(
        @Query("payPeriodStartMonth") payPeriodStartMonth: String,
        @Query("budgetGroupId") budgetGroupId: String,
    ): Response<WSObjectResponse<BudgetGroupInfo>>

    @GET("")
    suspend fun callVerifyOtpApi(@Body params: VerifyOtpReq): Response<WSObjectResponse<VerifyMobileOtp>>

    @GET("")
    suspend fun callForgotPasswordApi(@Body params: ForgotPasswordReq): Response<WSObjectResponse<JsonElement>>

    @GET("")
    suspend fun callResetPasswordApi(@Body params: ResetPasswordReq): Response<WSObjectResponse<JsonElement>>
    @GET("")
    suspend fun callCategoryInfoApi(@Query("categoryId") categoryId: String, @Query("budgetGroupId") budgetGroupId: String,@Query("payPeriodStartMonth") payPeriodStartMonth: String)
    : Response<WSObjectResponse<CategoryInfoRes>>

    @GET("")
    suspend fun callGetUserProfileApi(@Path("userId") userId: String): Response<WSObjectResponse<User>>
    @GET("")
    @Multipart
    suspend fun callUpdateProfileApi(@PartMap params: HashMap<String, RequestBody>,
                                     @Part image: MultipartBody.Part?): Response<WSObjectResponse<User>>
    @GET("")
    suspend fun callCategoryListApi(@QueryMap params: HashMap<String,Any?>): Response<WSObjectResponse<CategoryApiResponse>>

    @GET("")
    suspend fun callInvoiceDetailsApi(@Path("invoiceId") invoiceId: String,@Query("refType") refType: String): Response<WSObjectResponse<InvoiceDetailsApiResponse>>

    @GET("")
    suspend fun callChangePasswordApi(@Body params: ChangePasswordReq): Response<WSObjectResponse<JsonElement>>
    @GET("")
    suspend fun callStaticPageApi(@Path("pageIndex") pageIndex: Int): Response<WSObjectResponse<StaticPage>>

    @GET("")
    suspend fun callContactUsdApi(@Body params: ContactUsReq): Response<WSObjectResponse<JsonElement>>

    @POST("/userApi/logout")
    suspend fun callLogoutApi(
        @Body params: LogoutReq,
        ): Response<WSObjectResponse<JsonElement>>
    @GET("")
    suspend fun callNotificationListApi(): Response<WSObjectResponse<Notification>>

    @GET("")
    suspend fun callNotificationMarkAllReadApi(): Response<WSObjectResponse<JsonElement>>

    @GET("")
    suspend fun callCategoryCategorySubCategoryListApi(@Path("userId")userId: String): Response<WSListResponse<SelectCategoryDataItem>>

    @GET("")
    @Multipart
    suspend fun callAddInvoiceApi(@PartMap params: HashMap<String, RequestBody>, @Part image: MultipartBody.Part? ,@Part image1: ArrayList<MultipartBody.Part?>): Response<WSObjectResponse<AddInvoice>>

    @GET("")
    suspend fun callEnableDisableNotificationApi(
        @Path("userId") userId: Int?,
        @Body params: EnableDisablePushNotificationReq,
    ): Response<WSObjectResponse<Int>>

    @GET("")
    suspend fun callDeleteNotificationApi(@Body params: NotificationDeleteReq): Response<WSObjectResponse<Int>>

    @GET("")
    suspend fun callNotificationReadUnReadApi(
        @Path("notificationId") notificationId: Int?,
        @Body params: NotificationReadUnReadReq,
    ): Response<WSObjectResponse<NotificationReadUnread>>

    @GET("")
    suspend fun callCreateFCMTokenApi(
        @Body params: CreateTokenReq,
    ): Response<WSObjectResponse<JsonElement>>

    @GET("")
    suspend fun callCardMasterApi(): Response<WSObjectResponse<CardData>>
    @GET("")
    suspend fun callNotificationCountApi(): Response<WSObjectResponse<Notification>>

    @GET("")
    suspend fun callDeleteAccountApi(): Response<WSObjectResponse<JsonElement>>

    @GET("")
    suspend fun callAppVersionApi(@Query("appType") appType:String): Response<WSObjectResponse<AppUpdate>>

    @GET("")
    suspend fun callSourceDistanceApi(@Query("search") search:String ,@Query("mbm") mbm:Boolean,@Query("apikey") apikey:String): Response<Co2Source>

    @GET("")
    suspend fun callModeOfTransportApi(): Response<WSListResponse<ModeOfTransport>>

    @GET("")
    suspend fun callModeOfTransportReceiptApi(): Response<WSListResponse<ModeOfTransport>>

    @GET("")
    suspend fun callCo2SurveyApi(@Body params: Co2SurveryReq/* @PartMap  params: HashMap<String, RequestBody>*/): Response<WSObjectResponse<JsonElement>>

    @GET("")
    suspend fun callCo2SurveyApiNew(@Body params: JsonObject/* @PartMap  params: HashMap<String, RequestBody>*/):
            Response<WSObjectResponse<JsonElement>>


    @GET("")
    suspend fun callGetUserSurveyApi(@Query("pageNo") pageNo:Int ,@Query("limit") limit:Int): Response<WSObjectResponse<SurveyDataList>>

    @GET("")
    suspend fun callInProgressSurveyApi(): Response<WSObjectResponse<SurveyResp>>

    @GET("")
    suspend fun callGetStoredUserSurveyListApi(@Path("surveyId")surveyId: Int): Response<WSObjectResponse<Co2SurveyGetDataResp>>


    @GET("")
    suspend fun callGetSurveyStatisticsByUserIdApi(): Response<WSListResponse<SurveyStatisticsResp>>

    @GET("")
    suspend fun callGetSurveyStatusByUserId(@Path("surveyId")surveyId: Int): Response<WSListResponse<SurveyResp>>

    @GET("")
    fun callGenerateNewAccessToken(@HeaderMap param: HashMap<String,String>,@Body params: HashMap<String,String>): Call<WSObjectResponse<RefreshToken>>

    @GET("")
    @Multipart
    suspend fun callDistanceApi(@PartMap params: HashMap<String, RequestBody>): Response<WSObjectResponse<DistanceRes>>

    @GET("")
    suspend fun callCityListApi(
        @Body params: CityReq
    ): Response<WSObjectResponse<CityResponse>>

    @GET("")
    suspend fun callInvoiceCalculateCo2Api(@Body params: InvoiceCalculateCo2Req): Response<WSObjectResponse<Co2CalculateRes>>

    @GET("")
    suspend fun callInvoiceCalculateCo2FuelTypeApi(@Body params1: InvoiceFuelTypeReq): Response<WSObjectResponse<Co2CalculateRes>>

    @GET("")
    suspend fun callGetFuelType(): Response<WSListResponse<FuelTypeRes>>

    @GET("")
    suspend fun callChartCo2Api(@Body params: ChartReq): Response<WSObjectResponse<ChartRes>>
    @GET("")
    suspend fun callDTicketApi(): Response<WSListResponse<DTicketRes>>

    @GET("")
    suspend fun callCreateAccountApi(@Body params: SignUpReq): Response<WSObjectResponse<JsonElement>>


    @GET("")
    suspend fun callDTicketActivatedtApi(@Path("id")surveyId: String,@Body params: DTicketActivatedReq): Response<DTicketActivatedRes>

    @GET("")
    suspend fun callDTicketCreatePaymentIntentApi(@Body params: DTicketCreatePaymentIntent): Response<WSObjectResponse<DTicketCreatePaymentRes>>

    @GET("")
    suspend fun callActiveTicketInfoUserId(): Response<WSObjectResponse<TicketInfoRes>>



}