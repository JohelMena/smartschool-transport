package com.johel.smartschoolapp.controllers

import com.johel.smartschoolapp.data.MemoryDataManager
import com.johel.smartschoolapp.domain.Attendance

class AttendanceController(private val dataManager: MemoryDataManager<Attendance>) {

    fun add(attendance: Attendance) {
        dataManager.add(attendance)
    }

    fun getAll(): List<Attendance> = dataManager.getAll()
}
