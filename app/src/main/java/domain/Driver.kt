package com.johel.smartschool.domain

data class Driver(
    val id: String,
    var fullName: String,
    var nationalId: String,
    var licenseNumber: String,
    var phone: String? = null,
    var email: String? = null,
    var available: Boolean = true
)
