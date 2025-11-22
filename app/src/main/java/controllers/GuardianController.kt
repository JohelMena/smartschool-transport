package com.johel.smartschool.controllers

import com.johel.smartschool.data.DataManager
import com.johel.smartschool.domain.Guardian
import com.johel.smartschool.util.Validators

class GuardianController(
    private val guardians: DataManager<Guardian>
) {
    fun create(guardian: Guardian): Guardian {
        Validators.nonEmpty(guardian.fullName, "Nombre del encargado")
        Validators.nonEmpty(guardian.phone, "Teléfono")
        Validators.nonEmpty(guardian.address, "Dirección")
        return guardians.create(guardian)
    }

    fun get(id: String) = guardians.getById(id)
    fun list(): List<Guardian> = guardians.getAll()

    fun update(id: String, updater: (Guardian) -> Guardian) =
        guardians.update(id, updater)

    fun delete(id: String) = guardians.delete(id)
}
