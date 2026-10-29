package io.github.jonathan3467.music.Controlador

import io.github.jonathan3467.music.modelo.RolaDetallada

//todo lo que la vista necesita para dibujarse
data class EstadoPantalla(
    val rolas: List<RolaDetallada> = emptyList(),
    val minando: Boolean = false,
    val mensaje: String? = null,
    val idSeleccionada: Long? = null
){
    //la rola seleccionada
    val seleccionada: RolaDetallada?
        get() = rolas.firstOrNull{ it.rola.id == idSeleccionada}
}
