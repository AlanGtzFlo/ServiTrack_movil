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
import okhttp3.ResponseBody
import retrofit2.http.Query

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
    fun getTickets(@Header("Authorization") token: String): Call<List<TicketResponse>>

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
    fun getTicketById(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Call<TicketResponse>

    //Ticket por usuario
    @GET("api/tickets/tickets_usuario/")
    fun getTicketsByUser(
        @Header("Authorization") token: String
    ): Call<List<TicketResponse>>

    //Conteo de tickets
    @GET("/api/tickets/contar_estados_por_usuario")
    fun getTicketCountByTechnician(
        @Header("Authorization") token: String
    ): Call<ConteoTicketsResponse>

    //Generar PDF
    @GET("api/reportes/{id}/exportar_reporte/")
    fun exportarPdf(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Call<ResponseBody>

    //Obtener Empresas
    @GET("api/empresas/")
    fun getEmpresas(@Header("Authorization") token: String): Call<List<EmpresaResponse>>

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

    @Multipart
    @POST("api/mensajes_reporte/")
    fun createMensaje(
        @Header("Authorization") token: String,
        @Part("reporte") reporte: RequestBody,
        @Part("mensaje") mensaje: RequestBody,
        @Part imagen: MultipartBody.Part? = null
    ): Call<Void>


    @GET("api/mensajes_reporte/mensajes_por_reporte_id/")
    fun getMensajesPorReporteId(
        @Query("reporte") reporteId: Int,
        @Header("Authorization") token: String
    ): Call<List<MensajeResponse>>


    @GET("api/mensajes_reporte/{id}/")
    fun getMensajePorId(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Call<MensajeResponse>


    @GET("api/reportes/{id}")
    fun getReporteById(@Path("id") id: Int): Call<ReporteResponse>

    @GET(/* value = */ "api/reportes/")
    fun getReportes(@Header("Authorization") token: String): Call<List<ReporteResponse>>

    //Conteo Reportes
    @GET("api/reportes/conteo_por_tecnico/?tecnico={id}")
    fun getReporteCountByTechnician(@Path("id") id: Int): Call<Int>

    // Ubicaciones
    @GET("api/ubicaciones/")
    fun getUbicaciones(@Header("Authorization") token: String): Call<List<UbicacionResponse>>

    @GET("api/clientes/")
    fun getClientes(@Header("Authorization") token: String): Call<List<Cliente>>

    @GET("api/clientes/{id}/")
    fun getCliente(@Path("id") id: Int,@Header("Authorization") token: String): Call<Cliente>
}



