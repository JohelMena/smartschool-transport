package com.johel.smartschool.util

object Validators {
    fun nonEmpty(value: String, field: String) {
        require(value.isNotBlank()) { "$field no puede estar vacío" }
    }
}
