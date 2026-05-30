package com.example.onlinecoursesclient.domain.model

data class User(
    val firebaseUid: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val age: Int?
)