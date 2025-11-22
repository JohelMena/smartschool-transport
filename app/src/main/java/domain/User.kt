package com.johel.smartschool.domain
data class User(
    val id: String,
    val username: String,
    val passwordHash: String,
    val role: Role
)
