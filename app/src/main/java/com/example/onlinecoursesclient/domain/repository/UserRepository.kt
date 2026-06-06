package com.example.onlinecoursesclient.domain.repository

import com.example.onlinecoursesclient.domain.model.User
import com.example.onlinecoursesclient.domain.model.UserProfile
interface UserRepository {

    suspend fun getCurrentUser(): User?


    suspend fun updateCurrentUserAge(age: Int): User?

    //  метод для получения профиля
    suspend fun getCurrentUserProfile(): UserProfile

    //  Выход
    suspend fun logout()
    suspend fun createUser(email: String, firstName: String, lastName: String)
}