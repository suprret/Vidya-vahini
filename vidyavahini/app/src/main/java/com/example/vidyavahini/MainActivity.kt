package com.example.vidyavahini

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.vidyavahini.navigation.VidyaVahiniNavGraph
import com.example.vidyavahini.ui.theme.VidyaVahiniTheme

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        try {
            val language = LocaleHelper.getSavedLanguage(newBase)
            super.attachBaseContext(LocaleHelper.setLocale(newBase, language))
        } catch (e: Exception) {
            super.attachBaseContext(newBase)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }
            var currentLanguage by remember {
                mutableStateOf(
                    try { LocaleHelper.getSavedLanguage(this) }
                    catch (e: Exception) { "English" }
                )
            }
            VidyaVahiniTheme(darkTheme = isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    VidyaVahiniNavGraph(
                        isDarkMode       = isDarkMode,
                        onDarkModeToggle = { isDarkMode = it },
                        currentLanguage  = currentLanguage,
                        onLanguageChange = { language ->
                            try {
                                LocaleHelper.saveLanguage(this, language)
                                currentLanguage = language
                                recreate()
                            } catch (e: Exception) {
                                currentLanguage = language
                            }
                        }
                    )
                }
            }
        }
    }
}