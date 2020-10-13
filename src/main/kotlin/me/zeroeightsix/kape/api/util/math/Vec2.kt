package me.zeroeightsix.kape.api.util.math

data class Vec2<T : Number>(val x: T, val y: T)

typealias Vec2f = Vec2<Float>
typealias Vec2d = Vec2<Double>
typealias Vec2i = Vec2<Int>

// Unfortunately [Number] doesn't have any math functions, so we can't overload generically.
operator fun Vec2f.unaryMinus() = Vec2f(-this.x, -this.y)

operator fun Vec2f.times(multiplier: Float) = Vec2f(this.x * multiplier, this.y * multiplier)