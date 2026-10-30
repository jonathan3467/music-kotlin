package io.github.jonathan3467.music.basedatos

import io.github.jonathan3467.music.modelo.Album
import io.github.jonathan3467.music.modelo.Performer
import io.github.jonathan3467.music.modelo.Rola
import io.github.jonathan3467.music.modelo.RolaDetallada
import io.github.jonathan3467.music.modelo.TipoPerformer


// las operaciones que los demas pueden hacer con la base de datos
interface RepositorioMusica {

    fun buscarPerformerPorNombre(nombre: String): Performer?

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