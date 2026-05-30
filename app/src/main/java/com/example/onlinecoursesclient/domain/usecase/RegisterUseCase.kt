package com.example.onlinecoursesclient.domain.usecase

import com.example.onlinecoursesclient.domain.repository.AuthRepository
import com.example.onlinecoursesclient.domain.model.AuthResult
class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Result<AuthResult> {
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Заполните все поля"))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Пароль должен быть не короче 6 символов"))
        }
        return authRepository.register(firstName.trim(), lastName.trim(), email.trim(), password)
    }
}