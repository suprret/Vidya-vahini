package com.example.vidyavahini.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vidyavahini.model.Route
import com.example.vidyavahini.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RouteUiState(
    val routes: List<Route> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val selectedRoute: Route? = null
)

class RouteViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RouteUiState())
    val uiState: StateFlow<RouteUiState> = _uiState.asStateFlow()

    init { loadRoutes() }

    private fun loadRoutes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try { repository.seedDefaultRoutes() } catch (_: Exception) {}
            repository.observeRoutes()
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.localizedMessage)
                    }
                }
                .collect { routes ->
                    _uiState.update {
                        it.copy(routes = routes, isLoading = false, errorMessage = null)
                    }
                }
        }
    }

    fun selectRoute(route: Route) =
        _uiState.update { it.copy(selectedRoute = route) }

    fun retry() = loadRoutes()
}