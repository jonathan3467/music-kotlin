package io.github.jonathan3467.music.compilador

import io.github.jonathan3467.music.Compilador.compilarBusqueda
import io.github.jonathan3467.music.basedatos.BaseDeDatos
import io.github.jonathan3467.music.basedatos.RepositorioJdbc
import io.github.jonathan3467.music.modelo.Album
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BusquedaTest {

    private val repo = RepositorioJdbc()
    private lateinit var dir: File

    @BeforeTest
    fun prepararBase() {
        dir = createTempDirectory("busqueda-test").toFile()
        BaseDeDatos.abrir(File(dir, "musica.db"))

        val beatles = repo.insertarPerformer("The Beatles")
        val juanes = repo.insertarPerformer("Juanes")
        val alizee = repo.insertarPerformer("Alizee")
        val letItBe = repo.insertarAlbum("Let It Be", "/musica/let-it-be", 1970)
        val help = repo.insertarAlbum("Help", "/musica/help", 1965)
        val gourmandises = repo.insertarAlbum("Gourmandises", "/musica/gourmandises", 2000)

        repo.insertarRola(beatles.id, letItBe.id, "/1.mp3", "Get Back", 12, 1970, "Rock")
        repo.insertarRola(beatles.id, help.id, "/2.mp3", "Help!", 1, 1965, "Rock")
        repo.insertarRola(juanes.id, Album.ID_DESCONOCIDO, "/3.mp3", "A Dios Le Pido", 1, 2002, "Pop")
        repo.insertarRola(alizee.id, gourmandises.id, "/4.mp3", "Abracadabra", 3, 2000, "Pop")
    }

    @AfterTest
    fun cerrarYBorrar(){
        BaseDeDatos.cerrar()
        dir.deleteRecursively()
    }

    private fun titulos(busqueda: String): List<String> =
        repo.buscarRolas(compilarBusqueda(busqueda)).map { it.rola.titulo }

    @Test
    fun porInterprete(){
        assertEquals(listOf("Get Back", "Help!"),titulos("interprete: beatles"))
    }

    @Test
    fun palabraSueltaYComparacion(){
        assertEquals(listOf("Get Back"), titulos("beatles y año > 1965"))
    }

    @Test
    fun conNegacion(){
        assertEquals(listOf("Abracadabra"), titulos("genero: pop y no interprete: juanes"))
    }

    @Test
    fun sinImportarMayusculasYConComillas(){
        assertEquals(listOf("A Dios Le Pido"), titulos("titulo: \"a dios\""))
    }

    @Test
    fun lasRolasSinAlbumEstanEnUnknown(){
        assertEquals(listOf("A Dios Le Pido"), titulos("album: unknown"))
    }

    @Test
    fun sinResultados(){
        assertTrue(titulos("pista = 99").isEmpty())
    }

    @Test
    fun unIntentoDeInyeccionSqlNoHaceNada(){
        assertTrue(titulos("titulo: \"x' OR '1'='1\"").isEmpty())
        assertEquals(4, repo.todasLasRolas().size)
    }
}