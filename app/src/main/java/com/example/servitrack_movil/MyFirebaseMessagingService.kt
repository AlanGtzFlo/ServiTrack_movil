package com.example.servitrack_movil

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "Nuevo token generado: $token")
        // Si quieres enviarlo al servidor, podrías hacerlo aquí (pero en tu caso no es necesario)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCM_MESSAGE", "Mensaje recibido de: ${message.from}")

        // Si el mensaje contiene datos personalizados
        message.data.isNotEmpty().let {
            Log.d("FCM_DATA", "Datos: ${message.data}")
        }

        // Si el mensaje contiene una notificación estándar
        message.notification?.let {
            val title = it.title ?: "ServiTrack"
            val body = it.body ?: "Tienes una nueva notificación"
            showNotification(title, body)
        }
    }

    /**
     * Muestra una notificación personalizada.
     */
    private fun showNotification(title: String, message: String) {
        val channelId = "servitrack_notifications"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Cuando el usuario toque la notificación, lo llevará al menú principal
        val intent = Intent(this, MenuActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construcción de la notificación
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logofixflow) // ícono de tu app
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal en Android 8.0 (Oreo) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones ServiTrack",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal de notificaciones generales de ServiTrack"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Mostrar notificación
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}
