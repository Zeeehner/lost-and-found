package de.syntax_institut.androidabschlussprojekt.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun loginUser(email: String, password: String): Boolean {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            return true
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun registerUser(email: String, password: String): Boolean {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
