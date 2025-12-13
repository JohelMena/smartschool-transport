package com.johel.smartschoolapp.api

data class GuardianDto(
    val id: String? = null,
    val fullName: String,
    val phone: String,
    val address: String? = null
)
