package com.example.servitrack_movil.Network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Header
import retrofit2.http.Part
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ApiService {

    //Login
    @POST("api/login/")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    //Logout
    @POST("api/logout/")
    fun logout(
        @Header("Authorization") authHeader: String,
        @Body logoutRequest: LogoutRequest
    ): Call<LogoutResponse>

    @Multipart
    @PATCH("api/usuarios/{id}/cambiar_foto/")
    fun cambiarFoto(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Part foto: MultipartBody.Part
    ): Call<Void>


    @Multipart
    @POST("api/usuarios/")
    fun subirImagen(@Part foto: MultipartBody.Part): Call<User>

    //Todos los tickets
    @GET("api/tickets/")
    fun getTickets(): Call<List<TicketResponse>>

    @GET("api/usuarios/{id}/")
    fun obtenerUsuarioPorId(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Call<User>

    @PATCH("api/usuarios/{id}/cambiar_password/")
    fun cambiarPassword(
        @Path("id") id: Int,
        @Header("Authorization") token: String,
        @Body nuevaPassword: NuevaPasswordRequest
    ): Call<Void>

    //Ticket por ID
    @GET("api/tickets/{id}")
    fun getTicketById(@Path("id") id: Int): Call<TicketResponse>

    //Conteo de tickets
    @GET("/api/tickets/contar_estados_por_usuario")
    fun getTicketCountByTechnician(
        @Header("Authorization") token: String
    ): Call<ConteoTicketsResponse>


    //Obtener Empresas
    @GET("api/empresas/")
    fun getEmpresas(): Call<List<EmpresaResponse>>

    //Crear Reporte
    @Multipart
    @POST("api/reportes/")
    fun createReporte(
        @Header("Authorization") token: String,
        @Part("ticket") ticket: RequestBody,
        @Part("tecnico") tecnico: RequestBody,
        @Part("ubicacion") ubicacion: RequestBody,
        @Part("empresa") empresa: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part("es_poliza") esPoliza: RequestBody,
        @Part("tipo_poliza") tipoPoliza: RequestBody?,
        @Part("categoria") categoria: RequestBody,
        @Part("informacion_reporte") informacionReporte: RequestBody
    ): Call<ReporteResponse>






    @GET("api/reportes/{id}")
    fun getReporteById(@Path("id") id: Int): Call<ReporteResponse>

    @GET("api/reportes/")
    fun getReportes(): Call<List<ReporteResponse>>


    //Conteo Reportes
    @GET("api/reportes/conteo_por_tecnico/?tecnico={id}")
    fun getReporteCountByTechnician(@Path("id") id: Int): Call<Int>

    // Ubicaciones
    @GET("api/ubicaciones/")
    fun getUbicaciones(): Call<List<UbicacionResponse>>

    @GET("api/clientes/")
    fun getClientes(): Call<List<Cliente>>

    @GET("api/clientes/{id}/")
    fun getCliente(@Path("id") id: Int): Call<Cliente>
}



