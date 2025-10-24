package com.johel.smartschool.domain

data class Bus(
    val id: String,
    var plate: String,
    var capacity: Int,
    var status: String,         // activo / mantenimiento
    var type: String? = null,
    var nextMaintenance: String? = null
)
