package com.johel.smartschoolapp.Db

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla de Room para estudiantes
@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey
    val id: String,              // mismo id que usas en domain.Student
    val fullName: String,
    val grade: String,           // aquí guardamos el "enrollmentCode" (grado)
    val photoUri: String?,       // más adelante podemos usarlo para la foto
    val routeId: String?,        // id de ruta (por ahora null)
    val busId: String?,          // id de bus (por ahora null)
    val guardianId: String?      // id de encargado (puede ser null)
)
