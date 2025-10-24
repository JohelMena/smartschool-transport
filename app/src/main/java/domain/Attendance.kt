package com.johel.smartschool.domain

data class Attendance(
    val id: String,
    val studentId: String,
    val date: String,         // "yyyy-MM-dd"
    var checkIn: String?,     // "HH:mm"
    var checkOut: String?
)
