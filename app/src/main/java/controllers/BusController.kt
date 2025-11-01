package com.johel.smartschoolapp.controllers

import com.johel.smartschoolapp.data.MemoryDataManager
import com.johel.smartschoolapp.domain.Bus

class BusController(private val dataManager: MemoryDataManager<Bus>) {

    fun add(bus: Bus) {
        dataManager.add(bus)
    }

    fun getAll(): List<Bus> = dataManager.getAll()
}
