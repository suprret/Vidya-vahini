package com.example.vidyavahini

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.vidyavahini.navigation.VidyaVahiniNavGraph
import com.example.vidyavahini.ui.theme.VidyaVahiniTheme

// UI Setup
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }
            var currentLanguage by remember {
                mutableStateOf(
                    LocaleHelper.getSavedLanguage(this)
                )
            }
            val strings = remember(currentLanguage) {
                getStrings(currentLanguage)
            }

            CompositionLocalProvider(LocalStrings provides strings) {
                VidyaVahiniTheme(darkTheme = isDarkMode) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        VidyaVahiniNavGraph(
                            isDarkMode       = isDarkMode,
                            onDarkModeToggle = { isDarkMode = it },
                            currentLanguage  = currentLanguage,
                            onLanguageChange = { language ->
                                LocaleHelper.saveLanguage(this, language)
                                currentLanguage = language
                            }
                        )
                    }
                }
            }
        }
    }
}