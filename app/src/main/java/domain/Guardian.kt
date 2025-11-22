package com.johel.smartschool.domain

data class Guardian(
    val id: String,
    var fullName: String,
    var phone: String,
    var address: String,
    var idCopyUrl: String? = null,
    var paymentMethod: String? = null
)
