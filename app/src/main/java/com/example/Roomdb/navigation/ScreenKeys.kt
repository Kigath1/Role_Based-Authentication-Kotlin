package com.example.Roomdb.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface ScreenKey : NavKey {

    @Serializable
    data object Splash : ScreenKey

    @Serializable
    data object Login : ScreenKey

    @Serializable
    data object WorkerHome : ScreenKey

    @Serializable
    data object ClientHome : ScreenKey

    @Serializable data class Chat(
        val recipientId: String,
        val recipientName: String
    ) : ScreenKey


    // REGISTRATION

    @Serializable data object Registration        : ScreenKey
    @Serializable data object VerifyEmail         : ScreenKey
    @Serializable data object ClientProfileSetup  : ScreenKey

    //WORKER ONBOARDING

    @Serializable data object WorkerOnboarding    : ScreenKey

    data object WorkerMessages : ScreenKey
}