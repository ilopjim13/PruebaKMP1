package org.dam2.fctcting.model

import kotlinx.serialization.Serializable

@Serializable
data class Area(
    val idArea: Int,
    val desc: String
)