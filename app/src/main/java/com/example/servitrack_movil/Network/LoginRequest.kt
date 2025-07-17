package com.example.servitrack_movil.Network

data class LoginRequest(
    val correo: String,
    val password: String
)

data class LoginResponse(
    val refresh: String,
    val access: String,
    val user: User
)

data class User(
    val id: Int,
    val nombre: String,
    val correo: String,
    val rol: String,
    val activo: Boolean,
    val fecha_registro: String,
    val img: String? = null
)

data class UserInfo(
    val id: Int,
    val name: String,
    val email: String
)