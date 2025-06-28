package de.fast2work.mobility.ui.dashboard

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import de.fast2work.mobility.data.remote.ApiService
import de.fast2work.mobility.data.remote.cit.HBBaseResponse
import de.fast2work.mobility.data.remote.cit.HBSuccessCallback
import de.fast2work.mobility.data.remote.cit.WSListResponse
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.ChartReq
import de.fast2work.mobility.data.request.CityReq
import de.fast2work.mobility.data.request.Co2SurveryReq
import de.fast2work.mobility.data.request.CreateTokenReq
import de.fast2work.mobility.data.request.DTicketCreatePaymentIntent
import de.fast2work.mobility.data.request.EnableDisablePushNotificationReq
import de.fast2work.mobility.data.request.InvoiceCalculateCo2Req
import de.fast2work.mobility.data.request.InvoiceFuelTypeReq
import de.fast2work.mobility.data.request.InvoiceReq
import de.fast2work.mobility.data.request.LogoutReq
import de.fast2work.mobility.data.response.ActiveTicket
import de.fast2work.mobility.data.response.AddInvoice
import de.fast2work.mobility.data.response.BudgetGroup
import de.fast2work.mobility.data.response.BudgetGroupInfo
import de.fast2work.mobility.data.response.CardData
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
import de.fast2work.mobility.di.helper.DashBoardRepoHelper
import de.fast2work.mobility.ui.core.BaseRepository
import de.fast2work.mobility.utility.extension.toBlankString
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import javax.inject.Inject

class DashBoardRepository @Inject constructor(val apiService: ApiService) : BaseRepository(), DashBoardRepoHelper {


    override suspend fun callBudgetGroupApi(model: String, onResult: (response: WSObjectResponse<BudgetGroup>) -> Unit, onFailure: (message: String) -> Unit) {
        try {
            safeApiCall(apiService.callBudgetGroupApi(model), object : HBSuccessCallback<WSObjectResponse<BudgetGroup>> {
                override fun onSuccess(response: WSObjectResponse<BudgetGroup>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }
                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callBudgetGroupInfoApi(payPeriodStartMonth: String, budgetGroupId: String, onResult: (response: WSObjectResponse<BudgetGroupInfo>) -> Unit, onFailure: (message: String) -> Unit) {
        try {
            safeApiCall(apiService.callBudgetGroupInfoApi(payPeriodStartMonth,budgetGroupId), object : HBSuccessCallback<WSObjectResponse<BudgetGroupInfo>> {
                override fun onSuccess(response: WSObjectResponse<BudgetGroupInfo>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }
                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun calInvoiceListApi(model: InvoiceReq, onResult: (response: WSObjectResponse<InvoiceDataList>) -> Unit, onFailure: (message: String) -> Unit) {

        try {
            safeApiCall(apiService.calInvoiceListApi(model), object : HBSuccessCallback<WSObjectResponse<InvoiceDataList>> {
                override fun onSuccess(response: WSObjectResponse<InvoiceDataList>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }
                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callCategoryListApi(
        model: HashMap<String,Any?>,
        onResult: (response: WSObjectResponse<CategoryApiResponse>) -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        try {
            safeApiCall(apiService.callCategoryListApi(model), object : HBSuccessCallback<WSObjectResponse<CategoryApiResponse>> {
                override fun onSuccess(response: WSObjectResponse<CategoryApiResponse>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }

                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callCategoryInfoApi(
        categoryId: String, budgetGroupId: String, payPeriodStartMonth: String,
        onResult: (response: WSObjectResponse<CategoryInfoRes>) -> Unit,
        onFailure: (message: String) -> Unit,
    ) {

        try {
            safeApiCall(apiService.callCategoryInfoApi(categoryId, budgetGroupId, payPeriodStartMonth),
                object : HBSuccessCallback<WSObjectResponse<CategoryInfoRes>> {
                    override fun onSuccess(response: WSObjectResponse<CategoryInfoRes>) {
                        if (response.responseCode == HBBaseResponse.SUCCESS) {
                            onResult(response)
                        } else {
                            onFailure(response.responseMessage)
                        }
                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callInvoiceDetailsApi(
        invoiceId: String,
        refType: String,
        onResult: (response: WSObjectResponse<InvoiceDetailsApiResponse>) -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        try {
            safeApiCall(apiService.callInvoiceDetailsApi(invoiceId,refType), object : HBSuccessCallback<WSObjectResponse<InvoiceDetailsApiResponse>> {
                override fun onSuccess(response: WSObjectResponse<InvoiceDetailsApiResponse>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }
                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callGetUserProfileApi(userId: String, onResult: (response: WSObjectResponse<User>) -> Unit, onFailure: (message: String) -> Unit) {
        try {
            safeApiCall(apiService.callGetUserProfileApi(userId), object : HBSuccessCallback<WSObjectResponse<User>> {
                override fun onSuccess(response: WSObjectResponse<User>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }
                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callUpdateProfileApi(params: HashMap<String, RequestBody>, file: MultipartBody.Part?, onResult: (response: WSObjectResponse<User>) -> Unit, onFailure: (message: String) -> Unit) {

        try {
            safeApiCall(apiService.callUpdateProfileApi(params,file), object : HBSuccessCallback<WSObjectResponse<User>> {
                override fun onSuccess(response: WSObjectResponse<User>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }

                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callLogoutApi(model:LogoutReq, onResult: (response: WSObjectResponse<JsonElement>) -> Unit, onFailure: (message:
                                                                                                                                 String) -> Unit) {
        try {
            safeApiCall(apiService.callLogoutApi(model), object : HBSuccessCallback<WSObjectResponse<JsonElement>> {
                override fun onSuccess(response: WSObjectResponse<JsonElement>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }

                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callCategoryCategorySubCategoryListApi(userId: String, onResult: (response: WSListResponse<SelectCategoryDataItem>) -> Unit, onFailure: (message: String) -> Unit) {

        try {
            safeApiCall(apiService.callCategoryCategorySubCategoryListApi(userId), object : HBSuccessCallback<WSListResponse<SelectCategoryDataItem>> {
                override fun onSuccess(response: WSListResponse<SelectCategoryDataItem>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }
                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callEnableDisableNotificationApi(
        userId: Int?,
        model: EnableDisablePushNotificationReq,
        onResult: (response: WSObjectResponse<Int>) -> Unit,
        onFailure: (message: String) -> Unit,
    ) {
        try {
            safeApiCall(apiService.callEnableDisableNotificationApi(userId, model), object : HBSuccessCallback<WSObjectResponse<Int>> {
                override fun onSuccess(response: WSObjectResponse<Int>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }

                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callAddInvoiceApi(params: HashMap<String, RequestBody>, file: MultipartBody.Part?,fileList: ArrayList<MultipartBody.Part?>, onResult: (response: WSObjectResponse<AddInvoice>) -> Unit, onFailure: (message: String) -> Unit) {
        try {
            safeApiCall(apiService.callAddInvoiceApi(params,file,fileList), object : HBSuccessCallback<WSObjectResponse<AddInvoice>> {
                override fun onSuccess(response: WSObjectResponse<AddInvoice>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }

                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callNotificationCountApi(
        onResult: (response: WSObjectResponse<Notification>) -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        try {
            safeApiCall(apiService.callNotificationCountApi(), object : HBSuccessCallback<WSObjectResponse<Notification>> {
                override fun onSuccess(response: WSObjectResponse<Notification>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }

                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }


    override suspend fun callCreateFCMTokenApi(
        params: CreateTokenReq,
        onResult: (response: WSObjectResponse<JsonElement>) -> Unit,
        onFailure: (message: String) -> Unit,
    ) {
        try {
            safeApiCall(apiService.callCreateFCMTokenApi(params), object : HBSuccessCallback<WSObjectResponse<JsonElement>> {
                override fun onSuccess(response: WSObjectResponse<JsonElement>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }

                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callCardMasterApi(
        onResult: (response: WSObjectResponse<CardData>) -> Unit,
        onFailure: (message: String) -> Unit,
    ) {
        try {
            safeApiCall(apiService.callCardMasterApi(), object : HBSuccessCallback<WSObjectResponse<CardData>> {
                override fun onSuccess(response: WSObjectResponse<CardData>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }

                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callGetTenantThemeApi(
        tenantName: String, onResult: (response: WSObjectResponse<TenantInfoModel>) -> Unit,
        onFailure: (message: String) -> Unit,
    ) {
        try {
            safeApiCall(apiService.callGetTenantThemeApi(tenantName), object : HBSuccessCallback<WSObjectResponse<TenantInfoModel>> {
                override fun onSuccess(response: WSObjectResponse<TenantInfoModel>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }
                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callDeleteAccountApi( onResult: (response: WSObjectResponse<JsonElement>) -> Unit,
        onFailure: (message: String) -> Unit,
    ) {
        try {
            safeApiCall(apiService.callDeleteAccountApi(), object : HBSuccessCallback<WSObjectResponse<JsonElement>> {
                override fun onSuccess(response: WSObjectResponse<JsonElement>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }
                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callModeOfTransportApi(
        onResult: (response: WSListResponse<ModeOfTransport>) -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        try {
            safeApiCall(apiService.callModeOfTransportApi(), object : HBSuccessCallback<WSListResponse<ModeOfTransport>> {
                override fun onSuccess(response: WSListResponse<ModeOfTransport>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }
                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callModeOfTransportReceiptApi(
        onResult: (response: WSListResponse<ModeOfTransport>) -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        try {
            safeApiCall(apiService.callModeOfTransportReceiptApi(), object : HBSuccessCallback<WSListResponse<ModeOfTransport>> {
                override fun onSuccess(response: WSListResponse<ModeOfTransport>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }
                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callCo2SurveyApi(
        params: Co2SurveryReq
        /*params: HashMap<String, RequestBody>*/,
        onResult: (response: WSObjectResponse<JsonElement>) -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        try {
            safeApiCall(apiService.callCo2SurveyApi(params), object : HBSuccessCallback<WSObjectResponse<JsonElement>> {
                override fun onSuccess(response: WSObjectResponse<JsonElement>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }
                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callCo2SurveyApiNew(
        params: JsonObject
        /*params: HashMap<String, RequestBody>*/,
        onResult: (response: WSObjectResponse<JsonElement>) -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        try {
            safeApiCall(apiService.callCo2SurveyApiNew(params), object : HBSuccessCallback<WSObjectResponse<JsonElement>> {
                override fun onSuccess(response: WSObjectResponse<JsonElement>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }
                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callGetUserSurveyApi(
        pageNo:Int,limit:Int,
        onResult: (response: WSObjectResponse<SurveyDataList>) -> Unit,
        onFailure: (message: String) -> Unit
    ) {

        try {
            safeApiCall(apiService.callGetUserSurveyApi(pageNo,limit), object : HBSuccessCallback<WSObjectResponse<SurveyDataList>> {
                override fun onSuccess(response: WSObjectResponse<SurveyDataList>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }
                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callInProgressSurveyApi(
        onResult: (response: WSObjectResponse<SurveyResp>) -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        try {
            safeApiCall(apiService.callInProgressSurveyApi(), object : HBSuccessCallback<WSObjectResponse<SurveyResp>> {
                override fun onSuccess(response: WSObjectResponse<SurveyResp>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }
                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callGetStoredUserSurveyListApi(
        surveyId: Int,
        onResult: (response: WSObjectResponse<Co2SurveyGetDataResp>) -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        try {
            safeApiCall(apiService.callGetStoredUserSurveyListApi(surveyId), object : HBSuccessCallback<WSObjectResponse<Co2SurveyGetDataResp>> {
                override fun onSuccess(response: WSObjectResponse<Co2SurveyGetDataResp>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }
                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callGetSurveyStatisticsByUserIdApi(
        onResult: (response: WSListResponse<SurveyStatisticsResp>) -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        try {
            safeApiCall(apiService.callGetSurveyStatisticsByUserIdApi(), object : HBSuccessCallback<WSListResponse<SurveyStatisticsResp>> {
                override fun onSuccess(response: WSListResponse<SurveyStatisticsResp>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }
                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callGetSurveyStatusByUserId(
        surveyId: Int,
        onResult: (response: WSListResponse<SurveyResp>) -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        try {
            safeApiCall(apiService.callGetSurveyStatusByUserId(surveyId), object : HBSuccessCallback<WSListResponse<SurveyResp>> {
                override fun onSuccess(response: WSListResponse<SurveyResp>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }
                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callDistanceApi(params: HashMap<String, RequestBody>, onResult: (response: WSObjectResponse<DistanceRes>) -> Unit, onFailure: (message: String) -> Unit) {
        try {
            safeApiCall(apiService.callDistanceApi(params), object : HBSuccessCallback<WSObjectResponse<DistanceRes>> {
                override fun onSuccess(response: WSObjectResponse<DistanceRes>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS) {
                        onResult(response)
                    } else {
                        onFailure(response.responseMessage)
                    }
                }

                override fun onFailure(code: Int?, message: String?) {
                    onFailure(message!!)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callCityListApi(
        params: CityReq,
        onResult: (response: WSObjectResponse<CityResponse>) -> Unit, onFailure: (message: String) -> Unit,
    ) {
        try {
            safeApiCall(apiService.callCityListApi(params),
                object : HBSuccessCallback<WSObjectResponse<CityResponse>> {
                    override fun onSuccess(response: WSObjectResponse<CityResponse>) {
                        if (response.responseCode == HBBaseResponse.SUCCESS) {
                            onResult(response)
                        } else {
                            onFailure(response.responseMessage)
                        }
                    }

                    override fun onFailure(code: Int?, message: String?) {
                        onFailure(message!!)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callInvoiceCalculateCo2Api(params: InvoiceCalculateCo2Req, onResult: (response: WSObjectResponse<Co2CalculateRes>) -> Unit, onFailure: (message: String) -> Unit) {
        try {
            safeApiCall(apiService.callInvoiceCalculateCo2Api(params),
                object : HBSuccessCallback<WSObjectResponse<Co2CalculateRes>> {
                    override fun onSuccess(response: WSObjectResponse<Co2CalculateRes>) {
                        if (response.responseCode == HBBaseResponse.SUCCESS) {
                            onResult(response)
                        } else {
                            onFailure(response.responseMessage)
                        }
                    }

                    override fun onFailure(code: Int?, message: String?) {
                        onFailure(message!!)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callGetFuelTypeId(onResult: (response: WSListResponse<FuelTypeRes>) -> Unit, onFailure: (message: String) -> Unit) {
        try {
            safeApiCall(apiService.callGetFuelType(),
                object : HBSuccessCallback<WSListResponse<FuelTypeRes>> {
                    override fun onSuccess(response: WSListResponse<FuelTypeRes>) {
                        if (response.responseCode == HBBaseResponse.SUCCESS) {
                            onResult(response)
                        } else {
                            onFailure(response.responseMessage)
                        }
                    }

                    override fun onFailure(code: Int?, message: String?) {
                        onFailure(message!!)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callInvoiceCalculateCo2FuelTypeApi(
        params: InvoiceFuelTypeReq,
        onResult: (response: WSObjectResponse<Co2CalculateRes>) -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        try {
            safeApiCall(apiService.callInvoiceCalculateCo2FuelTypeApi(params),
                object : HBSuccessCallback<WSObjectResponse<Co2CalculateRes>> {
                    override fun onSuccess(response: WSObjectResponse<Co2CalculateRes>) {
                        if (response.responseCode == HBBaseResponse.SUCCESS) {
                            onResult(response)
                        } else {
                            onFailure(response.responseMessage)
                        }
                    }

                    override fun onFailure(code: Int?, message: String?) {
                        onFailure(message!!)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callChartCo2Api(
        params: ChartReq,
        onResult: (response: WSObjectResponse<ChartRes>) -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        try {
            safeApiCall(apiService.callChartCo2Api(params),
                object : HBSuccessCallback<WSObjectResponse<ChartRes>> {
                    override fun onSuccess(response: WSObjectResponse<ChartRes>) {
                        if (response.responseCode == HBBaseResponse.SUCCESS) {
                            onResult(response)
                        } else {
                            onFailure(response.responseMessage)
                        }
                    }

                    override fun onFailure(code: Int?, message: String?) {
                        onFailure(message!!)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callDTicketCreatePaymentIntentApi(
        params: DTicketCreatePaymentIntent,
        onResult: (response: WSObjectResponse<DTicketCreatePaymentRes>) -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        try {
            safeApiCall(apiService.callDTicketCreatePaymentIntentApi(params),
                object : HBSuccessCallback<WSObjectResponse<DTicketCreatePaymentRes>> {
                    override fun onSuccess(response: WSObjectResponse<DTicketCreatePaymentRes>) {
                        if (response.responseCode == HBBaseResponse.SUCCESS) {
                            onResult(response)
                        } else {
                            onFailure(response.responseMessage)
                        }
                    }

                    override fun onFailure(code: Int?, message: String?) {
                        onFailure(message!!)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callDTicketApi(
        onResult: (response: WSListResponse<DTicketRes>) -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        try {
            safeApiCall(apiService.callDTicketApi(),
                object : HBSuccessCallback<WSListResponse<DTicketRes>> {
                    override fun onSuccess(response: WSListResponse<DTicketRes>) {
                        if (response.responseCode == HBBaseResponse.SUCCESS) {
                            onResult(response)
                        } else {
                            onFailure(response.responseMessage)
                        }
                    }

                    override fun onFailure(code: Int?, message: String?) {
                        onFailure(message!!)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    override suspend fun callActiveTicketInfoUserId(
        onResult: (response: WSObjectResponse<TicketInfoRes>) -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        try {
            safeApiCall(apiService.callActiveTicketInfoUserId(),
                object : HBSuccessCallback<WSObjectResponse<TicketInfoRes>> {
                    override fun onSuccess(response: WSObjectResponse<TicketInfoRes>) {
                        if (response.responseCode == HBBaseResponse.SUCCESS) {
                            onResult(response)
                        } else {
                            onFailure(response.responseMessage)
                        }
                    }

                    override fun onFailure(code: Int?, message: String?) {
                        onFailure(message!!)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e.message.toBlankString())
        }
    }

    /* override suspend fun callGetSurveyStatusByUserId(
         surveyId: Int,
         onResult: (response: WSListResponse<SurveyResp>) -> Unit,
         onFailure: (message: String) -> Unit
     ) {
         try {
             safeApiCall(apiService.callGetSurveyStatusByUserId(surveyId), object : HBSuccessCallback<WSListResponse<SurveyResp>> {
                 override fun onSuccess(response: WSListResponse<SurveyResp>) {
                     if (response.responseCode == HBBaseResponse.SUCCESS) {
                         onResult(response)
                     } else {
                         onFailure(response.responseMessage)
                     }
                 }

                 override fun onFailure(code: Int?, message: String?) {
                     onFailure(message!!)
                 }
             })
         } catch (e: Exception) {
             e.printStackTrace()
             onFailure(e.message.toString())
         }
     }*/

}