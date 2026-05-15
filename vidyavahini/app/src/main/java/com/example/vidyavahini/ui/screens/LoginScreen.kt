package com.example.vidyavahini.ui.screens

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.vidyavahini.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vidyavahini.ui.theme.Amber400
import com.example.vidyavahini.ui.theme.Amber900
import com.example.vidyavahini.viewmodel.AuthStep
import com.example.vidyavahini.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onOtpSent: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context  = LocalContext.current
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(uiState.step) {
        if (uiState.step == AuthStep.OTP_VERIFICATION) onOtpSent()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Amber400, Color(0xFFFFFBF5))
                )
            )
    ) {
        // ── Hero ──────────────────────────────────────────────────────────────
        Box(
            modifier         = Modifier
                .fillMaxWidth()
                .height(280.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter            = painterResource(id = R.mipmap.ic_launcher),
                    contentDescription = "Vidya-Vahini Logo",
                    modifier           = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text  = "Student Commute Buddy",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF854F0B)
                )
            }
        }

        // ── Card ──────────────────────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxSize(),
            shape    = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            colors   = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier            = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text  = "Enter your mobile number",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text  = "We'll send a one-time password to verify",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // ── Phone input ───────────────────────────────────────────────
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Surface(
                        shape    = RoundedCornerShape(12.dp),
                        color    = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.height(56.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier         = Modifier.padding(horizontal = 14.dp)
                        ) {
                            Text(
                                text       = "🇮🇳 +91",
                                style      = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    OutlinedTextField(
                        value         = uiState.phoneNumber,
                        onValueChange = viewModel::onPhoneNumberChange,
                        modifier      = Modifier.weight(1f),
                        placeholder   = { Text("XXXXXXXXXX") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction    = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboard?.hide()
                                viewModel.sendOtp(context as Activity)
                            }
                        ),
                        singleLine = true,
                        shape      = RoundedCornerShape(12.dp),
                        isError    = uiState.errorMessage != null
                    )
                }

                if (uiState.errorMessage != null) {
                    Text(
                        text  = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // ── Send OTP Button ───────────────────────────────────────────
                Button(
                    onClick = {
                        keyboard?.hide()
                        viewModel.sendOtp(context as Activity)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape   = RoundedCornerShape(14.dp),
                    enabled = !uiState.isLoading && uiState.phoneNumber.length == 10
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(22.dp),
                            color       = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text       = "Send OTP",
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text      = "For student use on Dharwad District routes.",
                    style     = MaterialTheme.typography.bodySmall,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.fillMaxWidth()
                )
            }
        }
    }
}