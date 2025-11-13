package com.example.servitrack_movil.Network
import java.util.Date


data class EmpresaResponse(
    val id: Int,
    val name: String,
    val logo: String,
    val address: String,
    val contact: String,
    val description: String,
    val plan_type: String,
    val status: String,
    val created_at: String
)


data class EmpresaRequest(
    val name: String,
    val logo: String,
    val address: String,
    val contact: String,
    val description: String,
    val plan_type: String
)
