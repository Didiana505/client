package com.example.onlinecoursesclient.domain.model

data class UserProfile(
    val displayName: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val avatarLetter: String
)