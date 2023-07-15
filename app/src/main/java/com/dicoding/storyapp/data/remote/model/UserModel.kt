package com.dicoding.storyapp.data.remote.model

data class UserModel(
    val name: String,
    val isLogin: Boolean,
    val token: String = ""
)
