package me.zeroeightsix.kape.api.util.math

data class Vec2<T : Number>(val x: T, val y: T) {
    constructor(both: T) : this(both, both)
}

typealias Vec2f = Vec2<Float>
typealias Vec2d = Vec2<Double>
typealias Vec2i = Vec2<Int>

// Unfortunately [Number] doesn't have any math functions, so we can't overload generically.
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

fun Vec2d.toVec2f() = Vec2f(this.x.toFloat(), this.y.toFloat())