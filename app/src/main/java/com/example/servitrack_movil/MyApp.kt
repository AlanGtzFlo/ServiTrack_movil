package com.example.servitrack_movil

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import android.os.Bundle
import com.google.firebase.FirebaseApp

class MyApp : Application(), Application.ActivityLifecycleCallbacks {

    private var activityCount = 0
    private lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        // ✅ Inicializar Firebase al iniciar la aplicación
        FirebaseApp.initializeApp(this)

        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityStarted(activity: Activity) {
        activityCount++
    }

    override fun onActivityStopped(activity: Activity) {
        activityCount--
        if (activityCount == 0) {
            // Cuando ya no hay pantallas visibles, limpia la sesión
            prefs.edit().clear().apply()
        }
    }

    // Métodos requeridos por la interfaz
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}
