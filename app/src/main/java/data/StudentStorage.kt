package com.johel.smartschoolapp.data

import android.graphics.Bitmap
import com.johel.smartschoolapp.controllers.StudentController
import com.johel.smartschoolapp.domain.Student

object StudentStorage {
    private val dataManager = MemoryDataManager<Student> { it.id }

    // Controller used by all activities
    val controller = StudentController(dataManager)

    // In-memory map: student id -> photo bitmap
    val photos: MutableMap<String, Bitmap> = mutableMapOf()
}
