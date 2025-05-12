package org.dam2.fctcting.model

import kotlinx.serialization.Serializable

@Serializable
data class Aircraft(
    val idAircraft: Int,
    val desc: String
)