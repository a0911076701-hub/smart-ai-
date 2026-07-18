package com.yourcompany.appname.data.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yourcompany.appname.data.model.User
import com.yourcompany.appname.utils.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * يدير كل ما يخص تسجيل الدخول/الخروج وربط بيانات المستخدم بـ Firestore.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val currentUserId: String? get() = auth.currentUser?.uid

    /** تسجيل الدخول عبر بيانات اعتماد Google (يُمرَّر IdToken من شاشة تسجيل الدخول) */
    suspend fun signInWithGoogle(credential: AuthCredential): Result<User> = runCatching {
        val result = auth.signInWithCredential(credential).await()
        val fUser = result.user ?: error("فشل تسجيل الدخول")
        val user = User(
            id = fUser.uid,
            email = fUser.email.orEmpty(),
            displayName = fUser.displayName.orEmpty(),
            photoUrl = fUser.photoUrl?.toString().orEmpty()
        )
        saveUserIfNotExists(user)
        user
    }

    /** تسجيل الدخول بالبريد وكلمة المرور */
    suspend fun signInWithEmail(email: String, password: String): Result<User> = runCatching {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val fUser = result.user ?: error("فشل تسجيل الدخول")
        getUser(fUser.uid) ?: User(id = fUser.uid, email = email)
    }

    /** إنشاء حساب جديد بالبريد وكلمة المرور */
    suspend fun signUpWithEmail(email: String, password: String, name: String): Result<User> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val fUser = result.user ?: error("فشل إنشاء الحساب")
        val user = User(id = fUser.uid, email = email, displayName = name)
        saveUserIfNotExists(user)
        user
    }

    /** الدخول كزائر - بدون حساب Firebase، فقط جلسة محلية */
    fun signInAsGuest(): User = User(
        id = "guest_${System.currentTimeMillis()}",
        displayName = "زائر",
        isGuest = true
    )

    fun signOut() = auth.signOut()

    private suspend fun saveUserIfNotExists(user: User) {
        val ref = firestore.collection(Constants.COLLECTION_USERS).document(user.id)
        val snapshot = ref.get().await()
        if (!snapshot.exists()) {
            ref.set(user).await()
        }
    }

    private suspend fun getUser(uid: String): User? =
        firestore.collection(Constants.COLLECTION_USERS).document(uid)
            .get().await().toObject(User::class.java)
}
