package com.kotlinconf.workshop.househelper

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavUri
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.kotlinconf.workshop.househelper.dashboard.DashboardScreen
import com.kotlinconf.workshop.househelper.devices.CameraDetailsScreen
import com.kotlinconf.workshop.househelper.devices.LightDetailsScreen
import com.kotlinconf.workshop.househelper.devices.RenameDeviceScreen
import com.kotlinconf.workshop.househelper.navigation.CameraDetails
import com.kotlinconf.workshop.househelper.navigation.Dashboard
import com.kotlinconf.workshop.househelper.navigation.LightDetails
import com.kotlinconf.workshop.househelper.navigation.Onboarding
import com.kotlinconf.workshop.househelper.navigation.OnboardingAbout
import com.kotlinconf.workshop.househelper.navigation.OnboardingDone
import com.kotlinconf.workshop.househelper.navigation.OnboardingWelcome
import com.kotlinconf.workshop.househelper.navigation.RenameDevice
import com.kotlinconf.workshop.househelper.theme.AppDarkColorScheme
import com.kotlinconf.workshop.househelper.theme.AppLightColorScheme
import com.kotlinconf.workshop.househelper.theme.AppShapes
import househelper.composeapp.generated.resources.Res
import househelper.composeapp.generated.resources.onboarding_about
import househelper.composeapp.generated.resources.onboarding_about_subtitle
import househelper.composeapp.generated.resources.onboarding_done
import househelper.composeapp.generated.resources.onboarding_done_subtitle
import househelper.composeapp.generated.resources.onboarding_next_button
import househelper.composeapp.generated.resources.onboarding_welcome
import househelper.composeapp.generated.resources.onboarding_welcome_subtitle
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import org.jetbrains.compose.resources.stringResource

fun navigateToDeepLink(uri: String) {
    deepLinkUris.trySend(uri)
}

private val deepLinkUris = Channel<String>(capacity = 1)

@Composable
fun App() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) {
            AppDarkColorScheme
        } else {
            AppLightColorScheme
        },
        shapes = AppShapes,
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()

            LaunchedEffect(Unit) {
                while (true) {
                    val uri = deepLinkUris.receive()

                    // Make sure navController has had time to initialize
                    navController.currentBackStackEntryFlow.first()

                    // Make sure we have a Dashboard
                    navController.navigate(Dashboard) {
                        popUpTo<Onboarding> { inclusive = true }
                    }

                    // Go to deeplinked screen on top of Dashboard
                    navController.navigate(
                        deepLink = NavUri(uri),
                        navOptions = navOptions {
                            popUpTo<Dashboard>()
                        },
                    )
                }
            }

            NavHost(navController, startDestination = Onboarding) {
                navigation<Onboarding>(startDestination = OnboardingWelcome) {
                    composable<OnboardingWelcome> {
                        OnboardingScreen(
                            text = stringResource(Res.string.onboarding_welcome),
                            subtitle = stringResource(Res.string.onboarding_welcome_subtitle),
                            buttonText = stringResource(Res.string.onboarding_next_button),
                            icon = Icons.Default.Favorite,
                            onNext = { navController.navigate(OnboardingAbout) }
                        )
                    }
                    composable<OnboardingAbout> {
                        OnboardingScreen(
                            text = stringResource(Res.string.onboarding_about),
                            subtitle = stringResource(Res.string.onboarding_about_subtitle),
                            buttonText = stringResource(Res.string.onboarding_next_button),
                            icon = Icons.Default.Info,
                            onNext = { navController.navigate(OnboardingDone) }
                        )
                    }
                    composable<OnboardingDone> {
                        OnboardingScreen(
                            text = stringResource(Res.string.onboarding_done),
                            subtitle = stringResource(Res.string.onboarding_done_subtitle),
                            buttonText = stringResource(Res.string.onboarding_next_button),
                            icon = Icons.Default.Home,
                            onNext = {
                                navController.navigate(Dashboard) {
                                    popUpTo<Onboarding>()
                                }
                            }
                        )
                    }
                }
                composable<Dashboard> {
                    DashboardScreen(
                        onNavigateToLightDetails = { deviceId ->
                            navController.navigate(LightDetails(deviceId))
                        },
                        onNavigateToCameraDetails = { deviceId ->
                            navController.navigate(CameraDetails(deviceId))
                        }
                    )
                }
                composable<LightDetails>(
                    deepLinks = listOf(
                        navDeepLink {
                            uriPattern = "househelper://light/{deviceId}"
                        }
                    ),
                ) {
                    LightDetailsScreen(
                        deviceId = it.toRoute<LightDetails>().deviceId,
                        onNavigateUp = { navController.navigateUp() },
                    )
                }
                composable<CameraDetails>(
                ) {
                    CameraDetailsScreen(
                        deviceId = it.toRoute<CameraDetails>().deviceId,
                        onNavigateUp = { navController.navigateUp() },
                        onNavigateToRename = { deviceId ->
                            navController.navigate(RenameDevice(deviceId))
                        },
                    )
                }
                dialog<RenameDevice> {
                    RenameDeviceScreen(
                        deviceId = it.toRoute<RenameDevice>().deviceId,
                        onDismiss = {
                            navController.navigateUp()
                        },
                    )
                }
            }
        }
    }
}
