package com.johel.smartschoolapp.domain

class Driver(
    val id: String,
    val fullName: String,
    val licenseNumber: String,
    val phone: String
) {
    override fun toString(): String {
        return "Driver(id='$id', fullName='$fullName', licenseNumber='$licenseNumber', phone='$phone')"
    }
}
