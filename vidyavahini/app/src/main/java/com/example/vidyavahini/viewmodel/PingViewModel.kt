package com.example.vidyavahini.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.vidyavahini.model.BusPing
import com.example.vidyavahini.model.Route
import com.example.vidyavahini.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TrackerUiState(
    val route: Route? = null,
    val pings: List<BusPing> = emptyList(),
    val busStopIndex: Int = 0,
    val etaMinutes: Int = 0,
    val isLoading: Boolean = false,
    val isPinging: Boolean = false,
    val pingSuccess: Boolean = false,
    val breakdownReported: Boolean = false,
    val watcherCount: Int = 14,
    val errorMessage: String? = null
)

class PingViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackerUiState())
    val uiState: StateFlow<TrackerUiState> = _uiState.asStateFlow()

    private val db = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    fun loadRoute(routeId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.getRoute(routeId)
            result.fold(
                onSuccess = { route ->
                    _uiState.update {
                        it.copy(route = route, isLoading = false)
                    }
                    observePings(routeId)
                },
                onFailure = {
                    _uiState.update { it.copy(isLoading = false) }
                }
            )
        }
    }

    private fun observePings(routeId: String) {
        viewModelScope.launch {
            repository.observePings(routeId)
                .catch { }
                .collect { pings ->
                    val busStop = if (pings.isNotEmpty())
                        pings.first().stopIndex else 0
                    val eta = calculateEta(busStop)
                    _uiState.update {
                        it.copy(
                            pings = pings,
                            busStopIndex = busStop,
                            etaMinutes = eta
                        )
                    }
                }
        }
    }

    private fun calculateEta(busStopIndex: Int): Int {
        val totalStops = _uiState.value.route?.totalStops ?: 8
        val avgMinutesPerStop = _uiState.value.route?.estimatedMinutes?.div(totalStops) ?: 5
        val myStopIndex = totalStops - 1
        val remainingStops = maxOf(0, myStopIndex - busStopIndex)
        return remainingStops * avgMinutesPerStop
    }

    fun sendPing(stopIndex: Int, landmark: String) {
        val routeId = _uiState.value.route?.id ?: return
        val userId = auth.currentUser?.uid ?: "anonymous"
        val userName = auth.currentUser?.phoneNumber ?: "Student"

        _uiState.update { it.copy(isPinging = true) }

        val ping = BusPing(
            pingId = db.child("pings").push().key ?: "",
            routeId = routeId,
            userId = userId,
            userName = userName,
            landmark = landmark,
            stopIndex = stopIndex,
            timestamp = System.currentTimeMillis(),
            isBreakdown = false
        )

        db.child("pings").child(routeId).push().setValue(ping)
            .addOnSuccessListener {
                _uiState.update {
                    it.copy(isPinging = false, pingSuccess = true)
                }
                resetPingSuccess()
            }
            .addOnFailureListener {
                _uiState.update { it.copy(isPinging = false) }
            }
    }

    fun reportBreakdown() {
        val routeId = _uiState.value.route?.id ?: return
        val userId = auth.currentUser?.uid ?: "anonymous"

        val ping = BusPing(
            pingId = "",
            routeId = routeId,
            userId = userId,
            userName = "Student",
            landmark = "Breakdown reported",
            stopIndex = _uiState.value.busStopIndex,
            timestamp = System.currentTimeMillis(),
            isBreakdown = true
        )

        db.child("pings").child(routeId).push().setValue(ping)
        _uiState.update { it.copy(breakdownReported = true) }
    }

    private fun resetPingSuccess() {
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            _uiState.update { it.copy(pingSuccess = false) }
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
}