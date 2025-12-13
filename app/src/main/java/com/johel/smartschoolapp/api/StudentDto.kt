package com.johel.smartschoolapp.api

data class StudentDto(
    val id: String? = null,
    val fullName: String,
    val grade: String,
    val guardianId: String? = null,
    val routeId: String? = null
)
