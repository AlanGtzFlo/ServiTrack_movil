package com.example.servitrack_movil

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Reporte(
    val id: Int,
    val titulo: String,         // Puedes usar descripción corta o categoría
    val descripcion: String,
    val prioridad: String,
    val estado: String,
    val fecha: String,
    val ubicacion: String,
    val creador: String,
    val tecnico: String
) : Parcelable

