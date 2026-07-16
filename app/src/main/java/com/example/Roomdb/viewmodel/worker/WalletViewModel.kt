package com.example.Roomdb.viewmodel.worker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomdb.data.remote.model.worker.WalletTransactionDto
import com.example.Roomdb.domain.usecases.worker.GetWalletBalanceUseCase
import com.example.Roomdb.domain.usecases.worker.GetWalletTransactionsUseCase
import com.example.Roomdb.domain.usecases.worker.WithdrawFundsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WalletUiState(
    val isLoading: Boolean = false,
    val balance: Double = 0.0,
    val availableBalance: Double = 0.0,
    val inEscrow: Double = 0.0,
    val transactions: List<WalletTransactionDto> = emptyList(),
    val error: String? = null,
    val isWithdrawing: Boolean = false,
    val withdrawSuccess: Boolean = false,
    val showWithdrawDialog: Boolean = false
)

class WalletViewModel(
    private val getBalanceUseCase: GetWalletBalanceUseCase,
    private val getTransactionsUseCase: GetWalletTransactionsUseCase,
    private val withdrawFundsUseCase: WithdrawFundsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    init {
        loadWalletData()
    }

    fun loadWalletData() {
        viewModelScope.launch {
            android.util.Log.d("WalletVM", "Loading wallet data...")
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Load balance and transactions in parallel
            val balanceResult = getBalanceUseCase()
            val transactionsResult = getTransactionsUseCase()

            val newState = _uiState.value.copy(isLoading = false)

            balanceResult.onSuccess { balanceResponse ->
                android.util.Log.d("WalletVM", "Balance: ${balanceResponse.balance}")
                _uiState.value = newState.copy(
                    balance = balanceResponse.balance,
                    availableBalance = balanceResponse.balance
                )
            }.onFailure { e ->
                android.util.Log.e("WalletVM", "Balance error: ${e.message}")
                _uiState.value = newState.copy(error = e.message)
            }

            transactionsResult.onSuccess { transactions ->
                android.util.Log.d("WalletVM", "Transactions: ${transactions.size}")
                _uiState.value = _uiState.value.copy(
                    transactions = transactions
                )
            }.onFailure { e ->
                android.util.Log.e("WalletVM", "Transactions error: ${e.message}")
                if (_uiState.value.error == null) {
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
            }
        }
    }

    fun showWithdrawDialog() {
        _uiState.value = _uiState.value.copy(showWithdrawDialog = true)
    }

    fun dismissWithdrawDialog() {
        _uiState.value = _uiState.value.copy(showWithdrawDialog = false)
    }

    fun withdrawFunds(amount: Double, phoneNumber: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isWithdrawing = true,
                error = null,
                withdrawSuccess = false
            )

            withdrawFundsUseCase(amount, phoneNumber)
                .onSuccess { response ->
                    android.util.Log.d("WalletVM", "Withdraw success: ${response.message}")
                    _uiState.value = _uiState.value.copy(
                        isWithdrawing = false,
                        withdrawSuccess = true,
                        balance = response.balance,
                        availableBalance = response.balance,
                        showWithdrawDialog = false
                    )
                    refreshTransactions()
                }
                .onFailure { e ->
                    android.util.Log.e("WalletVM", "Withdraw error: ${e.message}")
                    _uiState.value = _uiState.value.copy(
                        isWithdrawing = false,
                        error = e.message
                    )
                }
        }
    }

    private fun refreshTransactions() {
        viewModelScope.launch {
            getTransactionsUseCase()
                .onSuccess { transactions ->
                    _uiState.value = _uiState.value.copy(
                        transactions = transactions
                    )
                }
                .onFailure { /* Silent fail */ }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearWithdrawSuccess() {
        _uiState.value = _uiState.value.copy(withdrawSuccess = false)
    }
}