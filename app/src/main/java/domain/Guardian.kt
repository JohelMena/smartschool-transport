package com.johel.smartschoolapp.domain

class Guardian(
    val id: String,
    val fullName: String,
    val phone: String,
    val address: String
) {
    override fun toString(): String {
        return "Guardian(id='$id', fullName='$fullName', phone='$phone', address='$address')"
    }
}
