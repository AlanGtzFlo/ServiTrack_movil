package com.example.servitrack_movil

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import android.os.Bundle
import com.google.firebase.FirebaseApp

class MyApp : Application(), Application.ActivityLifecycleCallbacks {

    private var activityCount = 0
    private lateinit var prefs: SharedPreferences
    // Tiempo máximo en segundo plano (ej. 5 minutos en milisegundos)
    private val MAX_BACKGROUND_TIME = 5 * 60 * 1000

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityStarted(activity: Activity) {
        if (activityCount == 0) {
            // La app regresa de segundo plano (o inicia)
            verificarTiempoInactividad()
        }
        activityCount++
    }

    override fun onActivityStopped(activity: Activity) {
        activityCount--
        if (activityCount == 0) {
            // Se fue a segundo plano: Guardamos la hora exacta
            prefs.edit().putLong("last_active_time", System.currentTimeMillis()).apply()
        }
    }

    private fun verificarTiempoInactividad() {
        val lastActiveTime = prefs.getLong("last_active_time", 0)
        val currentTime = System.currentTimeMillis()

        // Si lastActiveTime es 0, es la primera vez que se abre, no hacemos nada
        if (lastActiveTime != 0L) {
            val tiempoTranscurrido = currentTime - lastActiveTime

            if (tiempoTranscurrido > MAX_BACKGROUND_TIME) {
                // Pasó mucho tiempo: Borramos el token y la hora guardada
                prefs.edit().clear().apply()
                // Opcional: Navegar a la pantalla de Login aquí o forzar cierre
            } else {
                // Regresó rápido: Borramos la marca de tiempo para que siga usando la app
                prefs.edit().remove("last_active_time").apply()
            }
        }
    }

    // ... Resto de los métodos vacíos ...
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}