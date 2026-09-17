package com.thryvii.app.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.thryvii.app.ui.screens.AvatarScreen
import com.thryvii.app.ui.screens.EmailScreen
import com.thryvii.app.ui.screens.MainScreen
import com.thryvii.app.ui.screens.MakeGardenScreen
import com.thryvii.app.ui.screens.PasswordScreen
import com.thryvii.app.ui.screens.StarterScreen
import com.thryvii.app.ui.screens.SignInScreen
import com.thryvii.app.ui.screens.WelcomeScreen

object Routes {
    const val WELCOME = "welcome"
    const val EMAIL = "email"
    const val PASSWORD = "password"
    const val SIGN_IN = "sign_in"
    const val MAKE_GARDEN = "make_garden"
    const val STARTER = "starter"
    const val AVATAR = "avatar"
    const val MAIN = "main"
}

@Composable
fun ThryviiNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.WELCOME) {

        composable(Routes.WELCOME) {
            WelcomeScreen(
                // "CREATE ACCOUNT" -> Email page
                onCreateAccountClick = { navController.navigate(Routes.EMAIL) },
                // "LOGIN" -> Sign In page
                onLoginClick = { navController.navigate(Routes.SIGN_IN) }
            )
        }

        composable(Routes.EMAIL) {
            EmailScreen(
                // "CONTINUE" -> Password page
                onContinueClick = { navController.navigate(Routes.PASSWORD) }
            )
        }

        composable(Routes.PASSWORD) {
            PasswordScreen(
                onSignUpClick = { navController.navigate(Routes.SIGN_IN) }
            )
        }

        composable(Routes.SIGN_IN) {
            SignInScreen(
                // "LOG IN" -> Make Garden page
                onLoginClick = { navController.navigate(Routes.MAKE_GARDEN) }
            )
        }

        composable(Routes.MAKE_GARDEN) {
            MakeGardenScreen(
                // "START MY GARDEN!" -> Starter page
                onStartGardenClick = { navController.navigate(Routes.STARTER) }
            )
        }

        composable(Routes.STARTER) {
            StarterScreen(
                // checkmark -> Avatar page
                onCheckmarkClick = { navController.navigate(Routes.AVATAR) }
            )
        }

        // Fades in/out (instead of an instant cut) both here and on Main below, so hopping
        // between Main and Avatar (from the Settings panel's "Avatar" row, or from Starter
        // during onboarding) feels like a smooth transition rather than a sudden jump.
        composable(
            route = Routes.AVATAR,
            enterTransition = { fadeIn(animationSpec = tween(350)) },
            exitTransition = { fadeOut(animationSpec = tween(350)) },
            popEnterTransition = { fadeIn(animationSpec = tween(350)) },
            popExitTransition = { fadeOut(animationSpec = tween(350)) }
        ) {
            AvatarScreen(
                // checkmark -> Main page
                onCheckmarkClick = { navController.navigate(Routes.MAIN) }
            )
        }

        composable(
            route = Routes.MAIN,
            enterTransition = { fadeIn(animationSpec = tween(350)) },
            exitTransition = { fadeOut(animationSpec = tween(350)) },
            popEnterTransition = { fadeIn(animationSpec = tween(350)) },
            popExitTransition = { fadeOut(animationSpec = tween(350)) }
        ) {
            // Edit Garden is a toggled mode within MainScreen itself, not a separate route -
            // see MainScreen.kt for why (it's the same background/top bar in Figma's real design).
            MainScreen(
                // Settings panel's "Avatar" row -> Avatar page
                onAvatarClick = { navController.navigate(Routes.AVATAR) }
            )
        }
    }
}
