package com.example.vidyavahini.ui.screens

import com.example.vidyavahini.LocalStrings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vidyavahini.model.Route
import com.example.vidyavahini.model.RouteStatus
import com.example.vidyavahini.ui.theme.Amber400
import com.example.vidyavahini.ui.theme.Amber900
import com.example.vidyavahini.ui.theme.GreenLight
import com.example.vidyavahini.ui.theme.Green600
import com.example.vidyavahini.ui.theme.Teal500
import com.example.vidyavahini.ui.theme.TealLight
import com.example.vidyavahini.viewmodel.RouteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteSelectionScreen(
    onRouteSelected: (routeId: String) -> Unit,
    onSignOut: () -> Unit,
    onProfile: () -> Unit = {},
    onSettings: () -> Unit = {},
    onChatbot: () -> Unit = {},
    routeViewModel: RouteViewModel = viewModel()
) {
    val uiState by routeViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "${LocalStrings.current.appName} 🚌",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 18.sp
                        )
                        Text(
                            text     = LocalStrings.current.kolarDistrictRoutes,
                            fontSize = 11.sp,
                            color    = Amber900.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { routeViewModel.retry() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = onChatbot) {
                        Icon(Icons.Default.Chat, contentDescription = "AI Assistant")
                    }
                    IconButton(onClick = onProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor         = Amber400,
                    titleContentColor      = Amber900,
                    actionIconContentColor = Amber900
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {

            // ── Loading state ─────────────────────────────────────
            if (uiState.isLoading) {
                Column(
                    modifier            = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = Teal500)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text  = LocalStrings.current.fetchingRoutes,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                return@Scaffold
            }

            // ── Error state ───────────────────────────────────────
            if (uiState.errorMessage != null) {
                Column(
                    modifier            = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint     = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text      = uiState.errorMessage ?: "Something went wrong",
                        textAlign = TextAlign.Center,
                        color     = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { routeViewModel.retry() },
                        colors  = ButtonDefaults.buttonColors(containerColor = Teal500)
                    ) {
                        Text("Try Again")
                    }
                }
                return@Scaffold
            }

            // ── Success state ─────────────────────────────────────
            LazyColumn(
                modifier            = Modifier.fillMaxSize(),
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Community stats banner
                item {
                    CommunityStatsBanner(routeCount = uiState.routes.size)
                }

                // Section header
                item {
                    Text(
                        text     = "  ${LocalStrings.current.selectYourRoute}",
                        style    = MaterialTheme.typography.labelMedium,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // Empty state
                if (uiState.routes.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(16.dp),
                            colors   = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier            = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("🚌", fontSize = 48.sp)
                                Text(
                                    text      = "No routes available yet",
                                    style     = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text      = "Routes will appear here once added to Firebase",
                                    style     = MaterialTheme.typography.bodySmall,
                                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Button(
                                    onClick = { routeViewModel.retry() },
                                    colors  = ButtonDefaults.buttonColors(containerColor = Teal500)
                                ) {
                                    Text("Refresh")
                                }
                            }
                        }
                    }
                }

                // Route cards
                items(uiState.routes) { route ->
                    RouteCard(
                        route   = route,
                        onClick = { onRouteSelected(route.id) }
                    )
                }

                // Sign out
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick  = onSignOut,
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text  = "Sign Out",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// ── Community Stats Banner ────────────────────────────────────────

@Composable
fun CommunityStatsBanner(routeCount: Int) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = TealLight),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            StatItem(value = "$routeCount", label = "routes live")
            StatDivider()
            StatItem(value = "247", label = "students today")
            StatDivider()
            StatItem(value = "Live", label = LocalStrings.current.communityPowered)
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text       = value,
            fontWeight = FontWeight.Bold,
            fontSize   = 18.sp,
            color      = Green600
        )
        Text(
            text  = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StatDivider() {
    Box(
        modifier = Modifier
            .height(32.dp)
            .width(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}

// ── Route Card ────────────────────────────────────────────────────

@Composable
fun RouteCard(
    route: Route,
    onClick: () -> Unit
) {
    // Status colors based on your RouteStatus enum
    val statusColor = when (route.status) {
        RouteStatus.ACTIVE    -> Green600
        RouteStatus.DELAYED   -> Color(0xFFF59E0B)
        RouteStatus.BREAKDOWN -> Color(0xFFEF4444)
        RouteStatus.UNKNOWN   -> Color.Gray
    }
    val statusBg = when (route.status) {
        RouteStatus.ACTIVE    -> GreenLight
        RouteStatus.DELAYED   -> Color(0xFFFEF3C7)
        RouteStatus.BREAKDOWN -> Color(0xFFFEE2E2)
        RouteStatus.UNKNOWN   -> Color(0xFFF3F4F6)
    }
    val statusLabel = when (route.status) {
        RouteStatus.ACTIVE    -> "🟢 Active"
        RouteStatus.DELAYED   -> "🟡 Delayed"
        RouteStatus.BREAKDOWN -> "🔴 Breakdown"
        RouteStatus.UNKNOWN   -> "⚫ Unknown"
    }

    Card(
        onClick   = onClick,
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier            = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // Top row — icon + name + status badge
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier              = Modifier.weight(1f)
                ) {
                    Box(
                        modifier         = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Amber400),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.DirectionsBus,
                            contentDescription = null,
                            tint     = Amber900,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text       = route.name,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 15.sp,
                            maxLines   = 1
                        )
                        Text(
                            text  = "${route.totalStops} stops · ${route.estimatedMinutes} min",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(statusBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text       = statusLabel,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = statusColor
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            // Destination row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text  = "📍",
                    fontSize = 16.sp
                )
                Column {
                    Text(
                        text  = "DESTINATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text       = route.collegeArea,
                        fontWeight = FontWeight.Medium,
                        fontSize   = 14.sp
                    )
                }
            }

            // ETA row — only show if route is active or delayed
            if (route.status == RouteStatus.ACTIVE || route.status == RouteStatus.DELAYED) {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(TealLight)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text  = "⏱ Estimated journey time",
                        style = MaterialTheme.typography.bodySmall,
                        color = Green600
                    )
                    Text(
                        text       = "~${route.estimatedMinutes} min",
                        fontWeight = FontWeight.Bold,
                        color      = Green600,
                        fontSize   = 13.sp
                    )
                }
            }

            // Breakdown warning
            if (route.status == RouteStatus.BREAKDOWN) {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFEE2E2))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint     = Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text  = "Bus breakdown reported — find alternative transport",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFEF4444)
                    )
                }
            }

            // Track button
            Button(
                onClick  = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(10.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Teal500),
                enabled  = route.status != RouteStatus.BREAKDOWN
            ) {
                Text(
                    text       = if (route.status == RouteStatus.BREAKDOWN)
                        "Bus Breakdown ⚠️"
                    else
                        "Tap to Track Live 🚌",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp
                )
            }
        }
    }
}