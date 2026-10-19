package io.github.jonathan3467.music.minero

import io.github.jonathan3467.music.basedatos.RepositorioMusica
import io.github.jonathan3467.music.modelo.Album
import io.github.jonathan3467.music.modelo.RolaMinada

// Guarda en la base las rolas que encontro el minero, antes de guardar borra todo
class Importador (private val repo: RepositorioMusica){

    //regresa cuantas rolas guardo
    fun importar(rolas: List<RolaMinada>): Int =
        repo.enTransaccion {
            repo.vaciar()
            for (rola in rolas){
                guardar(rola)
            }
            rolas.size
        }

    private fun guardar (rola: RolaMinada){
        val idPerformer = idDelPerformer(rola.interprete)
        val idAlbum = idDelAlbum(rola)

        repo.insertarRola(
            idPerformer = idPerformer,
            idAlbum = idAlbum,
            ruta = rola.ruta,
            titulo = rola.titulo,
            pista = rola.pista,
            year = rola.year,
            genero = rola.genero
        )
    }

    // Si el performer ya existe usa su id, si no lo crea como unknown
    private fun idDelPerformer(nombre: String): Long =
        repo.buscarPerformerPorNombre(nombre)?.id
            ?: repo.insertarPerformer(nombre).id

    //sin album va al 0 osea unknown, si no lo busca por nombre y carpeta o lo crea
    private fun idDelAlbum(rola: RolaMinada): Long{
        val nombre = rola.album ?: return Album.ID_DESCONOCIDO
        return repo.buscarAlbum(nombre, rola.rutaAlbum)?.id
            ?: repo.insertarAlbum(nombre, rola.rutaAlbum, rola.year).id
    }
}