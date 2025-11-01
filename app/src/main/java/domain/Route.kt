package com.johel.smartschoolapp.domain

class Route(
    val id: String,
    val name: String,
    val driverId: String,
    val busId: String
) {
    override fun toString(): String {
        return "Route(id='$id', name='$name', driverId='$driverId', busId='$busId')"
    }
}
