package com.example.vidyavahini

import android.app.Application
import com.google.firebase.FirebaseApp

class VidyaVahiniApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}