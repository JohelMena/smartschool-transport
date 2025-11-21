package com.johel.smartschoolapp.controllers

import com.johel.smartschoolapp.data.MemoryDataManager
import com.johel.smartschoolapp.domain.Route

class RouteController(private val dataManager: MemoryDataManager<Route>) {

    fun add(route: Route) {
        dataManager.add(route)
    }

    fun getAll(): List<Route> = dataManager.getAll()

    fun getById(id: String): Route? {
        return dataManager.getById(id)
    }

    fun update(route: Route) {
        // Forma simple: eliminar la anterior y agregar la nueva
        dataManager.removeById(route.id)
        dataManager.add(route)
    }

    fun removeById(id: String) {
        dataManager.removeById(id)
    }
}

