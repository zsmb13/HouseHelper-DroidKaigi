package com.kotlinconf.workshop.househelper.navigation


import kotlinx.serialization.Serializable

// Start screens
@Serializable
data object Onboarding

@Serializable
data object OnboardingWelcome

@Serializable
data object OnboardingAbout

@Serializable
data object OnboardingDone

// Main screens
@Serializable
data object Dashboard

@Serializable
data class LightDetails(val deviceId: String)

@Serializable
data class CameraDetails(val deviceId: String)

@Serializable
data class RenameDevice(val deviceId: String)
