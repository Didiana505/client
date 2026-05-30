package com.example.onlinecoursesclient.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlinecoursesclient.domain.model.User
import com.example.onlinecoursesclient.domain.model.UserProfile
import com.example.onlinecoursesclient.domain.usecase.GetCurrentUserUseCase
import com.example.onlinecoursesclient.domain.usecase.UpdateCurrentUserAgeUseCase
import com.example.onlinecoursesclient.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    application: Application,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateCurrentUserAgeUseCase: UpdateCurrentUserAgeUseCase,
    private val userRepository: UserRepository
) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("theme_prefs", 0)

    private val _isDarkTheme = MutableStateFlow(prefs.getBoolean("dark_theme", false))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo: StateFlow<User?> = _userInfo.asStateFlow()


    private val _userProfile = MutableStateFlow(UserProfile("", "", "", "", ""))
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun toggleTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
        prefs.edit().putBoolean("dark_theme", enabled).apply()
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _userInfo.value = getCurrentUserUseCase()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _userProfile.value = userRepository.getCurrentUserProfile()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }


    fun updateCurrentUserAge(age: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _userInfo.value = updateCurrentUserAgeUseCase(age)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    //  Выход
    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    fun clearError() {
        _error.value = null
    }
}