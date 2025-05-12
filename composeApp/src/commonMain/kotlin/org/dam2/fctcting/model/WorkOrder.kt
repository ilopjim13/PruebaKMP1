package org.dam2.fctcting.model

import kotlinx.serialization.Serializable

@Serializable
data class WorkOrder(
    val idWorkOrder: String,
    val desc: String,
    val projectManager: Int?,
    val idProject: String,
    val idAircraft: Int?
)