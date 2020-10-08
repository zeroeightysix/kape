package me.zeroeightsix.kape.api.gl

import me.zeroeightsix.kape.api.Destroy

interface Buffer : Destroy {
    fun bind()
    fun unBind()
}