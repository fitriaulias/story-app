package com.dicoding.storyapp.ui.story

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.databinding.ActivityStoryBinding
import com.dicoding.storyapp.ui.ViewModelFactory
import androidx.activity.viewModels
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.local.datastore.UserPreference
import com.dicoding.storyapp.ui.story.paging.PagingViewModel
import com.dicoding.storyapp.ui.story.paging.PagingViewModelFactory
import com.dicoding.storyapp.ui.login.LoginActivity
import com.dicoding.storyapp.ui.story.paging.LoadingStateAdapter

class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
    private val storyViewModel by viewModels<StoryViewModel> {
        ViewModelFactory.getInstance(UserPreference.getInstance(dataStore))
    }
    private lateinit var adapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        adapter = StoryAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        storyViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        storyViewModel.getUser().observe(this) { userModel ->
            if (userModel.isLogin) {
                val token = userModel.token
                val pagingViewModel = ViewModelProvider(this, PagingViewModelFactory(token))
                    .get(PagingViewModel::class.java)
                pagingViewModel.story.observe(this) { pagingData ->
                    adapter.submitData(lifecycle, pagingData)
                }
            }
            else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}