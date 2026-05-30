package com.example.onlinecoursesclient.domain.usecase

import com.example.onlinecoursesclient.domain.model.User
import com.example.onlinecoursesclient.domain.repository.UserRepository

class GetCurrentUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): User? {
        return repository.getCurrentUser()
    }
}