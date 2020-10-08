package me.zeroeightsix.kape.impl.gl

internal typealias BoolRef = IntArray

internal fun newBoolRef(): BoolRef = BoolRef(1)
internal operator fun BoolRef.invoke() = this[0] == 1
