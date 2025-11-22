package com.johel.smartschool.util

import java.util.UUID
import java.util.Calendar

object IdGenerator {
    fun newId(): String = UUID.randomUUID().toString()

    fun enrollmentCode(year: Int = Calendar.getInstance().get(Calendar.YEAR), seq: Int): String =
        "MAT-$year-${seq.toString().padStart(4, '0')}"
}
