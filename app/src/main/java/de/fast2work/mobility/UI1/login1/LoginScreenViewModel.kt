package de.fast2work.mobility.UI1.login1

import dagger.hilt.android.lifecycle.HiltViewModel
import de.fast2work.mobility.di.helper.AuthenticationRepoHelper
import de.fast2work.mobility.ui.core.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(private val authenticationRepo: AuthenticationRepoHelper) : BaseViewModel() {

}