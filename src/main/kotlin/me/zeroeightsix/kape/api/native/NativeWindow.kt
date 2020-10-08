package me.zeroeightsix.kape.api.native

interface NativeWindow {

    /**
     * Updates this [NativeWindow]. Updating might imply polling buffers, polling close state, etc.
     */
    fun update() = Unit

    /**
     * Whether or not this window was closed by the user. This may return `true` even if the window is not yet closed, but has instead requested to be closed.
     */
    val shouldClose: Boolean

}