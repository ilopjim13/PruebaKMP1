package org.dam2.fctcting.utils

import org.dam2.fctcting.model.TimeCode
import org.dam2.fctcting.model.dto.TimeCodeDTO

object DTOConverter {

    fun TimeCode.toDTO(): TimeCodeDTO {
        return TimeCodeDTO(
            idTimeCode = idTimeCode,
            desc = desc,
            color = color.removePrefix("0x").toULong(16).toLong(),
            chkProd = chkProd
        )
    }
    fun TimeCodeDTO.toEntity(): TimeCode {
        return TimeCode(
            idTimeCode = idTimeCode,
            desc = desc,
            color = "0x" + color.toString(16).uppercase(),
            chkProd = chkProd
        )
    }


}