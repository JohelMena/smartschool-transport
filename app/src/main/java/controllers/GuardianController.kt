package com.johel.smartschoolapp.controllers

import com.johel.smartschoolapp.data.MemoryDataManager
import com.johel.smartschoolapp.domain.Guardian

class GuardianController(private val dataManager: MemoryDataManager<Guardian>) {

    fun add(guardian: Guardian) {
        dataManager.add(guardian)
    }

    fun getAll(): List<Guardian> = dataManager.getAll()
}
