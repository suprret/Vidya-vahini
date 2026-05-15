package com.example.vidyavahini.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.example.vidyavahini.model.StudentProfile
import com.example.vidyavahini.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val step: AuthStep = AuthStep.PHONE_ENTRY,
    val phoneNumber: String = "",
    val otpCode: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val studentName: String = "",
    val collegeName: String = "",
    val needsProfileSetup: Boolean = false
)

enum class AuthStep {
    PHONE_ENTRY,
    OTP_VERIFICATION,
    PROFILE_SETUP,
    DONE
}

class AuthViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    init {
        if (repository.currentUser != null) {
            _uiState.update {
                it.copy(isAuthenticated = true, step = AuthStep.DONE)
            }
        }
    }

    fun onPhoneNumberChange(value: String) {
        val digits = value.filter { it.isDigit() }.take(10)
        _uiState.update { it.copy(phoneNumber = digits, errorMessage = null) }
    }

    fun onOtpChange(value: String) {
        val digits = value.filter { it.isDigit() }.take(6)
        _uiState.update { it.copy(otpCode = digits, errorMessage = null) }
        if (digits.length == 6) verifyOtp()
    }

    fun onNameChange(value: String) =
        _uiState.update { it.copy(studentName = value) }

    fun onCollegeChange(value: String) =
        _uiState.update { it.copy(collegeName = value) }

    fun sendOtp(activity: Activity) {
        val phone = "+91" + _uiState.value.phoneNumber
        if (_uiState.value.phoneNumber.length < 10) {
            _uiState.update {
                it.copy(errorMessage = "Enter a valid 10-digit number")
            }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        repository.sendOtp(
            phoneNumber = phone,
            activity = activity,
            onCodeSent = { verificationId, token ->
                storedVerificationId = verificationId
                resendToken = token
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        step = AuthStep.OTP_VERIFICATION
                    )
                }
            },
            onVerificationCompleted = { credential ->
                viewModelScope.launch {
                    repository.signInWithCredential(credential)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            step = AuthStep.DONE
                        )
                    }
                }
            },
            onVerificationFailed = { _ ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to send OTP. Try again."
                    )
                }
            }
        )
    }

    fun verifyOtp() {
        val verificationId = storedVerificationId ?: run {
            _uiState.update {
                it.copy(errorMessage = "Session expired. Resend OTP.")
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.verifyOtp(
                verificationId,
                _uiState.value.otpCode
            )
            result.fold(
                onSuccess = { _ ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            step = AuthStep.PROFILE_SETUP
                        )
                    }
                },
                onFailure = { _ ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Wrong OTP. Try again."
                        )
                    }
                }
            )
        }
    }

    fun saveProfile() {
        val user = repository.currentUser ?: return
        if (_uiState.value.studentName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Enter your name") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val profile = StudentProfile(
                uid = user.uid,
                phoneNumber = user.phoneNumber ?: "",
                name = _uiState.value.studentName,
                college = _uiState.value.collegeName
            )
            repository.saveStudentProfile(profile)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    step = AuthStep.DONE
                )
            }
        }
    }

    fun goBackToPhone() {
        _uiState.update {
            it.copy(
                step = AuthStep.PHONE_ENTRY,
                otpCode = "",
                errorMessage = null
            )
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
}