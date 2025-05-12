package org.dam2.fctcting.model

import kotlinx.serialization.Serializable

@Serializable
data class Calendar(
    val idCalendar: Int,
    val date: String
)