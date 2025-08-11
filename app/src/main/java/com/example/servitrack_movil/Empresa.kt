package com.example.servitrack_movil

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Empresa(
    val id: String,
    val nombre: String,
    val estatus: Boolean,
    val tipo_poliza: String,
    val fecha_inicio_poliza: String,
    val fecha_fin_poliza: String
) : Parcelable
