package com.example.servitrack_movil

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Ubicacion(
    val id: Int,
    val nombre: String,
    val direccion: String,
    val cliente_id: Int?,
    val empresa_id: Int?,
    val contacto: String,
    val estatus: Boolean,
    val fecha_creacion: String?
) : Parcelable