package org.dam2.fctcting.model

import kotlinx.serialization.Serializable

@Serializable
data class Rol(
    val idRol: Int,
    val rol: String
)