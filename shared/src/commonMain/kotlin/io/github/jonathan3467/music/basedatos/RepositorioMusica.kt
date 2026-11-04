package io.github.jonathan3467.music.basedatos

import io.github.jonathan3467.music.modelo.Album
import io.github.jonathan3467.music.modelo.Grupo
import io.github.jonathan3467.music.modelo.Performer
import io.github.jonathan3467.music.modelo.Persona
import io.github.jonathan3467.music.modelo.Rola
import io.github.jonathan3467.music.modelo.RolaDetallada
import io.github.jonathan3467.music.modelo.TipoPerformer


// las operaciones que los demas pueden hacer con la base de datos
interface RepositorioMusica {

    fun buscarPerformerPorNombre(nombre: String): Performer?

    fun buscarPerformerPorId(id: Long): Performer?

    //cambia el nombre y el ripo del performer
    fun actualizarPerformer(performer: Performer)

    fun buscarPersona(nombreArtistico: String): Persona?

    //inserta la persiona (el id se ignora) y la regresa con su id real
    fun insertarPersona(persona: Persona): Persona

    fun actualizarPersona(persona: Persona)

    //borra la persona y en los grupos tambien se borra a esa persona
    fun borrarPersona(id: Long)

    fun buscarGrupo(nombre: String): Grupo?

    // Inserta al grupo(el id se ignora) y lo regresa con su id real
    fun insertarGrupo(grupo: Grupo): Grupo

    fun actualizarGrupo(grupo: Grupo)

    //borra el grupo y sus miembors(las personas se quedan)
    fun borrarGrupo(id: Long)

    fun insertarPerformer(
        nombre: String,
        tipo: TipoPerformer = TipoPerformer.DESCONOCIDO
    ): Performer

    //un album se identifica por nombre y caprtea, dos artistas pueden tener un "los mejores exitos"
    fun buscarAlbum(nombre: String, ruta: String): Album?

    fun insertarAlbum(nombre: String, ruta:String, year: Int): Album

    fun buscarAlbumPorId(id: Long): Album?

    //cambia los campos que se pueden editar de la rola como el performer el titulo, pista,años y genero
    fun actualizarRola(rola: Rola)

    //cambia el nombre y el año del album
    fun actualizarAlbum(album: Album)

    //regresa el performer con ese nombre, si no existe se crea como desconocido
    fun obtenerOCrearPerformer(nombre: String): Performer =
        buscarPerformerPorNombre(nombre) ?: insertarPerformer(nombre)

    fun insertarRola(
        idPerformer: Long,
        idAlbum: Long,
        ruta: String,
        titulo: String,
        pista:Int,
        year: Int,
        genero: String
    ): Rola

    fun todasLasRolas(): List<Rola>

    fun todasLasRolasDetalladas(): List<RolaDetallada>

    fun vaciar()

    // se guarda todo o nada en una transaccion
    fun <T> enTransaccion(bloque: () -> T): T
}