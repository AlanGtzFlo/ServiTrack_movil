package com.example.servitrack_movil.Network

import java.util.Date

data class TicketResponse(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val priority: String,
    val equipment: String,
    val start_time: Date,   // puede ser null
    val end_time: Date,     // puede ser null
    val duration: String,
    val report: String,
    val status: String,
    val company: Int,
    val user: Int,
    val location: String
)

data class TicketRequest(
    val title: String,
    val description: String,
    val category: String,
    val priority: String,
    val equipment: String,
    val duration: String,
    val report: String,
    val status: String,
    val company: Int,
    val user: Int,
    val location: String?
)


data class ConteoTicketsResponse(
    val pendiente: Int,
    val completado: Int,
    val en_proceso: Int,
    val cerrado: Int,
    val total: Int
)
