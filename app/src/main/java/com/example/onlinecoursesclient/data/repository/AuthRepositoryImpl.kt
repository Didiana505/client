package com.example.onlinecoursesclient.data.repository

import com.example.onlinecoursesclient.domain.model.AuthResult
import com.example.onlinecoursesclient.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthRepositoryImpl : AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun login(email: String, password: String): Result<AuthResult> {
        return suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        continuation.resume(
                            Result.success(
                                AuthResult(
                                    userId = user?.uid ?: "",
                                    email = user?.email ?: "",
                                    displayName = user?.displayName ?: ""
                                )
                            )
                        )
                    } else {
                        continuation.resume(
                            Result.failure(Exception("Неправильный логин или пароль"))
                        )
                    }
                }
        }
    }

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Result<AuthResult> {
        return suspendCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName("$firstName $lastName")
                            .build()

                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    continuation.resume(
                                        Result.success(
                                            AuthResult(
                                                userId = user.uid,
                                                email = user.email ?: "",
                                                displayName = "$firstName $lastName"
                                            )
                                        )
                                    )
                                } else {
                                    continuation.resume(
                                        Result.success(
                                            AuthResult(
                                                userId = user.uid,
                                                email = user.email ?: "",
                                                displayName = "$firstName $lastName"
                                            )
                                        )
                                    )
                                }
                            }
                    } else {
                        continuation.resume(
                            Result.failure(task.exception ?: Exception("Ошибка регистрации"))
                        )
                    }
                }
        }
    }

    override fun logout() {
        auth.signOut()
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}