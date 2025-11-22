package com.johel.smartschool.controllers

import com.johel.smartschool.data.DataManager
import com.johel.smartschool.domain.Student
import com.johel.smartschool.util.Validators

class StudentController(
    private val students: DataManager<Student>
) {
    fun create(student: Student): Student {
        Validators.nonEmpty(student.fullName, "Nombre")
        Validators.nonEmpty(student.grade, "Nivel educativo")
        return students.create(student)
    }
    fun get(id: String) = students.getById(id)
    fun list() = students.getAll()
    fun update(id: String, updater: (Student) -> Student) = students.update(id, updater)
    fun delete(id: String) = students.delete(id)
}

