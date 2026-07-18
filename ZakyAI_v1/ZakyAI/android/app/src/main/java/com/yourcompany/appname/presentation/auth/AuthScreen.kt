package com.yourcompany.appname.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yourcompany.appname.data.model.User

/**
 * شاشة تسجيل الدخول: بريد/كلمة مرور + دخول كزائر.
 * ملاحظة: زر Google Sign-In يحتاج ربط GoogleSignInClient الفعلي في Activity
 * (انظر MainActivity.kt) وتمرير الـ credential الناتج إلى viewModel.signInWithGoogle().
 */
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onGoogleSignInClick: () -> Unit,
    onAuthenticated: (User) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUpMode by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    LaunchedEffect(uiState.user) {
        uiState.user?.let(onAuthenticated)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("مرحبًا بك في ZakyAI 🤖", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        if (isSignUpMode) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("الاسم") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
        }

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("البريد الإلكتروني") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it }, label = { Text("كلمة المرور") },
            visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (isSignUpMode) viewModel.signUp(email, password, name)
                else viewModel.signInWithEmail(email, password)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isSignUpMode) "إنشاء حساب" else "تسجيل الدخول")
        }

        TextButton(onClick = { isSignUpMode = !isSignUpMode }) {
            Text(if (isSignUpMode) "لديك حساب؟ سجّل الدخول" else "ليس لديك حساب؟ أنشئ واحدًا")
        }

        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onGoogleSignInClick, modifier = Modifier.fillMaxWidth()) {
            Text("المتابعة عبر Google")
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { viewModel.continueAsGuest() }, modifier = Modifier.fillMaxWidth()) {
            Text("الدخول كزائر")
        }

        uiState.errorMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        if (uiState.isLoading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}
