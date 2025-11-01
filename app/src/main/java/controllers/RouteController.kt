package com.johel.smartschoolapp.controllers

import com.johel.smartschoolapp.data.MemoryDataManager
import com.johel.smartschoolapp.domain.Route

class RouteController(private val dataManager: MemoryDataManager<Route>) {

    fun add(route: Route) {
        dataManager.add(route)
    }

    fun getAll(): List<Route> = dataManager.getAll()
}
