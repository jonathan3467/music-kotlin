package io.github.jonathan3467.music.minero

import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class MineroTest{
    private val minero = Minero()
    private lateinit var dir: File

    @BeforeTest
    fun crearCarpeta(){
        dir = createTempDirectory("minero-test").toFile()
    }

    @AfterTest
    fun borrarCarpeta(){
        dir.deleteRecursively()
    }

    @Test
    fun encuentraMp3EnSubcarpetasEIgnoraOtrosArchivos(){
        File(dir, "a.mp3").writeText("x")
        File(dir, "sub/b.MP3").apply { parentFile.mkdirs(); writeText("x") }
        File(dir, "notas.txt").writeText("x")
        val nombres = minero.buscarMp3(dir).map { it.name }.toSet()
        assertEquals(setOf("a.mp3", "b.MP3"), nombres)
    }

    @Test
    fun archivosSinEtiquetasUsaValoresPorOmision(){
        val archivo = File(dir, "Mi Cancion.mp3").apply { writeText("esto no es un mp3") }
        val rola = minero.leerRola(archivo)
        assertEquals("Mi Cancion", rola.titulo)
        assertEquals("Unknown", rola.interprete)
        assertNull(rola.album)
        assertEquals(0, rola.year)
        assertEquals(0, rola.pista)
        assertEquals(dir.absolutePath, rola.rutaAlbum)
    }

    @Test
    fun extraerNumeroDeLasEtiquetas(){
        assertEquals(1970, extraerNumero("1970-05-08"))
        assertEquals(3, extraerNumero("3/12"))
        assertEquals(0, extraerNumero("abc"))
        assertEquals(0, extraerNumero(null))
    }

    @Test
    fun minarFallasSiNoEsDirectorio() {
        val archivo = File(dir, "a.mp3").apply { writeText("x") }
        assertFailsWith<IllegalArgumentException>{ minero.minar(archivo) }
    }
}