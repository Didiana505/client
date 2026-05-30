package com.example.onlinecoursesclient.utils

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object TokenManager {

    suspend fun getFirebaseToken(): String {
        return suspendCancellableCoroutine { continuation ->
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                continuation.resume("")
                return@suspendCancellableCoroutine
            }

            user.getIdToken(true)
                .addOnSuccessListener { tokenResult ->
                    continuation.resume(tokenResult.token ?: "")
                }
                .addOnFailureListener {
                    continuation.resume("")
                }
        }
    }
}