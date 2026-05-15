package com.example.vidyavahini

import android.app.Application
import com.google.firebase.FirebaseApp
// Final polish before submission
class VidyaVahiniApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}