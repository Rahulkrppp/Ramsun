package de.fast2work.mobility.ui.authentication

import com.google.gson.JsonElement
import de.fast2work.mobility.data.remote.ApiService
import de.fast2work.mobility.data.remote.cit.HBBaseResponse
import de.fast2work.mobility.data.remote.cit.HBSuccessCallback
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.request.ChangePasswordReq
import de.fast2work.mobility.data.request.ForgotPasswordReq
import de.fast2work.mobility.data.request.Login2FAReq
import de.fast2work.mobility.data.request.LoginRequest
import de.fast2work.mobility.data.request.ResetPasswordReq
import de.fast2work.mobility.data.request.SignUpReq
import de.fast2work.mobility.data.request.VerifyOtpReq
import de.fast2work.mobility.data.response.AppUpdate
import de.fast2work.mobility.data.response.TenantInfoModel
import de.fast2work.mobility.data.response.User
import de.fast2work.mobility.data.response.VerifyMobileOtp
import de.fast2work.mobility.di.helper.AuthenticationRepoHelper
import de.fast2work.mobility.ui.core.BaseRepository
import de.fast2work.mobility.utility.extension.toBlankString
import javax.inject.Inject

/**
 * This class use for all api call onSuccess or onFailure Authentication Repository
 */
class AuthenticationRepository @Inject constructor(private val apiService: ApiService) : BaseRepository(), AuthenticationRepoHelper {

    /**
     *  Login api call
     */
     override suspend fun callLoginApi(model: LoginRequest, onResult: (response: WSObjectResponse<User>) -> Unit, onFailure: (message: String) -> Unit, ) {
        try {
            safeApiCall(apiService.callLoginApi(model), object : HBSuccessCallback<WSObjectResponse<User>> {
                override fun onSuccess(response: WSObjectResponse<User>) {
                    if (response.responseCode == HBBaseResponse.SUCCESS ) {
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

    override suspend fun call2FALoginApi(
        model: Login2FAReq, onResult: (response: WSObjectResponse<User>) -> Unit,
        onFailure: (message: String) -> Unit,
    ) {
        try {
            safeApiCall(apiService.call2FALoginApi(model), object : HBSuccessCallback<WSObjectResponse<User>> {
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

    override suspend fun callVerifyOtpApi(
        model: VerifyOtpReq, onResult: (response: WSObjectResponse<VerifyMobileOtp>) -> Unit,
        onFailure: (message: String) -> Unit,
    ) {
        try {
            safeApiCall(apiService.callVerifyOtpApi(model), object : HBSuccessCallback<WSObjectResponse<VerifyMobileOtp>> {
                override fun onSuccess(response: WSObjectResponse<VerifyMobileOtp>) {
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

    override suspend fun callForgotPasswordApi(
        model: ForgotPasswordReq, onResult: (response: WSObjectResponse<JsonElement>) -> Unit,
        onFailure: (message: String) -> Unit,
    ) {
        try {
            safeApiCall(apiService.callForgotPasswordApi(model), object : HBSuccessCallback<WSObjectResponse<JsonElement>> {
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

    override suspend fun callResetPasswordApi(
        model: ResetPasswordReq, onResult: (response: WSObjectResponse<JsonElement>) -> Unit,
        onFailure: (message: String) -> Unit,
    ) {
        try {
            safeApiCall(apiService.callResetPasswordApi(model), object : HBSuccessCallback<WSObjectResponse<JsonElement>> {
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

    override suspend fun callChangePasswordApi(model: ChangePasswordReq, onResult: (response: WSObjectResponse<JsonElement>) -> Unit, onFailure: (message: String) -> Unit) {
        try {
            safeApiCall(apiService.callChangePasswordApi(model), object : HBSuccessCallback<WSObjectResponse<JsonElement>> {
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

    override suspend fun callAppVersionApi( appType:String,
        onResult: (response: WSObjectResponse<AppUpdate>) -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        try {
            safeApiCall(apiService.callAppVersionApi(appType), object : HBSuccessCallback<WSObjectResponse<AppUpdate>> {
                override fun onSuccess(response: WSObjectResponse<AppUpdate>) {
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

    override suspend fun callCreateAccountApi(
        model: SignUpReq,
        onResult: (response: WSObjectResponse<JsonElement>) -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        try {
            safeApiCall(apiService.callCreateAccountApi(model), object : HBSuccessCallback<WSObjectResponse<JsonElement>> {
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

}