package com.johel.smartschoolapp.data

import com.johel.smartschoolapp.controllers.RouteController
import com.johel.smartschoolapp.domain.Route

object RouteStorage {

    private val dataManager = MemoryDataManager<Route> { it.id }

    // Controller for routes (same pattern as StudentStorage)
    val controller = RouteController(dataManager)

    init {
        // Predefined routes (examples)
        controller.add(Route("R1", "Route 1", driverId = "D1", busId = "B1"))
        controller.add(Route("R2", "Route 2", driverId = "D2", busId = "B2"))
        controller.add(Route("R3", "Route 3", driverId = "D3", busId = "B3"))
    }

    fun getAll(): List<Route> = controller.getAll()

    fun getById(id: String?): Route? {
        if (id.isNullOrEmpty()) return null
        return controller.getById(id)
    }
}