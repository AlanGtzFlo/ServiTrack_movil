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

    // MARK: - Autenticación
    //================================================================================

    @POST("api/login/")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/logout/")
    fun logout(
        @Header("Authorization") authHeader: String,
        @Body logoutRequest: LogoutRequest
    ): Call<LogoutResponse>

    // MARK: - Usuarios
    //================================================================================

    @Multipart
    @PATCH("api/users/{id}/cambiar_foto/")
    fun cambiarFoto(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Part photo: MultipartBody.Part
    ): Call<Void>

    @Multipart
    @POST("api/users/")
    fun subirImagen(@Part foto: MultipartBody.Part): Call<User>

    @GET("api/users/{id}/")
    fun obtenerUsuarioPorId(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Call<User>

    @PATCH("api/users/{id}/cambiar_password/")
    fun cambiarPassword(
        @Path("id") id: Int,
        @Header("Authorization") token: String,
        @Body nuevaPassword: NuevaPasswordRequest
    ): Call<Void>

    // MARK: - Tickets
    //================================================================================

    @GET("api/tickets/")
    fun getTickets(@Header("Authorization") token: String): Call<List<TicketResponse>>

    @GET("api/tickets/{id}")
    fun getTicketById(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Call<TicketResponse>

    @GET("api/tickets/tickets_usuario/")
    fun getTicketsByUser(
        @Header("Authorization") token: String
    ): Call<List<TicketResponse>>

    @GET("/api/tickets/contar_estados_por_usuario")
    fun getTicketCountByTechnician(
        @Header("Authorization") token: String
    ): Call<ConteoTicketsResponse>

    // MARK: - Reportes
    //================================================================================

    @Multipart
    @POST("api/reports/")
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

    @GET("api/reports/")
    fun getReportes(@Header("Authorization") token: String): Call<List<ReporteResponse>>

    @GET("api/reports/{id}")
    fun getReporteById(@Path("id") id: Int): Call<ReporteResponse>

    @GET("api/reports/{id}/exportar_reporte/")
    fun exportarPdf(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Call<ResponseBody>

    @GET("api/reports/conteo_por_tecnico/?tecnico={id}")
    fun getReporteCountByTechnician(@Path("id") id: Int): Call<Int>

    // MARK: - Mensajes de Reporte
    //================================================================================

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

    // MARK: - Entidades (Empresas, Ubicaciones, Clientes)
    //================================================================================

    @GET("api/companies/")
    fun getEmpresas(@Header("Authorization") token: String): Call<List<EmpresaResponse>>

    @GET("api/locations/")
    fun getUbicaciones(@Header("Authorization") token: String): Call<List<UbicacionResponse>>

    @GET("api/clientes/")
    fun getClientes(@Header("Authorization") token: String): Call<List<Cliente>>

    @GET("api/clientes/{id}/")
    fun getCliente(@Path("id") id: Int,@Header("Authorization") token: String): Call<Cliente>

    @POST("user/registrar-token")
    fun registrarTokenFCM(
        @Query("userId") userId: Int,
        @Query("token") token: String
    ): Call<Void>

}