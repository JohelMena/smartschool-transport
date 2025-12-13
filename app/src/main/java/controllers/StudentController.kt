package com.johel.smartschoolapp.controllers

import com.johel.smartschoolapp.Db.StudentRepository
import com.johel.smartschoolapp.domain.Student

/**
 * Controlador de alto nivel para la pantalla de Students.
 * Internamente usa StudentRepository (Room).
 */
class StudentController(
    private val repository: StudentRepository
) {

    fun add(student: Student) {
        repository.insertStudent(student)
    }

    fun getAll(): List<Student> {
        return repository.getAllStudents()
    }

    fun getById(id: String): Student? {
        return repository.getStudentById(id)
    }

    fun removeById(id: String): Boolean {
        repository.deleteStudentById(id)
        return true   // por ahora no nos interesa si existía o no
    }

    fun update(student: Student): Boolean {
        repository.updateStudent(student)
        return true
    }

    fun clearAll() {
        repository.clearAll()
    }
}
