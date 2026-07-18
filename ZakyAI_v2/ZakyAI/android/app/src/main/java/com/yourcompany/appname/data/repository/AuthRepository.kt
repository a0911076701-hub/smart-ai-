package com.yourcompany.appname.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.yourcompany.appname.data.model.User
import com.yourcompany.appname.utils.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val currentUser get() = auth.currentUser

    suspend fun signInWithGoogle(idToken: String): Result<User> = try {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val firebaseUser = result.user!!
        val user = User(
            uid = firebaseUser.uid,
            name = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: "",
            photoUrl = firebaseUser.photoUrl?.toString() ?: ""
        )
        saveUserIfNew(user)
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun signInWithEmail(email: String, password: String): Result<User> = try {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user!!
        Result.success(
            User(uid = firebaseUser.uid, name = firebaseUser.displayName ?: "", email = firebaseUser.email ?: "")
        )
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun registerWithEmail(name: String, email: String, password: String): Result<User> = try {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user!!
        val user = User(uid = firebaseUser.uid, name = name, email = email)
        saveUserIfNew(user)
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun signOut() = auth.signOut()

    private suspend fun saveUserIfNew(user: User) {
        val docRef = firestore.collection(Constants.COLLECTION_USERS).document(user.uid)
        val snapshot = docRef.get().await()
        if (!snapshot.exists()) {
            docRef.set(user).await()
        }
    }
}
