package me.zeroeightsix.kape.api.context

import me.zeroeightsix.kape.api.element.GlPrimitive

interface Reproducible<T: Reproducible<T>> {
    fun createNext(): T
}

class Context : Reproducible<Context> {
    infix fun draw(primitive: GlPrimitive) {
        println("adding prim $primitive")
    }

    operator fun plusAssign(primitive: GlPrimitive) = this.draw(primitive)

    override fun createNext() = Context()
}