package com.example.onlinecoursesclient.domain.usecase

import com.example.onlinecoursesclient.domain.model.User
import com.example.onlinecoursesclient.domain.repository.UserRepository

class UpdateCurrentUserAgeUseCase(
    private val repository: UserRepository
) {
    //  Только age! email/firstName/lastName получаем внутри RepositoryImpl
    suspend operator fun invoke(age: Int): User? {
        return repository.updateCurrentUserAge(age)
    }
}