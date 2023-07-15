package com.dicoding.storyapp.ui.story.detail

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.storyapp.data.local.datastore.UserPreference
import com.dicoding.storyapp.data.remote.model.UserModel
import com.dicoding.storyapp.data.remote.response.DetailStoryResponse
import com.dicoding.storyapp.data.remote.response.Story
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailStoryViewModel(private val pref: UserPreference) : ViewModel() {
    private val _detail = MutableLiveData<Story>()
    val detail: LiveData<Story> = _detail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun getDetailStory(token: String, id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            pref.getToken().collect { token ->
                if (token != null) {
                    val client = ApiConfig.getApiService().getDetailStoryById("Bearer $token", id)
                    client.enqueue(object : Callback<DetailStoryResponse> {
                        override fun onResponse(
                            call: Call<DetailStoryResponse>,
                            response: Response<DetailStoryResponse>
                        ) {
                            _isLoading.value = false
                            if (response.isSuccessful) {
                                _detail.value = response.body()?.story
                            } else {
                                Log.e(TAG, "onFailure: ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                            _isLoading.value = false
                            Log.e(TAG, "onFailure: ${t.message.toString()}")
                        }
                    })
                } else {
                    Log.e(TAG, "Token is null")
                    _isLoading.value = false
                }
            }
        }

    }

    companion object {
        private const val TAG = "DetailStoryViewModel"
    }
}