package com.example.onlinecoursesclient.domain.usecase

import com.example.onlinecoursesclient.domain.repository.AuthRepository
import com.example.onlinecoursesclient.domain.model.AuthResult

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<AuthResult> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Заполните email и пароль"))
        }
        return authRepository.login(email.trim(), password)
    }
}