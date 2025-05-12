package org.dam2.fctcting.model

import kotlinx.serialization.Serializable

@Serializable
data class ProjectTimeCode(
    val idProject: String,
    val idTimeCode: Int
)