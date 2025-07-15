package com.example.servitrack_movil.Network

data class LoginRequest(
    val email: String,
    val password: String
)

// Cambia esta clase para que coincida con la respuesta de tu servidor
data class LoginResponse(
    val refresh: String, // Coincide con la clave "refresh" del JSON
    val access: String   // Coincide con la clave "access" del JSON
    // Si tu servidor enviara la información del usuario en otro endpoint o en una respuesta diferente,
    // tendrías que hacer una llamada separada para obtenerla, o pedirle al backend que la incluya aquí.
)

// Esta clase UserInfo ya no sería necesaria para la respuesta de login actual,
// a menos que tu servidor la envíe en otro lugar o la respuesta de login cambie.
// Si la necesitas después de obtener los tokens (por ejemplo, para una pantalla de perfil),
// tendrías que hacer otra llamada a la API con el token de acceso.
data class UserInfo(
    val id: Int,
    val name: String,
    val email: String
)