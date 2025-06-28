package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(
    @SerializedName("userId") var userId: Int? = null,
    @SerializedName("firstName") var firstName: String? = null,
    @SerializedName("lastName") var lastName: String? = null,
    @SerializedName("countryCode") var countryCode: String? = null,
    @SerializedName("mobileNo") var mobileNo: String? = null,
    @SerializedName("systemCode") var systemCode: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("profilePic") var profilePic: String? = null,
    @SerializedName("roleId") var roleId: Int? = null,
    @SerializedName("roleCode") var roleCode: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("preferredLanguage") var preferredLanguage: String? = null,
    @SerializedName("currencyFormat") var currencyFormat: String? = null,
    @SerializedName("addressLine1") var addressLine1: String? = null,
    @SerializedName("addressLine2") var addressLine2: String? = null,
    @SerializedName("systemUser") var systemUser: String? = null,
    @SerializedName("isPrimaryAdmin") var isPrimaryAdmin: Boolean? = null,
    @SerializedName("tenantLogo") var tenantLogo: String? = null,
    @SerializedName("tenantFavIcon") var tenantFavIcon: String? = null,
    @SerializedName("pwaIcon512") var pwaIcon512: String? = null,
    @SerializedName("pwaIcon192") var pwaIcon192: String? = null,
    @SerializedName("pwaIcon180") var pwaIcon180: String? = null,
    @SerializedName("tenantLoginImage") var tenantLoginImage: String? = null,
    @SerializedName("accessToken") var accessToken: String? = null,
    @SerializedName("refreshToken") var refreshToken: String? = null,
    @SerializedName("userPermissions") var userPermissions: ArrayList<String> = arrayListOf(),
    @SerializedName("token") var token: String? = null,
    @SerializedName("twoFactorEnabled") var twoFactorEnabled: String? = null,
    @SerializedName("notificationsEnabled") var notificationsEnabled: String? = "0",
    @SerializedName("lockCo2SurveyDashboard") var lockCo2SurveyDashboard: Boolean? = false,
    @SerializedName("lockCo2ReceiptDashboard") var lockCo2ReceiptDashboard: Boolean? = false,

    @SerializedName("displayRole") var displayRole: String = "",

    @field:SerializedName("zipCode")
    val zipCode: String? = null,

    @field:SerializedName("groupId")
    val groupId: Int? = null,

    @field:SerializedName("departmentId")
    val departmentId: Int? = null,

    @field:SerializedName("newEmailId")
    val newEmailId: String? = null,

    @field:SerializedName("joiningDate")
    val joiningDate: String? = null,

    @field:SerializedName("cityId")
    val cityId: Int? = null,

    @field:SerializedName("countryId")
    val countryId: Int? = null,

    @field:SerializedName("emailVerify")
    val emailVerify: String? = null,

    @field:SerializedName("departmentName")
    val departmentName: String? = null,

    @field:SerializedName("tags")
    val tags: ArrayList<TagsItem> = arrayListOf(),

    @field:SerializedName("attributes")
    val attributes: ArrayList<String> = arrayListOf(),

    @SerializedName("fromAddress")
    var fromAddress : String = "",

    @SerializedName("fromLatitude")
    var fromLatitude : String = "",

    @SerializedName("fromLongitude")
    var fromLongitude : String ="",
    @SerializedName("toAddress")
    var toAddress : String = "",
    @SerializedName("toLatitude")
    var toLatitude : String = "",

    @SerializedName("toLongitude")
    var toLongitude : String = "",

    @SerializedName("distance")
    var distance : String = "",

    @field:SerializedName("assignedBudgetGroups")
    val assignedBudgetGroups: ArrayList<AssignedBudgetGroups> = arrayListOf(),

    @field:SerializedName("cityName")
    val cityName: String? = null,

    @field:SerializedName("countryName")
    val countryName: String? = null,

    @field:SerializedName("isProfileComplete")
    val isProfileComplete: String? = "",

    @field:SerializedName("mobilityBoxRefNo")
    val mobilityBoxRefNo: String? = "",

    @field:SerializedName("activeTicket")
    val activeTicket: ActiveTicket = ActiveTicket(),

    ) : Parcelable


