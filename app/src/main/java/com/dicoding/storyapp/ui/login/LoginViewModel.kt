package com.dicoding.storyapp.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.remote.model.UserModel
import com.dicoding.storyapp.data.local.datastore.UserPreference
import com.dicoding.storyapp.data.remote.response.LoginResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun authenticate(email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().loginUser(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.isSuccessful) {
                    _isLoading.value = false
                    val responseBody = response.body()
                    if(responseBody != null && !responseBody.error) {
                        viewModelScope.launch {
                            pref.saveUser(
                                UserModel(
                                    responseBody.loginResult.name,
                                    true,
                                    responseBody.loginResult.token
                                )
                            )
                            pref.login(responseBody.loginResult.token)
                        }
                    } else {
                        Log.e("LoginViewModel", "Authentication failed: ${responseBody?.message}\"")
                    }
                }
                else {
                    Log.e("LoginViewModel", "Authentication failed")
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("LoginViewModel", "Authentication failed: ${t.message}")
            }
        })
    }


}