package com.example.servitrack_movil

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Se llama cuando el sistema genera un nuevo token de FCM.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "Nuevo token generado: $token")

        // Aquí deberás llamar a tu función para enviar el token al servidor.
        // sendTokenToServer(token)
    }

    /**
     * Se llama cuando recibes una notificación y la app está en primer plano.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCM_MESSAGE", "Mensaje recibido: ${message.notification?.title}")
        // Aquí puedes decidir si muestras una notificación personalizada.
    }
}