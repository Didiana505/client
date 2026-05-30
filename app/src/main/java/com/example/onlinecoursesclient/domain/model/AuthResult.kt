package com.example.onlinecoursesclient.domain.model

data class AuthResult(
    val userId: String,
    val email: String,
    val displayName: String
)