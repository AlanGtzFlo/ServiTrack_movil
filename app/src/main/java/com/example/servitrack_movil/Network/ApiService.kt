package com.example.servitrack_movil.Network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.servitrack_movil.Network.LoginRequest
import com.example.servitrack_movil.Network.LoginResponse

interface ApiService {
    @POST("/api/login/")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}