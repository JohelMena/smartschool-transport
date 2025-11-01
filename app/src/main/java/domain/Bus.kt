package com.johel.smartschoolapp.domain

class Bus(
    val id: String,
    val plate: String,
    val capacity: Int
) {
    override fun toString(): String {
        return "Bus(id='$id', plate='$plate', capacity=$capacity)"
    }
}
