package com.example.Roomdb.data.remote.model.worker

import kotlinx.serialization.Serializable


// ── Get Wallet Balance ──
@Serializable
data class WalletBalanceResponse(
    val balance: Double = 0.0
)

// ── Withdraw from Wallet ──
@Serializable
data class WithdrawRequest(
    val amount: Double,
    val destinationPhoneNumber: String
)

@Serializable
data class WithdrawResponse(
    val message: String,
    val balance: Double
)

// ── Wallet Transactions ──
@Serializable
data class WalletTransactionDto(
    val id: String,
    val txnType: String, // "CREDIT" or "DEBIT"
    val amount: Double,
    val balanceAfter: Double,
    val referenceId: String?,
    val description: String?,
    val createdAt: String
)

