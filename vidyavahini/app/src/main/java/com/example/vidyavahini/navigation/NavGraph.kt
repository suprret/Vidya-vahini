package com.example.vidyavahini.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vidyavahini.ui.screens.ChatbotScreen
import com.example.vidyavahini.ui.screens.LoginScreen
import com.example.vidyavahini.ui.screens.OtpScreen
import com.example.vidyavahini.ui.screens.ProfileScreen
import com.example.vidyavahini.ui.screens.ProfileSetupScreen
import com.example.vidyavahini.ui.screens.RouteSelectionScreen
import com.example.vidyavahini.ui.screens.SafeReachScreen
import com.example.vidyavahini.ui.screens.SettingsScreen
import com.example.vidyavahini.ui.screens.TrackerScreen
import com.example.vidyavahini.viewmodel.AuthStep
import com.example.vidyavahini.viewmodel.AuthViewModel

object VVScreens {
    const val LOGIN           = "login"
    const val OTP             = "otp"
    const val PROFILE_SETUP   = "profile_setup"
    const val ROUTE_SELECTION = "route_selection"
    const val TRACKER         = "tracker/{routeId}"
    const val SAFE_REACH      = "safe_reach"
    const val PROFILE         = "profile"
    const val SETTINGS        = "settings"
    const val CHATBOT         = "chatbot"
    fun tracker(routeId: String) = "tracker/$routeId"
}

@Composable
fun VidyaVahiniNavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    isDarkMode: Boolean = false,
    onDarkModeToggle: (Boolean) -> Unit = {},
    currentLanguage: String = "English",
    onLanguageChange: (String) -> Unit = {}
) {
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    val startDestination = if (authState.isAuthenticated)
        VVScreens.ROUTE_SELECTION else VVScreens.LOGIN

    NavHost(
        navController    = navController,
        startDestination = startDestination
    ) {
        composable(VVScreens.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onOtpSent = { navController.navigate(VVScreens.OTP) }
            )
        }

        composable(VVScreens.OTP) {
            OtpScreen(
                viewModel  = authViewModel,
                onVerified = {
                    when (authState.step) {
                        AuthStep.PROFILE_SETUP ->
                            navController.navigate(VVScreens.PROFILE_SETUP) {
                                popUpTo(VVScreens.LOGIN) { inclusive = true }
                            }
                        AuthStep.DONE ->
                            navController.navigate(VVScreens.ROUTE_SELECTION) {
                                popUpTo(VVScreens.LOGIN) { inclusive = true }
                            }
                        else -> {}
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(VVScreens.PROFILE_SETUP) {
            ProfileSetupScreen(
                viewModel      = authViewModel,
                onProfileSaved = {
                    navController.navigate(VVScreens.ROUTE_SELECTION) {
                        popUpTo(VVScreens.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(VVScreens.ROUTE_SELECTION) {
            RouteSelectionScreen(
                onRouteSelected = { routeId ->
                    navController.navigate(VVScreens.tracker(routeId))
                },
                // Fix 3: Added authViewModel.signOut() before navigation
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(VVScreens.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onProfile  = { navController.navigate(VVScreens.PROFILE) },
                onSettings = { navController.navigate(VVScreens.SETTINGS) },
                onChatbot  = { navController.navigate(VVScreens.CHATBOT) }
            )
        }

        composable(VVScreens.TRACKER) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString("routeId") ?: ""
            TrackerScreen(
                routeId     = routeId,
                onBack      = { navController.popBackStack() },
                onSafeReach = { navController.navigate(VVScreens.SAFE_REACH) }
            )
        }

        composable(VVScreens.SAFE_REACH) {
            SafeReachScreen(onBack = { navController.popBackStack() })
        }

        composable(VVScreens.PROFILE) {
            ProfileScreen(onBack = { navController.popBackStack() })
        }

        composable(VVScreens.SETTINGS) {
            SettingsScreen(
                onBack           = { navController.popBackStack() },
                // Fix 5: Added authViewModel.signOut() before navigation
                onSignOut        = {
                    authViewModel.signOut()
                    navController.navigate(VVScreens.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                isDarkMode       = isDarkMode,
                onDarkModeToggle = onDarkModeToggle,
                currentLanguage  = currentLanguage,
                onLanguageChange = onLanguageChange
            )
        }

        composable(VVScreens.CHATBOT) {
            ChatbotScreen(onBack = { navController.popBackStack() })
        }
    }
}