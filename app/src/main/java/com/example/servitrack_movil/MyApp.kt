package com.example.servitrack_movil

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import android.os.Bundle

class MyApp : Application(), Application.ActivityLifecycleCallbacks {

    private var activityCount = 0
    private lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityStarted(activity: Activity) {
        activityCount++
    }

    override fun onActivityStopped(activity: Activity) {
        activityCount--
        if (activityCount == 0) {
            // Aquí significa que la app ya no tiene pantallas visibles
            // Si queremos borrar la sesión al cerrar totalmente:
            prefs.edit().clear().apply()
        }
    }

    // Métodos vacíos que hay que implementar por la interfaz
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}
