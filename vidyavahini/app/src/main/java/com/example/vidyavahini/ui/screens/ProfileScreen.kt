package com.example.vidyavahini.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vidyavahini.ui.theme.Amber400
import com.example.vidyavahini.ui.theme.Amber900
import com.example.vidyavahini.ui.theme.GreenLight
import com.example.vidyavahini.ui.theme.Green600
import com.example.vidyavahini.ui.theme.TealLight
import com.example.vidyavahini.ui.theme.Teal500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBack: () -> Unit) {

    var name          by remember { mutableStateOf("Supreet") }
    var college       by remember { mutableStateOf("GEC Kolar") }
    var parentNumber  by remember { mutableStateOf("+91 98765 43210") }
    var siblingNumber by remember { mutableStateOf("+91 91234 56789") }
    var isEditing     by remember { mutableStateOf(false) }
    var isSaved       by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Profile",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isEditing = !isEditing
                        if (!isEditing) isSaved = false
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor        = Amber400,
                    titleContentColor     = Amber900,
                    navigationIconContentColor = Amber900,
                    actionIconContentColor     = Amber900
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

            // ── Avatar card ───────────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier            = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier          = Modifier.size(80.dp).clip(CircleShape).background(Amber400),
                        contentAlignment  = Alignment.Center
                    ) {
                        Text(
                            text       = name.firstOrNull()?.uppercase() ?: "S",
                            fontSize   = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Amber900
                        )
                    }
                    Text(
                        text       = name,
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text  = college,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ── Stats row ─────────────────────────────────────────
            Row(
                modifier            = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("47",  "Pings sent",   GreenLight),
                    Triple("23",  "Days tracked", TealLight),
                    Triple("4.8", "Ping score",   Amber400)
                ).forEach { (value, label, color) ->
                    Card(
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = CardDefaults.cardColors(containerColor = color)
                    ) {
                        Column(
                            modifier            = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text       = value,
                                fontSize   = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color      = Amber900
                            )
                            Text(
                                text  = label,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // ── Personal info ─────────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier            = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text  = "PERSONAL INFO",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value         = name,
                        onValueChange = { name = it },
                        label         = { Text("Full Name") },
                        modifier      = Modifier.fillMaxWidth(),
                        enabled       = isEditing,
                        shape         = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        )
                    )
                    OutlinedTextField(
                        value         = college,
                        onValueChange = { college = it },
                        label         = { Text("College / School") },
                        modifier      = Modifier.fillMaxWidth(),
                        enabled       = isEditing,
                        shape         = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        )
                    )
                }
            }

            // ── Emergency contacts ────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier            = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text  = "EMERGENCY CONTACTS",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value         = parentNumber,
                        onValueChange = { parentNumber = it },
                        label         = { Text("Parent / Guardian Number") },
                        modifier      = Modifier.fillMaxWidth(),
                        enabled       = isEditing,
                        shape         = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    OutlinedTextField(
                        value         = siblingNumber,
                        onValueChange = { siblingNumber = it },
                        label         = { Text("Sibling / Friend Number") },
                        modifier      = Modifier.fillMaxWidth(),
                        enabled       = isEditing,
                        shape         = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                }
            }

            // ── Save button ───────────────────────────────────────
            if (isEditing) {
                Button(
                    onClick = {
                        isEditing = false
                        isSaved   = true
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Teal500)
                ) {
                    Text(
                        text       = "Save Changes",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 16.sp
                    )
                }
            }

            // ── Saved confirmation ────────────────────────────────
            if (isSaved) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = CardDefaults.cardColors(containerColor = GreenLight)
                ) {
                    Text(
                        text       = "✓ Profile saved successfully!",
                        modifier   = Modifier.padding(16.dp),
                        color      = Green600,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}