package de.syntax_institut.androidabschlussprojekt.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

/**
 * Repository zur Verwaltung von Benutzerdaten in Firestore.
 */
class UserRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    /**
     * Speichert oder aktualisiert die Telefonnummer des aktuellen Benutzers in Firestore.
     *
     * @param phoneNumber Die Telefonnummer, die gespeichert werden soll (oder `null` zum Entfernen).
     * @throws IllegalStateException, wenn kein Benutzer eingeloggt ist.
     */
    suspend fun savePhoneNumber(phoneNumber: String?) {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("User not logged in")

        val userRef = firestore.collection("users").document(uid)
        userRef.set(
            mapOf("phoneNumber" to phoneNumber),
            SetOptions.merge()
        ).await()
    }
}