package me.zeroeightsix.kape.window

interface Window {

    /**
     * Updates this [Window]. Updating might imply polling buffers, polling close state, etc.
     */
    fun update() = Unit

    /**
     * Whether or not this window was closed by the user. This may return `true` even if the window is not yet closed, but has instead requested to be closed.
     */
    val shouldClose: Boolean

}