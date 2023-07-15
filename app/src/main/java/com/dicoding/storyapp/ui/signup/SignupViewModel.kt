package com.dicoding.storyapp.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.remote.model.UserModel
import com.dicoding.storyapp.data.local.datastore.UserPreference
import com.dicoding.storyapp.data.remote.response.RegisterResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupViewModel(private val pref: UserPreference) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()

    private val _isRegistrationSuccessful = MutableLiveData<Boolean>()
    val isRegistrationSuccessful: LiveData<Boolean> = _isRegistrationSuccessful

    private val _errorMessage = MutableLiveData<String>()

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().registerUser(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if(response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody != null) {
                        viewModelScope.launch {
                            pref.saveUser(
                                UserModel(
                                    "",
                                    false,
                                    ""
                                )
                            )
                        }
                        _isRegistrationSuccessful.value = true
                    } else {
                        _errorMessage.value = "Registration failed"
                        _isRegistrationSuccessful.value = false
                    }
                }
                else {
                    val errorResponse = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        RegisterResponse::class.java
                    )
                    val errorMessage = errorResponse?.message ?: "Unknown error"
                    _errorMessage.value = errorMessage
                    _isRegistrationSuccessful.value = false

                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message.toString()
                _isRegistrationSuccessful.value = false
            }
        })
    }
}