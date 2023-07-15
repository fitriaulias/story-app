package com.dicoding.storyapp.ui.story.detail

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bumptech.glide.Glide
import com.dicoding.storyapp.data.local.datastore.UserPreference
import com.dicoding.storyapp.databinding.ActivityDetailStoryBinding
import com.dicoding.storyapp.ui.ViewModelFactory

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
    private val detailStoryViewModel by viewModels<DetailStoryViewModel> {
        ViewModelFactory.getInstance(UserPreference.getInstance(dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detailStoryViewModel.isLoading.observe(this){
            showLoading(it)
        }

        detailStoryViewModel.detail.observe(this) {
            binding.tvName.text = it.name
            Glide.with(this)
                .load(it.photoUrl)
                .into(binding.ivImage)
            binding.tvDesc.text = it.description
        }

        detailStoryViewModel.getUser().observe(this) {
            if (it.isLogin) {
                val id = intent.getStringExtra(EXTRA_ID)
                val token = it.token
                if (id != null) {
                    detailStoryViewModel.getDetailStory(token, id)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_ID = "key_id"
    }
}