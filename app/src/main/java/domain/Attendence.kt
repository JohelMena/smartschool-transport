package com.johel.smartschoolapp.domain

class Attendance(
    val id: String,
    val studentId: String,
    val routeId: String,
    val date: String,
    val status: String,
    val checkIn: String,
    val checkOut: String
) {
    override fun toString(): String {
        return "Attendance(id='$id', studentId='$studentId', routeId='$routeId', date='$date', status='$status', checkIn='$checkIn', checkOut='$checkOut')"
    }
}
