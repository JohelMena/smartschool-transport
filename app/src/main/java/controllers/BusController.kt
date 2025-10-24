package com.johel.smartschool.controllers

import com.johel.smartschool.data.DataManager
import com.johel.smartschool.domain.Bus
import com.johel.smartschool.util.Validators

class BusController(
    private val buses: DataManager<Bus>
) {
    fun create(bus: Bus): Bus {
        Validators.nonEmpty(bus.plate, "Placa")
        require(bus.capacity > 0) { "La capacidad debe ser mayor a 0" }
        return buses.create(bus)
    }

    fun get(id: String) = buses.getById(id)
    fun list(): List<Bus> = buses.getAll()

    fun update(id: String, updater: (Bus) -> Bus) =
        buses.update(id, updater)

    fun delete(id: String) = buses.delete(id)
}
