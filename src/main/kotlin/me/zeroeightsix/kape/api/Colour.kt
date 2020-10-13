@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package me.zeroeightsix.kape.api

/**
 * A colour encoded in hex RRGGBBAA
 */
typealias Colour = UInt

const val white: Colour = 0xFFFFFFFFu
const val red: Colour = 0xFF0000FFu
const val green: Colour = 0x00FF00FFu
const val blue: Colour = 0x0000FFFFu

val Colour.r
    get() = ((this shr 8 * 3) and 0xFFu).toUByte()
val Colour.g
    get() = ((this shr 8 * 2) and 0xFFu).toUByte()
val Colour.b
    get() = ((this shr 8 * 1) and 0xFFu).toUByte()
val Colour.a
    get() = ((this/* shr 8 * 0*/) and 0xFFu).toUByte()

fun fromRGBA(r: UByte = 255u, g: UByte = 255u, b: UByte = 255u, a: UByte = 255u): Colour
    = (r.toUInt() shl 8 * 3) or (g.toUInt() shl 8 * 2) or (b.toUInt() shl 8 * 1) or a.toUInt()

fun Colour.copy(r: UByte = this.r, g: UByte = this.g, b: UByte = this.b, a: UByte = this.a) = fromRGBA(r, g, b, a)