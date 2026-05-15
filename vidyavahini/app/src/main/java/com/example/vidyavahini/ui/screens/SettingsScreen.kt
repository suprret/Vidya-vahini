package com.example.vidyavahini.ui.screens

import com.example.vidyavahini.LocalStrings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vidyavahini.ui.theme.Amber400
import com.example.vidyavahini.ui.theme.Amber900
import com.example.vidyavahini.ui.theme.Coral500
import com.example.vidyavahini.ui.theme.TealLight
import com.example.vidyavahini.ui.theme.Teal500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    currentLanguage: String = "English",
    onLanguageChange: (String) -> Unit = {}
) {
    var notificationsEnabled   by remember { mutableStateOf(true) }
    var delayAlertsEnabled     by remember { mutableStateOf(true) }
    var breakdownAlertsEnabled by remember { mutableStateOf(true) }
    var safeReachEnabled       by remember { mutableStateOf(true) }
    var showLanguageDialog     by remember { mutableStateOf(false) }
    var showLogoutDialog       by remember { mutableStateOf(false) }

    // ── Language dialog ───────────────────────────────────────────
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Language") },
            text = {
                Column {
                    listOf("English", "ಕನ್ನಡ", "हिंदी", "తెలుగు").forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onLanguageChange(lang)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            RadioButton(
                                selected = currentLanguage == lang,
                                onClick  = {
                                    onLanguageChange(lang)
                                    showLanguageDialog = false
                                }
                            )
                            Text(text = lang)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // ── Logout dialog ─────────────────────────────────────────────
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign Out") },
            text  = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onSignOut()
                }) { Text("Sign Out", color = Coral500) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = LocalStrings.current.settings, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor             = Amber400,
                    titleContentColor          = Amber900,
                    navigationIconContentColor = Amber900
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Appearance ────────────────────────────────────────
            SettingsSection(title = LocalStrings.current.appearance) {
                SettingsToggleItem(
                    icon            = Icons.Default.DarkMode,
                    title           = LocalStrings.current.darkMode,
                    subtitle        = "Switch to dark theme",
                    checked         = isDarkMode,
                    onCheckedChange = onDarkModeToggle
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                SettingsClickItem(
                    icon     = Icons.Default.Language,
                    title    = LocalStrings.current.language,
                    subtitle = currentLanguage,
                    onClick  = { showLanguageDialog = true }
                )
            }

            // ── Notifications ─────────────────────────────────────
            SettingsSection(title = LocalStrings.current.notifications) {
                SettingsToggleItem(
                    icon            = Icons.Default.Notifications,
                    title           = LocalStrings.current.allNotifications,
                    subtitle        = "Enable or disable all alerts",
                    checked         = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                SettingsToggleItem(
                    icon            = Icons.Default.Warning,
                    title           = LocalStrings.current.delayAlerts,
                    subtitle        = "Get notified when bus is delayed",
                    checked         = delayAlertsEnabled,
                    onCheckedChange = { delayAlertsEnabled = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                SettingsToggleItem(
                    icon            = Icons.Default.Build,
                    title           = LocalStrings.current.breakdownAlerts,
                    subtitle        = "Get notified of bus breakdowns",
                    checked         = breakdownAlertsEnabled,
                    onCheckedChange = { breakdownAlertsEnabled = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                SettingsToggleItem(
                    icon            = Icons.Default.Shield,
                    title           = LocalStrings.current.safeReachAlerts,
                    subtitle        = "Notify family when you arrive",
                    checked         = safeReachEnabled,
                    onCheckedChange = { safeReachEnabled = it }
                )
            }

            // ── About ─────────────────────────────────────────────
            SettingsSection(title = LocalStrings.current.about) {
                SettingsInfoItem(
                    icon     = Icons.Default.Info,
                    title    = "App Version",
                    subtitle = "Vidya-Vahini v1.0.0"
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                SettingsInfoItem(
                    icon     = Icons.Default.School,
                    title    = "Project",
                    subtitle = "VTU MindMatrix Internship 2026"
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                SettingsInfoItem(
                    icon     = Icons.Default.LocationOn,
                    title    = "Region",
                    subtitle = "Dharwad District, Karnataka"
                )
            }

            // ── Sign out ──────────────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLogoutDialog = true }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint     = Coral500,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text       = LocalStrings.current.signOut,
                        style      = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color      = Coral500
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ── Reusable composables ──────────────────────────────────────────

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Text(
        text  = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(content = content)
    }
}

@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint     = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title,    style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall,  color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked         = checked,
            onCheckedChange = onCheckedChange,
            colors          = SwitchDefaults.colors(
                checkedThumbColor = Teal500,
                checkedTrackColor = TealLight
            )
        )
    }
}

@Composable
fun SettingsClickItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint     = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title,    style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall,  color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun SettingsInfoItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint     = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title,    style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall,  color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}