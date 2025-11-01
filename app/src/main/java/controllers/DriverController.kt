package com.johel.smartschoolapp.controllers

import com.johel.smartschoolapp.data.MemoryDataManager
import com.johel.smartschoolapp.domain.Driver

class DriverController(private val dataManager: MemoryDataManager<Driver>) {

    fun add(driver: Driver) {
        dataManager.add(driver)
    }

    fun getAll(): List<Driver> = dataManager.getAll()
}
