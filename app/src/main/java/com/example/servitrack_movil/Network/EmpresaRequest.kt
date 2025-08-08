package com.example.servitrack_movil.Network
import java.util.Date


data class EmpresaRequest (
    val nombre: String,
    val tipo_poliza: String,
    val fecha_inicio_poliza: Date,
    val fecha_fin_poliza: Date,
    val estatus: Boolean
)

data class EmpresaResponse(
    val id: Int,
    val nombre: String,
    val tipo_poliza: String,
    val fecha_inicio_poliza: Date,
    val fecha_fin_poliza: Date,
    val estatus: Boolean
)