package com.johel.smartschoolapp.util

object Validators {

    fun isValidName(name: String): Boolean {
        return name.isNotBlank() && name.length >= 3
    }

    fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("\\d{8}"))
    }

    fun isValidLicense(license: String): Boolean {
        return license.startsWith("L-") && license.length > 4
    }
}
