package com.johel.smartschoolapp.data

import com.johel.smartschoolapp.domain.Driver

object DriverStorage {

    private val dataManager = MemoryDataManager<Driver> { it.id }

    init {
        // Predefined drivers (you can change the data)
        dataManager.add(Driver("D1", "John Smith", "LIC-1234", "8888-1111"))
        dataManager.add(Driver("D2", "Ana Johnson", "LIC-5678", "8888-2222"))
        dataManager.add(Driver("D3", "Carlos Brown", "LIC-9012", "8888-3333"))
    }

    fun getAll(): List<Driver> = dataManager.getAll()
}