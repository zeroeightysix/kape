# kape
kape, short for **k**otlin sh**ape**, is an [immediate mode GUI](https://en.wikipedia.org/wiki/Immediate_mode_GUI) library written in Kotlin.

kape is built on OpenGL (through [LWJGL](https://www.lwjgl.org/)), but everything that does not directly depend on OpenGL has been abstracted and can be implemented for another graphics API.

### Immediate mode GUI

does not imply kape uses the archaic OpenGL 'immediate rendering' technique.

kape uses the modern rendering pipeline, and stores all rendered data in vertex array/buffer objects.
It will only re-render parts of the UI when a visual change occurs.

Read more about the immediate mode gui paradigm [here](https://en.wikipedia.org/wiki/Immediate_mode_GUI).