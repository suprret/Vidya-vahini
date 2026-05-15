package com.example.vidyavahini

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    fun setLocale(context: Context, language: String): Context {
        val locale = when (language) {
            "ಕನ್ನಡ" -> Locale("kn")
            "हिंदी"  -> Locale("hi")
            "తెలుగు" -> Locale("te")
            else     -> Locale("en")
        }
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    fun saveLanguage(context: Context, language: String) {
        context.getSharedPreferences("vidyavahini_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("language", language)
            .apply()
    }

    fun getSavedLanguage(context: Context): String {
        return context.getSharedPreferences("vidyavahini_prefs", Context.MODE_PRIVATE)
            .getString("language", "English") ?: "English"
    }
}