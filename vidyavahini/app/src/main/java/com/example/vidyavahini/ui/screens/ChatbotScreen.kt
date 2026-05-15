package com.example.vidyavahini.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vidyavahini.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotScreen(onBack: () -> Unit) {

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                text = "Hello! 👋 I am Vahini — your Vidya-Vahini AI assistant!\n\n" +
                        "I can help you with:\n" +
                        "• How to track your bus\n" +
                        "• How to send a Ping\n" +
                        "• How Safe-Reach works\n" +
                        "• Route information\n" +
                        "• App features\n\n" +
                        "Ask me anything about the app!",
                isUser = false
            )
        )
    }

    var inputText by remember { mutableStateOf("") }
    var isTyping  by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope     = rememberCoroutineScope()
    val keyboard  = LocalSoftwareKeyboardController.current

    fun getBotResponse(userMessage: String): String {
        val msg = userMessage.lowercase().trim()
        return when {

            msg.contains("ping") || msg.contains("how to ping") ->
                "🚌 To send a Ping:\n\n" +
                        "1. Open the app\n" +
                        "2. Select your route\n" +
                        "3. Tap the Ping button — 'Bus just passed my stop!'\n" +
                        "4. All students on your route get notified instantly!\n\n" +
                        "The ping also updates the ETA for everyone. 👍"

            msg.contains("safe reach") || msg.contains("safety") || msg.contains("reached") ->
                "🛡️ Safe-Reach feature:\n\n" +
                        "1. When you reach college, open the Tracker screen\n" +
                        "2. Tap '🛡️ I've Reached Safely!'\n" +
                        "3. Your family contacts get notified immediately\n" +
                        "4. Your arrival time is recorded\n\n" +
                        "Always use Safe-Reach so your family knows you are safe!"

            msg.contains("route") || msg.contains("bus") ->
                "🗺️ Available Routes in Dharwad District:\n\n" +
                        "• DH030 — Dharwad → B.V.B College\n" +
                        "  6 stops · ~25 min · 🟡 Delayed\n\n" +
                        "• RNR101 — Ranebennur → STJIT College\n" +
                        "  8 stops · ~38 min · 🟢 Active\n\n" +
                        "• ML305 — Ranebennur → RTES\n" +
                        "  5 stops · ~20 min\n\n" +
                        "Tap any route on the home screen to see live tracking!"

            msg.contains("stop") || msg.contains("station") ->
                "🚏 Bus Stops:\n\n" +
                        "DH030 — Dharwad → B.V.B College:\n" +
                        "1. Dharwad Bus Stand\n" +
                        "2. Sadashivanagar\n" +
                        "3. PB Road\n" +
                        "4. Navalur Cross\n" +
                        "5. Vidyanagar\n" +
                        "6. B.V.B College Gate\n\n" +
                        "RNR101 — Ranebennur → STJIT College:\n" +
                        "1. Ranebennur Bus Stand\n" +
                        "2. Town Hall\n" +
                        "3. NH48 Junction\n" +
                        "4. Hubli Road Cross\n" +
                        "5. Shiggaon Road\n" +
                        "6. Bypass Cross\n" +
                        "7. Industrial Area\n" +
                        "8. STJIT College Gate\n\n" +
                        "ML305 — Ranebennur → RTES:\n" +
                        "1. Ranebennur Market\n" +
                        "2. KSRTC Stop\n" +
                        "3. College Road\n" +
                        "4. Water Tank\n" +
                        "5. RTES College"

            msg.contains("eta") || msg.contains("time") || msg.contains("minutes") ->
                "⏱️ ETA is calculated based on:\n\n" +
                        "1. The last community Ping\n" +
                        "2. Average time between stops\n" +
                        "3. Distance from your stop\n\n" +
                        "The more students Ping, the more accurate the ETA!\n\n" +
                        "Current estimates:\n" +
                        "• Dharwad → B.V.B College: ~25 min\n" +
                        "• Ranebennur → STJIT: ~38 min\n" +
                        "• Ranebennur → RTES: ~20 min"

            msg.contains("breakdown") || msg.contains("problem") || msg.contains("issue") ->
                "⚠️ Report a Breakdown:\n\n" +
                        "1. Open the route tracker\n" +
                        "2. Tap 'Report a Breakdown'\n" +
                        "3. All students on the route get alerted\n" +
                        "4. Students can then find alternative transport\n\n" +
                        "Always report breakdowns so your classmates are not stranded!"

            msg.contains("login") || msg.contains("otp") || msg.contains("sign in") ->
                "📱 How to Login:\n\n" +
                        "1. Enter your 10-digit mobile number\n" +
                        "2. Tap 'Send OTP'\n" +
                        "3. Check your SMS for the 6-digit code\n" +
                        "4. Enter the code in the boxes\n" +
                        "5. Set up your profile\n\n" +
                        "You only need to login once — the app remembers you!"

            msg.contains("dark mode") || msg.contains("theme") ->
                "🌙 Dark Mode:\n\n" +
                        "1. Go to Home screen\n" +
                        "2. Tap the Settings icon ⚙️\n" +
                        "3. Toggle 'Dark Mode' ON\n\n" +
                        "The entire app switches to dark theme immediately!"

            msg.contains("language") || msg.contains("kannada") || msg.contains("hindi") ->
                "🌐 Change Language:\n\n" +
                        "1. Go to Settings ⚙️\n" +
                        "2. Tap 'Language'\n" +
                        "3. Select: English, ಕನ್ನಡ, हिंदी, or తెలుగు\n" +
                        "4. App restarts with new language\n\n" +
                        "Currently supporting 4 languages!"

            msg.contains("profile") || msg.contains("name") || msg.contains("contact") ->
                "👤 Update Your Profile:\n\n" +
                        "1. Tap the Profile icon on home screen\n" +
                        "2. Tap the Edit ✏️ button\n" +
                        "3. Update your name, college, and emergency contacts\n" +
                        "4. Tap 'Save Changes'\n\n" +
                        "Your emergency contacts receive Safe-Reach notifications!"

            msg.contains("hello") || msg.contains("hi") || msg.contains("hey") ->
                "Hello! 😊 Great to see you!\n\n" +
                        "I am Vahini, your Vidya-Vahini assistant.\n\n" +
                        "You can ask me about:\n" +
                        "• Bus tracking\n" +
                        "• Ping feature\n" +
                        "• Safe-Reach\n" +
                        "• Routes in Dharwad District\n" +
                        "• App settings"

            msg.contains("thank") || msg.contains("thanks") ->
                "You're welcome! 😊\n\n" +
                        "Stay safe and have a great journey to college! 🚌\n\n" +
                        "Remember to Ping when your bus passes and use Safe-Reach when you arrive!"

            msg.contains("help") || msg.contains("what can you do") ->
                "I can help you with:\n\n" +
                        "🚌 Bus Tracking — how to track your bus\n" +
                        "📍 Ping — how to send location pings\n" +
                        "🛡️ Safe-Reach — family notifications\n" +
                        "🗺️ Routes — Dharwad District bus routes\n" +
                        "⚠️ Breakdown — reporting issues\n" +
                        "📱 Login — OTP and account\n" +
                        "🌙 Dark Mode — theme settings\n" +
                        "🌐 Language — change app language\n" +
                        "👤 Profile — update your info\n\n" +
                        "Just ask me anything!"

            msg.contains("notification") || msg.contains("alert") ->
                "🔔 App Notifications:\n\n" +
                        "• Bus delay alerts\n" +
                        "• Breakdown alerts\n" +
                        "• Safe-Reach confirmations\n\n" +
                        "Manage notifications in Settings → Notifications\n\n" +
                        "Make sure notifications are enabled for the best experience!"

            msg.contains("dharwad") || msg.contains("bvb") || msg.contains("b.v.b") ->
                "🏫 Dharwad → B.V.B College Route (DH030):\n\n" +
                        "• Status: 🟡 Delayed\n" +
                        "• Total Stops: 6\n" +
                        "• Estimated Time: ~25 min\n" +
                        "• Start: Dharwad Bus Stand\n" +
                        "• End: B.V.B College Gate\n\n" +
                        "Tap this route on the home screen to track live!"

            msg.contains("ranebennur") || msg.contains("stjit") ->
                "🏫 Ranebennur → STJIT College Route (RNR101):\n\n" +
                        "• Status: 🟢 Active\n" +
                        "• Total Stops: 8\n" +
                        "• Estimated Time: ~38 min\n" +
                        "• Start: Ranebennur Bus Stand\n" +
                        "• End: STJIT College Gate\n\n" +
                        "Tap this route on the home screen to track live!"

            msg.contains("rtes") ->
                "🏫 Ranebennur → RTES Route (ML305):\n\n" +
                        "• Total Stops: 5\n" +
                        "• Estimated Time: ~20 min\n" +
                        "• Start: Ranebennur Market\n" +
                        "• End: RTES College\n\n" +
                        "Tap this route on the home screen to track live!"

            else ->
                "🤔 I am not sure about that specific query.\n\n" +
                        "I can help you with Vidya-Vahini app features like:\n" +
                        "• Bus tracking and Pings\n" +
                        "• Safe-Reach for family alerts\n" +
                        "• Route information for Dharwad District\n" +
                        "• App settings\n\n" +
                        "Try asking:\n" +
                        "• 'Show me available routes'\n" +
                        "• 'How do I send a ping?'\n" +
                        "• 'What is Safe-Reach?'"
        }
    }

    fun sendMessage() {
        val text = inputText.trim()
        if (text.isEmpty()) return
        messages.add(ChatMessage(text = text, isUser = true))
        inputText = ""
        keyboard?.hide()
        isTyping = true
        scope.launch {
            listState.animateScrollToItem(messages.size - 1)
            delay(1200)
            val response = getBotResponse(text)
            messages.add(ChatMessage(text = response, isUser = false))
            isTyping = false
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Teal500),
                            contentAlignment = Alignment.Center
                        ) { Text(text = "🤖", fontSize = 18.sp) }
                        Column {
                            Text(text = "Vahini AI", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                text  = if (isTyping) "typing..." else "Online",
                                fontSize = 11.sp,
                                color = if (isTyping) MaterialTheme.colorScheme.onSurfaceVariant else Teal500
                            )
                        }
                    }
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
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier              = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value         = inputText,
                        onValueChange = { inputText = it },
                        modifier      = Modifier.weight(1f),
                        placeholder   = { Text("Ask about the app...") },
                        shape         = RoundedCornerShape(24.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { sendMessage() }),
                        maxLines      = 3,
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = Teal500,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    FloatingActionButton(
                        onClick        = { sendMessage() },
                        modifier       = Modifier.size(48.dp),
                        containerColor = Teal500,
                        contentColor   = Color.White
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state               = listState,
            modifier            = Modifier.fillMaxSize().padding(padding),
            contentPadding      = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message -> ChatBubble(message = message) }

            if (isTyping) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(Amber400),
                            contentAlignment = Alignment.Center
                        ) { Text("🤖", fontSize = 14.sp) }
                        Card(
                            shape  = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier              = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                repeat(3) {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Teal500))
                                }
                            }
                        }
                    }
                }
            }

            if (messages.size == 1) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text  = "Quick questions:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        listOf(
                            "Show me available routes",
                            "How do I send a ping?",
                            "What is Safe-Reach?",
                            "How to report a breakdown?"
                        ).forEach { suggestion ->
                            SuggestionChip(
                                onClick = { inputText = suggestion; sendMessage() },
                                label   = { Text(suggestion, fontSize = 12.sp) },
                                colors  = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = TealLight,
                                    labelColor     = Teal700
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment     = Alignment.Bottom
    ) {
        if (!message.isUser) {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(Amber400),
                contentAlignment = Alignment.Center
            ) { Text("🤖", fontSize = 14.sp) }
            Spacer(modifier = Modifier.width(8.dp))
        }
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            shape    = RoundedCornerShape(
                topStart    = if (message.isUser) 16.dp else 4.dp,
                topEnd      = if (message.isUser) 4.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd   = 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser) Teal500 else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text      = message.text,
                modifier  = Modifier.padding(12.dp),
                style     = MaterialTheme.typography.bodyMedium,
                color     = if (message.isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )
        }
        if (message.isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(Teal500),
                contentAlignment = Alignment.Center
            ) { Text("👤", fontSize = 14.sp) }
        }
    }
}