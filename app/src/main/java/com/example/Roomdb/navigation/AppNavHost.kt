package com.example.Roomdb.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.Roomdb.viewmodel.auth.AuthViewModel

@Composable
fun AppNavHost(
    authViewModel: AuthViewModel,
    loginContent: @Composable (
        onLoginSuccess: (role: String) -> Unit
    ) -> Unit,
    workerHomeContent: @Composable (
        onLogout: () -> Unit
    ) -> Unit,
    clientHomeContent: @Composable (
        onLogout: () -> Unit,
        onOpenChat: (recipientId: String, recipientName: String) -> Unit
    ) -> Unit,
    chatContent: @Composable (
        recipientId: String,
        recipientName: String,
        onBack: () -> Unit
    ) -> Unit
) {
    val backStack = rememberNavBackStack(ScreenKey.Splash)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {

            // ── SPLASH ───────────────────────────────────────────────────────
            entry<ScreenKey.Splash> {
                LaunchedEffect(Unit) {
                    val isLoggedIn = authViewModel.checkAutoLogin()
                    val nextKey = if (isLoggedIn) {
                        when (authViewModel.getUserRole()) {
                            "WORKER" -> ScreenKey.WorkerHome
                            "CLIENT" -> ScreenKey.ClientHome
                            else     -> ScreenKey.Login
                        }
                    } else {
                        ScreenKey.Login
                    }
                    backStack.removeLastOrNull()
                    backStack.add(nextKey)
                }
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            // ── LOGIN ────────────────────────────────────────────────────────
            entry<ScreenKey.Login> {
                loginContent { role ->
                    backStack.removeLastOrNull()
                    backStack.add(
                        if (role == "WORKER") ScreenKey.WorkerHome
                        else ScreenKey.ClientHome
                    )
                }
            }

            // ── WORKER HOME ──────────────────────────────────────────────────
            entry<ScreenKey.WorkerHome> {
                workerHomeContent {
                    backStack.clear()
                    backStack.add(ScreenKey.Login)
                }
            }

            // ── CLIENT HOME ──────────────────────────────────────────────────
            entry<ScreenKey.ClientHome> {
                clientHomeContent(
                    // onLogout
                    {
                        backStack.clear()
                        backStack.add(ScreenKey.Login)
                    },
                    // onOpenChat — backStack lives here so we push Chat from here
                    { recipientId, recipientName ->
                        backStack.add(ScreenKey.Chat(recipientId, recipientName))
                    }
                )
            }

            // ── CHAT ─────────────────────────────────────────────────────────
            entry<ScreenKey.Chat> { key ->
                chatContent(
                    key.recipientId,
                    key.recipientName
                ) {
                    backStack.removeLastOrNull()
                }
            }
        }
    )
}