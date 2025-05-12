package org.dam2.fctcting.model

import kotlinx.serialization.Serializable

@Serializable
data class TimeCode(
    val idTimeCode: Int,
    val desc: String,
    val color: String,
    val chkProd: Boolean
)