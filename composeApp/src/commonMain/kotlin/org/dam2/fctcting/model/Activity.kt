package org.dam2.fctcting.model

import kotlinx.serialization.Serializable

@Serializable
data class Activity(
    val idActivity: Int,
    val idTimeCode: Int,
    val desc: String,
    val dateFrom: String?,
    val dateTo: String?
)