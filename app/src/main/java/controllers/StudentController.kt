package controllers

import com.johel.smartschoolapp.data.MemoryDataManager
import com.johel.smartschoolapp.domain.Student

class StudentController(private val dataManager: MemoryDataManager<Student>) {

    fun add(student: Student) {
        dataManager.add(student)
    }

    fun getAll(): List<Student> = dataManager.getAll()
}