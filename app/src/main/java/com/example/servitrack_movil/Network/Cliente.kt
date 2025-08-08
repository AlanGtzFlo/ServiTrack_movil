package com.example.servitrack_movil.Network

data class Cliente(
    val id: Int,
    val nombre: String?,
    val rfc: String?,
    val telefono: String?,
    val correo: String?,
    val direccion: String?,
    val estatus: Boolean?
)

data class ClienteResponse(
    val id: Int,
    val nombre: String?,
    val rfc: String?,
    val telefono: String?,
    val correo: String?,
    val direccion: String?,
    val estatus: Boolean?
)