package com.johel.smartschoolapp.api


data class AttendanceDto(
    val id: String? = null,
    val studentId: String,
    val date: String,
    val status: String,
    val routeId: String? = null,
    val notes: String? = null
)
