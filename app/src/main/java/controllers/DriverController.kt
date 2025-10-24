package com.johel.smartschool.controllers

import com.johel.smartschool.data.DataManager
import com.johel.smartschool.domain.Driver
import com.johel.smartschool.util.Validators

class DriverController(
    private val drivers: DataManager<Driver>
) {
    fun create(driver: Driver): Driver {
        Validators.nonEmpty(driver.fullName, "Nombre del chofer")
        Validators.nonEmpty(driver.nationalId, "Cédula")
        Validators.nonEmpty(driver.licenseNumber, "Licencia")
        return drivers.create(driver)
    }

    fun setAvailability(id: String, available: Boolean) =
        drivers.update(id) { it.copy(available = available) }

    fun get(id: String) = drivers.getById(id)
    fun list(): List<Driver> = drivers.getAll()

    fun update(id: String, updater: (Driver) -> Driver) =
        drivers.update(id, updater)

    fun delete(id: String) = drivers.delete(id)
}
