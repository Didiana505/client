package com.example.onlinecoursesclient.data.repository

import com.example.onlinecoursesclient.data.model.UpdateAgeRequestDto
import com.example.onlinecoursesclient.data.model.UserDto
import com.example.onlinecoursesclient.data.remote.httpClient
import com.example.onlinecoursesclient.domain.model.User
import com.example.onlinecoursesclient.domain.model.UserProfile
import com.example.onlinecoursesclient.domain.repository.UserRepository
import com.example.onlinecoursesclient.data.model.CreateUserRequestDto
import com.example.onlinecoursesclient.utils.TokenManager
import com.google.firebase.auth.FirebaseAuth
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.client.request.post

class UserRepositoryImpl(
    private val client: HttpClient = httpClient
) : UserRepository {

    private val baseUrl = "http://10.0.2.2:8080"
    private val auth = FirebaseAuth.getInstance()

    override suspend fun getCurrentUser(): User? {
        val token = TokenManager.getFirebaseToken()
        if (token.isBlank()) return null

        val response = client.get("$baseUrl/users/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        if (!response.status.isSuccess()) {
            return null
        }

        val dto: UserDto = response.body()
        return dto.toUser()
    }

    override suspend fun updateCurrentUserAge(age: Int): User? {
        val token = TokenManager.getFirebaseToken()
        if (token.isBlank()) return null

        val user = auth.currentUser
        val email = user?.email ?: throw Exception("Email не найден")
        val displayName = user?.displayName ?: ""
        val nameParts = displayName.split(" ")
        val firstName = nameParts.getOrNull(0) ?: ""
        val lastName = nameParts.getOrNull(1) ?: ""

        val response = client.put("$baseUrl/users/me/age") {
            header(HttpHeaders.Authorization, "Bearer $token")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(
                UpdateAgeRequestDto(
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    age = age
                )
            )
        }

        if (!response.status.isSuccess()) {
            throw Exception("Ошибка сохранения возраста: ${response.status}")
        }

        val dto: UserDto = response.body()
        return dto.toUser()
    }


    override suspend fun getCurrentUserProfile(): UserProfile {
        val user = auth.currentUser
        val displayName = user?.displayName ?: "Не указано"
        val email = user?.email ?: "Email не указан"
        val nameParts = displayName.split(" ")
        val firstName = nameParts.getOrNull(0)?.takeIf { it.isNotBlank() } ?: "Не указано"
        val lastName = nameParts.getOrNull(1)?.takeIf { it.isNotBlank() } ?: "Не указано"
        val avatarLetter = firstName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

        return UserProfile(
            displayName = displayName,
            email = email,
            firstName = firstName,
            lastName = lastName,
            avatarLetter = avatarLetter
        )
    }


    override suspend fun logout() {
        auth.signOut()
    }
    override suspend fun createUser(email: String, firstName: String, lastName: String) {
        val token = TokenManager.getFirebaseToken()
        if (token.isBlank()) throw Exception("Не авторизован")

        val response = client.post("$baseUrl/users/create") {
            header(HttpHeaders.Authorization, "Bearer $token")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(
                CreateUserRequestDto(
                    email = email,
                    firstName = firstName,
                    lastName = lastName
                )
            )
        }

        if (!response.status.isSuccess()) {
            throw Exception("Ошибка создания пользователя: ${response.status}")
        }
    }

    private fun UserDto.toUser(): User {
        return User(
            firebaseUid = firebaseUid,
            email = email,
            firstName = firstName,
            lastName = lastName,
            age = age
        )
    }
}