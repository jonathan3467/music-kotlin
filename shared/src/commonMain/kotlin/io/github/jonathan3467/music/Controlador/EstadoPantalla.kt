package io.github.jonathan3467.music.Controlador

import io.github.jonathan3467.music.modelo.RolaDetallada

//todo lo que la vista necesita para dibujarse
data class EstadoPantalla(
    val rolas: List<RolaDetallada> = emptyList(),
    val minando: Boolean = false,
    val mensaje: String? = null
)
