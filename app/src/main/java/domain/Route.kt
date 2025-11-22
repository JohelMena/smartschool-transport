package com.johel.smartschool.domain

data class Route(
    val id: String,
    var name: String,
    var pickupPoints: List<String> = emptyList(),
    var driverId: String? = null,
    var busId: String? = null,
    var studentIds: MutableSet<String> = mutableSetOf()
)
