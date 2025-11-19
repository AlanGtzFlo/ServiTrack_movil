package com.example.servitrack_movil.Network

import java.util.Date

data class TicketResponse(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val priority: String,
    val equipment: String,
    val start_time: Date,
    val end_time: Date,
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
    val abierto: Int,
    val en_espera: Int,
    val en_curso: Int,
    val cerrado: Int,
    val total: Int
)
