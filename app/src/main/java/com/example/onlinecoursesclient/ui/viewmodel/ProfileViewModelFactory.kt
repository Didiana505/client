package com.example.onlinecoursesclient.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.onlinecoursesclient.domain.repository.UserRepository
import com.example.onlinecoursesclient.domain.usecase.GetCurrentUserUseCase
import com.example.onlinecoursesclient.domain.usecase.UpdateCurrentUserAgeUseCase

class ProfileViewModelFactory(
    private val application: Application,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(
                application = application,
                getCurrentUserUseCase = GetCurrentUserUseCase(userRepository),
                updateCurrentUserAgeUseCase = UpdateCurrentUserAgeUseCase(userRepository),
                userRepository = userRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}