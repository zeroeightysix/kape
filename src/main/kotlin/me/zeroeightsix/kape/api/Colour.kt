package me.zeroeightsix.kape.api

/**
 * A colour encoded in hex RRGGBBAA
 */
typealias Colour = Int

const val white: Colour = (0xFFFFF shl 8) or 0xFF
const val red: Colour = (0xFF0000 shl 8) or 0xFF
const val green: Colour = (0x00FF00 shl 8) or 0xFF
const val blue: Colour = (0x0000FF shl 8) or 0xFF

val Colour.r
    get() = (this shr 8 * 3) and 0xFF
val Colour.g
    get() = (this shr 8 * 2) and 0xFF
val Colour.b
    get() = (this shr 8 * 1) and 0xFF
val Colour.a
    get() = (this/* shr 8 * 0*/) and 0xFF

fun fromRGBA(r: Int = 255, g: Int = 255, b: Int = 255, a: Int = 255): Colour
    = (r shl 8 * 3) or (g shl 8 * 2) or (b shl 8 * 1) or a

fun Colour.copy(r: Int = this.r, g: Int = this.g, b: Int = this.b, a: Int = this.a) = fromRGBA(r, g, b, a)