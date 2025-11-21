package com.johel.smartschoolapp.controllers

import com.johel.smartschoolapp.data.MemoryDataManager
import com.johel.smartschoolapp.domain.Student

class StudentController(private val dataManager: MemoryDataManager<Student>) {

    fun add(student: Student) {
        dataManager.add(student)
    }

    fun getAll(): List<Student> = dataManager.getAll()

    fun getById(id: String): Student? = dataManager.getById(id)

    fun removeById(id: String): Boolean = dataManager.removeById(id)

    fun update(student: Student): Boolean {
        // Estrategia simple: eliminar por id y volver a agregar actualizado
        val removed = dataManager.removeById(student.id)
        dataManager.add(student)
        return removed
    }
}
