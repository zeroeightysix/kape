package me.zeroeightsix.kape

import me.zeroeightsix.kape.element.IDMap
import me.zeroeightsix.kape.element.Window

class Kape(private val windowMap: IDMap<Window>) : IDMap<Window> by windowMap {



}