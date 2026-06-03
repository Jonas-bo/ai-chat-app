package com.kouti.aichat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.Navigation
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kouti.aichat.ui.screens.ChatScreen
import com.kouti.aichat.ui.screens.SettingsScreen
import com.kouti.aichat.viewmodel.ChatViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val chatViewModel: ChatViewModel = viewModel()

            NavHost(
                navController = navController,
                startDestination = "chat"
            ) {
                composable("chat") {
                    ChatScreen(chatViewModel, onNavigateToSettings = {
                        navController.navigate("settings")
                    })
                }
                composable("settings") {
                    SettingsScreen(chatViewModel, onBack = {
                        navController.popBackStack()
                    })
                }
            }
        }
    }
}
