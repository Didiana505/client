package com.example.onlinecoursesclient.domain.repository

import com.example.onlinecoursesclient.domain.model.AuthResult

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthResult>
    suspend fun register(firstName: String, lastName: String, email: String, password: String): Result<AuthResult>
    fun logout()
    fun getCurrentUserId(): String?
}