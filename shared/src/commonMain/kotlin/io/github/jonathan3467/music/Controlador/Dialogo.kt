package io.github.jonathan3467.music.Controlador

import io.github.jonathan3467.music.modelo.Album
import io.github.jonathan3467.music.modelo.RolaDetallada


//los dialogos que se pueden abrir, cada uno va a guardar lo que necesita para mostrarse
sealed interface Dialogo{
    data class EditarRola(
        val detallada: RolaDetallada,
        val error: String? = null
    ) : Dialogo

    data class EditarAlbum(
        val album: Album,
        val error: String? = null
    ) : Dialogo
}