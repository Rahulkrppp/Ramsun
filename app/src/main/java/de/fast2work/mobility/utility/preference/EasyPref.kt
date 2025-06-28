package de.fast2work.mobility.utility.preference

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import de.fast2work.mobility.data.response.User
import java.lang.reflect.Type

/**
 * This class stores data locally and shared preference
 *
 */
class EasyPref {

    private lateinit var pref: SharedPreferences
    private lateinit var tenantPref: SharedPreferences
    private lateinit var languagePref: SharedPreferences
    private val gson = GsonBuilder().create()

    companion object {
        const val CATEGORY_LIST = "category_list"
        const val FCM_KEY = "FCM_KEY"
        const val USER_DATA = "USER_DATA"
        const val TENANT_DATA = "TENANT_DATA"
        const val ACCESS_KEY = "ACCESS_KEY"
        const val DATE_OF_JOIN = "date_of_join"
        const val PLAN_END_DATE = "plan_end_date"
        const val CONTACT_SYNC = "CONTACT_SYNC"
        const val GENERAL_SETTING = "General_settings"
        const val USER_ACCESS_TOKEN = "access_token"
        const val CURRENCY_SYMBOL = "currency_symbol"
        const val TOKEN = "token"
        const val CHAT_ACCESS_TOKEN = "chat_access_token"
        const val IS_ON_BOARDING_PASSED = "is_on_boarding_passed"
        const val IS_GUEST_USER = "is_guest_user"
        const val IS_PERMISSION_DENIED = "isPermissionDenied"
        const val REMOVED_USER_LIST = "removed_user_list"
        const val USER_ID = "user_id"
        const val PARAM_LANGUAGE = "preferredLanguage"
        const val CURRENT_LANGUAGE = "currentLanguage"
        const val CURRENCY_FORMAT = "currencyFormat"
        const val NOTIFICATION_ENABLED = "notificationEnabled"
        const val D_TICKET="dTicket"
        const val activeTicket="activeTicket"
        const val order_reference="order_reference"
        const val COUPON_ID="coupon_ids"
        const val TICKET_ID="ticketId"

        const val MINDFULLNESS_X = "mindfullness_x"
        const val MINDFULLNESS_Y = "mindfullness_y"

        const val SELFCARE_X = "selfcare_x"
        const val SELFCARE_Y = "selfcare_y"

        const val WELLNESS_X = "wellness_X"
        const val WELLNESS_Y = "wellness_y"

        const val PRODUCTIVITY_X = "productivity_x"
        const val PRODUCTIVITY_Y = "productivity_y"


        private const val APP_PREF = "AppPref"
        private const val TENANT_PREF = "TenantPref"
        private const val LANGUAGE_PREF = "LanguagePref"

        const val HABIT_CHANGE_PRESENT = "habit_change_present"

        fun createInstance(context: Context, name: String): EasyPref {
            val easyPref = EasyPref()
            easyPref.pref = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            easyPref.tenantPref = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            easyPref.languagePref = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            return easyPref
        }

        fun createAppPref(context: Context): EasyPref {
            return createInstance(context, APP_PREF)
        }

        fun createTenantAppPref(context: Context): EasyPref {
            return createInstance(context, TENANT_PREF)
        }
        fun createLanguageAppPref(context: Context): EasyPref {
            return createInstance(context, LANGUAGE_PREF)
        }
    }

    /**
     * This method contains code to set Shared preference for all types
     *
     * @param key
     * @param value
     */
    fun setPref(key: String, value: Any) {
        pref.edit().apply {
            when (value) {
                is String -> {
                    putString(key, value)
                }
                is Boolean -> {
                    putBoolean(key, value)
                }
                is Int -> {
                    putInt(key, value)
                }
                else -> {
                    putString(key, getJsonStringFromObject(value))
                }
            }
            apply()
        }
    }


    fun setTenantPref(key: String, value: Any) {
        tenantPref.edit().apply {
            when (value) {
                is String -> {
                    putString(key, value)
                }

                is Boolean -> {
                    putBoolean(key, value)
                }

                is Int -> {
                    putInt(key, value)
                }

                else -> {
                    putString(key, getJsonStringFromObject(value))
                }
            }
            apply()
        }
    }

    fun setLanguagePref(key: String, value: Any) {
        languagePref.edit().apply {
            when (value) {
                is String -> {
                    putString(key, value)
                }

                is Boolean -> {
                    putBoolean(key, value)
                }

                is Int -> {
                    putInt(key, value)
                }

                else -> {
                    putString(key, getJsonStringFromObject(value))
                }
            }
            apply()
        }
    }



    /**
     * this method contains code to get String Shared Preference data
     *
     * @param key
     * @param value
     * @return
     */
    fun getPref(key: String, value: String): String {
        return pref.getString(key, value) ?: ""
    }

    fun getTenantPref(key: String, value: String): String {
        return tenantPref.getString(key, value) ?: ""
    }

    fun getLanguagePref(key: String, value: String): String {
        return languagePref.getString(key, value) ?: ""
    }
    /**
     * this method contains code to get Int Shared Preference data
     *
     * @param key
     * @param value
     * @return
     */
    fun getPref(key: String, value: Int): Int {
        return pref.getInt(key, value)
    }
    /**
     * this method contains code to get Boolean Shared Preference data
     *
     * @param key
     * @param value
     * @return
     */
    fun getPref(key: String, value: Boolean): Boolean {
        return pref.getBoolean(key, value)
    }

    /**
     * this method contains code to set arraylist as shared preference
     *
     * @param T
     * @param securePrefType
     * @param list
     */
    fun <T> setPrefList(securePrefType: String, list: ArrayList<T>) {
        pref.edit().apply {
            putString(securePrefType, gson.toJson(list))
            apply()
        }
    }

    /**
     * This method contains code to get list from shared preference
     *
     * @param T
     * @param securePrefType
     * @return
     */
    fun <T> getPrefList(securePrefType: String): ArrayList<T> {
        val listInString = pref.getString(securePrefType, "")
        val arrayList = arrayListOf<T>()
        try {
            val type: Type = object : TypeToken<List<T>>() {}.type
            arrayList.addAll(gson.fromJson(listInString, type))

        } catch (e: Exception) {
            //e.printStackTrace()
        }
        return arrayList
    }

    /**
     * This method contains code to get modal data shared preference
     *
     * @param T
     * @param securePrefType
     * @param modelClass
     * @return
     */
    fun <T> getPrefModel(securePrefType: String, modelClass: Class<T>): T? {
        val listInString = pref.getString(securePrefType, "")
        try {
            return gson.fromJson(listInString, modelClass)
        } catch (e: Exception) {
            //e.printStackTrace()
        }
        return null

    }

    /**
     * This method contains code to get modal data shared preference
     *
     * @param T
     * @param securePrefType
     * @param modelClass
     * @return
     */
    fun <T> getTenantPrefModel(securePrefType: String, modelClass: Class<T>): T? {
        val listInString = tenantPref.getString(securePrefType, "")
        try {
            return gson.fromJson(listInString, modelClass)
        } catch (e: Exception) {
            //e.printStackTrace()
        }
        return null

    }

    /**
     * Tghis method is used to get JSON string from object
     *
     * @param modelClass
     * @return
     */
    private fun getJsonStringFromObject(modelClass: Any): String {
        return gson.toJson(modelClass)
    }

    /**
     * This method is used to clear shared preference
     *
     * @param key
     */
    fun clear(key: String) {
        pref.edit().remove(key).apply()
    }

    /**
     * This method is used to clear all shared preferences
     *
     */
    fun clearAll() {
        pref.edit().clear().apply()
    }
}