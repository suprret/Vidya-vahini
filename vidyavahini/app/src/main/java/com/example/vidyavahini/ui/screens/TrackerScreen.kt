package com.example.vidyavahini.ui.screens

import com.example.vidyavahini.LocalStrings
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vidyavahini.model.BusPing
import com.example.vidyavahini.ui.theme.*
import com.example.vidyavahini.viewmodel.PingViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerScreen(
    routeId: String,
    onBack: () -> Unit,
    onSafeReach: () -> Unit = {},
    pingViewModel: PingViewModel = viewModel()
) {
    val uiState by pingViewModel.uiState.collectAsStateWithLifecycle()
    val strings = LocalStrings.current

    LaunchedEffect(routeId) {
        pingViewModel.loadRoute(routeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.route?.name ?: "Loading...",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = uiState.route?.id ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Amber400,
                    titleContentColor = Amber900,
                    navigationIconContentColor = Amber900
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Amber600)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    RouteLineCard(
                        stops = uiState.route?.stops ?: emptyList(),
                        busStopIndex = uiState.busStopIndex,
                        watcherCount = uiState.watcherCount
                    )
                }

                item {
                    EtaCard(etaMinutes = uiState.etaMinutes)
                }

                item {
                    PingButton(
                        isPinging = uiState.isPinging,
                        pingSuccess = uiState.pingSuccess,
                        stops = uiState.route?.stops ?: emptyList(),
                        busStopIndex = uiState.busStopIndex,
                        onPing = { stopIndex, landmark ->
                            pingViewModel.sendPing(stopIndex, landmark)
                        }
                    )
                }

                item {
                    OutlinedButton(
                        onClick = { pingViewModel.reportBreakdown() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Coral500
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Coral500)
                        )
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.breakdownReported)
                                "✓ ${strings.reportBreakdown}"
                            else
                                strings.reportBreakdown,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                item {
                    Button(
                        onClick = onSafeReach,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Teal500
                        )
                    ) {
                        Text(
                            text = "🛡️ ${strings.safeReach}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }

                if (uiState.pings.isNotEmpty()) {
                    item {
                        Text(
                            text = "RECENT PINGS  •  ${uiState.pings.size}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    items(uiState.pings.take(10)) { ping ->
                        PingFeedItem(ping = ping)
                    }
                }
            }
        }
    }
}

@Composable
fun RouteLineCard(
    stops: List<String>,
    busStopIndex: Int,
    watcherCount: Int
) {
    val progress = if (stops.isEmpty()) 0f
    else busStopIndex.toFloat() / (stops.size - 1).coerceAtLeast(1)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "busProgress"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = LocalStrings.current.livePosition,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar and stop dots logic...
            BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(32.dp)) {
                val totalWidth = maxWidth
                Box(modifier = Modifier.fillMaxWidth().height(3.dp).align(Alignment.Center).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(2.dp)))
                Box(modifier = Modifier.width(totalWidth * animatedProgress).height(3.dp).align(Alignment.CenterStart).background(Teal500, RoundedCornerShape(2.dp)))

                stops.forEachIndexed { index, _ ->
                    val dotProgress = if (stops.size <= 1) 0f else index.toFloat() / (stops.size - 1)
                    Box(modifier = Modifier.offset(x = totalWidth * dotProgress - 4.dp).size(8.dp).align(Alignment.Center).clip(CircleShape).background(if (index <= busStopIndex) Teal500 else MaterialTheme.colorScheme.surfaceVariant))
                }

                Box(modifier = Modifier.offset(x = totalWidth * animatedProgress - 14.dp).size(28.dp).align(Alignment.Center).clip(RoundedCornerShape(6.dp)).background(Teal500), contentAlignment = Alignment.Center) {
                    Text(text = "🚌", fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(3) { Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Teal500)) }
                Text(
                    text = "$watcherCount students watching",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EtaCard(etaMinutes: Int) {
    val strings = LocalStrings.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TealLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (etaMinutes > 0) "$etaMinutes" else "—",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Teal700
            )
            Column {
                Text(
                    text = if (etaMinutes > 0) strings.minutesToStop else "No ETA",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Teal500,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Live community update",
                    style = MaterialTheme.typography.bodySmall,
                    color = Teal500.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun PingButton(
    isPinging: Boolean,
    pingSuccess: Boolean,
    stops: List<String>,
    busStopIndex: Int,
    onPing: (Int, String) -> Unit
) {
    val strings = LocalStrings.current
    val landmark = stops.getOrNull(busStopIndex) ?: "Bus Stop"

    Button(
        onClick = { onPing(busStopIndex, landmark) },
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = if (pingSuccess) Green600 else Amber600),
        enabled = !isPinging
    ) {
        if (isPinging) {
            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
        } else {
            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (pingSuccess) "✓ Pinged" else strings.pingButton,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun PingFeedItem(ping: BusPing) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (ping.isBreakdown) CoralLight else MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(if (ping.isBreakdown) CoralLight else GreenLight), contentAlignment = Alignment.Center) {
                Text(text = if (ping.isBreakdown) "⚠️" else "📍", fontSize = 16.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (ping.isBreakdown) "Breakdown: ${ping.landmark}" else "Spotted: ${ping.landmark}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (ping.isBreakdown) Coral500 else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Update from user",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}