package com.yourcompany.appname.presentation.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yourcompany.appname.presentation.ui.theme.ZakyAITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentUserId by remember { mutableStateOf<String?>(null) }

            ZakyAITheme {
                Surface {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "auth") {
                        composable("auth") {
                            AuthScreen(onAuthSuccess = {
                                currentUserId = "current_user_id"
                                navController.navigate("chat") { popUpTo("auth") { inclusive = true } }
                            })
                        }
                        composable("chat") {
                            ChatScreen(
                                userId = currentUserId ?: "guest",
                                onOpenSettings = { navController.navigate("settings") },
                                onOpenDrawer = { /* TODO: فتح Drawer */ }
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
