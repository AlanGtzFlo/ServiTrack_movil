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
    val ticket: Int,
    val tecnico: Int,
    val ubicacion: Int,
    val empresa: Int,
    val descripcion: String,
    val es_poliza: Boolean,
    val tipo_poliza: String?,
    val categoria: String,
    val informacion_reporte: String?,
    val fecha_creacion: String,
    val mensajes: List<MensajeResponse>
)

data class MensajeRequest(
    val reporte: Int,
    val mensaje: String,
    val imagen: String? = null
)

data class MensajeResponse(
    val id: Int,
    val reporte: Int,
    val mensaje: String,
    val imagen: String? = null,
    val fecha: String
)
