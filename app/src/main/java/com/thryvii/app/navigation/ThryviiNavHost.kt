package com.thryvii.app.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.thryvii.app.ui.theme.BackgroundGreen
import com.thryvii.app.ui.viewmodel.SessionViewModel
import com.thryvii.app.ui.viewmodel.rememberViewModelFactory

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
    // One SessionViewModel for the whole graph (it holds the email typed on the first sign-up
    // screen until the password screen).
    val sessionViewModel: SessionViewModel = viewModel(factory = rememberViewModelFactory())
    val session by sessionViewModel.session.collectAsState()

    // Wait for the saved data to load so a returning user lands on Main instead of flashing
    // the Welcome screen first.
    if (!session.loaded) {
        Box(modifier = Modifier.fillMaxSize().background(BackgroundGreen))
        return
    }

    // Decided once, at the moment loading finishes - later profile changes (e.g. finishing
    // onboarding) must not restart the navigation graph.
    val startDestination = remember {
        if (session.profile?.onboardingComplete == true) Routes.MAIN else Routes.WELCOME
    }
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

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
                onContinueClick = { email ->
                    sessionViewModel.onEmailEntered(email)
                    navController.navigate(Routes.PASSWORD)
                }
            )
        }

        composable(Routes.PASSWORD) {
            PasswordScreen(
                onSignUpClick = {
                    sessionViewModel.signUp()
                    navController.navigate(Routes.SIGN_IN)
                }
            )
        }

        composable(Routes.SIGN_IN) {
            SignInScreen(
                // "LOG IN" -> Make Garden page
                onLoginClick = { usernameOrEmail ->
                    sessionViewModel.signIn(usernameOrEmail)
                    navController.navigate(Routes.MAKE_GARDEN)
                }
            )
        }

        composable(Routes.MAKE_GARDEN) {
            MakeGardenScreen(
                // "START MY GARDEN!" -> Starter page
                onStartGardenClick = { gardenName, username, trackLocation ->
                    sessionViewModel.saveGarden(gardenName, username, trackLocation)
                    navController.navigate(Routes.STARTER)
                }
            )
        }

        composable(Routes.STARTER) {
            StarterScreen(
                // checkmark -> Avatar page
                onCheckmarkClick = { seed ->
                    sessionViewModel.saveStarterSeed(seed)
                    navController.navigate(Routes.AVATAR)
                }
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
                initialHairstyleIndex = session.profile?.hairstyleIndex ?: 2,
                initialColorIndex = session.profile?.colorIndex ?: 1,
                // checkmark -> save avatar (this also finishes onboarding) -> Main page
                onCheckmarkClick = { hairstyleIndex, colorIndex ->
                    sessionViewModel.saveAvatar(hairstyleIndex, colorIndex)
                    navController.navigate(Routes.MAIN)
                }
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
                profile = session.profile,
                // Settings panel's "Avatar" row -> Avatar page
                onAvatarClick = { navController.navigate(Routes.AVATAR) },
                // Settings panel's "Log out" row -> back to Welcome, clearing the back stack
                onLogoutClick = {
                    sessionViewModel.signOut()
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
    }
}
