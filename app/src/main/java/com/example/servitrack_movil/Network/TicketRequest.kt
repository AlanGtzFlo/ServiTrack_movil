package com.example.servitrack_movil.Network

import java.util.Date

data class TicketResponse(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val prioridad: String,
    val estado: String,
    val fecha_limite: Date,
    val ubicacion: String,
    val usuario_creador: String,
    val tecnico_asignado: String
)

data class TicketRequest(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val prioridad: String,
    val estado: String,
    val fecha_limite: Date,
    val ubicacion: String,
    val usuario_creador: String,
    val tecnico_asignado: String
)

data class ConteoTicketsResponse(
    val pendiente: Int,
    val completado: Int,
    val en_proceso: Int,
    val cerrado: Int,
    val total: Int
)
