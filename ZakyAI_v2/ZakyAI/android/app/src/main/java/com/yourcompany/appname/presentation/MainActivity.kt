package com.yourcompany.appname.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yourcompany.appname.presentation.admin.AdminScreen
import com.yourcompany.appname.presentation.auth.AuthScreen
import com.yourcompany.appname.presentation.chat.ChatScreen
import com.yourcompany.appname.presentation.settings.SettingsScreen
import com.yourcompany.appname.presentation.theme.ZakyAITheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * نقطة الدخول الرئيسية للتطبيق.
 * ملاحظة: تكامل Google Sign-In الفعلي (GoogleSignInClient + startActivityForResult/Contracts)
 * وSpeechRecognizer وTextToSpeech يُضافان هنا لأنها تتطلب Activity Context حقيقي،
 * ثم تُمرَّر النتائج إلى الـ ViewModels المناسبة عبر callbacks.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentUserId by remember { mutableStateOf<String?>(null) }

            ZakyAITheme {
                Surface(modifier = Modifier) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "auth") {
                        composable("auth") {
                            AuthScreen(onAuthSuccess = {
                                currentUserId = "current_user_id" // استبدلها بمعرّف المستخدم الفعلي من AuthViewModel
                                navController.navigate("chat") { popUpTo("auth") { inclusive = true } }
                            })
                        }
                        composable("chat") {
                            ChatScreen(
                                userId = currentUserId ?: "guest",
                                onOpenSettings = { navController.navigate("settings") },
                                onOpenDrawer = { /* TODO: فتح Drawer بقائمة المحادثات المحفوظة */ }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(onOpenAdmin = { navController.navigate("admin") })
                        }
                        composable("admin") {
                            AdminScreen()
                        }
                    }
                }
            }
        }
    }
}
