package com.johel.smartschoolapp.data

import android.graphics.Bitmap

/**
 * Almacenamiento en memoria para fotos de estudiantes.
 * (id de estudiante -> Bitmap de la foto)
 */
object StudentStorage {

    val photos: MutableMap<String, Bitmap> = mutableMapOf()
}
