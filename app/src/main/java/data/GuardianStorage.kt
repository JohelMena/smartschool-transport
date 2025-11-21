package com.johel.smartschoolapp.data

import com.johel.smartschoolapp.domain.Guardian

object GuardianStorage {

    private val dataManager = MemoryDataManager<Guardian> { it.id }

    init {
        // Guardians de ejemplo
        dataManager.add(Guardian("G1", "Juan Pérez", "8888-8888", "Downtown"))
        dataManager.add(Guardian("G2", "María López", "7777-7777", "Uptown"))
        dataManager.add(Guardian("G3", "Carlos Gómez", "6666-6666", "Near school"))
    }

    fun add(guardian: Guardian) {
        dataManager.add(guardian)
    }

    fun update(guardian: Guardian) {

        dataManager.removeById(guardian.id)
        dataManager.add(guardian)
    }

    fun removeById(id: String) {
        dataManager.removeById(id)
    }

    fun getAll(): List<Guardian> = dataManager.getAll()

    fun getById(id: String?): Guardian? {
        if (id.isNullOrEmpty()) return null
        return dataManager.getById(id)
    }
}