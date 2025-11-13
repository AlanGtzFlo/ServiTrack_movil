package com.example.servitrack_movil.Network

data class UbicacionRequest(
    val name: String,
    val address: String,
    val company: Int
)


data class UbicacionResponse(
    val id: Int,
    val name: String,
    val address: String,
    val company: Int
)
