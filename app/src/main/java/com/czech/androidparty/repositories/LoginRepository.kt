package com.czech.androidparty.repositories

import androidx.lifecycle.asLiveData
import com.czech.androidparty.datasource.network.ApiService
import com.czech.androidparty.models.LoginRequest
import com.czech.androidparty.models.LoginResponse
import com.czech.androidparty.utils.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class LoginRepository(
    private val apiService: ApiService
) {
    fun execute(
        userData: LoginRequest
    ): Flow<DataState<LoginResponse>> {
        return flow<DataState<LoginResponse>> {

            emit(DataState.loading())

            val response = apiService.login(userData)

            try {
                if (response.message != null) {
                    DataState.error<LoginResponse>(response.message)
                } else {
                    emit(DataState.data(data = response))
                }
            }catch (e: Exception) {
                emit(
                    DataState.error(
                        message = response.message.toString()
                    )
                )
            }
        }
    }
}