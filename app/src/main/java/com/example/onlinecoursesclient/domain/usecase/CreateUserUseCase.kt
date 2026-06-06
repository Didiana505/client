package com.example.onlinecoursesclient.domain.usecase

import com.example.onlinecoursesclient.domain.repository.UserRepository

class CreateUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, firstName: String, lastName: String) {
        userRepository.createUser(email, firstName, lastName)
    }
}