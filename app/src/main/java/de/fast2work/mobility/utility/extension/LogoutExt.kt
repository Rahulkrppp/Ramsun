package de.fast2work.mobility.utility.extension

import android.content.Context

import de.fast2work.mobility.ui.authentication.login.LoginActivity
import de.fast2work.mobility.ui.core.BaseApplication

/**
 * Clear and Logout
 */
fun Context.performLogout() {
    BaseApplication.sharedPreference.clearAll()
    startActivity(LoginActivity.newTaskIntent(this))
}