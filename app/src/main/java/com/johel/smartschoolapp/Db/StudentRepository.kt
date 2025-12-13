package com.johel.smartschoolapp.Db

import com.johel.smartschoolapp.domain.Student

class StudentRepository(
    private val dao: StudentDao
) {

    // --------- Mapas entre Entity y dominio ---------

    private fun StudentEntity.toDomain(): Student {
        return Student(
            id = id,
            fullName = fullName,
            birthDate = "",
            enrollmentCode = grade,
            guardianId = guardianId ?: ""
        )
    }

    private fun Student.toEntity(): StudentEntity {
        return StudentEntity(
            id = id,
            fullName = fullName,
            grade = enrollmentCode,
            photoUri = null,   // más adelante si quieres manejar foto en Room
            routeId = null,
            busId = null,
            guardianId = if (guardianId.isBlank()) null else guardianId
        )
    }

    // --------- Operaciones públicas ---------

    fun getAllStudents(): List<Student> {
        return dao.getAll().map { it.toDomain() }
    }

    fun getStudentById(id: String): Student? {
        return dao.getById(id)?.toDomain()
    }

    fun insertStudent(student: Student) {
        dao.insert(student.toEntity())
    }

    fun updateStudent(student: Student) {
        dao.update(student.toEntity())
    }

    fun deleteStudent(student: Student) {
        dao.delete(student.toEntity())
    }

    fun deleteStudentById(id: String) {
        dao.deleteById(id)
    }

    fun clearAll() {
        dao.clearAll()
    }
}
