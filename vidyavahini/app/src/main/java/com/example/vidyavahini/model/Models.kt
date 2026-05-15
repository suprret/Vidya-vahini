package com.example.vidyavahini.model

data class Route(
    val id: String = "",
    val name: String = "",
    val collegeArea: String = "",
    val totalStops: Int = 0,
    val estimatedMinutes: Int = 0,
    val stops: List<String> = emptyList(),
    val status: RouteStatus = RouteStatus.UNKNOWN
)

enum class RouteStatus {
    ACTIVE,
    DELAYED,
    BREAKDOWN,
    UNKNOWN
}

data class BusPing(
    val pingId: String = "",
    val routeId: String = "",
    val userId: String = "",
    val userName: String = "",
    val landmark: String = "",
    val stopIndex: Int = 0,
    val timestamp: Long = 0L,
    val isBreakdown: Boolean = false
)

data class StudentProfile(
    val uid: String = "",
    val phoneNumber: String = "",
    val name: String = "",
    val college: String = "",
    val defaultRouteId: String = "",
    val myStopIndex: Int = 0,
    val emergencyContact: String = ""
)