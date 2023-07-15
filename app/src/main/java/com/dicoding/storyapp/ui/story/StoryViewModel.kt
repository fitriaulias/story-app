package com.dicoding.storyapp.ui.story

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.remote.response.ListStoryResponse
import com.dicoding.storyapp.data.local.datastore.UserPreference
import com.dicoding.storyapp.data.remote.model.UserModel
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryViewModel(private val pref: UserPreference) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _story = MutableLiveData<List<ListStoryItem>>()
    val story: LiveData<List<ListStoryItem>> = _story

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun getAllStoryWithLocation(token:String) {
        _isLoading.value = true
        viewModelScope.launch {
            pref.getToken().collect {token ->
                if (token != null) {
                    val client = ApiConfig.getApiService().getAllLocation("Bearer $token")
                    client.enqueue(object : Callback<ListStoryResponse> {
                        override fun onResponse(
                            call: Call<ListStoryResponse>,
                            response: Response<ListStoryResponse>
                        ) {
                            _isLoading.value = false
                            if (response.isSuccessful) {
                                _story.value = response.body()?.listStory
                            } else {
                                Log.e(TAG, "onFailure: ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
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

    companion object{
        private const val TAG = "StoryViewModel"
    }
}