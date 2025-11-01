package com.johel.smartschoolapp.util

object IdGenerator {
    private var counter = 0

    fun newID(): String {
        counter++
        return "ID-$counter"
    }
}
