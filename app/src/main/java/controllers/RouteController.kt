package com.johel.smartschool.controllers

import com.johel.smartschool.data.DataManager
import com.johel.smartschool.domain.Route

class RouteController(
    private val routes: DataManager<Route>
) {
    fun create(route: Route) = routes.create(route)
    fun list() = routes.getAll()
    fun get(id: String) = routes.getById(id)

    fun assignStudent(routeId: String, studentId: String): Route? =
        routes.update(routeId) { r ->
            r.studentIds.add(studentId); r
        }

    fun assignDriverAndBus(routeId: String, driverId: String, busId: String): Route? =
        routes.update(routeId) { r ->
            r.driverId = driverId; r.busId = busId; r
        }
}
