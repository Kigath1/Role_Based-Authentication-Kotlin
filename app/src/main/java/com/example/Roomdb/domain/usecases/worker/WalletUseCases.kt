package com.example.Roomdb.domain.usecases.worker

import com.example.Roomdb.data.remote.model.worker.WalletBalanceResponse
import com.example.Roomdb.data.remote.model.worker.WalletTransactionDto
import com.example.Roomdb.data.remote.model.worker.WithdrawResponse
import com.example.Roomdb.domain.repository.worker.WalletRepository

class GetWalletBalanceUseCase(
    private val repository: WalletRepository
) {
    suspend operator fun invoke(): Result<WalletBalanceResponse> {
        return repository.getBalance()
    }
}

class WithdrawFundsUseCase(
    private val repository: WalletRepository
) {
    suspend operator fun invoke(amount: Double, phoneNumber: String): Result<WithdrawResponse> {
        return repository.withdraw(amount, phoneNumber)
    }
}

class GetWalletTransactionsUseCase(
    private val repository: WalletRepository
) {
    suspend operator fun invoke(): Result<List<WalletTransactionDto>> {
        return repository.getTransactions()
    }
}

