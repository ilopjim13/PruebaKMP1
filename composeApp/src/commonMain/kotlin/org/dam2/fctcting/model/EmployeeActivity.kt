package org.dam2.fctcting.model

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeActivity(
    val idEmployee: Int,
    val idWorkOrder: String,
    val idTimeCode: Int,
    val idActivity: Int,
    val time: Float,
    val date: String,
    val comment: String?
)