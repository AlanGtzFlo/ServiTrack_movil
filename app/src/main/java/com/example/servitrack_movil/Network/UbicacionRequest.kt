package com.example.servitrack_movil.Network

data class UbicacionRequest (

    val nombre: String,
    val direccion: String,
    val cliente_id: Int,
    val empresa_id: Int,
    val contacto: String,
    val estatus: Boolean
    )

data class UbicacionResponse(
    val id: Int,
    val nombre: String,
    val direccion: String,
    val cliente: ClienteResponse?,
    val empresa: EmpresaResponse?,
    val contacto: String,
    val estatus: Boolean,
    val fecha_creacion: String
)