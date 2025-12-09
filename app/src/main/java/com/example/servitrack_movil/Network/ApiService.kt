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
    @POST("api/users/update_photo/")
    fun updateUserPhoto(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part
    ): Call<UpdatePhotoResponse>

    @GET("api/users/{id}/")
    fun obtenerUsuarioPorId(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Call<User>

    @POST("api/users/change_password/")
    fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Call<Void>



    @GET("api/tickets/")
    fun getTickets(@Header("Authorization") token: String): Call<List<TicketResponse>>

    @GET("api/tickets/{id}")
    fun getTicketById(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Call<TicketResponse>

    @GET("api/tickets/ticketsbyuserl/")
    fun getTicketsByUser(
        @Header("Authorization") token: String
    ): Call<List<TicketResponse>>

    @GET("/api/tickets/count_by_status")
    fun getTicketCountByTechnician(
        @Header("Authorization") token: String
    ): Call<ConteoTicketsResponse>


    @POST("api/tickets/save_token/")
    fun saveFcmToken(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Call<Void>


    @Multipart
    @POST("api/reports/")
    fun createReporte(
        @Header("Authorization") token: String,
        @Part("ticket") ticket: RequestBody,
        @Part("ticket_title") ticketTitle: RequestBody,
        @Part("created_at") createdAt: RequestBody,
        @Part("messages") messages: RequestBody
    ): Call<ReporteResponse>

    @GET("api/reports/")
    fun getReportes(@Header("Authorization") token: String): Call<List<ReporteResponse>>

    @GET("api/reports/reportes-by-user/")
    fun getReportesByUser(@Header("Authorization") token: String): Call<List<ReporteResponse>>


    @Multipart
    @POST("api/reports/{id}/add-message/")
    fun addMessageToReport(
        @Header("Authorization") token: String,
        @Path("id") reportId: Int,
        @Part("message") message: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): Call<MensajeResponse>




    @GET("api/reports/{id}/export-pdf/")
    fun exportarPdf(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Call<ResponseBody>

    @GET("api/reports/conteo_por_tecnico/?tecnico={id}")
    fun getReporteCountByTechnician(@Path("id") id: Int): Call<Int>

    // MARK: - Mensajes de Reporte
    //================================================================================

    @Multipart
    @POST("api/messages/")
    fun createMensaje(
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