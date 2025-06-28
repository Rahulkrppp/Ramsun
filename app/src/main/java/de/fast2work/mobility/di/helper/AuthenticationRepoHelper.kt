package de.fast2work.mobility.di.helper

import com.google.gson.JsonElement
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


interface AuthenticationRepoHelper {
    suspend fun callLoginApi(model: LoginRequest, onResult: (response: WSObjectResponse<User>) -> Unit, onFailure: (message: String) -> Unit)
    suspend fun call2FALoginApi(
        model: Login2FAReq, onResult: (response: WSObjectResponse<User>) -> Unit,
        onFailure: (message: String) -> Unit,
    )

    suspend fun callVerifyOtpApi(
        model: VerifyOtpReq, onResult: (response: WSObjectResponse<VerifyMobileOtp>) -> Unit,
        onFailure: (
            message: String,
        ) -> Unit,
    )

    suspend fun callForgotPasswordApi(
        model: ForgotPasswordReq, onResult: (response: WSObjectResponse<JsonElement>) -> Unit,
        onFailure: (
            message: String,
        ) -> Unit,
    )

    suspend fun callResetPasswordApi(
        model: ResetPasswordReq, onResult: (response: WSObjectResponse<JsonElement>) -> Unit,
        onFailure: (
            message: String,
        ) -> Unit,
    )

    suspend fun callGetTenantThemeApi(
        tenantName: String, onResult: (response: WSObjectResponse<TenantInfoModel>) -> Unit,
        onFailure: (message: String) -> Unit,
    )
    suspend fun callChangePasswordApi(
        model:ChangePasswordReq, onResult: (response: WSObjectResponse<JsonElement>) -> Unit,
        onFailure: (message: String) -> Unit,
    )


    suspend fun callAppVersionApi(appType:String, onResult: (response: WSObjectResponse<AppUpdate>) -> Unit,
                                  onFailure: (message: String) -> Unit,
    )

    suspend fun callCreateAccountApi(
        model:SignUpReq, onResult: (response: WSObjectResponse<JsonElement>) -> Unit,
        onFailure: (message: String) -> Unit,
    )
}

