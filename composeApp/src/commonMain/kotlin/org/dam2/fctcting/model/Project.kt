package org.dam2.fctcting.model

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val idProject: String,
    val desc: String,
    val idCliente: Int?,
    val idArea: Int?
)