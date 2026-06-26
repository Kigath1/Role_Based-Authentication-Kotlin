package com.example.Roomdb.viewmodel.employer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomdb.data.local.SecureTokenDataStore
import com.example.Roomdb.data.model.employer.RecentConversation
import com.example.Roomdb.domain.usecases.employer.GetRecentConversationsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatListUiState(
    val conversations: List<RecentConversation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChatListViewModel(
    val getRecentConversationsUseCase: GetRecentConversationsUseCase,
    val secureStore: SecureTokenDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    fun loadConversations() {
        viewModelScope.launch {
            val userId = secureStore.getUserIdOnce() ?: run {
                _uiState.update { it.copy(error = "Not logged in") }
                return@launch
            }
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = getRecentConversationsUseCase(userId)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    conversations = result.getOrElse { emptyList() },
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }
}