package me.zeroeightsix.kape.api.context

import me.zeroeightsix.kape.api.element.GlPrimitive

class Context : Reproducible<Context> {
    infix fun draw(primitive: GlPrimitive) {
//        println("adding prim $primitive")
    }

    infix fun draw(supplier: () -> GlPrimitive) = this.draw(supplier())

    operator fun plusAssign(primitive: GlPrimitive) = this.draw(primitive)

    override fun createNext() = Context()
}