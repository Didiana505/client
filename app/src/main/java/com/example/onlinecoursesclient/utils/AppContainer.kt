package com.example.onlinecoursesclient.utils

import android.app.Application
import com.example.onlinecoursesclient.data.repository.AuthRepositoryImpl
import com.example.onlinecoursesclient.data.repository.CourseRepositoryImpl
import com.example.onlinecoursesclient.data.repository.UserRepositoryImpl
import com.example.onlinecoursesclient.domain.repository.AuthRepository
import com.example.onlinecoursesclient.domain.repository.CourseRepository
import com.example.onlinecoursesclient.domain.repository.UserRepository
import com.example.onlinecoursesclient.domain.usecase.LoginUseCase
import com.example.onlinecoursesclient.domain.usecase.RegisterUseCase
import com.example.onlinecoursesclient.ui.viewmodel.AuthViewModel
import com.example.onlinecoursesclient.ui.viewmodel.CoursesViewModelFactory
import com.example.onlinecoursesclient.ui.viewmodel.ProfileViewModelFactory

object AppContainer {


    private val authRepository: AuthRepository by lazy { AuthRepositoryImpl() }
    private val courseRepository: CourseRepository by lazy { CourseRepositoryImpl() }
    private val userRepository: UserRepository by lazy { UserRepositoryImpl() }


    private val loginUseCase by lazy { LoginUseCase(authRepository) }
    private val registerUseCase by lazy { RegisterUseCase(authRepository) }

    fun getAuthViewModelFactory() = AuthViewModel.Factory(
        loginUseCase = loginUseCase,
        registerUseCase = registerUseCase
    )

    fun getCoursesViewModelFactory() = CoursesViewModelFactory(
        courseRepository = courseRepository
    )

    fun getProfileViewModelFactory(application: Application) = ProfileViewModelFactory(
        application = application,
        userRepository = userRepository
    )
}