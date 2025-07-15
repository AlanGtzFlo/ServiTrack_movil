package com.example.servitrack_movil

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Reporte(
    val id: String,
    val titulo: String,
    val estado: String,
    val fecha: String
) : Parcelable
