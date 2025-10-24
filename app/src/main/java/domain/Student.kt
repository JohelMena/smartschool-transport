package com.johel.smartschool.domain

data class Student(
    val id: String,             // UUID
    var fullName: String,
    var birthDate: String,      // "yyyy-MM-dd"
    var grade: String,          // ej: "2°"
    var medicalNotes: String? = null,
    var guardianId: String,     // id del Encargado/Guardian
    var enrollmentCode: String  // MAT-2025-0001
)
