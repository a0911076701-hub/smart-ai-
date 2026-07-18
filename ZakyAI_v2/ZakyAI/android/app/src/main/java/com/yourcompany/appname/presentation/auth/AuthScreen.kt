package com.yourcompany.appname.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/** شاشة تسجيل الدخول: Google / Email / زائر */
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) onAuthSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("ZakyAI", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("مساعدك الذكي المتطور", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("البريد الإلكتروني") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("كلمة المرور") },
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { viewModel.signInWithEmail(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("تسجيل الدخول") }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = { viewModel.register("مستخدم جديد", email, password) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("إنشاء حساب جديد") }

        Spacer(Modifier.height(8.dp))

        // ملاحظة: زر Google الحقيقي يتطلب ربط GoogleSignInClient في MainActivity
        // والحصول على idToken ثم استدعاء viewModel.signInWithGoogle(idToken)
        OutlinedButton(
            onClick = { /* TODO: تشغيل Google Sign-In Intent من MainActivity */ },
            modifier = Modifier.fillMaxWidth()
        ) { Text("المتابعة عبر Google") }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = { viewModel.continueAsGuest() }) {
            Text("المتابعة كزائر")
        }

        if (uiState is AuthUiState.Loading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator()
        }
        if (uiState is AuthUiState.Error) {
            Spacer(Modifier.height(16.dp))
            Text(
                (uiState as AuthUiState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
