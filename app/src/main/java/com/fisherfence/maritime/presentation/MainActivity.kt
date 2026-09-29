package com.fisherfence.maritime.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp
import com.fisherfence.maritime.presentation.coastguard.CoastGuardHomeScreen
import com.fisherfence.maritime.presentation.coastguard.CoastGuardViewModel
import com.fisherfence.maritime.presentation.family.FamilyHomeScreen
import com.fisherfence.maritime.presentation.family.FamilyViewModel
import com.fisherfence.maritime.presentation.fisherman.FishermanHomeScreen
import com.fisherfence.maritime.presentation.fisherman.FishermanViewModel
import com.fisherfence.maritime.presentation.login.LoginScreen
import com.fisherfence.maritime.presentation.roleselection.RoleSelectionScreen
import com.fisherfence.maritime.presentation.splash.SplashScreen
import com.fisherfence.maritime.presentation.theme.FisherTheme
import dagger.hilt.android.AndroidEntryPoint

import android.util.Log

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FisherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FisherFenceApp()
                }
            }
        }
    }
}

@Composable
fun FisherFenceApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = Modifier.fillMaxSize(),
        enterTransition = {
            slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(500)) + fadeIn(animationSpec = tween(500))
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(500)) + fadeOut(animationSpec = tween(500))
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(500)) + fadeIn(animationSpec = tween(500))
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(500)) + fadeOut(animationSpec = tween(500))
        }
    ) {
        composable("splash") {
            SplashScreen(
                onNavigateToRoleSelection = {
                    navController.navigate("role_selection") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("role_selection") {
            RoleSelectionScreen(
                onNavigateToLogin = { role ->
                    navController.navigate("login/$role")
                }
            )
        }

        composable(
            route = "login/{role}",
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "fisherman"
            LoginScreen(
                role = role,
                onLoginSuccess = { successfulRole ->
                    val destination = when (successfulRole) {
                        "fisherman" -> "fisherman_home"
                        "coastguard" -> "coastguard_home"
                        "family" -> "family_home"
                        else -> "role_selection"
                    }
                    navController.navigate(destination) {
                        popUpTo("role_selection") { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("fisherman_home") {
            val viewModel: FishermanViewModel = hiltViewModel()
            FishermanHomeScreen(
                viewModel = viewModel,
                onLogout = {
                    // Make sure trip stops when logging out
                    viewModel.stopTrip()
                    navController.navigate("role_selection") {
                        popUpTo("fisherman_home") { inclusive = true }
                    }
                }
            )
        }

        composable("coastguard_home") {
            val viewModel: CoastGuardViewModel = hiltViewModel()
            CoastGuardHomeScreen(
                viewModel = viewModel,
                onLogout = {
                    navController.navigate("role_selection") {
                        popUpTo("coastguard_home") { inclusive = true }
                    }
                }
            )
        }

        composable("family_home") {
            val viewModel: FamilyViewModel = hiltViewModel()
            FamilyHomeScreen(
                viewModel = viewModel,
                onLogout = {
                    navController.navigate("role_selection") {
                        popUpTo("family_home") { inclusive = true }
                    }
                }
            )
        }
    }
}
