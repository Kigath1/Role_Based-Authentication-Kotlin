package com.example.Roomdb.ui.view.worker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Roomdb.data.remote.model.worker.WalletTransactionDto
import com.example.Roomdb.viewmodel.worker.WalletViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WalletScreen(
    viewModel: WalletViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show errors
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    // Show success message
    LaunchedEffect(state.withdrawSuccess) {
        if (state.withdrawSuccess) {
            snackbarHostState.showSnackbar("✅ Withdrawal successful!")
            viewModel.clearWithdrawSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // ── Header ──
                Text(
                    "Wallet",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(16.dp))

                // ── Balance Card ──
                BalanceCard(
                    balance = state.balance,
                    isLoading = state.isLoading,
                    onWithdraw = { viewModel.showWithdrawDialog() }
                )

                Spacer(Modifier.height(16.dp))

                // ── Breakdown Stats ──
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BreakdownStat(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Outlined.CheckCircle,
                        label = "Available",
                        valueLabel = formatCurrency(state.availableBalance),
                        iconColor = MaterialTheme.colorScheme.primary
                    )
                    BreakdownStat(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Outlined.Lock,
                        label = "In Escrow",
                        valueLabel = formatCurrency(state.inEscrow),
                        iconColor = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(Modifier.height(24.dp))

                // ── Transactions Header ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (state.transactions.isNotEmpty()) {
                        TextButton(onClick = { /* Navigate to full history */ }) {
                            Text("See all")
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))

                // ── Transactions List ──
                if (state.isLoading && state.transactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else if (state.transactions.isEmpty()) {
                    TransactionsEmptyState()
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.transactions.take(5).forEach { transaction ->
                            TransactionRow(transaction = transaction)
                        }
                    }
                }
            }

            // ── Withdraw Dialog ──
            if (state.showWithdrawDialog) {
                WithdrawDialog(
                    balance = state.balance,
                    isWithdrawing = state.isWithdrawing,
                    onDismiss = { viewModel.dismissWithdrawDialog() },
                    onWithdraw = { amount, phone ->
                        viewModel.withdrawFunds(amount, phone)
                    }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Balance Card
// ─────────────────────────────────────────────────────────────
@Composable
private fun BalanceCard(
    balance: Double,
    isLoading: Boolean,
    onWithdraw: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                "Total Balance",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
            )
            Spacer(Modifier.height(6.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    "KES ${formatCurrency(balance)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onWithdraw,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = balance > 0 && !isLoading
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.AccountBalanceWallet,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Withdraw Funds")
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Breakdown Stat
// ─────────────────────────────────────────────────────────────
@Composable
private fun BreakdownStat(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    valueLabel: String,
    iconColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "KES $valueLabel",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Transaction Row
// ─────────────────────────────────────────────────────────────
@Composable
private fun TransactionRow(transaction: WalletTransactionDto) {
    val isCredit = transaction.txnType.uppercase() == "CREDIT"
    val formattedDate = formatDate(transaction.createdAt)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCredit)
                            MaterialTheme.colorScheme.secondaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isCredit) Icons.Outlined.ArrowDownward else Icons.Outlined.ArrowUpward,
                    contentDescription = null,
                    tint = if (isCredit)
                        MaterialTheme.colorScheme.onSecondaryContainer
                    else
                        MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description ?: (if (transaction.txnType == "CREDIT") "Credit" else "Debit"),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${if (isCredit) "+" else "-"}KES ${formatCurrency(transaction.amount)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (isCredit)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Withdraw Dialog
// ─────────────────────────────────────────────────────────────
@Composable
private fun WithdrawDialog(
    balance: Double,
    isWithdrawing: Boolean,
    onDismiss: () -> Unit,
    onWithdraw: (Double, String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    val amountError = remember(amount) {
        val value = amount.toDoubleOrNull()
        if (value != null && value > balance) "Amount exceeds balance" else null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.AccountBalanceWallet,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text("Withdraw Funds")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Available Balance: KES ${formatCurrency(balance)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (KES)") },
                    placeholder = { Text("Enter amount") },
                    isError = amountError != null,
                    supportingText = {
                        if (amountError != null) {
                            Text(amountError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("M-Pesa Phone Number") },
                    placeholder = { Text("254712345678") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    if (amountValue != null && amountValue > 0 &&
                        amountValue <= balance &&
                        phoneNumber.isNotBlank()) {
                        onWithdraw(amountValue, phoneNumber)
                    }
                },
                enabled = !isWithdrawing &&
                        amount.toDoubleOrNull() != null &&
                        (amount.toDoubleOrNull() ?: 0.0) <= balance &&
                        amount.toDoubleOrNull() != 0.0 &&
                        phoneNumber.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (isWithdrawing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Withdraw")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isWithdrawing
            ) { Text("Cancel") }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

// ─────────────────────────────────────────────────────────────
// Empty State
// ─────────────────────────────────────────────────────────────
@Composable
private fun TransactionsEmptyState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Outlined.ReceiptLong,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(36.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "No transactions yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Your payment activity will appear here.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Helper Functions
// ─────────────────────────────────────────────────────────────
private fun formatCurrency(amount: Double): String {
    return String.format("%,.2f", amount)
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}