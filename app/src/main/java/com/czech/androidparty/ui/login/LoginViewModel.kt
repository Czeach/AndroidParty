package com.czech.androidparty.ui.login

import android.content.Context
import android.util.Log
import androidx.core.content.ContentProviderCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.czech.androidparty.models.LoginRequest
import com.czech.androidparty.preferences.SharedPrefs
import com.czech.androidparty.repositories.LoginRepository
import com.czech.androidparty.responseStates.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    @ApplicationContext context: Context
) : ViewModel() {

    val loginState = MutableStateFlow<LoginState?>(null)

    private val sharedPrefs = SharedPrefs(context)

    fun login(
        username: String,
        password: String
    ) {
        viewModelScope.launch {
            loginRepository.execute(
                LoginRequest(
                    username = username,
                    password = password
                )
            ).collect {
                when {
                    it.isLoading -> {
                        loginState.value = LoginState.Loading
                    }
                    it.data?.token == null -> {
                        loginState.value = LoginState.Error(message = it.message!!)
                    }
                    else -> {
                        it.data.let { response ->
                            loginState.value = LoginState.Success(data = response)

                            sharedPrefs.saveToken(response.token.toString())
                        }
                    }
                }
            }
        }
    }

}