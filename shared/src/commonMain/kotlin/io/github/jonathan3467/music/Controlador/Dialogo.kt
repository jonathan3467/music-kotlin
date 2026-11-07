package io.github.jonathan3467.music.Controlador

import io.github.jonathan3467.music.modelo.Album
import io.github.jonathan3467.music.modelo.Grupo
import io.github.jonathan3467.music.modelo.Performer
import io.github.jonathan3467.music.modelo.Persona
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

    data class EditarPerformer(
        val performer: Performer,
        val persona: Persona?,
        val grupo: Grupo?,
        val error: String? = null
    ) : Dialogo

    data class Miembros(
        val grupo: Grupo,
        val miembros: List<Persona>,
        val error: String? = null
    ) : Dialogo

    data class NuevaPersona(
        val error: String? = null
    ) : Dialogo
}