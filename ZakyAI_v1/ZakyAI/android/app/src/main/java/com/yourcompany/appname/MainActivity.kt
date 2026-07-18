package com.yourcompany.appname

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yourcompany.appname.presentation.admin.AdminScreen
import com.yourcompany.appname.presentation.auth.AuthScreen
import com.yourcompany.appname.presentation.chat.ChatScreen
import com.yourcompany.appname.presentation.settings.SettingsScreen
import com.yourcompany.appname.presentation.settings.SettingsViewModel
import com.yourcompany.appname.presentation.theme.ZakyAITheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * نقطة الدخول الرئيسية للتطبيق. تستضيف NavHost الذي يتنقل بين:
 * تسجيل الدخول -> الدردشة -> الإعدادات -> لوحة الإدارة.
 *
 * ملاحظة حول Google Sign-In: أضف تهيئة GoogleSignInClient هنا باستخدام
 * الـ Web Client ID الموجود في google-services.json، ثم مرّر الـ
 * AuthCredential الناتج إلى AuthViewModel.signInWithGoogle().
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settings by settingsViewModel.settings.collectAsState()

            ZakyAITheme(themeName = settings.theme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "auth") {
                        composable("auth") {
                            AuthScreen(
                                onGoogleSignInClick = { /* TODO: ابدأ تدفق Google Sign-In هنا */ },
                                onAuthenticated = { navController.navigate("chat") }
                            )
                        }
                        composable("chat") {
                            ChatScreen(
                                onOpenSettings = { navController.navigate("settings") }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                onOpenAdmin = { navController.navigate("admin") },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("admin") {
                            AdminScreen(onClose = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
