package com.johel.smartschool.controllers

import com.johel.smartschool.data.DataManager
import com.johel.smartschool.domain.Attendance

class AttendanceController(
    private val att: DataManager<Attendance>
) {
    fun checkIn(record: Attendance) = att.create(record)

    fun checkOut(id: String, time: String) =
        att.update(id) { it.copy(checkOut = time) }

    fun byStudent(studentId: String) =
        att.getAll().filter { it.studentId == studentId }
}
