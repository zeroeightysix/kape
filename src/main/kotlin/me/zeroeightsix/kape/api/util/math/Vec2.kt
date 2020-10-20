package me.zeroeightsix.kape.api.util.math

data class Vec2<T : Number>(val x: T, val y: T) {
    constructor(both: T) : this(both, both)
}

typealias Vec2f = Vec2<Float>
typealias Vec2d = Vec2<Double>
typealias Vec2i = Vec2<Int>

// Unfortunately [Number] doesn't have any math functions, so we can't overload generically.
val Vec2f.justX
    get() = Vec2f(this.x, 0f)
val Vec2f.justY
    get() = Vec2f(0f, this.y)

fun Vec2f.offsets(inclusive: Boolean = false, vararg offsets: Vec2f) =
    (if (inclusive) mutableListOf(this) else mutableListOf())
        .apply { addAll(offsets.map { this@offsets + it }) }.toTypedArray()

operator fun Vec2f.unaryMinus() = Vec2f(-this.x, -this.y)

operator fun Vec2f.times(multiplier: Float) = Vec2f(this.x * multiplier, this.y * multiplier)

@JvmName("plusF")
operator fun Vec2f.plus(other: Vec2f) = Vec2f(this.x + other.x, this.y + other.y)

@JvmName("plusD")
operator fun Vec2d.plus(other: Vec2d) = Vec2d(this.x + other.x, this.y + other.y)

@JvmName("plusI")
operator fun Vec2i.plus(other: Vec2i) = Vec2i(this.x + other.x, this.y + other.y)

@JvmName("minusF")
operator fun Vec2f.minus(other: Vec2f) = Vec2f(this.x - other.x, this.y - other.y)

@JvmName("minusD")
operator fun Vec2d.minus(other: Vec2d) = Vec2d(this.x - other.x, this.y - other.y)

@JvmName("minusI")
operator fun Vec2i.minus(other: Vec2i) = Vec2i(this.x - other.x, this.y - other.y)

@JvmName("divD")
operator fun Vec2d.div(other: Vec2d) = Vec2d(this.x / other.x, this.y / other.y)

@JvmName("divI")
operator fun Vec2i.div(other: Vec2i) = Vec2i(this.x / other.x, this.y / other.y)

@JvmName("divF")
operator fun Vec2f.div(other: Vec2f) = Vec2f(this.x / other.x, this.y / other.y)

/**
 * Returns 'smaller than' (`-1`) if this vector has a smaller x and y value than `other`.
 *
 * Returns 'equal' (`0`) if this vector is equal to the other vector.
 *
 * Otherwise, returns 'greater than' (`1`)
 */
operator fun Vec2f.compareTo(other: Vec2f) = when {
    this.x < other.x && this.y < other.y -> -1
    this.x == other.x && this.y == other.y -> 0
    else -> 1
}

fun Vec2d.toVec2f() = Vec2f(this.x.toFloat(), this.y.toFloat())