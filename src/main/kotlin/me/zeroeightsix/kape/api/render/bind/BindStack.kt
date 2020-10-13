package me.zeroeightsix.kape.api.render.bind

import me.zeroeightsix.kape.api.ID

interface BindStack {
    fun Bind.bindScoped(block: () -> Unit)
}

class BasicBindStack : BindStack {
    private val bindStackMap = mutableMapOf<ID, ArrayDeque<Bind>>()

    private fun getBindStack(bind: Bind) = bindStackMap.getOrPut(bind.bindTypeId) { ArrayDeque() }

    override fun Bind.bindScoped(block: () -> Unit) {
        val stack = getBindStack(this)

        stack.add(this)
        this.bind()

        block()

        stack.removeLast()
        val last = stack.lastOrNull()
        if (last != null)
            last.bind()
        else
            this.resetBind()
    }
}

object NoBindStack: BindStack {
    override fun Bind.bindScoped(block: () -> Unit) {
        bind()
        block()
    }
}