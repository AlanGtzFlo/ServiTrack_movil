package com.example.servitrack_movil

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ubicacion(
    val id: Int,
    val name: String,
    val address: String,
    val company: Int
) : Parcelable
