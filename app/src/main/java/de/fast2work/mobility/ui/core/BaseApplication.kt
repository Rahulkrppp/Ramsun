package de.fast2work.mobility.ui.core

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.recreate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.HiltAndroidApp
import de.fast2work.mobility.R
import de.fast2work.mobility.data.remote.ApiService
import de.fast2work.mobility.data.remote.cit.WSObjectResponse
import de.fast2work.mobility.data.response.RefreshToken
import de.fast2work.mobility.data.response.User
import de.fast2work.mobility.ui.dashboard.DashboardActivity
import de.fast2work.mobility.utility.dialog.DialogUtil
import de.fast2work.mobility.utility.extension.performLogout
import de.fast2work.mobility.utility.extension.setWindowDimensions
import de.fast2work.mobility.utility.preference.EasyPref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Base application
 *
 * @constructor Create empty Base application
 */
@HiltAndroidApp
class BaseApplication : Application(), LifecycleEventObserver {

    lateinit var lifecycle: Lifecycle
    val apiService:ApiService?=null



    companion object {

        lateinit var sharedPreference: EasyPref
        lateinit var tenantSharedPreference: EasyPref
        lateinit var languageSharedPreference: EasyPref
        lateinit var application: BaseApplication

        var themeValue = 0
        var scannerFile : String= ""
        var scannerThreeStepFile : String= ""
        var isAppInForeground: Boolean = false
//        var isUnreadNotificationAvailable = false
        var notificationCount : MutableLiveData<Int> = MutableLiveData()

        //we want only MainApplication can set the instance value


    }
    /**
     * initializes all the instances used globally in app
     *
     */
    override fun onCreate() {
        super.onCreate()
        application = this
        this.setWindowDimensions()
        lifecycle = ProcessLifecycleOwner.get().lifecycle
        lifecycle.addObserver(this)
        sharedPreference = EasyPref.createAppPref(this)
        tenantSharedPreference = EasyPref.createTenantAppPref(this)
        languageSharedPreference = EasyPref.createLanguageAppPref(this)
//        Log.e("=================","is which:: ${getDefaultTheme(this)}")
//
//
//        Log.e("=================","is AppCompatDelegate:: ${AppCompatDelegate.getDefaultNightMode()}")
        themeValue = getDefaultTheme(this)

//        if (getDefaultTheme(this) == Configuration.UI_MODE_NIGHT_YES) {
//            setTheme(R.style.Theme_Mobility_Dark)
//        } else {
//            setTheme(R.style.Theme_Mobility_Light)
//        }

    }
    fun getDefaultTheme(context: Context): Int {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> Configuration.UI_MODE_NIGHT_YES
            Configuration.UI_MODE_NIGHT_NO -> Configuration.UI_MODE_NIGHT_NO
            else -> Configuration.UI_MODE_NIGHT_UNDEFINED
        }
    }


    /**
     * handles state changes
     *
     * @param source
     * @param event
     */
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> {
                isAppInForeground = true
            }

            Lifecycle.Event.ON_RESUME -> {
                isAppInForeground = true
            }

            Lifecycle.Event.ON_PAUSE -> {
                isAppInForeground = false
            }

            Lifecycle.Event.ON_STOP -> {
                isAppInForeground = false
            }

            Lifecycle.Event.ON_DESTROY -> {
                isAppInForeground = false
            }

            else -> {}
        }
    }
     fun callApi(){
        val intent = Intent(this,DashboardActivity::class.java)
         intent.flags =  Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
         startActivity(intent)

        /* val builder = AlertDialog.Builder(this)
         builder.setTitle("Androidly Alert")
         builder.setMessage("We have a message")
         val view = LayoutInflater.from(this).inflate(R.layout.dialog_common_layout,null)
         builder.setView(view)
         val title: AppCompatTextView = view.findViewById(R.id.tv_title)
         val tv_message: AppCompatTextView = view.findViewById(R.id.tv_message)
         val ok: AppCompatTextView = view.findViewById(R.id.tv_ok)

         title.setText(R.string.logout)
         tv_message.setText(msg)
         ok.setOnClickListener{
             application.performLogout()
         }
         builder.show()*/
    }

}