package me.zeroeightsix.kape.api.render.bind

import me.zeroeightsix.kape.api.ID

interface Bind {
    val bindTypeId: ID

    fun bind()
    fun resetBind()
}