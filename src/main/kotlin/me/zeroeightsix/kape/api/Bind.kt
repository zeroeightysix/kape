package me.zeroeightsix.kape.api

interface Bind {
    val bindTypeId: ID

    fun bind()
    fun resetBind()
}