package com.example.Roomdb.viewmodel.employer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomdb.data.model.Worker
import com.example.Roomdb.domain.usecases.employer.GetWorkersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientHomeUiState(
    val selectedTabIndex: Int = 0,
    val workers: List<Worker> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentLocation: String? = null
)

class ClientHomeViewModel(
    private val getWorkersUseCase: GetWorkersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientHomeUiState())
    val uiState: StateFlow<ClientHomeUiState> = _uiState.asStateFlow()

    // Wired from ClientHomeScreen — navigates to ChatScreen
    var onNavigateToChat: ((recipientId: String, recipientName: String) -> Unit)? = null

    init {
        loadWorkers()
    }

    fun selectTab(index: Int) { _uiState.update { it.copy(selectedTabIndex = index) } }

    fun updateLocation(location: String?) {
        _uiState.update { it.copy(currentLocation = location) }
        loadWorkers(forceRefresh = true)
    }

    fun loadWorkers(forceRefresh: Boolean = false) {
        val location = _uiState.value.currentLocation
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = getWorkersUseCase(location, forceRefresh)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    workers = result.getOrElse { emptyList() },
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun onMessageClicked(worker: Worker) {
        onNavigateToChat?.invoke(worker.userId, worker.fullName)
    }


    fun onHireClicked(workerId: String) {
        // TODO: implement hire flow
    }
}