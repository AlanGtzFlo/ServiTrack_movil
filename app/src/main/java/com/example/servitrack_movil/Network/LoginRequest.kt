package com.example.servitrack_movil.Network

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val refresh: String,
    val access: String,
    val user: User
)

data class LogoutRequest(
    val refresh: String
)

data class LogoutResponse(
    val message: String
)

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val first_name: String,
    val last_name: String,
    val phone: String?,
    val address: String?,
    val user_type: String,
    val age: Int,
    val rfc: String?,
    val status: String,
    val company: Int,
    val photo: String?
)

data class UserInfo(
    val id: Int,
    val name: String,
    val email: String
)

data class UpdatePhotoResponse(
    val message: String,
    val photo_url: String
)

data class ChangePasswordRequest(
    val current_password: String,
    val new_password: String,
    val confirm_password: String
)

data class GenericResponse(
    val message: String,
    val status: Boolean
)
