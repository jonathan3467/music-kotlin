package io.github.jonathan3467.music.Controlador

import io.github.jonathan3467.music.modelo.RolaDetallada

//todo lo que la vista necesita para dibujarse
data class EstadoPantalla(
    val rolas: List<RolaDetallada> = emptyList(),
    val minando: Boolean = false,
    val mensaje: String? = null,
    val idSeleccionada: Long? = null,
    val dialogo: Dialogo? = null,
    //la busqueda activa, se vacia si se muestran todas las rolas
    val busqueda: String = "",
    //el error de la ultima busqueda si estaba mal escrita
    val errorBusqueda: String? = null
){
    //la rola seleccionada
    val seleccionada: RolaDetallada?
        get() = rolas.firstOrNull{ it.rola.id == idSeleccionada}
}
