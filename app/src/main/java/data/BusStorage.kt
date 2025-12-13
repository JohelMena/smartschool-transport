package com.johel.smartschoolapp.data

import com.johel.smartschoolapp.domain.Bus

object BusStorage {

    private val dataManager = MemoryDataManager<Bus> { it.id }

    init {
        // Predefined buses (you can change plates/capacity)
        dataManager.add(Bus("B1", "ABC-123", 40))
        dataManager.add(Bus("B2", "XYZ-456", 30))
        dataManager.add(Bus("B3", "LMN-789", 50))
    }

    fun getAll(): List<Bus> = dataManager.getAll()
}