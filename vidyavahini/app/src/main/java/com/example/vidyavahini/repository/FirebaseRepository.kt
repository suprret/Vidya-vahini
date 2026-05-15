package com.example.vidyavahini.repository

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.vidyavahini.model.BusPing
import com.example.vidyavahini.model.Route
import com.example.vidyavahini.model.RouteStatus
import com.example.vidyavahini.model.StudentProfile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class FirebaseRepository {

    private val auth: FirebaseAuth   = FirebaseAuth.getInstance()
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser

    // ── SEND OTP ──────────────────────────────────────────────────────────────
    fun sendOtp(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: (String, PhoneAuthProvider.ForceResendingToken) -> Unit,
        onVerificationCompleted: (PhoneAuthCredential) -> Unit,
        onVerificationFailed: (Exception) -> Unit
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    onVerificationCompleted(credential)
                }
                override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                    onVerificationFailed(e)
                }
                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    onCodeSent(verificationId, token)
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // ── VERIFY OTP ────────────────────────────────────────────────────────────
    suspend fun verifyOtp(
        verificationId: String,
        otpCode: String
    ): Result<FirebaseUser> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
            val result     = auth.signInWithCredential(credential).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithCredential(credential: PhoneAuthCredential): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithCredential(credential).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() = auth.signOut()

    // ── STUDENT PROFILE ───────────────────────────────────────────────────────
    suspend fun saveStudentProfile(profile: StudentProfile): Result<Unit> {
        return try {
            db.reference.child("students").child(profile.uid).setValue(profile).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStudentProfile(uid: String): Result<StudentProfile> {
        return try {
            val snapshot = db.reference.child("students").child(uid).get().await()
            val profile  = snapshot.getValue(StudentProfile::class.java) ?: StudentProfile(uid = uid)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── ROUTES ────────────────────────────────────────────────────────────────
    fun observeRoutes(): Flow<List<Route>> = callbackFlow {
        val ref = db.reference.child("routes")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val routes = snapshot.children.mapNotNull { child ->
                    try {
                        // Read each field manually to handle status string → enum
                        val id               = child.child("id").getValue(String::class.java) ?: child.key ?: ""
                        val name             = child.child("name").getValue(String::class.java) ?: ""
                        val collegeArea      = child.child("collegeArea").getValue(String::class.java) ?: ""
                        val totalStops       = child.child("totalStops").getValue(Int::class.java) ?: 0
                        val estimatedMinutes = child.child("estimatedMinutes").getValue(Int::class.java) ?: 0
                        val statusStr        = child.child("status").getValue(String::class.java) ?: "UNKNOWN"
                        val stops            = child.child("stops").children
                            .mapNotNull { it.getValue(String::class.java) }

                        // Convert string from Firebase to enum safely
                        val status = when (statusStr.uppercase().trim()) {
                            "ACTIVE"    -> RouteStatus.ACTIVE
                            "DELAYED"   -> RouteStatus.DELAYED
                            "BREAKDOWN" -> RouteStatus.BREAKDOWN
                            else        -> RouteStatus.UNKNOWN
                        }

                        Route(
                            id               = id,
                            name             = name,
                            collegeArea      = collegeArea,
                            totalStops       = totalStops,
                            estimatedMinutes = estimatedMinutes,
                            stops            = stops,
                            status           = status
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(routes)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // ── GET SINGLE ROUTE ──────────────────────────────────────────────────────
    suspend fun getRoute(routeId: String): Result<Route> {
        return try {
            val snapshot = db.reference.child("routes").child(routeId).get().await()
            val route    = snapshot.getValue(Route::class.java)
                ?: return Result.failure(Exception("Route not found"))
            Result.success(route)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── SEED DEFAULT ROUTES — Updated for Dharwad District ───────────────────
    suspend fun seedDefaultRoutes() {
        val ref = db.reference.child("routes")

        // Only seed if routes don't exist yet
        val existing = ref.get().await()
        if (existing.exists() && existing.childrenCount > 0) return

        val defaults = listOf(
            Route(
                id               = "DH030",
                name             = "Dharwad → B.V.B College",
                collegeArea      = "Dharwad",
                totalStops       = 6,
                estimatedMinutes = 25,
                stops            = listOf(
                    "Dharwad Bus Stand",
                    "Sadashivanagar",
                    "PB Road",
                    "Navalur Cross",
                    "Vidyanagar",
                    "B.V.B College Gate"
                ),
                status = RouteStatus.DELAYED
            ),
            Route(
                id               = "RNR101",
                name             = "Ranebennur → STJIT College",
                collegeArea      = "Ranebennur",
                totalStops       = 8,
                estimatedMinutes = 38,
                stops            = listOf(
                    "Ranebennur Bus Stand",
                    "Town Hall",
                    "NH48 Junction",
                    "Hubli Road Cross",
                    "Shiggaon Road",
                    "Bypass Cross",
                    "Industrial Area",
                    "STJIT College Gate"
                ),
                status = RouteStatus.ACTIVE
            ),
            Route(
                id               = "ML305",
                name             = "Ranebennur → RTES",
                collegeArea      = "Ranebennur",
                totalStops       = 5,
                estimatedMinutes = 20,
                stops            = listOf(
                    "Ranebennur Market",
                    "KSRTC Stop",
                    "College Road",
                    "Water Tank",
                    "RTES College"
                ),
                status = RouteStatus.UNKNOWN
            )
        )

        defaults.forEach { route ->
            ref.child(route.id).setValue(route).await()
        }
    }

    // ── PINGS ─────────────────────────────────────────────────────────────────
    fun observePings(routeId: String): Flow<List<BusPing>> = callbackFlow {
        val ref = db.reference.child("pings").child(routeId)
            .orderByChild("timestamp").limitToLast(20)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pings = snapshot.children.mapNotNull {
                    it.getValue(BusPing::class.java)
                }.sortedByDescending { it.timestamp }
                trySend(pings)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}