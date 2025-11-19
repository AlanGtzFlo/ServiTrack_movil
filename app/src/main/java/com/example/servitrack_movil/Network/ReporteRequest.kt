package com.example.servitrack_movil.Network

data class ReporteRequest(
    val ticket: Int,
    val tecnico: Int,
    val ubicacion: Int,
    val empresa: Int,
    val descripcion: String,
    val es_poliza: Boolean,
    val tipo_poliza: String?,
    val categoria: String,
    val informacion_reporte: String?
)

data class ReporteResponse(
    val id: Int,
    val ticket_title: String,
    val messages: List<MensajeResponse>,
    val created_at: String,
    val ticket: Int
)

data class MensajeRequest(
    val reporte: Int,
    val mensaje: String,
    val imagen: String? = null
)

data class MensajeResponse(
    val id: Int,
    val message: String?,
    val image: String?,
    val created_at: String
)


