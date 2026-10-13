package io.github.jonathan3467.music.minero

import io.github.jonathan3467.music.modelo.RolaMinada
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger

class Minero {
    // recorre el directorio y regresa la informacion de cada MP3 que encuentre
    fun minar(directorio: File): List<RolaMinada>{
        require(directorio.isDirectory){"No es un directorio: ${directorio.path}"}
        return buscarMp3(directorio).map {leerRola(it)}
    }

    // todos los .mp3 dentro del directorio incluyendo subcarpetas
    fun buscarMp3(directorio: File): List<File> =
        directorio.walkTopDown()
            .filter { it.isFile && it.extension.equals("mp3", ignoreCase = true) }
            .toList()

    // lee las etiquetas de un MP3 si falta alguna usa valores por omision
    fun leerRola(archivo: File): RolaMinada{
        val etiqueta = leerEtiqueta(archivo)

        fun campo(clave: FieldKey): String? =
            runCatching { etiqueta?.getFirst(clave) }.getOrNull()
                ?.trim()
                ?.takeIf { it.isNotEmpty() }

        return RolaMinada(
            ruta = archivo.absolutePath,
            rutaAlbum = archivo.absoluteFile.parentFile.absolutePath,
            titulo = campo(FieldKey.TITLE) ?: archivo.nameWithoutExtension,
            interprete = campo(FieldKey.ARTIST) ?: "Unknown",
            album = campo(FieldKey.ALBUM),
            year = extraerNumero(campo(FieldKey.YEAR)),
            pista = extraerNumero(campo(FieldKey.TRACK)),
            genero = campo(FieldKey.GENRE) ?: "Unknown"
        )
    }

    // regresa null si el archivo no se puede leer
    private fun leerEtiqueta(archivo: File): Tag? =
        try {
            AudioFileIO.read(archivo).tag
        } catch (e: Exception){
            null
        }
    companion object{
        // Silencia los mensajes de jaudiotagger
        private  val logJaudiotagger: Logger =
            Logger.getLogger("org.jaudiotagger").apply { level= Level.OFF }
    }
}

// Sca el numero del inicio de un texto
// "1970-05-08" -> 1970 (en el id3v2 el año puede venir completo)
// 3/12 -> 3 (pista 3 de 12) si no hay numero regresa 0
fun extraerNumero(texto: String?): Int =
    texto?.let { Regex("^\\d+").find(it.trim())?.value?.toIntOrNull() } ?: 0