package org.dam2.fctcting.model

import kotlinx.serialization.Serializable

@Serializable
data class Client(
    val idCliente: Int,
    val nombre: String
)