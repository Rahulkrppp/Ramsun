package de.fast2work.mobility.di.helper

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import de.fast2work.mobility.data.remote.cit.WSListResponse
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.ChartReq
import de.fast2work.mobility.data.request.CityReq
import de.fast2work.mobility.data.request.Co2SurveryReq
import de.fast2work.mobility.data.request.CreateTokenReq
import de.fast2work.mobility.data.request.DTicketCreatePaymentIntent
import de.fast2work.mobility.data.request.DistanceReq
import de.fast2work.mobility.data.request.EnableDisablePushNotificationReq
import de.fast2work.mobility.data.request.InvoiceCalculateCo2Req
import de.fast2work.mobility.data.request.InvoiceFuelTypeReq
import de.fast2work.mobility.data.request.InvoiceReq
import de.fast2work.mobility.data.request.LogoutReq
import de.fast2work.mobility.data.request.SurveyReq
import de.fast2work.mobility.data.response.ActiveTicket
import de.fast2work.mobility.data.response.AddInvoice
import de.fast2work.mobility.data.response.BudgetGroup
import de.fast2work.mobility.data.response.BudgetGroupInfo
import de.fast2work.mobility.data.response.CardData
import de.fast2work.mobility.data.response.CardMaster
import de.fast2work.mobility.data.response.CategoryApiResponse
import de.fast2work.mobility.data.response.CategoryInfoRes
import de.fast2work.mobility.data.response.ChartRes
import de.fast2work.mobility.data.response.CityResponse
import de.fast2work.mobility.data.response.Co2CalculateRes
import de.fast2work.mobility.data.response.Co2SurveyGetDataResp
import de.fast2work.mobility.data.response.DTicketCreatePaymentRes
import de.fast2work.mobility.data.response.DTicketRes
import de.fast2work.mobility.data.response.DistanceRes
import de.fast2work.mobility.data.response.FuelTypeRes
import de.fast2work.mobility.data.response.InvoiceDataList
import de.fast2work.mobility.data.response.InvoiceDetailsApiResponse
import de.fast2work.mobility.data.response.ModeOfTransport
import de.fast2work.mobility.data.response.Notification
import de.fast2work.mobility.data.response.SelectCategoryDataItem
import de.fast2work.mobility.data.response.SurveyDataList
import de.fast2work.mobility.data.response.SurveyResp
import de.fast2work.mobility.data.response.SurveyStatisticsResp
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.data.response.TicketInfoRes
import de.fast2work.mobility.data.response.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject

interface DashBoardRepoHelper {
    suspend fun callBudgetGroupApi(model: String, onResult: (response: WSObjectResponse<BudgetGroup>) -> Unit, onFailure: (message: String) -> Unit)
    suspend fun callBudgetGroupInfoApi(payPeriodStartMonth: String,budgetGroupId:String, onResult: (response: WSObjectResponse<BudgetGroupInfo>) -> Unit, onFailure: (message: String) -> Unit)
    suspend fun calInvoiceListApi(model: InvoiceReq, onResult: (response: WSObjectResponse<InvoiceDataList>) -> Unit, onFailure: (message: String) -> Unit)
    suspend fun callCategoryListApi(model: HashMap<String,Any?>, onResult: (response: WSObjectResponse<CategoryApiResponse>) -> Unit, onFailure: (message: String) -> Unit)
    suspend fun callInvoiceDetailsApi(invoiceId: String,refType: String, onResult: (response: WSObjectResponse<InvoiceDetailsApiResponse>) -> Unit, onFailure: (message: String) -> Unit)
    suspend fun callCategoryInfoApi(categoryId: String,budgetGroupId:String,payPeriodStartMonth: String, onResult: (response: WSObjectResponse<CategoryInfoRes>) -> Unit, onFailure: (message: String) -> Unit)
    suspend fun callGetUserProfileApi(userId: String, onResult: (response: WSObjectResponse<User>) -> Unit, onFailure: (message: String) -> Unit)
    suspend fun callUpdateProfileApi(params: HashMap<String, RequestBody>, file: MultipartBody.Part?, onResult: (response: WSObjectResponse<User>) -> Unit, onFailure: (message: String) -> Unit)
    suspend fun callLogoutApi(model: LogoutReq, onResult: (response: WSObjectResponse<JsonElement>) -> Unit, onFailure: (message: String) -> Unit)
    suspend fun callCategoryCategorySubCategoryListApi(userId: String, onResult: (response: WSListResponse<SelectCategoryDataItem>) -> Unit, onFailure: (message: String) -> Unit)
    suspend fun callAddInvoiceApi(params: HashMap<String, RequestBody>, file: MultipartBody.Part?,fileList: ArrayList<MultipartBody.Part?>, onResult: (response: WSObjectResponse<AddInvoice>) -> Unit, onFailure: (message: String) -> Unit)
    suspend fun callNotificationCountApi( onResult: (response: WSObjectResponse<Notification>) -> Unit, onFailure: (message: String) -> Unit)
    suspend fun callEnableDisableNotificationApi(
        userId: Int?,
        model: EnableDisablePushNotificationReq,
        onResult: (response: WSObjectResponse<Int>) -> Unit,
        onFailure: (message: String) -> Unit,
    )

    suspend fun callCreateFCMTokenApi(
        params: CreateTokenReq,
        onResult: (response: WSObjectResponse<JsonElement>) -> Unit,
        onFailure: (message: String) -> Unit,
    )

    suspend fun callCardMasterApi(
        onResult: (response: WSObjectResponse<CardData>) -> Unit,
        onFailure: (message: String) -> Unit,
    )

    suspend fun callGetTenantThemeApi(
        tenantName: String, onResult: (response: WSObjectResponse<TenantInfoModel>) -> Unit,
        onFailure: (message: String) -> Unit,
    )

    suspend fun callDeleteAccountApi(
        onResult: (response: WSObjectResponse<JsonElement>) -> Unit,
        onFailure: (message: String) -> Unit,
    )
    suspend fun callModeOfTransportApi(
        onResult: (response: WSListResponse<ModeOfTransport>) -> Unit,
        onFailure: (message: String) -> Unit,
    )
    suspend fun callModeOfTransportReceiptApi(
        onResult: (response: WSListResponse<ModeOfTransport>) -> Unit,
        onFailure: (message: String) -> Unit,
    )
    suspend fun callCo2SurveyApi(params: Co2SurveryReq/*params: HashMap<String, RequestBody>*/,
                                 onResult: (response: WSObjectResponse<JsonElement>) -> Unit,
                                 onFailure: (message: String) -> Unit,
    )

    suspend fun callCo2SurveyApiNew(params: JsonObject/*params: HashMap<String, RequestBody>*/,
                                 onResult: (response: WSObjectResponse<JsonElement>) -> Unit,
                                 onFailure: (message: String) -> Unit,
    )

    suspend fun callGetUserSurveyApi(pageNo:Int,limit:Int,
                                     onResult: (response: WSObjectResponse<SurveyDataList>) -> Unit,
                                     onFailure: (message: String) -> Unit,
    )

    suspend fun callInProgressSurveyApi(
                                     onResult: (response: WSObjectResponse<SurveyResp>) -> Unit,
                                     onFailure: (message: String) -> Unit,
    )

    suspend fun callGetStoredUserSurveyListApi(
        surveyId: Int,
        onResult: (response: WSObjectResponse<Co2SurveyGetDataResp>) -> Unit,
        onFailure: (message: String) -> Unit,
    )

    suspend fun callGetSurveyStatisticsByUserIdApi(
        onResult: (response: WSListResponse<SurveyStatisticsResp>) -> Unit,
        onFailure: (message: String) -> Unit,
    )

    suspend fun callGetSurveyStatusByUserId(
        surveyId: Int,
        onResult: (response: WSListResponse<SurveyResp>) -> Unit,
        onFailure: (message: String) -> Unit
    )

    suspend fun callDistanceApi(params: HashMap<String, RequestBody>,
                                onResult: (response: WSObjectResponse<DistanceRes>) -> Unit,
                                onFailure: (message: String) -> Unit,
    )

    suspend fun callCityListApi(
        params: CityReq,
        onResult: (response: WSObjectResponse<CityResponse>) -> Unit, onFailure: (message: String) -> Unit,
    )

    suspend fun callInvoiceCalculateCo2Api(
        params: InvoiceCalculateCo2Req,
        onResult: (response: WSObjectResponse<Co2CalculateRes>) -> Unit, onFailure: (message: String) -> Unit,
    )

    suspend fun callGetFuelTypeId(
        onResult: (response: WSListResponse<FuelTypeRes>) -> Unit, onFailure: (message: String) -> Unit,
    )

    suspend fun callInvoiceCalculateCo2FuelTypeApi(
        params: InvoiceFuelTypeReq,
        onResult: (response: WSObjectResponse<Co2CalculateRes>) -> Unit, onFailure: (message: String) -> Unit,
    )

    suspend fun callChartCo2Api(
        params: ChartReq,
        onResult: (response: WSObjectResponse<ChartRes>) -> Unit, onFailure: (message: String) -> Unit,
    )
    suspend fun callDTicketCreatePaymentIntentApi(
        params: DTicketCreatePaymentIntent,
        onResult: (response: WSObjectResponse<DTicketCreatePaymentRes>) -> Unit, onFailure: (message: String) -> Unit,
    )
    suspend fun callDTicketApi(
        onResult: (response: WSListResponse<DTicketRes>) -> Unit, onFailure: (message: String) -> Unit,
    )

    suspend fun callActiveTicketInfoUserId(
        onResult: (response: WSObjectResponse<TicketInfoRes>) -> Unit, onFailure: (message: String) -> Unit,
    )
}