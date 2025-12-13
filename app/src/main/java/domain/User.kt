package com.johel.smartschoolapp.domain

open class User(
    val id: String,
    val fullName: String
) {
    override fun toString(): String {
        return "User(id='$id', fullName='$fullName')"
    }
}
