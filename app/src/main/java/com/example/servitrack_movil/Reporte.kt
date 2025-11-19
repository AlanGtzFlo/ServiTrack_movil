package com.example.servitrack_movil

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Reporte(
    val id: Int,
    val ticket_title: String,
    val created_at: String,
    val cantidadMensajes: Int,
    val mensajes: List<MensajeParcelable>
) : Parcelable


@Parcelize
data class MensajeParcelable(
    val id: Int,
    val message: String?,
    val image: String?,
    val created_at: String
) : Parcelable


