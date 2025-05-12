package org.dam2.fctcting

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform