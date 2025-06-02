package de.syntax_institut.androidabschlussprojekt.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * Repository für Authentifizierung mit Firebase.
 * Stellt Methoden zum Einloggen und Registrieren von Benutzern bereit.
 *
 * @property firebaseAuth Firebase-Instanz zur Benutzerverwaltung.
 */
class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    /**
     * Meldet einen Benutzer mit E-Mail und Passwort an.
     *
     * @param email Benutzer-E-Mail.
     * @param password Benutzer-Passwort.
     * @return `true`, wenn Anmeldung erfolgreich.
     * @throws Exception bei Authentifizierungsfehlern.
     */
    suspend fun loginUser(email: String, password: String): Boolean {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            return true
        } catch (e: Exception) {
            throw e // kann ggf. durch Logging oder spezifischeren Fehler ersetzt werden
        }
    }

    /**
     * Registriert einen neuen Benutzer mit E-Mail und Passwort.
     *
     * @param email Neue Benutzer-E-Mail.
     * @param password Neues Passwort.
     * @return `true`, wenn Registrierung erfolgreich; `false`, wenn fehlgeschlagen.
     */
    suspend fun registerUser(email: String, password: String): Boolean {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}